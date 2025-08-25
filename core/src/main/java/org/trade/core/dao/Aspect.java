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
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@MappedSuperclass
public abstract class Aspect extends AbstractPersistable<Long> {

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
     * Method getId. Used for the ObjectMapper to Dto as the AbstractPersistable
     * is protected.
     *
     * @return Long
     */
    public Long getId() {
        return super.getId();
    }

    /**
     * Method setId.
     *
     * @param id Long
     */
    public void setId(Long id) {
        super.setId(id);
    }

    /**
     * Method getCreatedDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    /**
     * Method setCreatedDate.
     *
     * @param createdDate ZonedDateTime
     */
    public void setCreatedDate(ZonedDateTime createdDate) {

        this.createdDate = createdDate;
    }

    /**
     * Method getUpdatedDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    /**
     * Method setUpdatedDate.
     *
     * @param updatedDate ZonedDateTime
     */
    public void setUpdatedDate(ZonedDateTime updatedDate) {

        this.updatedDate = updatedDate;
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
