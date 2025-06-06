/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.ui.widget;

import org.trade.core.valuetype.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.io.Serial;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Locale;

/**
 * @author Simon Allen
 * @version $Id: DateField.java,v 1.5 2001/12/28 21:14:55 simon Exp $
 */
public class DateField extends JTextField {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6932225666197539617L;

    private static final Hashtable<Integer, Character> editMask = new Hashtable<>();

    private final SimpleDateFormat dateFormat;
    private final Color originalColor;

    /**
     * Constructor for DateField.
     *
     * @param mask String
     */
    public DateField(String mask) {
        super(mask);
        originalColor = this.getBackground();
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));
        dateFormat = new SimpleDateFormat(mask, Locale.getDefault());
        dateFormat.setLenient(false);
    }

    /**
     * Method createDefaultModel.
     *
     * @return Document
     */
    protected Document createDefaultModel() {
        return new DateDocument();
    }

    /**
     * Method getDate.
     *
     * @return Date
     */
    public Date getDate() {
        try {
            return new Date(dateFormat.parse(this.getText().trim()));
        } catch (ParseException e) {
            return (Date.NULLIPDATE);
        }
    }

    /**
     * Method setDate.
     *
     * @param date Date
     */
    public void setDate(Date date) {
        if (date.equals(Date.NULLIPDATE)) {
            super.setText("");
        } else {
            super.setText(dateFormat.format(date.getDate()));
        }
    }

    /**
     * Method setDate.
     *
     * @param date java.util.Date
     */
    public void setDate(java.util.Date date) {
        if (date.equals(Date.NULLIPDATE)) {
            super.setText("");
        } else {
            super.setText(dateFormat.format(date));
        }
    }

    /**
     * Method isValid.
     *
     * @return boolean
     */
    public boolean isValid() {
        boolean isValid = super.isValid();

        if (isValid) {
            String dateText = this.getText().trim();

            if (!dateText.isEmpty()) {
                try {
                    dateFormat.parse(dateText);
                    this.setBackground(originalColor);
                } catch (ParseException ep) {
                    this.setBackground(Color.red);

                    isValid = false;
                }
            }
        }

        return isValid;
    }

    /**
     *
     */
    static class DateDocument extends PlainDocument {
        /**
         *
         */
        @Serial
        private static final long serialVersionUID = -498598784381540618L;

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

            if (editMask.isEmpty()) {
                char[] maskChars = str.toCharArray();

                for (int i = 0; i < maskChars.length; i++) {
                    editMask.put(i, maskChars[i]);
                }
            }
            String mask2 = null;
            for (int i = 0; i < editMask.size(); i++) {
                Character mask1 = editMask.get(i);
                if (null == mask2) {
                    mask2 = mask1.toString();
                } else {
                    mask2 = mask2 + mask1.toString();
                }

            }

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
