package org.trade.core.dao;


import java.util.List;

public interface AspectService {

    Aspects findByClassName(String className) throws ClassNotFoundException;

    Aspects findByClassNameAndFieldName(String className, String fieldName, String value) throws ClassNotFoundException;

    List<?> findCodesByClassName(String className) throws ClassNotFoundException;
}
