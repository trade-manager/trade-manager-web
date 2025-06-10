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
import jakarta.persistence.Table;
import org.trade.core.dao.Aspect;

import java.io.Serial;
import java.math.BigDecimal;

/**
 *
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
    }

    /**
     * Method getId.
     *
     * @return Integer

     @Id
     @GeneratedValue(strategy = IDENTITY)
     @Column(name = "id", unique = true, nullable = false)
     public Integer getId() {
     return this.id;
     }
     */
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
