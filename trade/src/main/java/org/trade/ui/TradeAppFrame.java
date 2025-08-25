package org.trade.ui;

import org.trade.core.persistent.TradeService;
import org.trade.core.properties.TradeAppLoadConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.Serial;

/**
 *
 */
public class TradeAppFrame extends JFrame {

    @Serial
    private static final long serialVersionUID = -206248291070367944L;

    private final TradeMainControllerPanel mainPanel;

    public TradeAppFrame(TradeService tradeService) {

        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new TradeMainControllerPanel(this, tradeService);
        this.setTitle("Trade Manager");
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.setLocationRelativeTo(null);
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    static {

        try {

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            TradeAppLoadConfig.loadAppProperties();
        } catch (Exception ex) {
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
