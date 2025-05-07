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
package org.trade.base;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Id: ExtendedDesktopManager.java,v 1.1 2001/10/18 01:32:16 simon Exp
 * $
 */
public class ExtendedDesktopManager extends DefaultDesktopManager {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6690132949361620306L;

    /**
     * ExtendedDesktopManager() - constructor
     *
     * @param targetPane JDesktopPane
     */
    public ExtendedDesktopManager(JDesktopPane targetPane) {
        ghostPanel = new JPanel();

        ghostPanel.setOpaque(false);
        ghostPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, BORDER_THICKNESS));

        this.targetPane = targetPane;
    }

    /**
     * beginDraggingFrame() -
     *
     * @param f JComponent
     */
    public void beginDraggingFrame(JComponent f) {
        Rectangle r = f.getBounds();

        ghostPanel.setBounds(r);
        f.setVisible(false);
        targetPane.add(ghostPanel);
        targetPane.setLayer(ghostPanel, JLayeredPane.DRAG_LAYER);
        targetPane.setVisible(true);
    }

    /**
     * dragFrame() -
     *
     * @param f    JComponent
     * @param newX int
     * @param newY int
     */
    public void dragFrame(JComponent f, int newX, int newY) {
        setBoundsForFrame(ghostPanel, newX, newY, ghostPanel.getWidth(), ghostPanel.getHeight());
    }

    /**
     * endDraggingFrame() -
     *
     * @param f JComponent
     */
    public void endDraggingFrame(JComponent f) {
        Rectangle r = ghostPanel.getBounds();

        f.setVisible(true);
        f.setBounds(r);
        targetPane.remove(ghostPanel);
    }

    /**
     * beginResizingFrame() -
     *
     * @param f         JComponent
     * @param direction int
     */
    public void beginResizingFrame(JComponent f, int direction) {
        oldCursor = f.getCursor();

        super.beginResizingFrame(f, direction);

        Cursor cursor = f.getCursor();
        Rectangle r = f.getBounds();

        ghostPanel.setBounds(r);
        f.setVisible(false);
        targetPane.add(ghostPanel);
        targetPane.setLayer(ghostPanel, JLayeredPane.DRAG_LAYER);
        ghostPanel.setCursor(cursor);
        targetPane.setVisible(true);
    }

    /**
     * resizeFrame() -
     *
     * @param f         JComponent
     * @param newX      int
     * @param newY      int
     * @param newWidth  int
     * @param newHeight int
     */
    public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
        setBoundsForFrame(ghostPanel, newX, newY, newWidth, newHeight);
    }

    /**
     * endResizingFrame() -
     *
     * @param f JComponent
     */
    public void endResizingFrame(JComponent f) {
        Rectangle r = ghostPanel.getBounds();

        f.setVisible(true);
        f.setBounds(r);
        ghostPanel.setCursor(oldCursor);
        targetPane.remove(ghostPanel);
        f.validate();
    }

    protected JPanel ghostPanel;

    protected JComponent targetComponent;

    protected JDesktopPane targetPane;

    protected Cursor oldCursor;

    protected static final Color BORDER_COLOR = Color.black;

    protected static final int BORDER_THICKNESS = 2;
}
