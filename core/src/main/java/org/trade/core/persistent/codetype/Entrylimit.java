package org.trade.core.persistent.codetype;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.trade.core.aspect.Aspect;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "entrylimit")
public class Entrylimit extends Aspect implements java.io.Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -8612117968275040016L;

    @Column(name = "start_price", nullable = false, precision = 10)
    private BigDecimal startPrice;

    @Column(name = "end_price", nullable = false, precision = 10)
    private BigDecimal endPrice;

    @Column(name = "limit_amount", nullable = false, precision = 10)
    private BigDecimal limitAmount;

    @Column(name = "percent_of_price", precision = 10)
    private BigDecimal percentOfPrice;

    @Column(name = "percent_of_margin", precision = 10)
    private BigDecimal percentOfMargin;

    @Column(name = "price_round", precision = 10)
    private BigDecimal priceRound;

    @Column(name = "share_round")
    private Integer shareRound;

    @Column(name = "pivot_range", precision = 10)
    private BigDecimal pivotRange;

    public Entrylimit() {
    }

    /**
     * Constructor for Entrylimit.
     *
     * @param startPrice  BigDecimal
     * @param endPrice    BigDecimal
     * @param limitAmount BigDecimal
     */
    public Entrylimit(BigDecimal startPrice, BigDecimal endPrice, BigDecimal limitAmount) {

        this.startPrice = startPrice;
        this.endPrice = endPrice;
        this.limitAmount = limitAmount;
    }

    /**
     * Constructor for Entrylimit.
     *
     * @param startPrice     BigDecimal
     * @param endPrice       BigDecimal
     * @param limitAmount    BigDecimal
     * @param percentOfPrice BigDecimal
     * @param priceRound     BigDecimal
     * @param shareRound     Integer
     * @param pivotRange     BigDecimal
     */
    public Entrylimit(BigDecimal startPrice, BigDecimal endPrice, BigDecimal limitAmount, BigDecimal percentOfPrice,
                      BigDecimal priceRound, BigDecimal percentOfMargin, Integer shareRound, BigDecimal pivotRange) {

        this.startPrice = startPrice;
        this.endPrice = endPrice;
        this.limitAmount = limitAmount;
        this.percentOfPrice = percentOfPrice;
        this.percentOfMargin = percentOfMargin;
        this.pivotRange = pivotRange;
        this.priceRound = priceRound;
        this.shareRound = shareRound;
    }

    /**
     * Method getStartPrice.
     *
     * @return BigDecimal
     */
    public BigDecimal getStartPrice() {
        return this.startPrice;
    }

    /**
     * Method setStartPrice.
     *
     * @param startPrice BigDecimal
     */
    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    /**
     * Method getEndPrice.
     *
     * @return BigDecimal
     */
    public BigDecimal getEndPrice() {
        return this.endPrice;
    }

    /**
     * Method setEndPrice.
     *
     * @param endPrice BigDecimal
     */
    public void setEndPrice(BigDecimal endPrice) {
        this.endPrice = endPrice;
    }

    /**
     * Method getLimitAmount.
     *
     * @return BigDecimal
     */
    public BigDecimal getLimitAmount() {
        return this.limitAmount;
    }

    /**
     * Method setLimitAmount.
     *
     * @param limitAmount BigDecimal
     */
    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    /**
     * Method getPercentOfPrice.
     *
     * @return BigDecimal
     */
    public BigDecimal getPercentOfPrice() {
        return this.percentOfPrice;
    }

    /**
     * Method setPercentOfPrice.
     *
     * @param percentOfPrice BigDecimal
     */
    public void setPercentOfPrice(BigDecimal percentOfPrice) {
        this.percentOfPrice = percentOfPrice;
    }

    /**
     * Method getPivotRange.
     *
     * @return BigDecimal
     */
    public BigDecimal getPivotRange() {
        return this.pivotRange;
    }

    /**
     * Method setPivotRange.
     *
     * @param pivotRange BigDecimal
     */
    public void setPivotRange(BigDecimal pivotRange) {
        this.pivotRange = pivotRange;
    }

    /**
     * Method getPercentOfMargin.
     *
     * @return BigDecimal
     */
    public BigDecimal getPercentOfMargin() {
        return this.percentOfMargin;
    }

    /**
     * Method setPercentOfMargin.
     *
     * @param percentOfMargin BigDecimal
     */
    public void setPercentOfMargin(BigDecimal percentOfMargin) {
        this.percentOfMargin = percentOfMargin;
    }

    /**
     * Method getPriceRound.
     *
     * @return BigDecimal
     */
    public BigDecimal getPriceRound() {
        return this.priceRound;
    }

    /**
     * Method setPriceRound.
     *
     * @param priceRound BigDecimal
     */
    public void setPriceRound(BigDecimal priceRound) {
        this.priceRound = priceRound;
    }

    /**
     * Method getShareRound.
     *
     * @return Integer
     */
    public Integer getShareRound() {
        return this.shareRound;
    }

    /**
     * Method setShareRound.
     *
     * @param shareRound Integer
     */
    public void setShareRound(Integer shareRound) {
        this.shareRound = shareRound;
    }
}
