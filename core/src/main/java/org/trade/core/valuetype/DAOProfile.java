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
@DiscriminatorValue("DAOProfile")
public class DAOProfile extends DAODecode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "PROFILE";
    public static final String _TABLE = "_TABLE";
    public static final String _TABLE_ID = "_TABLE_ID";
    public static final String _COLUMN = "_COLUMN";

    public DAOProfile() {

        super(DECODE, true);
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

                if (null != value) {
                    decodes.add(decode);
                }
            } else {

                if (Decode.NONE.equals(decode.getDisplayName())) {

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
     * @return DAOProfile
     */
    public static DAOProfile newInstance(String displayName) {

        final DAOProfile returnInstance = new DAOProfile();
        returnInstance.setDisplayName(displayName);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return DAOProfile
     */
    public static DAOProfile newInstance() {

        final DAOProfile returnInstance = new DAOProfile();
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