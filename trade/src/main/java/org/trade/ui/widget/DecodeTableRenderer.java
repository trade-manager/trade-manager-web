package org.trade.ui.widget;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.io.Serial;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DecodeTableRenderer extends DefaultTableCellRenderer {

    @Serial
    private static final long serialVersionUID = -3000452614626592712L;

    public DecodeTableRenderer() {
        super();
        setHorizontalAlignment(SwingConstants.LEFT);
    }
}
