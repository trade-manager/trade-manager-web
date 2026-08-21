package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("ContentType")
public class ContentType extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "CONTENT_TYPE";
    public static final String JAVA = "text/java";
    public static final String JAVASCRIPT = "text/javascript";
    public static final String TEXT = "text/rtf";

    public ContentType() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return ContentType
     */
    public static ContentType newInstance(String value) {

        final ContentType returnInstance = new ContentType();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return ContentType
     */
    public static ContentType newInstance() {

        final ContentType returnInstance = new ContentType();
        returnInstance.setDefaultCode();
        return returnInstance;
    }

    /**
     * Method convertToUppercase.
     *
     * @return boolean
     */
    protected boolean convertToUppercase() {
        return false;
    }

}