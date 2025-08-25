package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface CodeTypeRepository extends AspectRepository<CodeType, Long>, CodeTypeRepositoryCustom {

    CodeType findByName(String name);
}
