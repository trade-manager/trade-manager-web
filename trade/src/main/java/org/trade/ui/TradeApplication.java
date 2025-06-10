package org.trade.ui;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.trade.base.ImageBuilder;
import org.trade.base.WaitCursorEventQueue;

import java.awt.*;


@SpringBootApplication
public class TradeApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(TradeApplication.class)
                .headless(false).run(args);
        SwingApp frame = ctx.getBean(SwingApp.class);
    }

    @Component
    static class SwingApp {

        public SwingApp() {
            // SwingUtilities.invokeLater(TradeAppFrame::new);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            double appWidth = screenSize.getWidth() * 0.9;
            double appHieght = screenSize.getHeight() * 0.9;
            if (appHieght > 900)
                appHieght = 900;

            if (appWidth > 1200)
                appWidth = 1200;

            TradeAppFrame frame = new TradeAppFrame();
            frame.setIconImage(ImageBuilder.getImage("trade.gif"));
            frame.setSize((int) appWidth, (int) appHieght);
            frame.setLocation((int) ((screenSize.getWidth() - frame.getSize().getWidth()) / 2),
                    (int) ((screenSize.getHeight() - frame.getSize().getHeight()) / 2));
            frame.validate();
            frame.repaint();
            frame.setVisible(true);
            EventQueue waitQue = new WaitCursorEventQueue(500);
            Toolkit.getDefaultToolkit().getSystemEventQueue().push(waitQue);
        }
    }
}
