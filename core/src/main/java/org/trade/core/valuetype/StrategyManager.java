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
@DiscriminatorValue("StrategyManager")
public class StrategyManager extends DAODecode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "STRATEGY_MANAGER";
    public static final String _TABLE = "_TABLE";
    public static final String _TABLE_ID = "_TABLE_ID";
    public static final String _COLUMN = "_COLUMN";

    public StrategyManager() {
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
    public StrategyManager(String type, String category, String name, String description) {

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
    public static StrategyManager newInstance(String displayName) {
        final StrategyManager returnInstance = new StrategyManager();
        returnInstance.setDisplayName(displayName);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return StrategyManager
     */
    public static StrategyManager newInstance() {
        final StrategyManager returnInstance = new StrategyManager();
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