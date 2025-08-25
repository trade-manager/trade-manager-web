package org.trade.core.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.properties.PropertyNotFoundException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * Provides a simple class factory. Provide a string name for an interface, and
 * this class will provide the implementation.
 *
 * @author Simon Allen
 */
public class ClassFactory {

    private ClassFactory() {
    }

    private final static Logger _log = LoggerFactory.getLogger(ClassFactory.class);

    /**
     * Method getServiceForInterface.
     *
     * @param theInterface String
     * @param context      Object
     * @return Object
     */
    public static Object getServiceForInterface(String theInterface, List<Object> param, Object context)
            throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {

        String className = ConfigProperties.getPropAsString(theInterface);

        if (className == null) {

            throw new PropertyNotFoundException("The interface \"" + theInterface + "\" could not be located.  Check "
                    + "to see if the property file \"" + ConfigProperties.getDeploymentPropertyFileName()
                    + "\" is installed and available.");
        }

        Class<?>[] args = new Class[1];
        args[0] = Object.class;

        if (null == param) {

            param = new ArrayList<>();
        }

        return getCreateClass(className, param, context);
    }

    /**
     * Method getCreateClass.
     *
     * @param className String
     * @param param     List<Object>
     * @param context   Object
     * @return Object
     */
    public static Object getCreateClass(String className, List<Object> param, Object context)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        int listSize = param.size();
        Object instance = null;

        Class<?>[] params = new Class[listSize];
        Object[] object = new Object[listSize];
        StringBuilder classes = new StringBuilder();
        int i = 0;

        for (Object obj : param) {

            if (classes.isEmpty()) {

                classes.append(obj.getClass().getName());
            } else {

                classes.append(",").append(obj.getClass().getName());
            }
            params[i] = obj.getClass();
            object[i] = obj;
            i++;
        }

        Constructor<?> constructor;
        Class<?> c = Class.forName(className);

        try {

            constructor = c.getDeclaredConstructor(params);
            instance = constructor.newInstance(object);
        } catch (Exception e) {

            _log.debug("Could not find constructor for default params[{}] will test all constructors.", classes);
            Constructor<?>[] constructors = c.getConstructors();
            for (Constructor<?> constructor2 : constructors) {

                try {

                    instance = constructor2.newInstance(object);
                    _log.info("Found constructor: {} for params[{}]", constructor2.toGenericString(), classes);
                    break;
                } catch (Exception ex) {

                    _log.error("Constructor: {}, classes: {}, param: {} failed!!", constructor2.toGenericString(), classes, param);
                }
            }
        }
        if (null == instance) {
            instance = c.getDeclaredConstructor().newInstance();
        }

        return instance;
    }
}
