package org.trade.core.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloneUtils {
    public CloneUtils() {
    }

    public static Object clone(Object object) throws CloneNotSupportedException {
        if (object == null) {
            throw new IllegalArgumentException("Null 'object' argument.");
        } /*else if (object instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable)object;
            return pc.clone();
        }*/ else {
            try {
                Method method = object.getClass().getMethod("clone", (Class[]) null);
                if (Modifier.isPublic(method.getModifiers())) {
                    return method.invoke(object, (Object[]) null);
                }
            } catch (NoSuchMethodException var2) {
                throw new CloneNotSupportedException("Object without clone() method is impossible.");
            } catch (IllegalAccessException var3) {
                throw new CloneNotSupportedException("Object.clone(): unable to call method.");
            } catch (InvocationTargetException var4) {
                throw new CloneNotSupportedException("Object without clone() method is impossible.");
            }

            throw new CloneNotSupportedException("Failed to clone.");
        }
    }

    public static List<?> cloneList(List<?> source) {
        //Args.nullNotPermitted(source, "source");
        List result = new ArrayList();

        for (Object obj : source) {
            if (obj == null) {
                result.add((Object) null);
            } else if (obj.getClass() == String.class) {
                result.add(obj);
            } else {
                try {
                    result.add(CloneUtils.clone(obj));
                } catch (CloneNotSupportedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        return result;
    }

    public static Map cloneMapValues(Map source) {
        //Args.nullNotPermitted(source, "source");
        Map result = new HashMap();

        for (Object key : source.keySet()) {
            Object value = source.get(key);
            if (value != null) {
                try {
                    result.put(key, CloneUtils.clone(value));
                } catch (CloneNotSupportedException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                result.put(key, (Object) null);
            }
        }

        return result;
    }


    public static Collection deepClone(Collection collection) throws CloneNotSupportedException {
        if (collection == null) {
            throw new IllegalArgumentException("Null 'collection' argument.");
        } else {
            Collection result = (Collection) clone(collection);
            result.clear();

            for (Object item : collection) {
                if (item != null) {
                    result.add(clone(item));
                } else {
                    result.add((Object) null);
                }
            }

            return result;
        }
    }

}

