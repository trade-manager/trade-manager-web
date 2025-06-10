package org.trade.core.dao;

public interface AspectRepositoryCustom {

    Aspects findByClassName(String className) throws ClassNotFoundException;

    Aspects findByClassNameFieldName(String className, String fieldname, String value) throws ClassNotFoundException;
}
