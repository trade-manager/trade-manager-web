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

// Generated Feb 21, 2011 12:43:33 PM by Hibernate Tools 3.4.0.CR1

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import org.trade.core.dao.Aspect;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.time.TradingCalendar;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.Comparator;

import static jakarta.persistence.GenerationType.IDENTITY;


/**
 * Rule generated by hbm2java
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "rule")
public class Rule extends Aspect implements java.io.Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2273276207080568947L;
    private Strategy strategy;
    private String comment;
    @NotNull
    private ZonedDateTime createDate;
    private ZonedDateTime lastUpdateDate;
    private byte[] rule;
    private boolean dirty = false;

    public Rule() {
        this.createDate = TradingCalendar.getDateTimeNowMarketTimeZone();
        this.lastUpdateDate = this.createDate;
    }

    /**
     * Constructor for Rule.
     *
     * @param strategy       Strategy
     * @param version        Integer
     * @param comment        String
     * @param createDate     Date
     * @param lastUpdateDate Date
     */
    public Rule(Strategy strategy, Integer version, String comment, ZonedDateTime createDate,
                ZonedDateTime lastUpdateDate) {
        this.strategy = strategy;
        this.version = version;
        this.comment = comment;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    /**
     * Constructor for Rule.
     *
     * @param strategy       Strategy
     * @param version        Integer
     * @param comment        String
     * @param createDate     Date
     * @param rule           byte[]
     * @param lastUpdateDate Date
     */
    public Rule(Strategy strategy, Integer version, String comment, ZonedDateTime createDate, byte[] rule,
                ZonedDateTime lastUpdateDate) {
        this.strategy = strategy;
        this.version = version;
        this.comment = comment;
        this.createDate = createDate;
        this.rule = rule;
        this.lastUpdateDate = lastUpdateDate;
    }

    /**
     * Method getId.
     *
     * @return Integer
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /**
     * Method getStrategy.
     *
     * @return Strategy
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_strategy", nullable = false)
    public Strategy getStrategy() {
        return this.strategy;
    }

    /**
     * Method setStrategy.
     *
     * @param strategy Strategy
     */
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Method getComment.
     *
     * @return String
     */
    @Column(name = "comment", nullable = false, length = 200)
    public String getComment() {
        return this.comment;
    }

    /**
     * Method setComment.
     *
     * @param comment String
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Method getCreateDate.
     *
     * @return ZonedDateTime
     */
    @Column(name = "create_date", nullable = false)
    public ZonedDateTime getCreateDate() {
        return this.createDate;
    }

    /**
     * Method setCreateDate.
     *
     * @param createDate ZonedDateTime
     */
    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * Method getLastUpdateDate.
     *
     * @return ZonedDateTime
     */

    @Column(name = "last_update_date", nullable = false)
    public ZonedDateTime getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    /**
     * Method setLastUpdateDate.
     *
     * @param lastUpdateDate ZonedDateTime
     */
    public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    /**
     * Method getRule.
     *
     * @return byte[]
     */
    @Lob
    @Column(name = "rule")
    public byte[] getRule() {
        return this.rule;
    }

    /**
     * Method setRule.
     *
     * @param rule byte[]
     */
    public void setRule(byte[] rule) {
        this.rule = rule;
    }

    /**
     * Method getVersion.
     *
     * @return Integer
     */
    @Column(name = "version", nullable = false)
    public Integer getVersion() {
        return this.version;
    }

    /**
     * Method isDirty.
     *
     * @return boolean
     */
    @Transient
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Method setDirty.
     *
     * @param dirty boolean
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        return "Version-" + this.getVersion();
    }

    public static final Comparator<Rule> VERSION_ORDER = (o1, o2) -> CoreUtils.nullSafeComparator(o1.getVersion(), o2.getVersion());

    /**
     * Method equals.
     *
     * @param objectToCompare Object
     * @return boolean
     */
    public boolean equals(Object objectToCompare) {

        if (super.equals(objectToCompare))
            return true;

        if (objectToCompare instanceof Rule) {
            if (null == this.getId() || null == this.getVersion()) {
                return false;
            }

            return this.getStrategy().getId().equals(((Rule) objectToCompare).getStrategy().getId())
                    && this.getVersion().equals(((Rule) objectToCompare).getVersion());

        }
        return false;
    }
}
