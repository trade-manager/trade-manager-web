package org.trade.core.persistent.domain;

import java.time.ZonedDateTime;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record DomainRecord(Long id,
                           ZonedDateTime createdDate,
                           ZonedDateTime updatedDate,
                           Integer version,
                           Long domainId,
                           String name,
                           String description,
                           DomainRecord domain) {

    public static DomainRecord from(Domain domain) {

        return new DomainRecord(
                domain.getId(),
                domain.getCreatedDate(),
                domain.getUpdatedDate(),
                domain.getVersion(),
                domain.getDomainId(),
                domain.getName(),
                domain.getDescription(),
                (null == domain.getParent() ? null : DomainRecord.from(domain.getParent()))
        );
    }


    public Long getId() {
        return id;
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
     * Method getUpdatedDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getUpdatedDate() {
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
     * Method getDomainId
     *
     * @return Long
     */
    public Long getDomainId() {

        return domainId;
    }

    /**
     * @return String
     */
    public String getName() {

        return this.name;
    }

    /**
     * @return String
     */
    public String getDescription() {

        return this.description;
    }

    /**
     * @return DomainRecord
     */
    public DomainRecord getParent() {

        return this.domain;
    }

}