package org.trade.core.properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.persistent.codetype.CodeAttribute;
import org.trade.core.persistent.codetype.CodeType;
import org.trade.core.persistent.codetype.CodeValue;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Represents the application's configuration. This class is intended to be a bit
 * of a hack. I.e it wraps the apps property file, and provides a place to map
 * the keys in the file to constants.
 *
 * @author Simon Allen
 */
public class ConfigProperties {

    private static final Logger _log = LoggerFactory.getLogger(ConfigProperties.class);

    public static final String MANDATORY_PROPERTY = "mandatory_property";
    private static String _filename = null;

    // This is loaded as a system resource from the current core package
    private static final String CONFIG_PROPERTY_FILE = "config.properties";
    private static final String DECODE_PROPERTY_FILE = "decode.json";
    private static final String ENVIRONMENT_VARIABLE_PROPERTY_FILE = "trade.config";
    private static Properties deploymentProperties;

    /**
     * Method getDeploymentPropertyFileName.
     *
     * @return String
     */
    public static String getDeploymentPropertyFileName() {

        try {

            if (null == _filename) {

                _filename = System.getProperty(ENVIRONMENT_VARIABLE_PROPERTY_FILE, CONFIG_PROPERTY_FILE);
                _filename = _filename.replaceFirst("file:", "");
                File file = new File(_filename);
                _filename = file.toString();
            }
        } catch (Exception e) {
            // do nothing as we are an applet !!!
        }
        return _filename;
    }

    /**
     * Method getDeploymentProperties.
     *
     * @param context  Object
     * @param fileName String
     * @return Properties
     */
    public static Properties getDeploymentProperties(Object context, String fileName) throws MissingPropertiesException {

        return getProperties(context, fileName);
    }

    /**
     * Method getSystemPropertyFileName.
     *
     * @return String
     */
    public static String getSystemPropertyFileName() {

        return CONFIG_PROPERTY_FILE;
    }

    /**
     * Returns a string for a key.
     *
     * @param key String
     * @return String
     */
    public static String getPropAsString(String key) throws IOException {

        return retrieveProperty(key);
    }

    /**
     * Returns an int for a key. If the number is malformed, then it return 0.
     *
     * @param key String
     * @return int
     */
    public static int getPropAsInt(String key) throws IOException {

        return Integer.parseInt(retrieveProperty(key));
    }

    /**
     * Returns an boolean for a key. If the number is malformed, then it return
     * false.
     *
     * @param key String
     * @return boolean
     */
    public static boolean getPropAsBoolean(String key) throws IOException {

        return Boolean.parseBoolean(retrieveProperty(key));
    }

    /**
     * Method getPropAsEnumeration.
     *
     * @param keyRoot String
     * @return ListIterator<String>
     */
    public static ListIterator<String> getDecodesAsEnumeration(String keyRoot) throws IOException {

        List<String> resVec;
        int iNumEntries = getPropAsInt(keyRoot + "_NumOfItems");
        StringBuilder key = new StringBuilder(keyRoot);
        int keyLen = keyRoot.length();

        resVec = new ArrayList<>(iNumEntries);

        for (int iCount = 1; iCount < (iNumEntries + 1); iCount++) {

            String val = getPropAsString(key.append("_").append(iCount).toString());
            key.setLength(keyLen); // reset key

            if (null != val) {

                resVec.add(val);
            }
        }

        return resVec.listIterator();
    }

    /**
     * Method getPropertiesAsArrayOfProperties.
     *
     * @param keyRoot  String
     * @param keyNames Dictionary<?,?>
     * @return Properties[]
     */
    public static Properties[] getDecodesAsArrayOfProperties(String keyRoot, Dictionary<?, ?> keyNames)
            throws IOException {

        int iNumItems = getPropAsInt(keyRoot + "_NumOfItems");
        Properties[] propArray = new Properties[iNumItems];

        for (int iCount = 1; iCount <= iNumItems; iCount++) {

            propArray[iCount - 1] = getSetOfProperties(keyRoot + "_" + iCount, keyNames);
        }

        return propArray;
    }

    /**
     * Method getProperties. read configuration properties
     *
     * @param context  Object
     * @param fileName String
     * @return Properties
     */
    private static Properties getProperties(Object context, String fileName) throws MissingPropertiesException {

        try {

            if (null == deploymentProperties) {

                Properties systemProperties = new Properties();

                // Get the resource config.properties
                loadPropertiesAsResource(context, getSystemPropertyFileName(), systemProperties);

                // Get the resource core.properties
                loadPropertiesAsResource(context, fileName, systemProperties);
                deploymentProperties = new Properties();

                // Get the root config.properties
                loadPropertiesAsFile(getDeploymentPropertyFileName(), deploymentProperties);
                deploymentProperties.putAll(systemProperties);

                //generateDecodeSQL(context, DECODE_PROPERTY_FILE);
            }
        } catch (IOException ex) {

            throw new MissingPropertiesException(ex.getMessage(), ex);
        }

        return deploymentProperties;
    }

    /**
     * Method getSetOfProperties.
     *
     * @param keyRoot  String
     * @param keyNames Dictionary<?,?>
     * @return Properties
     */
    private static Properties getSetOfProperties(String keyRoot, Dictionary<?, ?> keyNames) throws IOException {

        Iterator<?> enumKey = keyNames.keys().asIterator();
        Properties result = new Properties();

        while (enumKey.hasNext()) {

            String key = (String) enumKey.next();
            String value;
            boolean mandatory = MANDATORY_PROPERTY.equals(keyNames.get(key));

            if (mandatory) {

                value = getPropAsString(keyRoot + "_" + key);
            } else {

                try {

                    value = getPropAsString(keyRoot + "_" + key);

                } catch (Exception e) {

                    value = null;
                }
            }

            if (value != null) {

                result.put(key, value);
            }
        }

        return (result);
    }

    /**
     * Method getCommaSeparatedStrings.
     *
     * @param key String
     * @return Enumeration<?>
     */
    public static Enumeration<?> getCommaSeparatedStrings(String key) throws IOException {

        String list = getPropAsString(key);
        return new StringTokenizer(list, ",");
    }

    /**
     * Method retrieveProperty. read configuration properties
     *
     * @param key String
     * @return String
     */
    private static String retrieveProperty(String key) throws IOException {

        if (null == deploymentProperties) {

            Properties systemProperties = new Properties();
            loadPropertiesAsResource(ConfigProperties.class, getSystemPropertyFileName(), systemProperties);
            deploymentProperties = new Properties(systemProperties);
            loadPropertiesAsFile(getDeploymentPropertyFileName(), deploymentProperties);
        }

        String ret = deploymentProperties.getProperty(key);

        if (null == ret) {

            throw new PropertyNotFoundException("The property \"" + key + "\" was not found in the property file \""
                    + getDeploymentPropertyFileName() + "\". \n Check the file is in the applications root dir.");
        }

        return ret;
    }

    /**
     * Method getPropertyAfterEnvSubstitution.
     *
     * @param key String
     * @return String
     */
    public static String getPropertyAfterEnvSubstitution(String key) throws IOException {

        // put env variables in the dictionary
        TemplateParser tp = new TemplateParser(retrieveProperty(key), System.getProperties());
        return tp.parseTemplate();
    }

    /**
     * Method readFileAsString.
     *
     * @param filePath String
     * @param loader   ClassLoader
     * @return String
     */
    public static String readFileAsString(String filePath, ClassLoader loader) throws IOException {

        StringBuilder fileData = new StringBuilder(1000);
        InputStream inputStream = loader.getResourceAsStream(filePath);
        InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream));

        BufferedReader reader = new BufferedReader(inputStreamReader);
        char[] buf = new char[1024];
        int numRead;

        while ((numRead = reader.read(buf)) != -1) {

            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }

        reader.close();
        return fileData.toString();
    }

    /**
     * Method loadPropertiesAsResource.
     *
     * @param context    Object
     * @param filename   String
     * @param properties Properties
     */
    private static void loadPropertiesAsResource(Object context, String filename, Properties properties) throws IOException {

        InputStream unbuffered;

        if (null == filename) {

            throw new PropertyFileNotFoundException("No property file name found"
                    + " please check your command line parameters e.g. " + "-Dconfig.properties=/filename.properties ");
        } else {

            unbuffered = context.getClass().getResourceAsStream(filename);
        }

        if (unbuffered == null) {

            throw new PropertyFileNotFoundException("Check " + "to see if the property file \"" + filename
                    + "\" is installed and available in the class path.");
        } else {

            InputStream in = new BufferedInputStream(unbuffered);
            properties.load(in);
            in.close();
            unbuffered.close();
        }
    }

    /**
     * Method loadPropertiesAsFile.
     *
     * @param filename   String
     * @param properties Properties
     */
    private static void loadPropertiesAsFile(String filename, Properties properties) throws IOException {

        if (null != filename) {

            File file = new File(filename);
            String filePath = file.getAbsolutePath();

            if (file.exists()) {

                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
                fis.close();
            } else {

                _log.error("The property file {} does not exist -- using defaults", filePath);
            }
        } else {

            _log.error("The property file does not exist -- using defaults");
        }
    }

    /**
     * Method loadPropertiesAsFile.
     *
     * @param filename String
     * @return jsonObject JSONObject
     */
    private static JSONObject loadJSONAsResource(Object context, String filename) throws IOException {

        InputStream unbuffered = context.getClass().getResourceAsStream(filename);

        if (null == unbuffered) {

            throw new PropertyFileNotFoundException(String.format("No property file %s found", filename));
        } else {

            InputStream in = new BufferedInputStream(unbuffered);
            JSONTokener tokener = new JSONTokener(in);
            JSONObject jsonObject = new JSONObject(tokener);
            in.close();
            unbuffered.close();
            return jsonObject;
        }
    }

    /**
     * Method generateDecodeSQL.
     *
     * @param filename String
     */
    public static void generateDecodeSQL(Object context, String filename) {

        try {

            AtomicInteger codeTypeId = new AtomicInteger(11);
            AtomicInteger codeAttributeId = new AtomicInteger(33);
            AtomicInteger codeValueId = new AtomicInteger(49);
            AtomicInteger decodeId = new AtomicInteger(0);
            JSONObject decodes = loadJSONAsResource(context, filename);
            decodes.keySet().forEach(category -> {

                JSONObject categories = decodes.getJSONObject(category);

                categories.keySet().forEach(type -> {

                    JSONArray values = categories.getJSONArray(type);
                    CodeType codeType = new CodeType(CodeType.Decode, category, type, String.format("%s::%s", type, category));
                    codeTypeId.getAndIncrement();

                    System.out.println(String.format("INSERT INTO codetype (id, name, type, category, description) VALUES(%s,'%s','%s','%s','%s')//", codeTypeId.get(), type, "CodeType", category, String.format("%s::%s", type, category)));

                    HashMap<String, CodeAttribute> codeAttributesMap = new HashMap<>();

                    if (!values.isEmpty()) {

                        JSONObject decode = values.getJSONObject(0);
                        Iterator<String> attributes = decode.keys();

                        while (attributes.hasNext()) {

                            String attribute = attributes.next();
                            CodeAttribute codeAttribute = codeType.addChild(new CodeAttribute(codeType, attribute, attribute, null, "java.lang.String",
                                    null));
                            codeAttributesMap.put(attribute, codeAttribute);
                            codeAttributeId.getAndIncrement();
                            codeAttribute.setId((long) codeAttributeId.get());
                            System.out.println(String.format("INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(%s,'%s','%s',null,'java.lang.String',null, %s)//", codeAttributeId.get(), attribute, attribute, codeTypeId.get()));
                        }
                    }

                    for (int i = 0; i < values.length(); i++) {

                        JSONObject decode = values.getJSONObject(i);
                        Iterator<String> attributes = decode.keys();

                        decodeId.getAndIncrement();
                        System.out.println(String.format("INSERT INTO decodetype (id, type, description) VALUES(%s,'%s','%s')//", decodeId.get(), type, String.format("Decode of type %s.", type)));

                        while (attributes.hasNext()) {

                            String attribute = attributes.next();
                            codeAttributesMap.get(attribute).addChild(new CodeValue(codeAttributesMap.get(attribute), decode.getString(attribute)));
                            codeValueId.getAndIncrement();
                            System.out.println(String.format("INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(%s,'%s','%s',%s,null, null)//", codeValueId.get(), decode.getString(attribute), decodeId.get(), codeAttributesMap.get(attribute).getId()));
                        }
                    }
                });
            });
        } catch (Exception ex) {

            _log.error("Error loading decodes file: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Method reNumberDecodesInPropertiesFile.
     *
     * @param propertyFileLocation String
     */
    public static void reNumberDecodesInPropertiesFile(String propertyFileLocation) {

        FileInputStream fileInputStream = null;
        Scanner scanString = null;

        try {
            /*
             * Location of the properties file. Copy the source one to this Dir.
             */
            File file = new File(propertyFileLocation);
            /*
             * The name of the Decodes to be renumbered. Copy the output BELOW
             * into the properties file and remember to set the _NumOfItems to
             * the last value. EACH new item should be number one greater that
             * the current total.
             */
            final String codeName = "CODE_DECODE";

            /*
             * lookupServiceProvideName current either PropertyFile or DBTable
             */

            final String lookupServiceProvideName = "PropertyFile";

            fileInputStream = new FileInputStream(file.getAbsoluteFile());
            scanString = new Scanner(fileInputStream);
            Pattern pattern = Pattern.compile("_\\d*=");
            scanString.useDelimiter(pattern);
            int count = 0;
            StringBuilder newText = new StringBuilder();

            String token = null;
            String delimiter;
            String oldDelimiter = null;

            while (scanString.hasNext()) {

                token = scanString.next();
                delimiter = scanString.findInLine(pattern);
                if (null != token && token.contains(codeName)) {

                    if (null != delimiter) {

                        if (!token.endsWith(lookupServiceProvideName)) {

                            if (!delimiter.equals(oldDelimiter)) {

                                count++;
                            }
                            newText.append(token).append("_").append(count).append("=");
                        } else {
                            newText.append(token).append(delimiter);
                        }

                        oldDelimiter = delimiter;
                    }
                }
            }

            newText.append(token);
            _log.error("{}", newText);
        } catch (Exception ex) {

            _log.error("Error paring file: {}", ex.getMessage(), ex);
        } finally {

            try {

                if (null != scanString) {

                    scanString.close();
                }

                if (null != fileInputStream) {

                    fileInputStream.close();
                }
            } catch (IOException e) {
                _log.error("Error closing input stream: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Method main.
     *
     * @param args String[]
     */
    public static void main(String[] args) {

        generateDecodeSQL(ConfigProperties.class, DECODE_PROPERTY_FILE);
        String propertyFileLocation = "C:\\Temp\\trade.properties";
        //reNumberDecodesInPropertiesFile(propertyFileLocation);
    }
}
