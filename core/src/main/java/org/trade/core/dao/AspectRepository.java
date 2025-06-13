package org.trade.core.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface AspectRepository extends JpaRepository<Aspect, Integer>, AspectRepositoryCustom {

}
