package org.trade.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.trade.core.persistent.TradeService;
import org.trade.core.properties.AppLoadConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.Serial;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class AppFrame extends JFrame {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6191549867093963518L;
    private final MainControllerPanel mainPanel;

    static {

        try {

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            AppLoadConfig.loadAppProperties();
        } catch (Exception e) {

            System.exit(0);
        }
    }

    @Autowired
    public AppFrame(TradeService tradeService) {

        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new MainControllerPanel(this, tradeService);
        this.setTitle("Application");
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.setLocationRelativeTo(null);
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
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
