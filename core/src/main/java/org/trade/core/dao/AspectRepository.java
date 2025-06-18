package org.trade.core.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface AspectRepository<T extends Aspect, ID extends Serializable> extends JpaRepository<T, ID> {
}

