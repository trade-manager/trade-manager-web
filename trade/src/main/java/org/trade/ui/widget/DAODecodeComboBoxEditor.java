package org.trade.ui.widget;

import org.trade.core.valuetype.DAODecode;
import org.trade.core.valuetype.Decode;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Id: DecodeComboBoxEditor.java,v 1.2 2001/11/06 17:14:47 simon Exp $
 */

public class DAODecodeComboBoxEditor extends JComboBox<Decode> implements ComboBoxEditor, ItemListener, FocusListener {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -1626795772462262674L;

    protected transient DAODecode originalValue;
    protected transient List<ActionListener> listeners;

    /**
     * Constructor for DAODecodeComboBoxEditor.
     *
     * @param model List<?>
     */
    public DAODecodeComboBoxEditor(List<Decode> model) {
        super(model.toArray(new Decode[0]));
        this.addItemListener(this);
        this.addFocusListener(this);
        listeners = new ArrayList<>();
    }

    /**
     * Return the component that should be added to the tree hierarchy for this
     * editor
     *
     * @return Component
     * @see javax.swing.ComboBoxEditor#getEditorComponent()
     */
    public Component getEditorComponent() {
        return this;
    }

    /**
     * Set the item that should be edited. Cancel any editing if necessary
     * * @param anObject Object
     *
     * @see javax.swing.ComboBoxEditor#setItem(Object)
     */
    public void setItem(Object anObject) {
        for (int i = 0; i < this.getItemCount(); i++) {
            DAODecode d = (DAODecode) this.getItemAt(i);
            if (d.getCode().equals(((DAODecode) anObject).getCode())) {
                setSelectedItem(d);
                break;
            }
        }
    }

    /**
     * Return the edited item * @return Object
     *
     * @see javax.swing.ComboBoxEditor#getItem()
     */
    public Object getItem() {
        return getSelectedItem();
    }

    /**
     * Ask the editor to start editing and to select everything * @see
     * javax.swing.ComboBoxEditor#selectAll()
     */
    public void selectAll() {
    }

    /**
     * Add an ActionListener. An action event is generated when the edited item
     * changes
     *
     * @param l ActionListener
     * @see javax.swing.ComboBoxEditor#addActionListener(ActionListener)
     */
    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }

    /**
     * Remove an ActionListener * @param l ActionListener
     *
     * @see javax.swing.ComboBoxEditor#removeActionListener(ActionListener)
     */
    public void removeActionListener(ActionListener l) {
        listeners.remove(l);
    }

    protected void fireEditingCanceled() {
        for (int i = 0; i < this.getItemCount(); i++) {
            DAODecode d = (DAODecode) this.getItemAt(i);
            if (d.equals(originalValue)) {
                setSelectedItem(originalValue);
                break;
            }
        }

        ChangeEvent ce = new ChangeEvent(this);
        for (int i = listeners.size(); i >= 0; i--) {
            ((CellEditorListener) listeners.get(i)).editingCanceled(ce);
        }
    }

    protected void fireEditingStopped() {
        ChangeEvent ce = new ChangeEvent(this);
        for (int i = listeners.size() - 1; i >= 0; i--) {
            ((CellEditorListener) listeners.get(i)).editingStopped(ce);
        }
    }

    /**
     * Method itemStateChanged.
     *
     * @param evt ItemEvent
     * @see java.awt.event.ItemListener#itemStateChanged(ItemEvent)
     */
    public void itemStateChanged(ItemEvent evt) {
        fireEditingStopped();
    }

    /**
     * Method focusGained.
     *
     * @param evt FocusEvent
     * @see java.awt.event.FocusListener#focusGained(FocusEvent)
     */
    public void focusGained(FocusEvent evt) {
    }

    /**
     * Method focusLost.
     *
     * @param evt FocusEvent
     * @see java.awt.event.FocusListener#focusLost(FocusEvent)
     */
    public void focusLost(FocusEvent evt) {
    }
}
