package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.trade.core.persistent.portfolio.Portfolio;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("FAGroup")
public class FAGroup extends DAODecode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "GROUP_DATA";
    public static final String _TABLE = "_TABLE";
    public static final String _TABLE_ID = "_TABLE_ID";
    public static final String _COLUMN = "_COLUMN";

    public FAGroup() {
        super(DECODE, true);
    }

    /**
     * Constructor for CodeType.
     *
     * @param type        String
     * @param category    String
     * @param name        String
     * @param description String
     */
    public FAGroup(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * Method getCodesDecodes.
     *
     * @return List<Decode>
     */

    public List<Decode> getCodesDecodes() throws ValueTypeException {

        final List<Decode> decodes = new ArrayList<>();
        final List<Decode> decodesAll = super.getCodesDecodes();

        for (final Decode decode : decodesAll) {

            final Portfolio portfolio = (Portfolio) decode.getObject();

            if (null != portfolio.getAllocationMethod()) {

                Integer value = null;

                try {

                    value = Integer.parseInt(portfolio.getAllocationMethod());
                } catch (NumberFormatException ex) {
                    // Do nothing
                }

                if (null == value) {

                    decodes.add(decode);
                }
            } else {

                if (ValueType.NONE.equals(decode.getDisplayName())) {

                    decodes.add(decode);
                }
            }
        }
        return decodes;
    }

    /**
     * Method newInstance.
     *
     * @param displayName String
     * @return Group
     */
    public static FAGroup newInstance(String displayName) {

        final FAGroup returnInstance = new FAGroup();
        returnInstance.setDisplayName(displayName);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return Group
     */
    public static FAGroup newInstance() {
        final FAGroup returnInstance = new FAGroup();
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