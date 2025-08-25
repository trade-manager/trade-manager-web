package org.trade.base;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageProducer;
import java.net.URL;

/**
 * TODO: Add class description
 *
 * @author TODO: Add your name here
 * @version $Id: ImageBuilder.java,v 1.1 2001/10/18 01:32:16 simon Exp $
 */
public class ImageBuilder {
    /**
     * getIcon( String name)
     *
     * @param name String
     * @return ImageIcon * @exception * @see
     */
    public static ImageIcon getImageIcon(String name) {

        try {

            URL url = ImageBuilder.class.getResource("images/" + name);
            Toolkit tk = Toolkit.getDefaultToolkit();
            return (new ImageIcon(tk.createImage((ImageProducer) url.getContent())));
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * getIcon( String name)
     *
     * @param name String
     * @return ImageIcon * @exception * @see
     */
    public static Image getImage(String name) {

        try {

            URL url = ImageBuilder.class.getResource("images/" + name);
            Toolkit tk = Toolkit.getDefaultToolkit();
            return (tk.createImage((ImageProducer) url.getContent()));
        } catch (Exception e) {
            return null;
        }
    }
}
