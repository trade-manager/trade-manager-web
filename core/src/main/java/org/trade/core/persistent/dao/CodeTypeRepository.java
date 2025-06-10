package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CodeTypeRepository extends JpaRepository<CodeType, Integer>, CodeTypeRepositoryCustom {

    CodeType findByName(String name);
}
