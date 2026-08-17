package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.trade.core.persistent.strategy.Strategy;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("DAOStrategyManager")
public class DAOStrategyManager extends DAODecode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "STRATEGY_MANAGER";
    public static final String _TABLE = "_TABLE";
    public static final String _TABLE_ID = "_TABLE_ID";
    public static final String _COLUMN = "_COLUMN";

    public DAOStrategyManager() {
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
    public DAOStrategyManager(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * Method getCodesDecodes.
     *
     * @return List<Decode>
     */
    @Override
    public List<Decode> getCodesDecodes() throws ValueTypeException {

        final List<Decode> decodes = new ArrayList<>();
        final List<Decode> decodesAll = super.getCodesDecodes();

        for (final Decode decode : decodesAll) {

            final Strategy strategy = (Strategy) decode.getObject();

            if (!strategy.hasStrategyManager()) {

                decodes.add(decode);
            }
        }
        return decodes;
    }

    /**
     * Method newInstance.
     *
     * @param displayName String
     * @return StrategyManager
     */
    public static DAOStrategyManager newInstance(String displayName) {
        final DAOStrategyManager returnInstance = new DAOStrategyManager();
        returnInstance.setDisplayName(displayName);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return StrategyManager
     */
    public static DAOStrategyManager newInstance() {
        final DAOStrategyManager returnInstance = new DAOStrategyManager();
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