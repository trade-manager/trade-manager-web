package org.trade.core.persistent.dao;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 *
 */
@Entity
@DiscriminatorValue("StrategyParameters")
public class StrategyParameters extends CodeType {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    public StrategyParameters(String name, String description) {

        super(name, CodeType.StrategyParameters, description);
    }

    public StrategyParameters() {
        super(CodeType.StrategyParameters);
    }

    /**
     * Constructor for CodeType.
     *
     * @param name        String
     * @param type        String
     * @param description String
     */
    public StrategyParameters(String name, String type, String description) {
        super(name, type, description);
    }
}
