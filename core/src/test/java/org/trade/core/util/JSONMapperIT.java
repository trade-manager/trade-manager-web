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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.CandleDto;
import org.trade.core.persistent.dao.ContractDto;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderDto;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyLiteDto;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
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

/**
 * Some tests for the JSONMapperIT class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class JSONMapperIT {

    private final static Logger _log = LoggerFactory.getLogger(org.trade.core.persistent.dao.CandleIT.class);

    @Autowired
    private TradeService tradeService;

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
        tradestrategy = TradestrategyBase.createTestTradestrategy(tradeService, strategy, symbol, Side.BOT, ChartDays.ONE_DAY, BarSize.FIVE_MIN);
        assertNotNull(tradestrategy);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        TradestrategyBase.clearDBData(tradeService, tradestrategy);
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {

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

        candle = tradeService.saveAspect(candle);
        assertNotNull(candle.getId());
        _log.info("mapCandleJSON IdCandle: {}", candle.getId());

        candle.getContract().setTradePositions(new ArrayList<>());
        candle.getContract().setCandles(new ArrayList<>());
        ContractDto contractDto = JSONMapper.convertToDto(candle.getContract(), ContractDto.class);
        CandleDto candleDto = JSONMapper.convertToDto(candle, CandleDto.class);
        candleDto.setContract(contractDto);

        String json = JSONMapper.getJSONString(candleDto);
        _log.info("mapCandleJSON Candle JSON: {}", json.toString());
        JSONObject dto = new JSONObject(json);
        assertEquals(candle.getId(), dto.getLong("id"));

        candleDto = JSONMapper.getDTO(json, CandleDto.class);
        _log.info("mapCandleJSON Candle JSON: {}", candleDto.toString());
        assertEquals(candle.getId(), candleDto.getId());

        Candle newCandle = JSONMapper.convertToEntity(candleDto, Candle.class);
        _log.info("mapCandleJSON new Candle: {}", newCandle.toString());
        assertEquals(candle.getId(), newCandle.getId());
    }

    @Test
    public void mapTradeOrder() throws Exception {

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


        TradestrategyLiteDto tradestrategyLiteDto = JSONMapper.convertToDto(tradestrategy, TradestrategyLiteDto.class);
        TradeOrderDto tradeOrderDto = JSONMapper.convertToDto(tradeOrder, TradeOrderDto.class);
        tradeOrderDto.setTradestrategyLite(tradestrategyLiteDto);

        String json = JSONMapper.getJSONString(tradeOrderDto);
        _log.info("mapTradeOrder tradeOrderDto JSON: {}", json.toString());
        JSONObject dto = new JSONObject(json);
        assertEquals(tradeOrder.getId(), dto.getLong("id"));

        tradeOrderDto = JSONMapper.getDTO(json, TradeOrderDto.class);
        _log.info("mapTradeOrder tradeOrderDto JSON: {}", tradeOrderDto.toString());
        assertEquals(tradeOrder.getId(), tradeOrderDto.getId());

        TradeOrder newTradeOrder = JSONMapper.convertToEntity(tradeOrderDto, TradeOrder.class);
        _log.info("mapTradeOrder new newTradeOrder: {}", newTradeOrder.toString());
        assertEquals(tradeOrder.getId(), newTradeOrder.getId());

        tradeOrder = tradeService.findTradeOrderByKey(newTradeOrder.getOrderKey());
        tradeOrderDto = JSONMapper.convertToDto(tradeOrder, TradeOrderDto.class);
        json = JSONMapper.getJSONString(tradeOrderDto);
        _log.info("mapTradeOrder tradeOrderDto JSON: {}", json.toString());
        dto = new JSONObject(json);
        assertEquals(tradeOrder.getId(), dto.getLong("id"));
    }
}

