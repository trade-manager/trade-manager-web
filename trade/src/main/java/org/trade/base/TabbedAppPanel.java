package org.trade.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.persistent.TradeService;
import org.trade.ui.widget.Clock;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.Serial;
import java.util.List;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public abstract class TabbedAppPanel extends BasePanel implements ChangeListener {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 8405644422808736326L;

    public final TradeService tradeService;
    private final static Logger _log = LoggerFactory.getLogger(TabbedAppPanel.class);
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JPanel menuPanel = new JPanel();
    private final PrintController printJob = new PrintController();
    private int currentTab = 0;
    private BasePanel currBasePanel = null;

    /**
     * Constructor for TabbedAppPanel.
     *
     * @param frame Frame
     */
    public TabbedAppPanel(Frame frame, TradeService tradeService) {
        this.tradeService = tradeService;

        try {

            this.setLayout(new BorderLayout());
            JPanel jPanel1 = new JPanel(new BorderLayout());
            JPanel jPanelProgressBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JProgressBar progressBar = new JProgressBar(0, 0);
            jPanelProgressBar.add(progressBar);

            JPanel jPanelClock = new JPanel(new FlowLayout(FlowLayout.LEFT));
            Clock clock = new Clock();
            jPanelClock.add(clock);

            JPanel jPanelStatus = new JPanel(new GridLayout());
            JTextField jTextFieldStatus = new JTextField();
            jTextFieldStatus.setRequestFocusEnabled(false);
            jTextFieldStatus.setMargin(new Insets(5, 5, 5, 5));
            jTextFieldStatus.setBackground(Color.white);
            jTextFieldStatus.setBorder(BorderFactory.createLoweredBevelBorder());
            jPanelStatus.add(jTextFieldStatus);

            JPanel jPanel3 = new JPanel(new BorderLayout());
            jPanel3.add(jPanelClock, BorderLayout.WEST);
            jPanel3.add(jPanelProgressBar, BorderLayout.EAST);
            jPanel3.add(jPanelStatus, BorderLayout.CENTER);

            JPanel jPanel2 = new JPanel(new BorderLayout());
            jPanel2.add(tabbedPane, BorderLayout.CENTER);
            jPanel1.add(jPanel2, BorderLayout.CENTER);
            jPanel1.add(jPanel3, BorderLayout.SOUTH);
            menuPanel.setLayout(new BorderLayout());
            jPanel1.add(menuPanel, BorderLayout.NORTH);
            this.add(jPanel1, BorderLayout.CENTER);
            this.setStatusBar(jTextFieldStatus);
            this.setProgressBar(progressBar);
            tabbedPane.addChangeListener(this);
        } catch (Exception e) {

            this.setErrorMessage("Error During Initialization.", e.getMessage(), e);
        }
    }

    /**
     * Method setMenu.
     *
     * @param menu BasePanelMenu
     */
    public void setMenu(BasePanelMenu menu) {

        menuPanel.removeAll();
        menuPanel.add(menu, BorderLayout.NORTH);
        super.setMenu(menu);
    }

    public void doWindowOpen() {
    }

    public void doWindowClose() {
        doExit();
    }

    public void doWindowActivated() {
    }

    /**
     * Method doWindowDeActivated.
     *
     * @return boolean
     */
    public boolean doWindowDeActivated() {
        return true;
    }

    /**
     * This method is fired when a different tab is selected.
     *
     * @param currBasePanel BasePanel
     * @param newBasePanel  BasePanel
     */

    public abstract void tabChanged(BasePanel currBasePanel, BasePanel newBasePanel);

    public void doLFMetal() {
        try {
            UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
            SwingUtilities.updateComponentTreeUI(getFrame());
        } catch (Exception eMetal) {
            _log.error("Could not load LookAndFeel: {}", String.valueOf(eMetal));
        }
    }

    public void doLFWindows() {
        try {
            // UIManager
            // .setLookAndFeel(new
            // com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
            SwingUtilities.updateComponentTreeUI(getFrame());
        } catch (Exception eMetal) {
            _log.error("Could not load LookAndFeel: {}", String.valueOf(eMetal));
        }
    }

    public void doLFMotif() {
        try {
            // UIManager
            // .setLookAndFeel(new
            // com.sun.java.swing.plaf.motif.MotifLookAndFeel());
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            SwingUtilities.updateComponentTreeUI(getFrame());
        } catch (Exception eMetal) {
            _log.error("Could not load LookAndFeel: {}", String.valueOf(eMetal));
        }
    }

    public void doExit() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            currBasePanel = (BasePanel) tabbedPane.getComponent(i);
            currBasePanel.doWindowClose();
        }
        System.exit(0);
    }

    public void doHelp() {

    }

    public void doPrint() {
        printComponent(this.getFrame());
    }

    /**
     * Method printComponent.
     *
     * @param comp Component
     */
    protected void printComponent(Component comp) {
        printJob.printComponent(getFrame(), comp, null);
    }

    /**
     * Method addTab.
     *
     * @param title String
     * @param panel BasePanel
     */
    protected void addTab(String title, final BasePanel panel) {
        tabbedPane.add(title, panel);
        SwingUtilities.invokeLater(panel::doWindowOpen);

    }

    /**
     * Method getSelectPanel.
     *
     * @return BasePanel
     */
    public BasePanel getSelectPanel() {
        return this.currBasePanel;
    }

    /**
     * Method setSelectPanel.
     *
     * @param tabIndex int
     * @param event    MessageEvent
     * @param params   List<Object>
     */
    public void setSelectPanel(int tabIndex, MessageEvent event, List<Object> params) {
        setSelectPanel(tabIndex);
        this.currBasePanel.handleEvent(event, params);
    }

    /**
     * Method setSelectPanel.
     *
     * @param tabIndex int
     */
    public void setSelectPanel(int tabIndex) {
        tabbedPane.setSelectedIndex(tabIndex);
    }

    /**
     * Method setSelectPanel.
     *
     * @param tabPanel BasePanel
     */
    public void setSelectPanel(BasePanel tabPanel) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            BasePanel tabBasePanel = ((BasePanel) tabbedPane.getComponent(i));
            if (tabBasePanel.equals(tabPanel)) {
                tabbedPane.setSelectedIndex(i);
            }
        }
    }

    /**
     * Method stateChanged.
     *
     * @param evt ChangeEvent
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent evt) {

        if (evt.getSource() instanceof JTabbedPane selectedTab) {
            BasePanel prevBasePanel;
            if (null == currBasePanel) {
                currBasePanel = (BasePanel) selectedTab.getSelectedComponent();
                currBasePanel.setSelected(true);
            }
            if (selectedTab.isShowing()) {
                // switch current frame
                prevBasePanel = currBasePanel;
                if (!currBasePanel.doWindowDeActivated()) {
                    setSelectPanel(currentTab);
                    return;
                }

                ((BasePanel) selectedTab.getComponent(currentTab)).setSelected(false);
                currentTab = selectedTab.getSelectedIndex();
                currBasePanel = (BasePanel) selectedTab.getComponent(currentTab);
                tabChanged(prevBasePanel, currBasePanel);
                currBasePanel.clearStatusBarMessage();
                currBasePanel.setSelected(true);
                currBasePanel.doWindowActivated();
            }
        }
    }
}
