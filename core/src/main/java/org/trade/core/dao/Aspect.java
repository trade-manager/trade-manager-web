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
package org.trade.core.dao;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.trade.core.util.time.TradingCalendar;

import java.time.ZonedDateTime;

/**
 *
 */
@MappedSuperclass
public abstract class Aspect extends AbstractPersistable<Integer> {

    @Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    protected Integer version;

    @Column(name = "created_date", nullable = false, updatable = false)
    private ZonedDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    private ZonedDateTime updatedDate;

    @Transient
    private boolean dirty = false;

    @Transient
    private static boolean ascending = false;

    /**
     * Constructor for Aspect.
     */
    public Aspect() {
        super();
    }


    @PrePersist
    protected void onCreate() {
        createdDate = TradingCalendar.getDateTimeNowMarketTimeZone();
        updatedDate = TradingCalendar.getDateTimeNowMarketTimeZone();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = TradingCalendar.getDateTimeNowMarketTimeZone();
    }

    /**
     * Method getCreateDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getCreateDate() {
        return this.createdDate;
    }

    /**
     * Method getUpdateDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getUpdateDate() {
        return this.updatedDate;
    }

    /**
     * Method getVersion.
     *
     * @return Integer
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Method setVersion.
     *
     * @param version Integer
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Method getAscending.
     *
     * @return Boolean
     */
    public static Boolean getAscending() {
        return ascending;
    }

    /**
     * Method setAscending.
     *
     * @param ascending Boolean
     */
    public static void setAscending(Boolean ascending) {
        Aspect.ascending = ascending;
    }

    /**
     * Method isDirty.
     *
     * @return boolean
     */
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
}
