package org.trade.core.valuetype;

import org.trade.core.persistent.codetype.Entrylimit;

import java.io.Serial;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class EntryLimit extends DAODecode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "ENTRY_LIMIT";
    public static final String _TABLE = "_TABLE";
    public static final String _TABLE_ID = "_TABLE_ID";
    public static final String _COLUMN = "_COLUMN";

    public EntryLimit() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @return EntryLimit
     */
    public static EntryLimit newInstance() {
        final EntryLimit returnInstance = new EntryLimit();
        returnInstance.setDefaultCode();
        return returnInstance;
    }

    /**
     * Method getValue.
     *
     * @param price Money
     * @return Entrylimit
     */
    public Entrylimit getValue(Money price) {

        List<Decode> decodes;

        try {

            decodes = this.getCodesDecodes();
            final ListIterator<Decode> enumDAODecode = decodes.listIterator();

            while (enumDAODecode.hasNext()) {

                final Decode decode = enumDAODecode.next();
                final Entrylimit entryLimit = (Entrylimit) decode.getObject();

                if ((entryLimit.getStartPrice().subtract(price.getBigDecimalValue()).doubleValue() <= 0)
                        && (entryLimit.getEndPrice().subtract(price.getBigDecimalValue()).doubleValue() >= 0)) {

                    return entryLimit;
                }
            }
        } catch (final ValueTypeException e) {
            /*
             * Do nothing is no code just report to log.
             */

        }
        return null;
    }
}