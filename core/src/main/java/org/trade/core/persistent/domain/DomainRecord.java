package org.trade.core.persistent.domain;

import org.trade.core.util.JSONMapper;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record DomainRecord(DomainDTO domain) {

    public static DomainRecord from(Domain doamin) {

        return new DomainRecord(
                JSONMapper.convertDTOToEntity(doamin, DomainDTO.class)
        );
    }
}