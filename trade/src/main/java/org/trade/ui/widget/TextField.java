package org.trade.ui.widget;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serial;
import java.util.Hashtable;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TextField extends JTextField implements FocusListener {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 339471596367850297L;

    private static final Hashtable<Integer, Character> editMask = new Hashtable<>();

    private static Color originalColor = null;

    /**
     * Constructor for TextField.
     *
     * @param mask String
     */
    public TextField(String mask) {
        super();

        originalColor = this.getBackground();

        this.addFocusListener(this);
        this.setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));

        char[] maskChars = mask.toCharArray();

        for (int i = 0; i < maskChars.length; i++) {
            editMask.put(i, maskChars[i]);
        }
    }

    /**
     * Method createDefaultModel.
     *
     * @return Document
     */
    protected Document createDefaultModel() {

        /*
         *
         * DocumentListener d = new DocumentListener() { public void
         * changedUpdate (DocumentEvent evt) { } public void insertUpdate
         * (DocumentEvent evt) { } public void removeUpdate (DocumentEvent evt)
         * { int i = evt.getOffset(); if (evt.getOffset() == 1 ||
         * evt.getOffset() == 4) { moveCursor(evt.getOffset()); } } };
         * doc.addDocumentListener(d);
         */

        return new TextDocument();
    }

    /**
     * Method getText.
     *
     * @return String
     */
    public String getText() {
        String text;

        text = super.getText();

        return text;
    }

    /**
     * Method focusGained.
     *
     * @param evt FocusEvent
     * @see java.awt.event.FocusListener#focusGained(FocusEvent)
     */
    public void focusGained(FocusEvent evt) {
        this.setSelectionStart(0);
        this.setSelectionEnd(0);
    }

    /**
     * Method focusLost.
     *
     * @param evt FocusEvent
     * @see java.awt.event.FocusListener#focusLost(FocusEvent)
     */
    public void focusLost(FocusEvent evt) {
        if (!(isValid())) {
            this.setSelectionStart(0);
            this.setSelectionEnd(0);
        }
    }

    /**
     * Method isValid.
     *
     * @return boolean
     */
    public boolean isValid() {

        if (!this.getText().trim().isEmpty()) {
            this.setBackground(Color.red);
            this.setBackground(originalColor);
        }

        this.repaint();

        return true;
    }

    /**
     * @author Simon Allen
     * @version $Id: TextField.java,v 1.2 2001/12/28 21:14:55 simon Exp $
     */
    static class TextDocument extends PlainDocument {
        /**
         *
         */
        @Serial
        private static final long serialVersionUID = -2258034828743548985L;

        /**
         * Method insertString.
         *
         * @param offs int
         * @param str  String
         * @param a    AttributeSet
         * @see javax.swing.text.Document#insertString(int, String,
         * AttributeSet)
         */
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str != null) {
                if (!(editMask.isEmpty())) {
                    Character selected = editMask.get(offs);

                    if (selected != null) {
                        if (!Character.isLetter(selected)) {

                            str = selected + str;
                        }
                    } else {
                        return;
                    }
                }
            } else {
                return;
            }

            char[] upper = str.toCharArray();

            for (int i = 0; i < upper.length; i++) {
                upper[i] = Character.toUpperCase(upper[i]);

                Character selected = editMask.get(offs + i);

                if (selected != null) {
                    if (Character.isLetter(selected)) {
                        if (!(Character.isDigit(upper[i]))) {
                            return;
                        }
                    }
                }
            }

            if (super.getLength() > offs) {
                super.remove(offs, upper.length);
            }

            super.insertString(offs, new String(upper), a);
        }
    }
}
