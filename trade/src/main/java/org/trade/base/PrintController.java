package org.trade.base;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

/**
 * @author Simon Allen
 * @version $Id: PrintController.java,v 1.3 2001/10/22 18:57:58 simon Exp $
 */
public class PrintController {

    Properties props = new Properties();

    public PrintController() {
    }

    /**
     * printComponent() - constructor
     *
     * @param frame        Frame
     * @param comp         Component
     * @param printJobName String
     */
    public void printComponent(Frame frame, Component comp, String printJobName) {
        if (printJobName == null) {
            printJobName = comp.getClass().getName();
        }

        if ((frame != null) && (comp != null)) {
            PrintJob pj = Toolkit.getDefaultToolkit().getPrintJob(frame, printJobName, props);

            if (pj != null) {
                Graphics g = pj.getGraphics();
                Dimension od = comp.getSize();
                Dimension pd = pj.getPageDimension();

                g.translate((pd.width - od.width) / 2, (pd.height - od.height) / 2);

                if (comp instanceof JFrame) {
                    comp.printAll(g);
                } else {
                    comp.print(g);
                }

                g.dispose();
                pj.end();
            }
        }
    }
}
