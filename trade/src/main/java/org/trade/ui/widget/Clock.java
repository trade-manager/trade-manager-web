package org.trade.ui.widget;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class Clock extends JPanel {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2205177128289778533L;
    private final JTextField timeField = new JTextField(5); // set by timer listener
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public Clock() {
        // Build the GUI - only one panel
        this.setLayout(new BorderLayout());
        this.add(timeField);
        // Create a 1-second timer and action listener for it.
        // Specify package because there are two Timer classes
        javax.swing.Timer t = new javax.swing.Timer(1000, _ -> {
            Calendar now = Calendar.getInstance();
            timeField.setText(dateFormat.format(now.getTime()));
        });
        t.start();
    }
}