package org.trade.base;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Id: FilePreviewer.java,v 1.1 2001/10/18 01:32:16 simon Exp $
 */
public class FilePreviewer extends JComponent implements PropertyChangeListener {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2163573903688220675L;

    ImageIcon thumbnail = null;
    File f = null;

    /**
     * Constructor for FilePreviewer.
     *
     * @param fc JFileChooser
     */
    public FilePreviewer(JFileChooser fc) {
        setPreferredSize(new Dimension(100, 50));
        fc.addPropertyChangeListener(this);
    }

    public void loadImage() {
        if (f != null) {
            ImageIcon tmpIcon = new ImageIcon(f.getPath());

            if (tmpIcon.getIconWidth() > 90) {
                thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(90, -1, Image.SCALE_DEFAULT));
            } else {
                thumbnail = tmpIcon;
            }
        }
    }

    /**
     * Method propertyChange.
     *
     * @param e PropertyChangeEvent
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            f = (File) e.getNewValue();

            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }

    /**
     * Method paint.
     *
     * @param g Graphics
     */
    public void paint(Graphics g) {
        if (thumbnail == null) {
            loadImage();
        }

        if (thumbnail != null) {
            int x = (getWidth() / 2) - (thumbnail.getIconWidth() / 2);
            int y = (getHeight() / 2) - (thumbnail.getIconHeight() / 2);

            if (y < 0) {
                y = 0;
            }

            if (x < 5) {
                x = 5;
            }

            thumbnail.paintIcon(this, g, x, y);
        }
    }
}
