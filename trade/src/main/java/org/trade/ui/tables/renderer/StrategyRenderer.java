package org.trade.ui.tables.renderer;

import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.persistent.strategy.strategyrule.IStrategyRule;
import org.trade.core.persistent.tradestrategy.Tradestrategy;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.ui.models.TradestrategyTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.Serial;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class StrategyRenderer extends DefaultTableCellRenderer {

    @Serial
    private static final long serialVersionUID = -6600633898553131547L;
    private final ConcurrentHashMap<String, IStrategyRule> strategyWorkers;

    /**
     * Constructor for DAOStrategyRenderer.
     *
     * @param strategyWorkers ConcurrentHashMap<String,IStrategyRule>
     */
    public StrategyRenderer(ConcurrentHashMap<String, IStrategyRule> strategyWorkers) {
        this.strategyWorkers = strategyWorkers;
    }

    /**
     * Method getTableCellRendererComponent.
     *
     * @param table       JTable
     * @param dAOStrategy Object
     * @param isSelected  boolean
     * @param hasFocus    boolean
     * @param row         int
     * @param column      int
     * @return Component
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(JTable,
     * Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object dAOStrategy, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {

        synchronized (dAOStrategy) {
            setBackground(null);
            super.getTableCellRendererComponent(table, dAOStrategy, isSelected, hasFocus, row, column);
            if (row > -1 && ((DAOStrategy) dAOStrategy).isValid()) {
                Tradestrategy transferObject = ((TradestrategyTableModel) table.getModel()).getData()
                        .getTradestrategies().get(table.convertRowIndexToModel(row));
                String key = ((Strategy) ((DAOStrategy) dAOStrategy).getObject()).getClassName()
                        + transferObject.getId();
                if (this.strategyWorkers.containsKey(key) && !isSelected) {
                    if (this.strategyWorkers.get(key).isDone()) {
                        setBackground(Color.YELLOW);
                        setToolTipText("Strategy complete");
                    } else if (this.strategyWorkers.get(key).isRunning()) {
                        setBackground(Color.GREEN);
                        setToolTipText("Strategy running");
                    }
                }
            }
            return this;
        }
    }
}
