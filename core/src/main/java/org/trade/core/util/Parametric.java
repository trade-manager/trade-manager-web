package org.trade.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * This class performs some useful reflection type functionality when the target
 * method is overloaded by other methods or when method signatures are
 * reflective of a class hierarchy.
 *
 * @author Wayne Milsted
 * @version $Id: Parametric.java,v 1.1 2001/10/18 01:32:15 simon Exp $
 */
public class Parametric {

    private final Class<?> clazz;

    /**
     * Constructor for Parametric.
     *
     * @param aClassToReflect Class<?>
     */
    public Parametric(Class<?> aClassToReflect) {

        super();
        clazz = aClassToReflect;
    }

    /**
     * Method getClassToReflect.
     *
     * @return Class<?>
     */
    public Class<?> getClassToReflect() {
        return clazz;
    }

    /**
     * Method findMethod.
     *
     * @param methodName String
     * @param parameters Class<?>[]
     * @return Method
     */
    public Method findMethod(String methodName, Class<?>[] parameters) {

        Method theReturn = null;
        Class<?> currentClass = clazz;

        while (true) {

            try {

                Method[] methods = currentClass.getDeclaredMethods();

                for (Method method : methods) {

                    int modifiers = method.getModifiers();

                    if (method.getName().equals(methodName) && Modifier.isPublic(modifiers)) {

                        if (isTargetSignature(method, parameters)) {

                            theReturn = method;
                            break;
                        }
                    }
                }
            } catch (Throwable t) {

                break;
            }

            currentClass = currentClass.getSuperclass();

            if (null == currentClass) {

                // we've reached beyond Object
                break;
            }
        }

        return theReturn;
    }

    /**
     * Method findField.
     *
     * @param fieldName String
     * @return Field
     */
    public Field findField(String fieldName) {

        Field theReturn = null;
        Class<?> currentClass = clazz;

        while (true) {

            try {

                Field[] fields = currentClass.getDeclaredFields();

                for (Field field : fields) {

                    int modifiers = field.getModifiers();

                    if (field.getName().equals(fieldName) && Modifier.isPublic(modifiers)) {

                        theReturn = field;
                        break;
                    }
                }
            } catch (Throwable t) {
                break;
            }

            currentClass = currentClass.getSuperclass();

            if (null == currentClass) {

                // we've reached beyond Object
                break;
            }
        } // end while loop

        return theReturn;
    }

    /**
     * Method isTargetSignature.
     *
     * @param aMethod    Method
     * @param parameters Class<?>[]
     * @return boolean
     */
    private boolean isTargetSignature(Method aMethod, Class<?>[] parameters) {
        boolean theReturn = false;
        Class<?>[] thisMethodsParameters = aMethod.getParameterTypes();

        // no need to check further if the number of parameters
        // are unequal
        if (thisMethodsParameters.length == parameters.length) {

            for (int i = 0; i < parameters.length; i++) {

                Class<?> thisParm = thisMethodsParameters[i];
                Class<?> target = parameters[i];
                theReturn = thisParm.equals(target) || thisParm.isAssignableFrom(target);

                if (!theReturn) {

                    break;
                }
            }
        }

        return theReturn;
    }
}
