package org.trade.core.lookup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.persistent.TradeService;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.Reflector;
import org.trade.core.valuetype.Decode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

/**
 * Implementation of the ILookupServiceProvider interface that uses the
 * devtool.properties.ConfigProperties object for obtaining ILookup information.
 *
 * @author Simon Allen
 */
public class DBTableLookupServiceProvider implements ILookupServiceProvider {

    private final static Logger _log = LoggerFactory.getLogger(DBTableLookupServiceProvider.class);

    private static TradeService _tradeService;
    /*
     * This will be a hashtable of hashtable's of ILookup objects. The first key
     * is the lookup name and the second key is the LookupQualifier.
     */
    private static final Hashtable<String, Hashtable<String, ILookup>> _lookups = new Hashtable<>();

    /**
     * Default Constructor
     */
    public DBTableLookupServiceProvider(TradeService tradeService) {

        if (null == _tradeService) {

            _tradeService = tradeService;
        }
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
    public ILookup getLookup(String lookupName, LookupQualifier qualifier, boolean optional) {

        ILookup lookup = getCachedLookup(lookupName, qualifier);

        if (null == lookup) {

            try {

                List<List<Object>> rows = new ArrayList<>();
                List<String> colNames = new ArrayList<>();
                ListIterator<?> en = ConfigProperties.getPropAsEnumeration(lookupName + "_DBTable");

                while (en.hasNext()) {

                    colNames.add((String) en.next());
                }

                // Have all of the columns - want to get a List for each
                // column value
                List<ListIterator<?>> colRows = new ArrayList<>();
                int i;
                int colNamesSize = colNames.size();

                for (i = 0; i < colNamesSize; i++) {

                    colRows.add(ConfigProperties.getPropAsEnumeration(colNames.get(i)));
                }

                // Now construct a List List - representing the table of
                // data
                boolean exit = false;

                do {

                    List<Object> row = new ArrayList<>();
                    boolean foundOne = false;
                    boolean addIt = true;
                    int colRowsSize = colRows.size();

                    for (i = 0; i < colRowsSize; i++) {

                        Object value = null;
                        en = colRows.get(i);

                        if (en.hasNext()) {

                            foundOne = true;
                            value = en.next();
                            row.add(value);
                        } else {

                            // Represent an empty value
                            row.add("");
                        }

                        // Check to see if the returned lookup is to be
                        // constrained
                        if (foundOne && (qualifier != null)) {

                            Object qualVal = qualifier.getValue(colNames.get(i));

                            if (null != qualVal) {

                                if (!qualVal.equals(value)) {

                                    addIt = false;
                                }
                            }
                        }
                    }

                    if (foundOne) {

                        if (addIt) {

                            rows.add(row);
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

                    List<Object> row = rows.get(i);
                    int rowSize = row.size();

                    for (int y = 0; y < rowSize; y++) {

                        if ("DAO_DECODE_TYPE".equals(colNames.get(y))) {

                            type = (String) row.get(y);

                        } else if ("DAO_DECODE_CODE".equals(colNames.get(y))) {

                            dao = (String) row.get(y);
                        } else if ("DAO_DECODE_DISPLAY_NAME".equals(colNames.get(y))) {

                            methodName = (String) row.get(y);
                        }
                    }
                    // Clear the first row and add the objects and display name
                    // from the DB
                    rows.clear();
                    /*
                     * Add the None selected row.
                     */
                    if (optional) {

                        List<Object> newRowNone = new ArrayList<>();
                        Class<?> clazz = Class.forName(dao);
                        Object daoObjectNone = clazz.getDeclaredConstructor().newInstance();
                        newRowNone.add(type);
                        newRowNone.add(daoObjectNone);
                        newRowNone.add(Decode.NONE);
                        rows.add(newRowNone);
                    }

                    List<?> codes = _tradeService.getAspectService().findCodesByClassName(dao);

                    for (Object daoObject : codes) {

                        Method method = Reflector.findMethod(daoObject.getClass(), methodName, null);

                        if (null != method) {

                            Object[] o = new Object[0];
                            Object displayNameValue = method.invoke(daoObject, o);

                            if (null != displayNameValue) {

                                List<Object> newRow = new ArrayList<>();
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
            } catch (Exception ex) {
                // If this occurs means this provider is unable to provide
                // the lookup ignore the exception.
                _log.info("Info: Failed to create lookup msg: {}", ex.getMessage());
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
         * returned would affect everyone using the object.
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
    private void addLookupToCache(String lookupName, LookupQualifier qualifier, ILookup lookup) {

        Hashtable<String, ILookup> lookupsByQualifier = _lookups.computeIfAbsent(lookupName, _ -> new Hashtable<>());
        lookupsByQualifier.put(qualifier.toString(), lookup);
    }

}
