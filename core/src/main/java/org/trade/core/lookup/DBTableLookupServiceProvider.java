/* ===========================================================
 * TradeManager : An application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.core.lookup;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.trade.core.dao.EntityManagerHelper;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.Reflector;
import org.trade.core.valuetype.Decode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Implementation of the ILookupServiceProvider interface that uses the
 * devtool.properties.ConfigProperties object for obtaining ILookup information.
 *
 * @author Simon Allen
 */
public class DBTableLookupServiceProvider implements ILookupServiceProvider {
    /*
     * This will be a hashtable of hashtables of ILookup objects. The first key
     * is the lookup name and the second key is the LookupQualifier.
     */
    private static final Hashtable<String, Hashtable<String, ILookup>> _lookups = new Hashtable<>();

    /**
     * Default Constructor
     */
    public DBTableLookupServiceProvider() {
    }

    public static void clearLookup() {
        _lookups.clear();
    }

    /**
     * Method getLookup.
     *
     * @param lookupName String
     * @param qualifier  LookupQualifier
     * @param optional   boolean
     * @return ILookup
     */
    public synchronized ILookup getLookup(String lookupName, LookupQualifier qualifier, boolean optional) {
        ILookup lookup = getCachedLookup(lookupName, qualifier);

        if (null == lookup) {
            try {
                Vector<Vector<Object>> rows = new Vector<>();
                Vector<String> colNames = new Vector<>();
                Enumeration<?> en = ConfigProperties.getPropAsEnumeration(lookupName + "_DBTable");

                while (en.hasMoreElements()) {
                    colNames.addElement((String) en.nextElement());
                }

                // Have all of the columns - want to get a vector for each
                // column value
                Vector<Enumeration<?>> colRows = new Vector<>();
                int i;
                int colNamesSize = colNames.size();

                for (i = 0; i < colNamesSize; i++) {
                    colRows.addElement(ConfigProperties.getPropAsEnumeration(colNames.elementAt(i)));
                }

                // Now construct a Vector Vector - representing the table of
                // data
                boolean exit = false;

                do {
                    Vector<Object> row = new Vector<>();
                    boolean foundOne = false;
                    boolean addIt = true;
                    int colRowsSize = colRows.size();

                    for (i = 0; i < colRowsSize; i++) {
                        Object value = null;

                        en = colRows.elementAt(i);

                        if (en.hasMoreElements()) {
                            foundOne = true;
                            value = en.nextElement();

                            row.addElement(value);
                        } else {
                            // Represent an empty value
                            row.addElement("");
                        }

                        // Check to see if the returned lookup is to be
                        // constrained
                        if (foundOne && (qualifier != null)) {
                            Object qualVal = qualifier.getValue(colNames.elementAt(i));

                            if (null != qualVal) {
                                if (!qualVal.equals(value)) {
                                    addIt = false;
                                }
                            }
                        }
                    }

                    if (foundOne) {
                        if (addIt) {
                            rows.addElement(row);
                        }
                    } else {
                        exit = true;
                    }
                } while (!exit);

                // There should be only one row per table that
                // contains the DAO name and method name for the display name
                String dao = null;
                String type = null;
                String methodName = null;
                int rowsSize = rows.size();
                for (i = 0; i < rowsSize; i++) {
                    Vector<Object> row = rows.elementAt(i);
                    int rowSize = row.size();

                    for (int y = 0; y < rowSize; y++) {

                        if ("DAO_DECODE_TYPE".equals(colNames.elementAt(y))) {
                            type = (String) row.elementAt(y);

                        } else if ("DAO_DECODE_CODE".equals(colNames.elementAt(y))) {
                            dao = (String) row.elementAt(y);
                        } else if ("DAO_DECODE_DISPLAY_NAME".equals(colNames.elementAt(y))) {
                            methodName = (String) row.elementAt(y);
                        }
                    }
                    // Clear the first row and add the objects and display name
                    // from the DB
                    rows.clear();
                    /*
                     * Add the None selected row.
                     */
                    if (optional) {
                        Vector<Object> newRowNone = new Vector<>();
                        Class<?> clazz = Class.forName(dao);
                        Object daoObjectNone = clazz.getDeclaredConstructor().newInstance();
                        newRowNone.add(type);
                        newRowNone.add(daoObjectNone);
                        newRowNone.add(Decode.NONE);
                        rows.add(newRowNone);
                    }

                    List<?> codes = getCodes(dao);
                    for (Object daoObject : codes) {

                        Method method = Reflector.findMethod(daoObject.getClass(), methodName, null);
                        if (null != method) {
                            Object[] o = new Object[0];
                            Object displayNameValue = method.invoke(daoObject, o);
                            if (null != displayNameValue) {
                                Vector<Object> newRow = new Vector<>();
                                newRow.add(type);
                                newRow.add(daoObject);
                                newRow.add(displayNameValue);
                                rows.add(newRow);
                            }
                        }
                    }
                }

                // If rows where found then I managed to provide the lookup
                if (!rows.isEmpty()) {
                    lookup = new PropertiesLookup(colNames, rows);
                }
            } catch (Throwable t) {
                // If this occurs means this provider is unable to provide
                // the lookup ignore the exception.
            }
            if (null != lookup) {
                assert qualifier != null;
                addLookupToCache(lookupName, qualifier, lookup);
            }
        }

        return lookup;
    }

    /**
     * Returns null if the lookup is not in the cache.
     *
     * @param lookupName String
     * @param qualifier  LookupQualifier
     * @return ILookup
     */
    private ILookup getCachedLookup(String lookupName, LookupQualifier qualifier) {
        ILookup lookup = null;
        Hashtable<?, ?> lookupsByQualifier = _lookups.get(lookupName);

        if (null != lookupsByQualifier) {
            lookup = (ILookup) lookupsByQualifier.get(qualifier.toString());
        }

        /*
         * Need to clone the object otherwise changes in position in the object
         * returned would effect everyone using the object.
         */
        if (null != lookup) {
            lookup = (ILookup) lookup.clone();
        }

        return (lookup);
    }

    /**
     * Method addLookupToCache.
     *
     * @param lookupName String
     * @param qualifier  LookupQualifier
     * @param lookup     ILookup
     */
    private synchronized void addLookupToCache(String lookupName, LookupQualifier qualifier, ILookup lookup) {
        Hashtable<String, ILookup> lookupsByQualifier = _lookups.computeIfAbsent(lookupName, k -> new Hashtable<>());

        lookupsByQualifier.put(qualifier.toString(), lookup);
    }

    /**
     * Method getCodes.
     *
     * @param className String
     * @return List<?>
     */
    private synchronized List<?> getCodes(String className) throws ClassNotFoundException {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            Class<?> c = Class.forName(className);
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();
            Root<?> from = criteriaQuery.from(c);
            CriteriaQuery<Object> select = criteriaQuery.select(from);
            TypedQuery<Object> typedQuery = entityManager.createQuery(select);
            List<Object> items = typedQuery.getResultList();
            entityManager.getTransaction().commit();
            if (!items.isEmpty()) {
                return items;
            }

        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
        return new ArrayList<>(0);
    }
}
