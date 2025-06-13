package org.trade.core.dao;

import java.util.List;

public interface AspectRepositoryCustom {

    Aspects findByClassName(String className) throws ClassNotFoundException;

    Aspects findByClassNameFieldName(String className, String fieldname, String value) throws ClassNotFoundException;

    List<?> getCodes(String className) throws ClassNotFoundException;
}
