package org.trade.core.persistent.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;


@Repository
public class StrategyRepositoryImpl implements StrategyRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;


}