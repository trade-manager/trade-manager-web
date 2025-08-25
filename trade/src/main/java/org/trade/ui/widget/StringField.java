package org.trade.ui.widget;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Id: StringField.java,v 1.2 2001/12/28 21:14:55 simon Exp $
 */
public class StringField extends JFormattedTextField implements FocusListener {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3791332898190722115L;

    /**
     * Constructor for StringField.
     *
     * @param mask            MaskFormatter
     * @param validCharacters String
     * @param placeHolder     String
     */
    public StringField(MaskFormatter mask, String validCharacters, String placeHolder) {
        super(mask);
        if (null != validCharacters)
            ((MaskFormatter) this.getFormatter()).setValidCharacters(validCharacters);
        if (null != placeHolder)
            ((MaskFormatter) this.getFormatter()).setPlaceholderCharacter(placeHolder.charAt(0));
        this.setHorizontalAlignment(SwingConstants.LEFT);
        this.setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));
        this.addFocusListener(this);
    }

    public StringField() {
        super();
        this.setHorizontalAlignment(SwingConstants.LEFT);
        this.setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));
    }

    /**
     * Constructor for StringField.
     *
     * @param columns int
     */
    public StringField(int columns) {
        super();
        this.setColumns(columns);
        this.setHorizontalAlignment(SwingConstants.LEFT);
        this.setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));
    }

    /**
     * Called when one of the fields gets the focus so that we can select the
     * focused field.
     *
     * @param e FocusEvent
     * @see java.awt.event.FocusListener#focusGained(FocusEvent)
     */
    public void focusGained(FocusEvent e) {
        Component c = e.getComponent();
        if (c instanceof JFormattedTextField) {
            selectItLater(c);
        }
    }

    // Workaround for formatted text field focus side effects.

    /**
     * Method selectItLater.
     *
     * @param c Component
     */
    protected void selectItLater(Component c) {
        if (c instanceof JFormattedTextField ftf) {
            SwingUtilities.invokeLater(ftf::selectAll);
        }
    }

    // Needed for FocusListener interface.

    /**
     * Method focusLost.
     *
     * @param e FocusEvent
     * @see java.awt.event.FocusListener#focusLost(FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        // ignore
    }
}
