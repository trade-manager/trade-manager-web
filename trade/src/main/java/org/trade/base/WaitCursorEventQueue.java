package org.trade.base;

import javax.swing.*;
import java.awt.*;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class WaitCursorEventQueue extends EventQueue {
    /**
     * Constructor for WaitCursorEventQueue.
     *
     * @param delay int
     */
    public WaitCursorEventQueue(int delay) {

        this.delay = delay;
        waitTimer = new WaitCursorTimer();
        waitTimer.setDaemon(true);
        waitTimer.start();
    }

    /**
     * Method dispatchEvent.
     *
     * @param event AWTEvent
     */
    protected void dispatchEvent(AWTEvent event) {

        waitTimer.startTimer(event.getSource());

        try {

            super.dispatchEvent(event);
        } finally {
            waitTimer.stopTimer();
        }
    }

    private final int delay;

    private final WaitCursorTimer waitTimer;

    /**
     *
     */
    private class WaitCursorTimer extends Thread {
        /**
         * Method startTimer.
         *
         * @param source Object
         */
        synchronized void startTimer(Object source) {
            this.source = source;

            notify();
        }

        synchronized void stopTimer() {
            if (parent == null) {
                interrupt();
            } else {
                parent.setCursor(null);

                parent = null;
            }
        }

        /**
         * Method run.
         *
         * @see java.lang.Runnable#run()
         */
        public synchronized void run() {
            while (true) {
                try { // wait for notification from startTimer()
                    wait();
                    // wait for event processing to reach the threshold, or
                    // interruption from stopTimer()
                    wait(delay);

                    if (source instanceof Component) {
                        parent = SwingUtilities.getRoot((Component) source);
                    } else if (source instanceof MenuComponent) {
                        MenuContainer mParent = ((MenuComponent) source).getParent();

                        if (mParent instanceof Component) {
                            parent = SwingUtilities.getRoot((Component) mParent);
                        }
                    }

                    if ((parent != null) && parent.isShowing()) {
                        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    }
                } catch (InterruptedException _) {
                }
            }
        }

        private Object source;
        private Component parent;
    }
}
