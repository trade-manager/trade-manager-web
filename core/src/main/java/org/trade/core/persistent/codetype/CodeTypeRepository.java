package org.trade.core.persistent.codetype;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;
import org.trade.core.persistent.domain.Domain;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface CodeTypeRepository extends AspectRepository<CodeType, Long> {

    /**
     * Method findAllByOrderByName.
     *
     * @return List<Domain>
     */
    List<Domain> findAllByOrderByName();

    /**
     * Method findByName.
     *
     * @param name
     * @return
     */
    Optional<CodeType> findByName(String name);

    /**
     * Method findByNameAndType.
     *
     * @param name
     * @param type
     * @return
     */
    List<CodeType> findByNameAndType(String name, String type);


    /**
     * Method findByNameAndTypeAndCategory.
     *
     * @param name
     * @param type
     * @param category
     * @return
     */
    List<CodeType> findByNameAndTypeAndCategory(String name, String type, String category);
}
