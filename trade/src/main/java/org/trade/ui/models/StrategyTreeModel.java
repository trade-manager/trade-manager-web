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

import org.trade.core.persistent.dao.Rule;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.valuetype.ValueTypeException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.io.Serial;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

/**
 *
 */
public class StrategyTreeModel extends DefaultTreeModel implements Serializable {

    @Serial
    private static final long serialVersionUID = -5543286790183657148L;

    static DefaultMutableTreeNode m_root = new DefaultMutableTreeNode("Strategies");
    private final Hashtable<MutableTreeNode, Object> m_nodeMap = new Hashtable<>();

    /**
     * Constructor for StrategyTreeModel.
     *
     * @param items List<Strategy>
     */
    public StrategyTreeModel(List<Strategy> items) {

        super(m_root);
        buildTree(items);
    }

    /**
     * Method setData.
     *
     * @param strategies List<Strategy>
     */
    public void setData(List<Strategy> strategies) throws ValueTypeException {
        ((DefaultMutableTreeNode) getRoot()).removeAllChildren();
        m_nodeMap.clear();
        buildTree(strategies);
        fireTreeStructureChanged(this, new Object[]{getRoot()}, new int[0], new Object[0]);
    }

    /**
     * Method buildTree.
     *
     * @param items List<Strategy>
     */
    private void buildTree(List<Strategy> items) {

        m_nodeMap.put(m_root, m_root.getRoot());
        for (Strategy strategy : items) {
            addItem(strategy);
        }
    }

    /**
     * Method addItem.
     *
     * @param item Strategy
     */
    private void addItem(Strategy item) {

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
        m_root.add(node);
        m_nodeMap.put(node, item);
        for (Rule rule : item.getRules()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(rule);
            m_nodeMap.put(childNode, rule);
            node.add(childNode);
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
