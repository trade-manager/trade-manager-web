package org.trade.ui;

import org.trade.base.BasePanel;
import org.trade.base.BasePanelMenu;

import javax.swing.*;
import java.io.Serial;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class MainPanelMenu extends BasePanelMenu {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -2716722655140661891L;

    /**
     * Constructor for MainPanelMenu.
     *
     * @param basePanel BasePanel
     */
    public MainPanelMenu(BasePanel basePanel) {

        super(basePanel);

        JMenu actionMenu = new JMenu("Action");
        menuBar.add(actionMenu, 2);
        this.editMenu.setVisible(false);
        fileMenu.insertSeparator(4);

        // windowMenu.add(close, 0);
        // windowMenu.add(closeAll, 1);
        // windowMenu.add(cascade, 2);
        // windowMenu.add(cascadeAll, 3);
        // windowMenu.add(tileAll, 4);
        // windowMenu.insertSeparator(5);

    }
}
