package org.trade.core.valuetype;

import java.io.Serial;
import java.util.List;

/**
 * This class is suppose to represent a base class for a specialized CodeDecode
 * type object e.g. US State Codes and Descriptions.
 * <p>
 * Note : This object is not intended to be used directly.
 * <p>
 * This object will use a LookupService in order to obtain a ILookup containing
 * all of the Systems CODE_DECODE values for a specific Type.
 *
 * @author Simon Allen
 */
public class BaseDecode extends Decode {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5356057478795774210L;

    private static final String BASE_DECODE_IDENTIFIER = "BASE_DECODE";

    /**
     * Default Constructor
     */
    public BaseDecode() {
    }

    /**
     * Default Constructor
     *
     * @param codeDecodeType String
     * @param columnNames    List<String>
     * @param values         List<Object>
     */
    public BaseDecode(String codeDecodeType, List<String> columnNames, List<Object> values) {
        super(codeDecodeType, columnNames, values, BASE_DECODE_IDENTIFIER);
    }

    /**
     * Default Constructor
     *
     * @param codeDecodeType String
     */
    public BaseDecode(String codeDecodeType) {
        this(codeDecodeType, false);
    }

    /**
     * Default Constructor
     *
     * @param codeDecodeType String
     * @param optional       boolean
     */
    public BaseDecode(String codeDecodeType, boolean optional) {
        super(codeDecodeType, BASE_DECODE_IDENTIFIER, optional);
    }

    /**
     * Default Constructor
     *
     * @param codeDecodeType String
     * @param identifier     String
     */
    public BaseDecode(String codeDecodeType, String identifier, boolean optional) {
        super(codeDecodeType, BASE_DECODE_IDENTIFIER, optional);
    }
}
