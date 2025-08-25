package org.trade.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class Reflector extends Object {

    /**
     * Find the specified method by walking the classes class hierarchy.
     *
     * @param cl     Class<?>
     * @param name   String
     * @param params Class<?>[]
     * @return The specified method if found, null otherwise.
     */
    public static Method findMethod(Class<?> cl, String name, Class<?>[] params) {
        Method m = null;
        boolean notFound = true;
        Class<?> currentCl = cl;

        while (notFound) {
            try {
                m = currentCl.getDeclaredMethod(name, params);
                notFound = false;
            } catch (Exception e) {
                currentCl = currentCl.getSuperclass();

                if (currentCl == null) {
                    // Break the loop - the method does not exist in the classes
                    // class hierarchy
                    //notFound = false;

                    break;
                }
            }
        }

        // if the method was not found, perform a more expensive polymorphic
        // test
        if (null == m) {
            Parametric p = new Parametric(cl);

            m = p.findMethod(name, params);
        }

        return (m);
    }

    /**
     * Find the specified method by walking the classes class hierarchy.
     *
     * @param cl   Class<?>
     * @param name String
     * @return The specified method if found, null otherwise.
     */
    public static Field findField(Class<?> cl, String name) {
        Field m = null;
        boolean notFound = true;
        Class<?> currentCl = cl;

        while (notFound) {
            try {
                m = currentCl.getDeclaredField(name);
                notFound = false;
            } catch (Exception e) {
                currentCl = currentCl.getSuperclass();

                if (currentCl == null) {
                    // Break the loop - the Field does not exist in the classes
                    // class hierarchy
                    // notFound = false;

                    break;
                }
            }
        }

        // if the Field was not found, perform a more expensive polymorphic test
        if (null == m) {
            Parametric p = new Parametric(cl);

            m = p.findField(name);
        }

        return (m);
    }
}
