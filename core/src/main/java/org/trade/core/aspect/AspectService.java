package org.trade.core.aspect;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface AspectService {

    /**
     * Method findById.
     *
     * @param aspect Aspect
     * @return Aspect
     * @throws ClassNotFoundException class not found
     */
    Aspect findById(final Aspect aspect) throws ClassNotFoundException;

    /**
     * Method findById.
     *
     * @param instance Aspect
     * @param <T>      an aspect
     * @return <T extends Aspect>
     */
    <T extends Aspect> T save(Aspect instance);

    /**
     * Method saveAll.
     *
     * @param entities Iterable<S> entities
     * @param <S>      an aspect
     * @return <S extends Aspect> List<S>
     */
    <S extends Aspect> List<S> saveAll(final Iterable<S> entities);

    /**
     * Method delete.
     *
     * @param instance Aspect
     */
    void delete(Aspect instance);

    /**
     * Method deleteAll.
     *
     * @param entities Iterable<? extends Aspect>
     */
    void deleteAll(Iterable<? extends Aspect> entities);

    /**
     * Method findByClassName.
     *
     * @param className String
     * @return Aspects
     * @throws ClassNotFoundException class not found
     */
    Aspects findByClassName(String className) throws ClassNotFoundException;

    /**
     * Method findByClassNameAndFieldName.
     *
     * @param className String
     * @param fieldName String
     * @param value     String
     * @return Aspects
     * @throws ClassNotFoundException class not found
     */
    Aspects findByClassNameAndFieldName(String className, String fieldName, String value) throws ClassNotFoundException;

    /**
     * Method findCodesByClassName.
     *
     * @param className String
     * @return List<?>
     * @throws ClassNotFoundException class not found
     */
    List<?> findCodesByClassName(String className) throws ClassNotFoundException;
}
