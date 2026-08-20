package org.trade.core.persistent.codetype;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface DecodeTypeRepository extends AspectRepository<DecodeType, Long> {

    /**
     * Method findByType.
     *
     * @param type
     * @return
     */
    List<DecodeType> findByType(String type);
}

