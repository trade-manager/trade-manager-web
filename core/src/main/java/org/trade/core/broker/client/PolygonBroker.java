/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.core.broker.client;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.Exchange;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PolygonBroker extends Broker {

    private final static Logger _log = LoggerFactory.getLogger(PolygonBroker.class);

    private final Integer reqId;
    private final Contract contract;
    private final String chartDays;
    private final String barSize;
    private final String endDateTime;
    private final IClientWrapper brokerModel;
    private final static String apiKey;

    static {

        try {

            apiKey = ConfigProperties.getPropAsString("trade.polygon.key");
        } catch (IOException ex) {

            throw new IllegalArgumentException("Error initializing PolygonBroker Msg: " + ex.getMessage());
        }
    }

    public PolygonBroker(Integer reqId, Contract contract, String endDateTime, String chartDays, String barSize,
                         IClientWrapper brokerModel) {
        this.reqId = reqId;
        this.contract = contract;
        this.barSize = barSize;
        this.chartDays = chartDays;
        this.endDateTime = endDateTime;
        this.brokerModel = brokerModel;
    }

    public Void doInBackground() {

        try {

            if (setContractDetails(contract)) {

                this.brokerModel.contractDetails(contract.getId(), contract);

                ZonedDateTime endDate = TradingCalendar.getZonedDateTimeFromDateTimeString(this.endDateTime,
                        "yyyyMMdd HH:mm:ss");
                ChartDays chartDays = ChartDays.newInstance();
                chartDays.setDisplayName(this.chartDays);

                BarSize barSize = BarSize.newInstance();
                barSize.setDisplayName(this.barSize);

                ZonedDateTime startDate = endDate.minusDays((Integer.parseInt(chartDays.getCode())));
                startDate = TradingCalendar.getPrevTradingDay(startDate);

                if (BarSize.DAY == Integer.parseInt(barSize.getValue())) {

                    this.setPriceDataDay(this.reqId, this.contract.getSymbol(), startDate, endDate);
                } else {

                    this.setPriceDataIntraday(this.reqId, this.contract.getSymbol(),
                            Integer.parseInt(chartDays.getValue()), startDate, endDate);
                }
            }
        } catch (Exception ex) {

            _log.error("Error: PolygonBroker::doInBackground Symbol: {} Msg: {}", contract.getSymbol(), ex.getMessage(), ex);
        } finally {
            // This will save the candle series.
            _log.debug("Debug: PolygonBroker::doInBackground finished ReqId: {} Symbol: {}", this.reqId, this.contract.getSymbol());
            this.brokerModel.contractDetailsEnd(contract.getId());
            this.brokerModel.historicalDataComplete(this.reqId);
        }
        return null;
    }

    public void done() {

        _log.debug("PolygonBroker done for: {}", contract.getSymbol());
    }

    private boolean setContractDetails(Contract contract) throws IOException, InterruptedException {


        /*
         * Polygon curl -X GET "https://api.polygon.io/v3/reference/tickers/AAPL?apiKey=WGlljpSus0Ai1mj2ayaASNTcxchw9aUp"
         */
        String strUrl = "https://api.polygon.io/v3/reference/tickers/" + contract.getSymbol() + "?apiKey=" + apiKey;

        _log.debug("Debug: PolygonBroker::setContractDetails URL: {}", strUrl);

        // create a request
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            var request = HttpRequest.newBuilder(
                            URI.create(strUrl))
                    .header("accept", "application/json")
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        if (response.statusCode() == 200) {

            String jsonString = response.body();
            JSONObject contractObj = new JSONObject(jsonString);
            JSONObject resultObj = contractObj.optJSONObject("results");

            if (null != resultObj) {
                String name = resultObj.getString("name");

                if (CoreUtils.nullSafeComparator(contract.getLongName(), name) != 0) {

                    contract.setLongName(name);
                    contract.setDirty(true);
                }

                String currency = resultObj.getString("currency_name").toUpperCase();

                if (CoreUtils.nullSafeComparator(contract.getCurrency(), currency) != 0) {

                    contract.setCurrency(Currency.newInstance(currency).getCode());
                    contract.setDirty(true);
                }

                String exchange = resultObj.getString("primary_exchange");

                if (CoreUtils.nullSafeComparator(contract.getPrimaryExchange(), exchange) != 0) {

                    contract.setPrimaryExchange(Exchange.newInstance(exchange).getCode());
                    contract.setDirty(true);
                }
                return true;
            } else {

                _log.error("Error: PolygonBroker::setContractDetails  request to URL: {}, no results found: {}, contractObj: {}", strUrl, response.statusCode(), contractObj);
            }
        } else {

            _log.error("Error: PolygonBroker::setContractDetails request to URL: {}, failed with status code: {}", strUrl, response.statusCode());
        }
        return false;
    }

    private void setPriceDataIntraday(int reqId, String symbol, int charDays, ZonedDateTime startDate,
                                      ZonedDateTime endDate) throws IOException, InterruptedException {

        /*
         * Polygon curl -X GET "https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/minute/1746696600000/1746734400000?adjusted=true&sort=asc&limit=1500&apiKey=WGlljpSus0Ai1mj2ayaASNTcxchw9aUp"
         */
        int barSize = 60;
        String strUrl = "https://api.polygon.io/v2/aggs/ticker/" + symbol + "/range/1/minute/" + startDate.toInstant().toEpochMilli() + "/" + endDate.toInstant().toEpochMilli() + "?adjusted=true&sort=asc&limit=3000&apiKey=" + apiKey;

        _log.debug("Debug: PolygonBroker::getPriceDataSetIntraday URL: {}", strUrl);

        // create a request
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            var request = HttpRequest.newBuilder(
                            URI.create(strUrl))
                    .header("accept", "application/json")
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        if (response.statusCode() == 200) {

            String jsonString = response.body();
            JSONObject contractObj = new JSONObject(jsonString);
            int resultsCount = contractObj.getInt("resultsCount");

            if (resultsCount > 0) {

                JSONArray bars = contractObj.optJSONArray("results");

                for (int i = 0; i < bars.length(); i++) {

                    JSONObject barObj = bars.getJSONObject(i);

                    ZonedDateTime time = TradingCalendar.getZonedDateTimeFromMilli((barObj.getLong("t")));
                    ZonedDateTime tradingdayStart = TradingCalendar.getTradingDayStart(time);
                    ZonedDateTime tradingdayEnd = TradingCalendar.getTradingDayEnd(time);

                    // values:Timestamp,close,high,low,open,volume
                    double close = barObj.getDouble("c");
                    double high = barObj.getDouble("h");
                    double low = barObj.getDouble("l");
                    double open = barObj.getDouble("o");
                    double vwap = barObj.getDouble("vw");
                    long volume = barObj.getLong("v");
                    int tradeCount = barObj.getInt("n");

                    _log.debug("Info: PolygonBroker::getPriceDataSetIntraday Time : {}, Open: {}, High: {}, Low: {}, Close: {}, VW: {}, Volume: {}, tradeCount: {}", time, open, high, low, close, vwap, volume, tradeCount);

                    if ((time.isAfter(tradingdayStart) || time.equals(tradingdayStart)) && time.isBefore(tradingdayEnd)) {

                        // On the last one let the brokerModel model know its finished.
                        String dateString = String.valueOf(time.toInstant().toEpochMilli());
                        this.brokerModel.historicalData(reqId, dateString, open, high, low, close, volume,
                                tradeCount, barSize, vwap, false);
                    }
                }
            } else {

                _log.error("Error: PolygonBroker::setPriceDataIntraday  request to URL: {}, no results found: {}, contractObj: {}", strUrl, response.statusCode(), contractObj);
            }
        } else {

            _log.error("Error: PolygonBroker::setPriceDataIntraday request to URL: {}, failed with status code: {}, msg: {}", strUrl, response.statusCode(), response.body());
        }
    }

    private void setPriceDataDay(int reqId, String symbol, ZonedDateTime startDate, ZonedDateTime endDate) throws IOException, InterruptedException {

        /*
         * Polygon curl -X GET "https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/day/1746696600000/1746734400000?adjusted=true&sort=asc&limit=1500&apiKey=WGlljpSus0Ai1mj2ayaASNTcxchw9aUp"
         */
        String strUrl = "https://api.polygon.io/v2/aggs/ticker/" + symbol + "/range/1/day/" + startDate.toInstant().toEpochMilli() + "/" + endDate.toInstant().toEpochMilli() + "?adjusted=true&sort=asc&limit=1500&apiKey=" + apiKey;

        _log.debug("Debug: PolygonBroker::setPriceDataDay URL: {}", strUrl);

        // create a request
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            var request = HttpRequest.newBuilder(
                            URI.create(strUrl))
                    .header("accept", "application/json")
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        if (response.statusCode() == 200) {

            List<Candle> candles = new ArrayList<>();
            String jsonString = response.body();
            JSONObject contractObj = new JSONObject(jsonString);
            int resultsCount = contractObj.getInt("resultsCount");

            if (resultsCount > 0) {

                JSONArray bars = contractObj.optJSONArray("results");

                for (int i = 0; i < bars.length(); i++) {

                    JSONObject barObj = bars.getJSONObject(i);

                    ZonedDateTime time = TradingCalendar.getZonedDateTimeFromMilli((barObj.getLong("t")));
                    // values:Timestamp,close,high,low,open,volume
                    double close = barObj.getDouble("c");
                    double high = barObj.getDouble("h");
                    double low = barObj.getDouble("l");
                    double open = barObj.getDouble("o");
                    double vw = barObj.getDouble("vw");
                    long volume = barObj.getLong("v");
                    int tradeCount = barObj.getInt("n");

                    _log.info("Info: PolygonBroker::getPriceDataSetIntraday Time : {}, Open: {}, High: {}, Low: {}, Close: {}, VW: {}, Volume: {}, tradeCount: {}", time, open, high, low, close, vw, volume, tradeCount);
                    CandlePeriod period = new CandlePeriod(time, TradingCalendar.getDateAtTime(time, endDate).minusSeconds(1));

                    Candle candle = new Candle(null, period, open, high, low, close, volume, vw,
                            tradeCount, TradingCalendar.getDateTimeNowMarketTimeZone());

                    candle.setLastUpdateDate(time);
                    candles.add(candle);
                }

                Collections.reverse(candles);
                for (Candle candle : candles) {

                    long millis = TradingCalendar.geMillisFromZonedDateTime(candle.getStartPeriod());

                    this.brokerModel.historicalData(reqId, String.valueOf(millis), candle.getOpen().doubleValue(),
                            candle.getHigh().doubleValue(), candle.getLow().doubleValue(), candle.getClose().doubleValue(),
                            candle.getVolume().intValue(), candle.getTradeCount(), 1, candle.getVwap().doubleValue(), false);
                }
            } else {

                _log.error("Error: PolygonBroker::setPriceDataDay  request to URL: {}, no results found: {}, contractObj: {}", strUrl, response.statusCode(), contractObj);
            }
        } else {

            _log.error("Error: PolygonBroker::setPriceDataDay  request to URL: {}, failed with status code: {}", strUrl, response.statusCode());
        }
    }
}
