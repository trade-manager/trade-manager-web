package org.trade.core.dao;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface AspectService {

    Aspects findByClassName(String className) throws ClassNotFoundException;

    Aspects findByClassNameAndFieldName(String className, String fieldName, String value) throws ClassNotFoundException;

    List<?> findCodesByClassName(String className) throws ClassNotFoundException;
}
