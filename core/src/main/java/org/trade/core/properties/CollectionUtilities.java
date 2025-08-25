
package org.trade.core.properties;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Properties;

/**
 * Property utilities library. Contains methods that allow to read, write, check
 * existence, merge and extract subsets of properties.
 *
 * @author : Simon Allen
 */
public class CollectionUtilities {

    private CollectionUtilities() {
    }

    /**
     * Method read.
     *
     * @param filepath String
     * @return Properties
     */
    public static Properties read(String filepath) throws FileNotFoundException {

        Properties rval;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filepath))) {

            rval = new Properties();
            rval.load(bis);
            return rval;
        } catch (IOException ex) {

            // try relative to java.home
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(System.getProperty("java.home") + File.separator + filepath))) {

                rval = new Properties();
                rval.load(bis);
                return rval;
            } catch (IOException ex1) {
                throw new FileNotFoundException("Property file " + filepath + " not found msg: " + ex1.getMessage());
            }
        }
    }

    /**
     * Method write.
     *
     * @param filepath      String
     * @param theProperties Properties
     */
    public static void write(String filepath, Properties theProperties) throws IOException { // put

        // filepat has a comment
        write(filepath, theProperties, filepath);
    }

    /**
     * Method write.
     *
     * @param filepath      String
     * @param theProperties Properties
     * @param propComments  String
     */
    public static void write(String filepath, Properties theProperties, String propComments) throws IOException {

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filepath))) {

            theProperties.store(bos, propComments);
        }
    }

    /**
     * Method checkProperties.
     *
     * @param myKeys String[]
     * @param p      Properties
     */
    public static void checkProperties(String[] myKeys, Properties p) throws MissingPropertiesException {

        if ((myKeys == null) || (p == null)) {

            return;
        }

        MissingPropertiesException mpe = null;

        for (String myKey : myKeys) {

            if (!p.containsKey(myKey)) {

                if (mpe == null) {

                    mpe = new MissingPropertiesException();
                }

                mpe.addProperty(myKey);
            }
        }

        if (mpe != null) {
            throw mpe;
        }
    }

    /**
     * Return a Dictionary object which is a subset of the given Dictionary,
     * where the tags all <b>begin</b> with the given tag.
     * <p>
     * Hastables and Properties can be used as they are Dictionaries.
     *
     * @param superset .
     * @param tag      String
     * @param result   Dictionary<String,Object>
     */
    public static void getSubset(Dictionary<String, Object> superset, String tag, Dictionary<String, Object> result) {

        if ((result == null) || (tag == null) || (superset == null)) {

            throw new IllegalArgumentException(
                    "Invalid arguments specified : superset = " + superset + " tag = " + tag + " result = " + result);
        }

        String key;
        Iterator<String> enumKey = superset.keys().asIterator();

        while (enumKey.hasNext()) {

            key = enumKey.next();

            if (key.startsWith(tag)) {

                result.put(key, superset.get(key));
            }
        }
    }

    /**
     * Combine two properties lists. All properties from source are copied to
     * destination. <i>destination</i> is the result.
     * <p>
     * Properties that exist in both source and destination will be overwritten
     * with values from source.
     * <p>
     * Hastables and Properties can be used as they are Dictionaries.
     *
     * @param source      Dictionary<Object,Object>
     * @param destination Dictionary<Object,Object>
     */
    public static void copyOverwrite(Dictionary<Object, Object> source, Dictionary<Object, Object> destination) {

        if ((destination == null) || (source == null)) {

            throw new IllegalArgumentException(
                    "Invalid arguments specified : source = " + source + " destination = " + destination);
        }

        Object key;
        Iterator<Object> enumKey = source.keys().asIterator();

        while (enumKey.hasNext()) {

            key = enumKey.next();
            destination.put(key, source.get(key));
        }
    }

    /**
     * Returns a semicolumn separated list of keys and values in the dictionary.
     * <p>
     * Here is an example of returned String "key1 = value1; key2 = value2;"
     *
     * @param dict Dictionary<Object,Object>
     * @return : String.
     */
    public static String dictionaryToString(Dictionary<Object, Object> dict) {

        Iterator<Object> keys = dict.keys().asIterator();
        Object key, value;
        StringBuilder result = new StringBuilder();

        while (keys.hasNext()) {

            key = keys.next();
            value = dict.get(key);
            result.append(key.toString());
            result.append(" = ");
            result.append(value.toString());
            result.append("; ");
        }

        return result.toString();
    }

    /**
     * Method n2sort.
     *
     * @param index String[]
     * @param asc   boolean
     */
    public static void n2sort(String[] index, boolean asc) {

        for (int i = 0; i < index.length; i++) {

            for (int j = i + 1; j < index.length; j++) {

                if (compare(index[i], index[j], asc) == -1) {

                    swap(i, j, index);
                }
            }
        }
    }

    /**
     * Method swap.
     *
     * @param i     int
     * @param j     int
     * @param index String[]
     */
    private static void swap(int i, int j, String[] index) {

        String tmp = index[i];
        index[i] = index[j];
        index[j] = tmp;
    }

    /**
     * Method compare.
     *
     * @param row1 String
     * @param row2 String
     * @param asc  boolean
     * @return int
     */
    private static int compare(String row1, String row2, boolean asc) {

        int result = row1.compareTo(row2);
        int returnVal;

        if (result < 0) {

            returnVal = -1;
        } else if (result != 0) // result > 0
        {
            returnVal = 1;
        } else {

            returnVal = 0;
        }

        return asc ? -returnVal : returnVal;
    }
}
