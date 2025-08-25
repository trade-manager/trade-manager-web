package org.trade.core.persistent.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.SqlResultSetMapping;
import org.trade.core.dao.Aspect;
import org.trade.core.properties.ConfigProperties;

import java.io.IOException;
import java.io.Serial;
import java.math.BigDecimal;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
//@SqlResultSetMappings({
//@SqlResultSetMapping(name = "TradelogSummaryMapping", entities = @EntityResult(entityClass = TradelogSummary.class))})

@SqlResultSetMapping(
        name = "TradelogSummaryMapping",
        entities = {
                @EntityResult(
                        entityClass = TradelogSummary.class,
                        fields = {
                                @FieldResult(name = "id", column = "id"),
                                @FieldResult(name = "period", column = "period"),
                                @FieldResult(name = "battingAverage", column = "batting_average"),
                                @FieldResult(name = "simpleSharpeRatio", column = "simple_sharpe_ratio"),
                                @FieldResult(name = "quantity", column = "quantity"),
                                @FieldResult(name = "commission", column = "commission"),
                                @FieldResult(name = "grossProfitLoss", column = "gross_profit_loss"),
                                @FieldResult(name = "netProfitLoss", column = "net_profit_loss"),
                                @FieldResult(name = "profitAmount", column = "profit_amount"),
                                @FieldResult(name = "lossAmount", column = "loss_amount"),
                                @FieldResult(name = "winCount", column = "win_count"),
                                @FieldResult(name = "lossCount", column = "loss_count"),
                                @FieldResult(name = "positionCount", column = "position_count"),
                                @FieldResult(name = "tradestrategyCount", column = "tradestrategy_count")})})

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
     * @param period             String
     * @param battingAverage     BigDecimal
     * @param simpleSharpeRatio  BigDecimal
     * @param quantity           Integer
     * @param commission         BigDecimal
     * @param grossProfitLoss    BigDecimal
     * @param netProfitLoss      BigDecimal
     * @param profitAmount       BigDecimal
     * @param lossAmount         BigDecimal
     * @param winCount           Integer
     * @param lossCount          Integer
     * @param positionCount      Integer
     * @param tradestrategyCount Integer
     */
    public TradelogSummary(String period, BigDecimal battingAverage,
                           BigDecimal simpleSharpeRatio, Integer quantity, BigDecimal commission, BigDecimal grossProfitLoss,
                           BigDecimal netProfitLoss, BigDecimal profitAmount, BigDecimal lossAmount, Integer winCount,
                           Integer lossCount, Integer positionCount, Integer tradestrategyCount) {

        this.period = period;
        this.battingAverage = battingAverage;
        this.simpleSharpeRatio = simpleSharpeRatio;
        this.quantity = quantity;
        this.commission = commission;
        this.grossProfitLoss = grossProfitLoss;
        this.netProfitLoss = netProfitLoss;
        this.profitAmount = profitAmount;
        this.lossAmount = lossAmount;
        this.winCount = winCount;
        this.lossCount = lossCount;
        this.positionCount = positionCount;
        this.tradestrategyCount = tradestrategyCount;
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
