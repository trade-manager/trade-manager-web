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
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import org.trade.core.dao.Aspect;
import org.trade.core.properties.ConfigProperties;

import java.io.IOException;
import java.io.Serial;
import java.math.BigDecimal;

/**
 *
 */
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(name = "TradelogSummaryMapping", entities = @EntityResult(entityClass = TradelogSummary.class))})
public class TradelogSummary extends Aspect implements java.io.Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -832064631322873796L;


    @Column(name = "period", length = 19)
    private String period;

    @Column(name = "batting_average", precision = 10)
    private BigDecimal battingAverage;

    @Column(name = "simple_sharpe_ratio", precision = 10)
    private BigDecimal simpleSharpeRatio;

    @Column(name = "gross_profit_loss", precision = 10)
    private BigDecimal grossProfitLoss;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "commission", precision = 10)
    private BigDecimal commission;

    @Column(name = "net_profit_loss", precision = 10)
    private BigDecimal netProfitLoss;

    @Column(name = "profit_amount", precision = 10)
    private BigDecimal profitAmount;

    @Column(name = "loss_amount", precision = 10)
    private BigDecimal lossAmount;

    @Column(name = "win_count")
    private Integer winCount;

    @Column(name = "loss_count")
    private Integer lossCount;

    @Column(name = "position_count")
    private Integer positionCount;

    @Column(name = "tradestrategy_count")
    private Integer tradestrategyCount;

    public TradelogSummary() {
    }

    /**
     * Constructor for TradelogSummary.
     *
     * @param idTradelogSummary  Integer
     * @param period             String
     * @param battingAverage     BigDecimal
     * @param simpleSharpeRatio  BigDecimal
     * @param grossProfitLoss    BigDecimal
     * @param quantity           Integer
     * @param commission         BigDecimal
     * @param netProfitLoss      BigDecimal
     * @param profitAmount       BigDecimal
     * @param lossAmount         BigDecimal
     * @param winCount           Integer
     * @param lossCount          Integer
     * @param positionCount      Integer
     * @param tradestrategyCount Integer
     */
    public TradelogSummary(Integer idTradelogSummary, String period, BigDecimal battingAverage,
                           BigDecimal simpleSharpeRatio, BigDecimal grossProfitLoss, Integer quantity, BigDecimal commission,
                           BigDecimal netProfitLoss, BigDecimal profitAmount, BigDecimal lossAmount, Integer winCount,
                           Integer lossCount, Integer positionCount, Integer tradestrategyCount) {

        this.setId(idTradelogSummary);
        this.period = period;
        this.battingAverage = battingAverage;
        this.simpleSharpeRatio = simpleSharpeRatio;
        this.grossProfitLoss = grossProfitLoss;
        this.quantity = quantity;
        this.commission = commission;
        this.netProfitLoss = netProfitLoss;
        this.profitAmount = profitAmount;
        this.lossAmount = lossAmount;
        this.winCount = winCount;
        this.lossCount = lossCount;
        this.positionCount = positionCount;
        this.tradestrategyCount = tradestrategyCount;
    }

    /**
     * Method getIdTradelogSummary.
     *
     * @return Integer

     @Id
     @Column(name = "id_tradelog_summary")
     public Integer getIdTradelogSummary() {
     return this.id;
     }
     */
    /**
     * Method setIdTradelogSummary.
     *
     * @param idTradelogSummary Integer
     */
    public void setIdTradelogSummary(Integer idTradelogSummary) {
        this.setId(idTradelogSummary);
    }

    /**
     * Method getPeriod.
     *
     * @return String
     */
    public String getPeriod() {
        return this.period;
    }

    /**
     * Method setPeriod.
     *
     * @param period String
     */
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * Method getBattingAverage.
     *
     * @return BigDecimal
     */
    public BigDecimal getBattingAverage() {
        return this.battingAverage;
    }

    /**
     * Method setBattingAverage.
     *
     * @param battingAverage BigDecimal
     */
    public void setBattingAverage(BigDecimal battingAverage) {
        this.battingAverage = battingAverage;
    }

    /**
     * Method getSimpleSharpeRatio.
     *
     * @return BigDecimal
     */
    public BigDecimal getSimpleSharpeRatio() {
        return this.simpleSharpeRatio;
    }

    /**
     * Method setSimpleSharpeRatio.
     *
     * @param simpleSharpeRatio BigDecimal
     */
    public void setSimpleSharpeRatio(BigDecimal simpleSharpeRatio) {
        this.simpleSharpeRatio = simpleSharpeRatio;
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
     * Method getGrossProfitLoss.
     *
     * @return BigDecimal
     */
    public BigDecimal getGrossProfitLoss() {
        return this.grossProfitLoss;
    }

    /**
     * Method setGrossProfitLoss.
     *
     * @param grossProfitLoss BigDecimal
     */
    public void setGrossProfitLoss(BigDecimal grossProfitLoss) {
        this.grossProfitLoss = grossProfitLoss;
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
     * Method getNetProfitLoss.
     *
     * @return BigDecimal
     */
    public BigDecimal getNetProfitLoss() {
        return this.netProfitLoss;
    }

    /**
     * Method setNetProfitLoss.
     *
     * @param netProfitLoss BigDecimal
     */
    public void setNetProfitLoss(BigDecimal netProfitLoss) {
        this.netProfitLoss = netProfitLoss;
    }

    /**
     * Method getProfitAmount.
     *
     * @return BigDecimal
     */
    public BigDecimal getProfitAmount() {
        return this.profitAmount;
    }

    /**
     * Method setProfitAmount.
     *
     * @param profitAmount BigDecimal
     */
    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    /**
     * Method getLossAmount.
     *
     * @return BigDecimal
     */
    public BigDecimal getLossAmount() {
        return this.lossAmount;
    }

    /**
     * Method setLossAmount.
     *
     * @param lossAmount BigDecimal
     */
    public void setLossAmount(BigDecimal lossAmount) {
        this.lossAmount = lossAmount;
    }

    /**
     * Method getWinCount.
     *
     * @return Integer
     */
    public Integer getWinCount() {
        return this.winCount;
    }

    /**
     * Method setWinCount.
     *
     * @param winCount Integer
     */
    public void setWinCount(Integer winCount) {
        this.winCount = winCount;
    }

    /**
     * Method getLossCount.
     *
     * @return Integer
     */
    public Integer getLossCount() {
        return this.lossCount;
    }

    /**
     * Method setLossCount.
     *
     * @param lossCount Integer
     */
    public void setLossCount(Integer lossCount) {
        this.lossCount = lossCount;
    }

    /**
     * Method getPositionCount.
     *
     * @return Integer
     */
    public Integer getPositionCount() {
        return this.positionCount;
    }

    /**
     * Method setPositionCount.
     *
     * @param positionCount Integer
     */
    public void setPositionCount(Integer positionCount) {
        this.positionCount = positionCount;
    }

    /**
     * Method getTradestrategyCount.
     *
     * @return Integer
     */
    public Integer getTradestrategyCount() {
        return this.tradestrategyCount;
    }

    /**
     * Method setTradestrategyCount.
     *
     * @param tradestrategyCount Integer
     */
    public void setTradestrategyCount(Integer tradestrategyCount) {
        this.tradestrategyCount = tradestrategyCount;
    }

    /**
     * Method getSQLString.
     *
     * @return String
     */
    public static String getSQLString() throws IOException {

        return ConfigProperties.readFileAsString("org/trade/core/persistent/dao/sql/TradelogSummary.sql",
                Thread.currentThread().getContextClassLoader());
    }
}
