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
package org.trade.ui;

import org.trade.ui.base.BaseButton;
import org.trade.ui.base.BaseMenuItem;
import org.trade.ui.base.BasePanel;
import org.trade.ui.base.BasePanelMenu;
import org.trade.ui.base.BaseUIPropertyCodes;

import javax.swing.*;
import java.io.Serial;

/**
 *
 */
public class TradeMainPanelMenu extends BasePanelMenu {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -2716722655140661891L;

    private final BaseMenuItem searchMenu = new BaseMenuItem(null, BaseUIPropertyCodes.SEARCH);
    private final BaseButton searchButton = new BaseButton(null, BaseUIPropertyCodes.SEARCH);
    private final BaseMenuItem refreshMenu = new BaseMenuItem(null, BaseUIPropertyCodes.REFRESH);
    private final BaseButton refreshButton = new BaseButton(null, BaseUIPropertyCodes.REFRESH);
    private final BaseButton deleteButton = new BaseButton(null, BaseUIPropertyCodes.DELETE);
    private final BaseMenuItem deleteMenu = new BaseMenuItem(null, BaseUIPropertyCodes.DELETE);
    private final BaseMenuItem brokerDataMenu = new BaseMenuItem(null, BaseUIPropertyCodes.DATA);
    private final BaseButton brokerDataButton = new BaseButton(null, BaseUIPropertyCodes.DATA);
    private final BaseButton runStrategyButton = new BaseButton(null, BaseUIPropertyCodes.RUN);
    private final BaseMenuItem runStrategyMenu = new BaseMenuItem(null, BaseUIPropertyCodes.RUN);
    private final BaseButton testStrategyButton = new BaseButton(null, BaseUIPropertyCodes.TEST);
    private final BaseMenuItem testStrategyMenu = new BaseMenuItem(null, BaseUIPropertyCodes.TEST);
    private final BaseButton cancelButton = new BaseButton(null, BaseUIPropertyCodes.CANCEL);
    private final BaseMenuItem cancelMenu = new BaseMenuItem(null, BaseUIPropertyCodes.CANCEL);
    private final BaseButton closeAllButton = new BaseButton(null, BaseUIPropertyCodes.CLOSE_ALL);
    private final BaseMenuItem closeAllMenu = new BaseMenuItem(null, BaseUIPropertyCodes.CLOSE_ALL);
    private final BaseMenuItem propertiesMenu = new BaseMenuItem(null, BaseUIPropertyCodes.PROPERTIES);
    private final BaseMenuItem connect = new BaseMenuItem(null, BaseUIPropertyCodes.CONNECT);
    private final BaseMenuItem disconnect = new BaseMenuItem(null, BaseUIPropertyCodes.DISCONNECT);
    private final BaseMenuItem disclaimer = new BaseMenuItem(null, BaseUIPropertyCodes.DISCLAIMER);

    /**
     * Constructor for TradeMainPanelMenu.
     *
     * @param basePanel BasePanel
     */
    public TradeMainPanelMenu(BasePanel basePanel) {
        super(basePanel);

        cancelButton.setToolTipText("Cancel Strategies & Data");
        cancelButton.addActionListener(_ -> messageEvent(cancelButton.getMethod()));
        cancelMenu.addActionListener(_ -> messageEvent(cancelMenu.getMethod()));
        closeAllButton.setToolTipText("Cancel Orders & Close Positions");
        closeAllButton.addActionListener(_ -> messageEvent(closeAllButton.getMethod()));
        closeAllMenu.addActionListener(_ -> messageEvent(closeAllMenu.getMethod()));
        runStrategyButton.setToolTipText("Run Strategy");
        runStrategyButton.addActionListener(_ -> messageEvent(runStrategyButton.getMethod()));
        runStrategyMenu.setText("Run Strategy");
        runStrategyMenu.addActionListener(_ -> messageEvent(runStrategyMenu.getMethod()));
        testStrategyButton.setToolTipText("Test Strategy");
        testStrategyButton.addActionListener(_ -> messageEvent(testStrategyButton.getMethod()));
        testStrategyMenu.setText("Test Strategy");
        testStrategyMenu.addActionListener(_ -> messageEvent(testStrategyMenu.getMethod()));
        brokerDataButton.setToolTipText("Get Chart Data");
        brokerDataButton.addActionListener(_ -> messageEvent(brokerDataButton.getMethod()));
        brokerDataMenu.setText("Get Chart Data");
        brokerDataMenu.addActionListener(_ -> messageEvent(brokerDataMenu.getMethod()));
        searchMenu.addActionListener(_ -> messageEvent(searchMenu.getMethod()));
        searchButton.addActionListener(_ -> messageEvent(searchButton.getMethod()));

        refreshMenu.addActionListener(_ -> messageEvent(refreshMenu.getMethod()));
        refreshButton.addActionListener(_ -> messageEvent(refreshButton.getMethod()));
        deleteMenu.setText("Delete all Orders");
        deleteMenu.addActionListener(_ -> messageEvent(deleteMenu.getMethod()));
        deleteButton.setToolTipText("Delete all Orders");
        deleteButton.addActionListener(_ -> messageEvent(deleteButton.getMethod()));
        propertiesMenu.setText("Contract Details");
        propertiesMenu.addActionListener(_ -> messageEvent(propertiesMenu.getMethod()));
        connect.addActionListener(_ -> messageEvent(connect.getMethod()));
        disconnect.addActionListener(_ -> messageEvent(disconnect.getMethod()));
        disclaimer.addActionListener(_ -> messageEvent(disclaimer.getMethod()));

        final BaseMenuItem close = new BaseMenuItem(null, BaseUIPropertyCodes.CLOSE);
        close.addActionListener(_ -> messageEvent(close.getMethod()));

        final BaseMenuItem closeAll = new BaseMenuItem(null, BaseUIPropertyCodes.CLOSE_ALL);
        closeAll.addActionListener(_ -> messageEvent(closeAll.getMethod()));

        final BaseMenuItem cascade = new BaseMenuItem(null, BaseUIPropertyCodes.CASCADE);
        cascade.addActionListener(_ -> messageEvent(cascade.getMethod()));

        final BaseMenuItem cascadeAll = new BaseMenuItem(null, BaseUIPropertyCodes.CASCADE_ALL);
        cascadeAll.addActionListener(_ -> messageEvent(cascadeAll.getMethod()));

        final BaseMenuItem tileAll = new BaseMenuItem(null, BaseUIPropertyCodes.TILE_ALL);
        tileAll.addActionListener(_ -> messageEvent(tileAll.getMethod()));
        JMenu actionMenu = new JMenu("Action");
        menuBar.add(actionMenu, 2);
        this.editMenu.setVisible(false);
        fileMenu.insertSeparator(4);
        fileMenu.add(connect, 5);
        fileMenu.add(disconnect, 6);

        getButtonPanel().add(brokerDataButton, 3);
        getButtonPanel().add(testStrategyButton, 4);
        getButtonPanel().add(runStrategyButton, 5);
        getButtonPanel().add(cancelButton, 6);
        getButtonPanel().add(searchButton, 7);
        getButtonPanel().add(refreshButton, 8);
        getButtonPanel().add(deleteButton, 9);
        getButtonPanel().add(closeAllButton, 10);

        actionMenu.add(brokerDataMenu, 0);
        actionMenu.add(testStrategyMenu, 1);
        actionMenu.add(runStrategyMenu, 2);
        actionMenu.add(cancelMenu, 3);
        actionMenu.add(searchMenu, 4);
        actionMenu.add(refreshMenu, 5);
        actionMenu.add(deleteMenu, 6);
        actionMenu.add(closeAllMenu, 7);
        actionMenu.add(propertiesMenu, 8);

        helpMenu.add(disclaimer, 1);

        // windowMenu.add(close, 0);
        // windowMenu.add(closeAll, 1);
        // windowMenu.add(cascade, 2);
        // windowMenu.add(cascadeAll, 3);
        // windowMenu.add(tileAll, 4);
        // windowMenu.insertSeparator(5);

    }

    /**
     * Method setEnabledBrokerData.
     *
     * @param enabled boolean
     */
    public void setEnabledBrokerData(boolean enabled) {
        brokerDataMenu.setEnabled(enabled);
        brokerDataButton.setEnabled(enabled);
    }

    /**
     * Method setEnabledRunStrategy.
     *
     * @param enabled boolean
     */
    public void setEnabledRunStrategy(boolean enabled) {
        runStrategyMenu.setEnabled(enabled);
        runStrategyButton.setEnabled(enabled);
    }

    /**
     * Method setEnabledConnect.
     *
     * @param enabled boolean
     */
    public void setEnabledConnect(boolean enabled) {

        if (enabled) {
            connect.setEnabled(enabled);
            disconnect.setEnabled(false);
        } else {
            connect.setEnabled(enabled);
            disconnect.setEnabled(true);
        }
    }

    /**
     * Method setEnabledTestStrategy.
     *
     * @param enabled boolean
     */
    public void setEnabledTestStrategy(boolean enabled) {
        testStrategyMenu.setEnabled(enabled);
        testStrategyButton.setEnabled(enabled);
    }

    /**
     * Method setEnabledSearchDeleteRefreshSave.
     *
     * @param enabled boolean
     */
    public void setEnabledSearchDeleteRefreshSave(boolean enabled) {
        refreshMenu.setEnabled(enabled);
        refreshButton.setEnabled(enabled);
        searchMenu.setEnabled(enabled);
        searchButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        deleteMenu.setEnabled(enabled);
    }

    /**
     * Method setEnabledDeleteSave.
     *
     * @param enabled boolean
     */
    public void setEnabledDelete(boolean enabled, String text) {
        deleteMenu.setText(text);
        deleteButton.setToolTipText(text);
        deleteButton.setEnabled(enabled);
        deleteMenu.setEnabled(enabled);
    }
}
