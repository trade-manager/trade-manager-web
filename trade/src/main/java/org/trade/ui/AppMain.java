package org.trade.ui;

import org.trade.base.ImageBuilder;
import org.trade.base.WaitCursorEventQueue;

import javax.swing.*;
import java.awt.*;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class AppMain {


    // Construct the application
    public AppMain() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double appWidth = screenSize.getWidth() * 0.9;
        double appHieght = screenSize.getHeight() * 0.9;

        if (appHieght > 900) {

            appHieght = 900;
        }

        if (appWidth > 1200) {

            appWidth = 1200;
        }
        AppFrame frame = new AppFrame();
        frame.setIconImage(ImageBuilder.getImage("trade.gif"));
        frame.setSize((int) appWidth, (int) appHieght);
        frame.setLocation((int) ((screenSize.getWidth() - frame.getSize().getWidth()) / 2),
                (int) ((screenSize.getHeight() - frame.getSize().getHeight()) / 2));
        frame.validate();
        frame.repaint();
        frame.setVisible(true);
        EventQueue waitQue = new WaitCursorEventQueue(500);
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(waitQue);
    }

    /**
     * Method main.
     *
     * @param args String[]
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(AppMain::new);
    }
}
