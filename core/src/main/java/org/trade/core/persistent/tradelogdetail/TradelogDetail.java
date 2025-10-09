package org.trade.core.persistent.tradelogdetail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.SqlResultSetMapping;
import org.trade.core.aspect.Aspect;
import org.trade.core.properties.ConfigProperties;

import java.io.IOException;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
//@SqlResultSetMappings({
//        @SqlResultSetMapping(name = "TradelogDetailMapping", entities = @EntityResult(entityClass = TradelogDetail.class))})

@SqlResultSetMapping(
        name = "TradelogDetailMapping",
        entities = {
                @EntityResult(
                        entityClass = TradelogDetail.class,
                        fields = {
                                @FieldResult(name = "id", column = "id"),
                                @FieldResult(name = "open", column = "open"),
                                @FieldResult(name = "symbol", column = "symbol"),
                                @FieldResult(name = "tradestrategyId", column = "tradestrategy_id"),
                                @FieldResult(name = "longShort", column = "long_short"),
                                @FieldResult(name = "tier", column = "tier"),
                                @FieldResult(name = "marketBias", column = "market_bias"),
                                @FieldResult(name = "marketBar", column = "market_bar"),
                                @FieldResult(name = "name", column = "name"),
                                @FieldResult(name = "status", column = "status"),
                                @FieldResult(name = "side", column = "side"),
                                @FieldResult(name = "action", column = "action"),
                                @FieldResult(name = "stopPrice", column = "stop_price"),
                                @FieldResult(name = "orderStatus", column = "order_status"),
                                @FieldResult(name = "filledDate", column = "filled_date"),
                                @FieldResult(name = "quantity", column = "quantity"),
                                @FieldResult(name = "averageFilledPrice", column = "average_filled_price"),
                                @FieldResult(name = "commission", column = "commission"),
                                @FieldResult(name = "profitLoss", column = "profit_loss")})})
public class TradelogDetail extends Aspect implements java.io.Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -832064631322873796L;

    @Column(name = "tradestrategy_id")
    private Long tradestrategyId;

    @Column(name = "open", length = 19)
    private String open;

    @Column(name = "market_bias", length = 10)
    private String marketBias;

    @Column(name = "market_bar", length = 10)
    private String marketBar;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "symbol", length = 10)
    private String symbol;

    @Column(name = "long_short", length = 6)
    private String longShort;

    @Column(name = "tier", length = 1)
    private String tier;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "side", nullable = false, length = 3)
    private String side;

    @Column(name = "action", length = 6)
    private String action;

    @Column(name = "stop_price", precision = 10)
    private BigDecimal stopPrice;

    @Column(name = "order_status", length = 45)
    private String orderStatus;

    @Column(name = "filled_date")
    private ZonedDateTime filledDate;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "average_filled_price", precision = 10)
    private BigDecimal averageFilledPrice;

    @Column(name = "commission", precision = 10)
    private BigDecimal commission;

    @Column(name = "profit_loss", precision = 10)
    private BigDecimal profitLoss;

    public TradelogDetail() {
    }

    /**
     * Constructor for TradelogDetail.
     *
     * @param open               String
     * @param symbol             String
     * @param tradestrategyId    Long
     * @param longShort          String
     * @param tier               String
     * @param marketBias         String
     * @param marketBar          String
     * @param name               String
     * @param status             String
     * @param side               String
     * @param action             String
     * @param stopPrice          BigDecimal
     * @param orderStatus        String
     * @param filledDate         Date
     * @param quantity           Integer
     * @param averageFilledPrice BigDecimal
     * @param commission         BigDecimal
     * @param profitLoss         BigDecimal
     */
    public TradelogDetail(String open, String symbol, Long tradestrategyId, String longShort, String tier, String marketBias, String marketBar, String name,
                          String status, String side, String action,
                          BigDecimal stopPrice, String orderStatus, ZonedDateTime filledDate, Integer quantity,
                          BigDecimal averageFilledPrice, BigDecimal commission, BigDecimal profitLoss) {

        this.open = open;
        this.symbol = symbol;
        this.tradestrategyId = tradestrategyId;
        this.marketBias = marketBias;
        this.marketBar = marketBar;
        this.name = name;
        this.longShort = longShort;
        this.tier = tier;
        this.status = status;
        this.side = side;
        this.action = action;
        this.stopPrice = stopPrice;
        this.orderStatus = orderStatus;
        this.filledDate = filledDate;
        this.quantity = quantity;
        this.averageFilledPrice = averageFilledPrice;
        this.commission = commission;
        this.profitLoss = profitLoss;
    }

    /**
     * Method gettradestrategyId.
     *
     * @return Integer
     */
    public Long getTradestrategyId() {
        return this.tradestrategyId;
    }

    /**
     * Method setTradestrategyId.
     *
     * @param tradestrategyId Long
     */
    public void setTradestrategyId(Long tradestrategyId) {
        this.tradestrategyId = tradestrategyId;
    }

    /**
     * Method getOpen.
     *
     * @return String
     */
    public String getOpen() {
        return this.open;
    }

    /**
     * Method setOpen.
     *
     * @param open String
     */
    public void setOpen(String open) {
        this.open = open;
    }

    /**
     * Method getMarketBias.
     *
     * @return String
     */
    public String getMarketBias() {
        return this.marketBias;
    }

    /**
     * Method setMarketBias.
     *
     * @param marketBias String
     */
    public void setMarketBias(String marketBias) {
        this.marketBias = marketBias;
    }

    /**
     * Method getMarketBar.
     *
     * @return String
     */

    public String getMarketBar() {
        return this.marketBar;
    }

    /**
     * Method setMarketBar.
     *
     * @param marketBar String
     */
    public void setMarketBar(String marketBar) {
        this.marketBar = marketBar;
    }

    /**
     * Method getName.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Method setName.
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method getSymbol.
     *
     * @return String
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * Method setSymbol.
     *
     * @param symbol String
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Method getLongShort.
     *
     * @return String
     */
    public String getLongShort() {
        return this.longShort;
    }

    /**
     * Method setLongShort.
     *
     * @param longShort String
     */
    public void setLongShort(String longShort) {
        this.longShort = longShort;
    }

    /**
     * Method getTier.
     *
     * @return String
     */
    public String getTier() {
        return this.tier;
    }

    /**
     * Method setTier.
     *
     * @param tier String
     */
    public void setTier(String tier) {
        this.tier = tier;
    }

    /**
     * Method getStatus.
     *
     * @return String
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Method setStatus.
     *
     * @param status String
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Method getSide.
     *
     * @return String
     */
    public String getSide() {
        return this.side;
    }

    /**
     * Method setSide.
     *
     * @param side String
     */
    public void setSide(String side) {
        this.side = side;
    }

    /**
     * Method getAction.
     *
     * @return String
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Method setAction.
     *
     * @param action String
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Method getStopPrice.
     *
     * @return BigDecimal
     */
    public BigDecimal getStopPrice() {
        return this.stopPrice;
    }

    /**
     * Method setStopPrice.
     *
     * @param stopPrice BigDecimal
     */
    public void setStopPrice(BigDecimal stopPrice) {
        this.stopPrice = stopPrice;
    }

    /**
     * Method getOrderStatus.
     *
     * @return String
     */
    public String getOrderStatus() {
        return this.orderStatus;
    }

    /**
     * Method setOrderStatus.
     *
     * @param orderStatus String
     */
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * Method getFilledDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getFilledDate() {
        return this.filledDate;
    }

    /**
     * Method setFilledDate.
     *
     * @param filledDate ZonedDateTime
     */
    public void setFilledDate(ZonedDateTime filledDate) {
        this.filledDate = filledDate;
    }

    /**
     * Method getQuantity.
     *
     * @return Integer
     */
    public Integer getQuantity() {
        return this.quantity;
    }

    /**
     * Method setQuantity.
     *
     * @param quantity Integer
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Method getAverageFilledPrice.
     *
     * @return BigDecimal
     */
    public BigDecimal getAverageFilledPrice() {
        return this.averageFilledPrice;
    }

    /**
     * Method setAverageFilledPrice.
     *
     * @param averageFilledPrice BigDecimal
     */
    public void setAverageFilledPrice(BigDecimal averageFilledPrice) {
        this.averageFilledPrice = averageFilledPrice;
    }

    /**
     * Method getCommission.
     *
     * @return BigDecimal
     */
    public BigDecimal getCommission() {
        return this.commission;
    }

    /**
     * Method setCommission.
     *
     * @param commission BigDecimal
     */
    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    /**
     * Method getProfitLoss.
     *
     * @return BigDecimal
     */
    public BigDecimal getProfitLoss() {
        return this.profitLoss;
    }

    /**
     * Method setProfitLoss.
     *
     * @param profitLoss BigDecimal
     */
    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }

    /**
     * Method getSQLString.
     *
     * @return String
     */
    public static String getSQLString() throws IOException {

        return ConfigProperties.readFileAsString("org/trade/core/persistent/dao/sql/TradelogDetail.sql",
                Thread.currentThread().getContextClassLoader());

    }
}
