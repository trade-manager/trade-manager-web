package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;


public interface CodeTypeRepository extends AspectRepository<CodeType, Integer>, CodeTypeRepositoryCustom {

    CodeType findByName(String name);
}
