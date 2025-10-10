package org.trade.ui.models;

import org.trade.core.persistent.tradestrategy.Tradestrategy;
import org.trade.core.persistent.tradingday.Tradingday;
import org.trade.core.persistent.tradingday.Tradingdays;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.ValueTypeException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.io.Serial;
import java.io.Serializable;
import java.util.Hashtable;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradingdayTreeModel extends DefaultTreeModel implements Serializable {

    @Serial
    private static final long serialVersionUID = -5543286790183657148L;

    static DefaultMutableTreeNode m_root = new DefaultMutableTreeNode("Tradingdays");
    private final Hashtable<MutableTreeNode, Object> nodeMap = new Hashtable<>();

    /**
     * Constructor for TradingdayTreeModel.
     *
     * @param tradingdays Tradingdays
     */
    public TradingdayTreeModel(Tradingdays tradingdays) {

        super(m_root);
        buildTree(tradingdays);
    }

    /**
     * Method setData.
     *
     * @param tradingdays Tradingdays
     */
    public void setData(Tradingdays tradingdays) throws ValueTypeException {
        ((DefaultMutableTreeNode) getRoot()).removeAllChildren();
        nodeMap.clear();
        buildTree(tradingdays);
        fireTreeStructureChanged(this, new Object[]{getRoot()}, new int[0], new Object[0]);
    }

    /**
     * Method buildTree.
     *
     * @param tradingdays Tradingdays
     */
    private void buildTree(Tradingdays tradingdays) {

        nodeMap.put(m_root, m_root.getRoot());

        tradingdays.getTradingdays().sort(Tradingday.DATE_ORDER_DESC);
        for (Tradingday tradingday : tradingdays.getTradingdays()) {
            tradingday.getTradestrategies().sort(Tradestrategy.DATE_ORDER_ASC);
            addTradingday(tradingday);
        }
    }

    /**
     * Method addTradingday.
     *
     * @param tradingday Tradingday
     */
    private void addTradingday(Tradingday tradingday) {

        MutableTreeNode tradingdayNode = new DefaultMutableTreeNode(tradingday);
        m_root.add(tradingdayNode);
        nodeMap.put(tradingdayNode, tradingday);
        int childStrategy = 0;
        int childContract = 0;
        Side side = Side.newInstance("");
        MutableTreeNode tradstrategyNode = null;
        for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {
            Side trdestrategySide = Side.newInstance(tradestrategy.getSide());
            if (tradstrategyNode == null || !side.equalsCode(trdestrategySide.getCode())) {
                side = trdestrategySide;
                childContract = 0;
                tradstrategyNode = new DefaultMutableTreeNode(side);
                nodeMap.put(tradstrategyNode, side);
                tradingdayNode.insert(tradstrategyNode, childStrategy);
                childStrategy++;
            }
            MutableTreeNode contractNode = new DefaultMutableTreeNode(tradestrategy);
            nodeMap.put(contractNode, tradestrategy);
            tradstrategyNode.insert(contractNode, childContract);
            childContract++;

        }
    }

    /**
     * Method getNode.
     *
     * @param treeNode Object
     * @return Object
     */
    public Object getNode(Object treeNode) {
        return nodeMap.get(treeNode);
    }
}
