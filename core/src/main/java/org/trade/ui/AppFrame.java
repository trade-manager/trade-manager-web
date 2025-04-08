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

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 *
 */
public class AppFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = -6191549867093963518L;
    private MainControllerPanel mainPanel = null;

    public AppFrame() {
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new MainControllerPanel(this);
        this.setTitle("Application");
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.setLocationRelativeTo(null);
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            AppLoadConfig.loadAppProperties();

        } catch (Exception e) {
            System.exit(0);
        }
    }

    /**
     * Method processWindowEvent.
     *
     * @param e WindowEvent
     */
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            mainPanel.doWindowClose();
        } else if (e.getID() == WindowEvent.WINDOW_OPENED) {
            mainPanel.doWindowOpen();
        }
    }
}
