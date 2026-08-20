package org.trade.base;

import org.trade.core.valuetype.UIComponentProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.io.Serial;
import java.util.ArrayList;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class BasePanelMenu extends JPanel {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -9043085427010337514L;

    private static JFrame frame = null;

    protected JMenuBar menuBar = new JMenuBar();
    protected JPanel buttonPanel = new JPanel();

    protected JMenu windowMenu = new JMenu();
    protected JMenu fileMenu = new JMenu();
    protected JMenu editMenu = new JMenu();
    protected JMenu helpMenu = new JMenu();
    protected JMenu viewMenu = new JMenu();
    protected JMenu menuItemUtils = new JMenu();

    protected JMenuItem menuItemNew = new JMenuItem();
    protected JMenuItem menuItemOpen = new JMenuItem();
    protected JMenuItem menuItemSave = new JMenuItem();
    protected JMenuItem menuItemSaveAs = new JMenuItem();
    protected JMenuItem menuItemPrint = new JMenuItem();
    protected JMenuItem menuItemPrintPreview = new JMenuItem();
    protected JMenuItem menuItemPrintSetUp = new JMenuItem();
    protected JMenuItem menuItemExit = new JMenuItem();
    protected JMenuItem menuItemUndo = new JMenuItem();
    protected JMenuItem menuItemRedo = new JMenuItem();
    protected JMenuItem menuItemCut = new JMenuItem();
    protected JMenuItem menuItemCopy = new JMenuItem();
    protected JMenuItem menuItemPaste = new JMenuItem();
    protected JMenuItem menuItemFind = new JMenuItem();
    protected JMenuItem menuItemReplace = new JMenuItem();
    protected JMenuItem menuItemGoto = new JMenuItem();
    protected JMenuItem menuItemContents = new JMenuItem();
    protected JMenuItem menuItemAboutHelp = new JMenuItem();

    protected BaseButton saveButton;
    protected BaseButton openFileButton;
    protected BaseButton helpButton;
    protected BaseButton printButton;

    protected MessageNotifier notifier = new MessageNotifier();

    /**
     * Constructor for BasePanelMenu.
     *
     * @param p BasePanel
     */
    public BasePanelMenu(BasePanel p) {
        this();

        if (p != null) {
            this.addMessageListener(p);
        }
    }

    public BasePanelMenu() {

        this.setLayout(new BorderLayout());
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new BorderLayout());
        jPanel1.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(ContainerEvent e) {
                jPanelToolPanel_componentChanged();
            }

            public void componentRemoved(ContainerEvent e) {
                jPanelToolPanel_componentChanged();
            }
        });

        FlowLayout flowLayout1 = new FlowLayout();
        flowLayout1.setVgap(0);
        flowLayout1.setHgap(0);
        buttonPanel.setLayout(flowLayout1);

        JPanel jPanelMenuPanel = new JPanel();
        jPanelMenuPanel.setLayout(new BorderLayout());
        JPanel jPanelToolPanel = new JPanel();
        jPanelToolPanel.setLayout(new BorderLayout());

        JToolBar jToolBarMain = new JToolBar();
        jToolBarMain.setLayout(new BorderLayout());

        fileMenu.setText("File");
        fileMenu.setMnemonic('F');
        editMenu.setText("Edit");
        editMenu.setMnemonic('E');
        helpMenu.setText("Help");
        helpMenu.setMnemonic('H');
        viewMenu.setText("View");
        viewMenu.setMnemonic('V');
        windowMenu.setText("Window");
        viewMenu.setMnemonic('A');

        openFileButton = new BaseButton(null, UIComponentProperties.OPEN_FILE);
        openFileButton.addActionListener(_ -> messageEvent(openFileButton.getMethod()));
        buttonPanel.add(openFileButton, null);
        saveButton = new BaseButton(null, UIComponentProperties.SAVE);
        saveButton.addActionListener(_ -> messageEvent(saveButton.getMethod()));
        buttonPanel.add(saveButton, null);

        helpButton = new BaseButton(null, UIComponentProperties.HELP);
        helpButton.addActionListener(_ -> messageEvent(helpButton.getMethod()));
        buttonPanel.add(helpButton, null);
        printButton = new BaseButton(null, UIComponentProperties.PRINT);
        printButton.addActionListener(_ -> messageEvent(printButton.getMethod()));
        buttonPanel.add(printButton, null);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);
        final BaseMenuItem menuItemNew = new BaseMenuItem(null, UIComponentProperties.NEW);
        menuItemNew.addActionListener(_ -> messageEvent(menuItemNew.getMethod()));
        final BaseMenuItem menuItemOpen = new BaseMenuItem(null, UIComponentProperties.OPEN_FILE);
        menuItemOpen.addActionListener(_ -> messageEvent(menuItemOpen.getMethod()));
        final BaseMenuItem menuItemSave = new BaseMenuItem(null, UIComponentProperties.SAVE);
        menuItemSave.addActionListener(_ -> messageEvent(menuItemSave.getMethod()));
        final BaseMenuItem menuItemSaveAs = new BaseMenuItem(null, UIComponentProperties.SAVE_AS);
        menuItemSaveAs.addActionListener(_ -> messageEvent(menuItemSaveAs.getMethod()));
        final BaseMenuItem menuItemPrint = new BaseMenuItem(null, UIComponentProperties.PRINT);
        menuItemPrint.addActionListener(_ -> messageEvent(menuItemPrint.getMethod()));
        final BaseMenuItem menuItemPrintPreview = new BaseMenuItem(null, UIComponentProperties.PRINT_PREVIEW);
        menuItemPrintPreview.addActionListener(_ -> messageEvent(menuItemPrintPreview.getMethod()));
        final BaseMenuItem menuItemPrintSetUp = new BaseMenuItem(null, UIComponentProperties.PRINT_OPTIONS);
        menuItemPrintSetUp.addActionListener(_ -> messageEvent(menuItemPrintSetUp.getMethod()));
        final BaseMenuItem menuItemExit = new BaseMenuItem(null, UIComponentProperties.EXIT);
        menuItemExit.addActionListener(_ -> messageEvent(menuItemExit.getMethod()));
        final BaseMenuItem menuItemUndo = new BaseMenuItem(null, UIComponentProperties.UNDO);
        menuItemUndo.addActionListener(_ -> messageEvent(menuItemUndo.getMethod()));
        final BaseMenuItem menuItemRedo = new BaseMenuItem(null, UIComponentProperties.REDO);
        menuItemRedo.addActionListener(_ -> messageEvent(menuItemRedo.getMethod()));
        final BaseMenuItem menuItemCut = new BaseMenuItem(null, UIComponentProperties.CUT);
        menuItemCut.addActionListener(_ -> messageEvent(menuItemCut.getMethod()));
        final BaseMenuItem menuItemCopy = new BaseMenuItem(null, UIComponentProperties.COPY);
        menuItemCopy.addActionListener(_ -> messageEvent(menuItemCopy.getMethod()));
        final BaseMenuItem menuItemPaste = new BaseMenuItem(null, UIComponentProperties.PASTE);
        menuItemPaste.addActionListener(_ -> messageEvent(menuItemPaste.getMethod()));
        final BaseMenuItem menuItemFind = new BaseMenuItem(null, UIComponentProperties.FIND);
        menuItemFind.addActionListener(_ -> messageEvent(menuItemFind.getMethod()));

        final BaseMenuItem menuItemReplace = new BaseMenuItem(null, UIComponentProperties.REPLACE);
        menuItemReplace.addActionListener(_ -> messageEvent(menuItemReplace.getMethod()));
        final BaseMenuItem menuItemContents = new BaseMenuItem(null, UIComponentProperties.CONTENTS);
        menuItemContents.addActionListener(_ -> messageEvent(menuItemContents.getMethod()));
        final BaseMenuItem menuItemAboutHelp = new BaseMenuItem(null, UIComponentProperties.ABOUT);
        menuItemAboutHelp.addActionListener(_ -> messageEvent(menuItemAboutHelp.getMethod()));
        menuItemUtils.setText("Utils");
        menuItemUtils.setEnabled(false);
        fileMenu.add(menuItemNew);
        fileMenu.add(menuItemOpen);
        fileMenu.add(menuItemSave);
        fileMenu.add(menuItemSaveAs);
        fileMenu.addSeparator();
        fileMenu.add(menuItemPrint);
        fileMenu.add(menuItemPrintPreview);
        fileMenu.add(menuItemPrintSetUp);
        fileMenu.addSeparator();
        fileMenu.add(menuItemExit);
        editMenu.add(menuItemUndo);
        editMenu.add(menuItemRedo);
        editMenu.addSeparator();
        editMenu.add(menuItemCut);
        editMenu.add(menuItemCopy);
        editMenu.add(menuItemPaste);
        editMenu.addSeparator();
        editMenu.add(menuItemFind);
        editMenu.add(menuItemReplace);
        editMenu.add(menuItemGoto);
        helpMenu.add(menuItemContents);
        helpMenu.add(menuItemAboutHelp);
        this.add(jPanelMenuPanel, BorderLayout.NORTH);
        this.add(jPanelToolPanel, BorderLayout.SOUTH);
        jPanelToolPanel.add(jPanel1, BorderLayout.WEST);
        jPanel1.add(jToolBarMain, BorderLayout.CENTER);
        jToolBarMain.add(buttonPanel, BorderLayout.NORTH);
        jPanelMenuPanel.add(menuBar, BorderLayout.NORTH);

    }

    /**
     * getFrame ()
     *
     * @return JFrame * @exception * @see
     */
    private JFrame getFrame() {
        if (frame == null) {
            Component parent = this;

            while ((parent != null) && !(parent instanceof JFrame)) {
                parent = parent.getParent();
            }

            frame = (JFrame) parent;
        }

        return frame;
    }

    /**
     * addMessageListener (IMessageListener listener)
     *
     * @param listener IMessageListener
     */
    public void addMessageListener(IMessageListener listener) {
        notifier.add(listener);
    }

    /**
     * removeMessageListener (IMessageListener listener)
     *
     * @param listener IMessageListener
     */
    public void removeMessageListener(IMessageListener listener) {
        notifier.remove(listener);
    }

    /**
     * getWindowsOpenMenu (IMessageListener listener)
     *
     * @return JMenuItem * @exception * @see
     */
    public JMenu getWindowsOpenMenu() {
        return windowMenu;
    }

    /**
     * Method getButtonPanel.
     *
     * @return JPanel
     */
    public JPanel getButtonPanel() {
        return buttonPanel;
    }

    /**
     * jPanelToolPanel_componentChanged(ContainerEvent e)
     */
    private void jPanelToolPanel_componentChanged() {
        if (getFrame() != null) {
            getFrame().validate();
        }
    }

    /**
     * messageEvent(String selection)
     *
     * @param selection String
     */
    public void messageEvent(String selection) {
        notifier.notifyEvent(new MessageEvent(selection), new ArrayList<>());
    }

    /**
     * messageEvent(String selection)
     *
     * @param enable boolean
     */
    public void enableSave(boolean enable) {
        menuItemSave.setEnabled(enable);
        menuItemSaveAs.setEnabled(enable);
        saveButton.setEnabled(enable);
    }
}
