package org.trade.base;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.util.Objects;

/**
 * @author Simon Allen
 * @version $Id: TextDialog.java,v 1.3 2001/12/20 17:11:29 simon Exp $
 */
public class TextDialog extends JDialog {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3288606526317779365L;

    private String text = null;
    private boolean cancel = true;
    private final JComponent component;

    /**
     * Constructor for TextDialog.
     *
     * @param frame            Frame
     * @param title            String
     * @param modal            boolean
     * @param component        JComponent
     * @param oKButtonText     String
     * @param cancelButtonText String
     */
    public TextDialog(Frame frame, String title, boolean modal, JComponent component, String oKButtonText,
                      String cancelButtonText) {
        super(frame, title, modal);
        JButton okButton = new JButton("OK");
        if (null != oKButtonText)
            okButton.setText(oKButtonText);
        JButton cancelButton = new JButton("Cancel");
        if (null != cancelButtonText)
            cancelButton.setText(cancelButtonText);

        if (okButton.getText().length() > cancelButton.getText().length()) {
            cancelButton.setPreferredSize(okButton.getPreferredSize());
        } else {
            okButton.setPreferredSize(cancelButton.getPreferredSize());
        }
        this.component = Objects.requireNonNullElseGet(component, JTextArea::new);
        JScrollPane detailArea = new JScrollPane();
        okButton.addActionListener(_ -> {
            if (this.component instanceof JTextArea) {
                setText(((JTextArea) this.component).getText().trim());
            }
            setCancel(false);
            dispose();
        });
        cancelButton.addActionListener(_ -> {
            setCancel(true);
            dispose();
        });
        JPanel jPanel = new JPanel(new BorderLayout());
        JPanel jPanel1 = new JPanel();
        JPanel jPanel2 = new JPanel(new BorderLayout());
        JPanel jPanel3 = new JPanel(new GridLayout());

        jPanel1.add(okButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 5, 5));

        jPanel1.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));

        detailArea.getViewport().add(this.component, null);
        jPanel2.add(detailArea, BorderLayout.CENTER);
        jPanel1.add(jPanel3, BorderLayout.CENTER);
        jPanel.add(jPanel2, BorderLayout.CENTER);
        jPanel.add(jPanel1, BorderLayout.SOUTH);
        this.getContentPane().add(jPanel);
        pack();
    }

    /**
     * Constructor for TextDialog.
     *
     * @param frame     Frame
     * @param title     String
     * @param modal     boolean
     * @param component JComponent
     */
    public TextDialog(Frame frame, String title, boolean modal, JComponent component) {
        this(frame, title, modal, component, null, null);
    }

    /**
     * Constructor for TextDialog.
     *
     * @param frame Frame
     * @param title String
     */
    public TextDialog(Frame frame, String title) {
        this(frame, title, false, null, null, null);
    }

    /**
     * Constructor for TextDialog.
     *
     * @param frame Frame
     */
    public TextDialog(Frame frame) {
        this(frame, "", false, null, null, null);
    }

    /**
     * Method this_windowClosing.
     *
     * @param e WindowEvent
     */
    void this_windowClosing(WindowEvent e) {
        setCancel(true);
        dispose();
    }

    /**
     * Method getText.
     *
     * @return String
     */
    public String getText() {
        return text;
    }

    /**
     * Method setText.
     *
     * @param text String
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Method getCancel.
     *
     * @return String
     */
    public boolean getCancel() {
        return cancel;
    }

    /**
     * Method setCancel.
     *
     * @param cancel boolean
     */
    private void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
}
