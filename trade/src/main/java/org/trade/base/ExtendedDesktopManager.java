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
