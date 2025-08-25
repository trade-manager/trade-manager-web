package org.trade.ui;

import org.trade.base.BasePanel;
import org.trade.base.TabbedAppPanel;
import org.trade.core.persistent.TradeService;
import org.trade.core.properties.ConfigProperties;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class MainControllerPanel extends TabbedAppPanel {

    @Serial
    private static final long serialVersionUID = -7717664255656430982L;

    public static String title = null;
    public static String version = null;
    public static String date = null;

    /**
     * The main application controller which interacts between the view and the
     * applications underlying models. This controller also listens to events
     * from the broker model.
     * <p>
     *
     * @param frame the main application Frame.
     */
    public MainControllerPanel(Frame frame, TradeService tradeService) {

        super(frame, tradeService);

        try {

            setMenu(new MainPanelMenu(this));
            /* This is always true as main panel needs to receive all events */
            setSelected(true);
            title = ConfigProperties.getPropAsString("component.name.base");
            version = ConfigProperties.getPropAsString("component.name.version");
            date = ConfigProperties.getPropAsString("component.name.date");

        } catch (Exception e) {

            this.setErrorMessage("Error During Initialization.", e.getMessage(), e);
        }
    }

    /**
     * This method is fired from the main menu. It displays the application
     * version.
     */
    public void doAbout() {
        try {
            StringBuffer message = new StringBuffer();
            message.append("Product version: ");
            message.append(MainControllerPanel.version);
            message.append("\nBuild Label:     ");
            message.append(MainControllerPanel.title);
            message.append("\nBuild Time:      ");
            message.append(MainControllerPanel.date);
            JOptionPane.showMessageDialog(this, message, "About Help", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            this.setErrorMessage("Could not load about help.", ex.getMessage(), ex);
        }
    }

    /**
     * This method is fired after the tab has been created and placed in the tab
     * controller.
     */

    public void doWindowOpen() {

    }

    /**
     * This method is fired when the tab closes.
     */

    public void doWindowClose() {
        doExit();
    }

    /**
     * This method is fired from the Main menu and will allow you to setup the
     * printer setting.
     */

    public void doPrintSetup() {

    }

    /**
     * This method is fired from the Main menu and will allow you to preview a
     * print of the current tab.
     */
    public void doPrintPreview() {

    }

    /**
     * This method is fired from the Main menu and will allow you to print the
     * current tab.
     */
    public void doPrint() {

    }

    /**
     * This method is fired when a different tab is selected.
     *
     * @param currBasePanel BasePanel
     * @param newBasePanel  BasePanel
     */
    public void tabChanged(BasePanel currBasePanel, BasePanel newBasePanel) {
    }
}
