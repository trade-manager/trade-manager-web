/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.ui.models;

import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.Tradingdays;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.ValueTypeException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.io.Serial;
import java.io.Serializable;
import java.util.Hashtable;

/**
 *
 */
public class TradingdayTreeModel extends DefaultTreeModel implements Serializable {

    @Serial
    private static final long serialVersionUID = -5543286790183657148L;

    static DefaultMutableTreeNode m_root = new DefaultMutableTreeNode("Tradingdays");
    private final Hashtable<MutableTreeNode, Object> m_nodeMap = new Hashtable<>();

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
        m_nodeMap.clear();
        buildTree(tradingdays);
        fireTreeStructureChanged(this, new Object[]{getRoot()}, new int[0], new Object[0]);
    }

    /**
     * Method buildTree.
     *
     * @param tradingdays Tradingdays
     */
    private void buildTree(Tradingdays tradingdays) {

        m_nodeMap.put(m_root, m_root.getRoot());

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
        m_nodeMap.put(tradingdayNode, tradingday);
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
                m_nodeMap.put(tradstrategyNode, side);
                tradingdayNode.insert(tradstrategyNode, childStrategy);
                childStrategy++;
            }
            MutableTreeNode contractNode = new DefaultMutableTreeNode(tradestrategy);
            m_nodeMap.put(contractNode, tradestrategy);
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
        return m_nodeMap.get(treeNode);
    }
}
