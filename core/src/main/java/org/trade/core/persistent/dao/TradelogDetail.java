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
package org.trade.core.persistent.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.Id;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import org.trade.core.dao.Aspect;
import org.trade.core.properties.ConfigProperties;

import java.io.IOException;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 *
 */
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(name = "TradelogDetailMapping", entities = @EntityResult(entityClass = TradelogDetail.class))})
public class TradelogDetail extends Aspect implements java.io.Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -832064631322873796L;

    @Id
    @Column(name = "tradelog_detail_id")
    private Integer id;

    @Column(name = "tradestrategy_id")
    private Integer tradestrategyId;

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
     * @param tradestrategyId    Integer
     * @param open               String
     * @param marketBias         String
     * @param marketBar          String
     * @param name               String
     * @param symbol             String
     * @param longShort          String
     * @param tier               String
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
    public TradelogDetail(Integer tradestrategyId, String open, String marketBias, String marketBar, String name,
                          String symbol, String longShort, String tier, String status, String side, String action,
                          BigDecimal stopPrice, String orderStatus, ZonedDateTime filledDate, Integer quantity,
                          BigDecimal averageFilledPrice, BigDecimal commission, BigDecimal profitLoss) {

        this.tradestrategyId = tradestrategyId;
        this.open = open;
        this.marketBias = marketBias;
        this.marketBar = marketBar;
        this.name = name;
        this.symbol = symbol;
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
     * Method getTradelogDetailId.
     *
     * @return Integer
     */
    public Integer getTradelogDetailId() {
        return this.id;
    }

    /**
     * Method setTradelogDetailId.
     *
     * @param tradelogDetailId Integer
     */
    public void setTradelogDetailId(Integer tradelogDetailId) {
        this.id = tradelogDetailId;
    }

    /**
     * Method gettradestrategyId.
     *
     * @return Integer
     */

    public Integer getTradestrategyId() {
        return this.tradestrategyId;
    }

    /**
     * Method setIdTradestrategy.
     *
     * @param tradestrategyId Integer
     */
    public void setIdTradestrategy(Integer tradestrategyId) {
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
