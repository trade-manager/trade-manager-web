package org.trade.core.persistent.codetype;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface CodeValueRepository extends AspectRepository<CodeValue, Long> {

    @Query("SELECT codeValues FROM CodeValue codeValues " +
            "JOIN codeValues.codeAttribute attribute " +
            "JOIN attribute.codeType codeType " +
            "WHERE codeType.type = :type " +
            "ORDER BY codeType.id ASC, codeValues.codeObjectId ASC")
    List<CodeValue> findByTypeSortedByCodeTypeAndCodeValue(@Param("type") String type);
}

