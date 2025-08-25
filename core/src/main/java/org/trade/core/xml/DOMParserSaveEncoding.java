package org.trade.core.xml;

import org.trade.core.util.XMLDOMParserWrapper;

import java.nio.charset.Charset;

/**
 * The DOMParserSaveEncoding class extends DOMParser. It also provides the Java
 * Encoding of the XML document by overriding the startDocument method and
 * providing a way to capture the MIME encoding from the XML document which in
 * turn is converted to the Java Encoding by the internal MIME2Java class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DOMParserSaveEncoding extends XMLDOMParserWrapper {
    /*
     * Default MIME so we check the file.encoding
     */
    String _mimeEncoding = "DEFAULT";

    public DOMParserSaveEncoding() {
        super(false, true);
    }

    private String getMimeEncoding() {
        return (_mimeEncoding);
    }

    public String getJavaEncoding() {
        String javaEncoding = null;
        final String mimeEncoding = getMimeEncoding();

        if (mimeEncoding != null) {

            if (mimeEncoding.equals("DEFAULT")) {

                //javaEncoding = System.getProperty("file.encoding");
                javaEncoding = Charset.defaultCharset().displayName();
            }
        }

        if (javaEncoding == null) // Should never return null
        {
            javaEncoding = "UTF8";
        }
        return (javaEncoding);
    }
}
