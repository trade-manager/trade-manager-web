package org.trade.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.persistent.candle.Candle;
import org.trade.core.persistent.candle.CandleRecord;
import org.trade.core.persistent.candle.CandleServiceIT;
import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.persistent.strategy.series.indicator.candle.CandlePeriod;
import org.trade.core.persistent.tradeorder.TradeOrder;
import org.trade.core.persistent.tradeorder.TradeOrderRecord;
import org.trade.core.persistent.tradestrategy.Tradestrategy;
import org.trade.core.persistent.tradestrategy.TradestrategyLiteRecord;
import org.trade.core.persistent.tradestrategy.TradestrategyRecord;
import org.trade.core.persistent.tradingday.Tradingday;
import org.trade.core.persistent.tradingday.TradingdayRecord;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.OrderType;
import org.trade.core.valuetype.Side;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Some tests for the JSONMapperIT class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class JSONMapperIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(CandleServiceIT.class);

    private static final String symbol = "TEST-" + TradestrategyBase.getRandomNumber(4);
    private static Tradestrategy tradestrategy;
    private static Integer clientId;

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() {

    }

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {

        clientId = ConfigProperties.getPropAsInt("trade.tws.clientId");
        Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
        tradestrategy = this.createTestTradestrategy(strategy, symbol, Side.BOT, ChartDays.ONE_DAY, BarSize.FIVE_MIN);
        assertNotNull(tradestrategy);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        this.deleteRecords();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {

    }
    @Test
    public void mapTradingdayJSON() throws JsonProcessingException {

        Tradingday tradingday = this.tradestrategy.getTradingday();
        TradingdayRecord tradingdayRecord = TradingdayRecord.from(tradingday);
        String tradingdayJSON = JSONMapper.getJSONString(tradingdayRecord);
        TradingdayRecord tradingdayRecordNew = JSONMapper.getRecord(tradingdayJSON, TradingdayRecord.class);
        assertEquals(tradingdayRecord.open(), tradingdayRecordNew.open());
        Tradingday tradingdayNew = JSONMapper.convertRecordToEntity(tradingdayRecordNew, Tradingday.class);
        assertEquals(tradingday.getOpen(), tradingdayNew.getOpen());
    }
    @Test
    public void mapCandleJSON() throws JsonProcessingException {

        RegularTimePeriod period = new CandlePeriod(
                TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone()), 300);

        Candle candle = new Candle(tradestrategy.getContract(), period, period.getStart());
        candle.setHigh(new BigDecimal("20.33"));
        candle.setLow(new BigDecimal("20.11"));
        candle.setOpen(new BigDecimal("20.23"));
        candle.setClose(new BigDecimal("20.28"));
        candle.setVolume(1500L);
        candle.setVwap(new BigDecimal("20.1"));
        candle.setTradeCount(10);

        candle = tradeService.getAspectService().save(candle);
        assertNotNull(candle.getId());
        _log.info("mapCandleJSON IdCandle: {}", candle.getId());

        candle.getContract().setTradePositions(new ArrayList<>());
        candle.getContract().setCandles(new ArrayList<>());
        String json = JSONMapper.getJSONString(CandleRecord.from(candle));
        _log.info("mapCandleJSON Candle JSON: {}", json);
        JSONObject candleJSON = new JSONObject(json);
        assertEquals(candle.getId(), candleJSON.getLong("id"));
        assertEquals(candle.getContract().getId(), candleJSON.getJSONObject("contract").getLong("id"));

        CandleRecord candleRecord = JSONMapper.getRecord(candleJSON.toString(), CandleRecord.class);
        _log.info("mapCandleJSON Candle JSON: {}", candleRecord.toString());
        assertEquals(candle.getId(), candleRecord.id());
        assertEquals(candle.getContract().getId(), candleRecord.contract().id());

        Candle newCandle = JSONMapper.convertRecordToEntity(candleRecord, Candle.class);

        Candle currCandle = tradeService.getCandleService().findById(candleRecord.id());
        _log.info("mapCandleJSON new Candle: {}", candle.toString());
        assertEquals(candle.getId(), currCandle.getId());
        assertEquals(candle.getContract().getId(), currCandle.getContract().getId());
    }

    @Test
    public void mapTradestrategyJSON() throws JsonProcessingException {

        Tradestrategy tradestrategy = tradeService.getTradestrategyService().findById(this.tradestrategy.getId());
        TradestrategyRecord tradestrategyRecord = TradestrategyRecord.from(tradestrategy);
        String json = JSONMapper.getJSONString(tradestrategyRecord);

        JSONObject tradestrategyJSON = new JSONObject(json);
        _log.info("mapCandleJSON Tradestrategy JSON: {}", json.toString());
        assertEquals(tradestrategy.getId(), tradestrategyJSON.getLong("id"));

        TradestrategyLiteRecord tradestrategyLiteRecord = TradestrategyLiteRecord.from(this.tradestrategy);
        json = JSONMapper.getJSONString(tradestrategyLiteRecord);
        _log.info("mapCandleJSON TradestrategyLite JSON: {}", json.toString());
        JSONObject tradestrategyLiteJSON = new JSONObject(json);
        assertEquals(tradestrategy.getId(), tradestrategyLiteJSON.getLong("id"));
    }

    @Test
    public void mapTradeOrderJSON() throws Exception {

        String side = tradestrategy.getSide();
        String action = Action.BUY;

        if (Side.SLD.equals(side)) {

            action = Action.SELL;
        }

        double risk = tradestrategy.getRiskAmount().doubleValue();
        double stop = 0.20;
        BigDecimal price = new BigDecimal(20);
        int quantity = (int) ((int) risk / stop);
        ZonedDateTime createDate = tradestrategy.getTradingday().getOpen().plusMinutes(5);

        TradeOrder tradeOrder = new TradeOrder(tradestrategy, action, OrderType.STPLMT, quantity, price,
                price.add(new BigDecimal("0.004")), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder.setClientId(clientId);
        tradeOrder.setTransmit(true);
        tradeOrder.setStatus("SUBMITTED");
        tradeOrder.validate();
        tradeOrder = tradeService.saveTradeOrder(tradeOrder);
        assertNotNull(tradeOrder);
        _log.info("IdOrder: {}", tradeOrder.getId());

        TradeOrderRecord tradeOrderRecord = TradeOrderRecord.from(tradeOrder);

        String json = JSONMapper.getJSONString(tradeOrderRecord);
        _log.info("mapTradeOrder tradeOrderRecord JSON: {}", json.toString());
        JSONObject dto = new JSONObject(json);
        assertEquals(tradeOrder.getId(), dto.getLong("id"));

        tradeOrderRecord = JSONMapper.getRecord(json, TradeOrderRecord.class);
        _log.info("mapTradeOrder tradeOrderRecord JSON: {}", tradeOrderRecord.getId());
        assertEquals(tradeOrder.getId(), tradeOrderRecord.getId());

        TradeOrder newTradeOrder = JSONMapper.convertRecordToEntity(tradeOrderRecord, TradeOrder.class);
        _log.info("mapTradeOrder new newTradeOrder: {}", newTradeOrder.toString());
        assertEquals(tradeOrder.getId(), newTradeOrder.getId());

        tradeOrder = tradeService.getTradeOrderService().findByOrderKey(newTradeOrder.getOrderKey());
        tradeOrderRecord = TradeOrderRecord.from(tradeOrder);
        json = JSONMapper.getJSONString(tradeOrderRecord);
        _log.info("mapTradeOrder tradeOrderRecord JSON: {}", json.toString());
        dto = new JSONObject(json);
        assertEquals(tradeOrder.getId(), dto.getLong("id"));
    }
}

