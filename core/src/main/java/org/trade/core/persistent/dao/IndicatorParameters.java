package org.trade.core.persistent.dao;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.trade.core.persistent.codetype.CodeType;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("IndicatorParameters")
public class IndicatorParameters extends CodeType {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    public IndicatorParameters(String name, String description) {
        super(name, CodeType.IndicatorParameters, description);
    }

    public IndicatorParameters() {
        super(CodeType.IndicatorParameters);
    }

    /**
     * Constructor for CodeType.
     *
     * @param name        String
     * @param type        String
     * @param description String
     */
    public IndicatorParameters(String name, String type, String description) {
        super(name, type, description);
    }
}
