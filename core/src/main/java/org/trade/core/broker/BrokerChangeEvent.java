
package org.trade.core.broker;

import java.io.Serial;

/**
 * A change event that encapsulates information about a change to a dataset.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class BrokerChangeEvent extends java.util.EventObject {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6068031665553697870L;
    /**
     * The brokerManagerModel that generated the change event.
     */
    private final IBrokerModel brokerManagerModel;

    /**
     * Constructs a new event. The source is either the brokerManagerModel or
     * the class. The brokerManagerModel can be <code>null</code> (in this case
     * the source will be the class).
     *
     * @param source             the source of the event.
     * @param brokerManagerModel IBrokerModel
     */
    public BrokerChangeEvent(Object source, IBrokerModel brokerManagerModel) {

        super(source);
        this.brokerManagerModel = brokerManagerModel;
    }

    /**
     * Returns the brokerManagerModel that generated the event. Note that the
     * brokerManagerModel may be <code>null</code> since adding a
     * <code>null</code> brokerManagerModel to a plot will generated a change
     * event.
     *
     * @return The brokerManagerModel (possibly <code>null</code>).
     */
    public IBrokerModel getBrokerManagerModel() {

        return this.brokerManagerModel;
    }
}
