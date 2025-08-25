package org.trade.ui.widget;

import org.trade.core.valuetype.Decode;

import javax.swing.*;
import java.io.Serial;

/**
 *
 */
public class DecodeTableEditor extends DefaultCellEditor {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5749981558651703467L;

    /**
     * Constructor for DecodeTableEditor.
     *
     * @param comboBox JComboBox
     */
    public DecodeTableEditor(final JComboBox<Decode> comboBox) {
        super(comboBox);
    }
}
