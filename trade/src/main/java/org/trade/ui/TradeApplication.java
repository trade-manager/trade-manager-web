package org.trade.ui;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.trade.base.ImageBuilder;
import org.trade.base.WaitCursorEventQueue;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.persistent.TradeService;

import java.awt.*;

@SpringBootApplication(scanBasePackages = {"org.trade.core", "org.trade.ui"})
@ConfigurationPropertiesScan("org.trade.core")
public class TradeApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(TradeApplication.class)
                .initializers(new ApplicationProfileInitializer()).headless(false).run(args);
    }

    @Component
    class SwingApp {


        public final TradeService tradeService;

        public SwingApp(TradeService tradeService) {

            this.tradeService = tradeService;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            double appWidth = screenSize.getWidth() * 0.9;
            double appHieght = screenSize.getHeight() * 0.9;

            if (appHieght > 900) {

                appHieght = 900;
            }

            if (appWidth > 1200) {

                appWidth = 1200;
            }

            TradeAppFrame frame = new TradeAppFrame(tradeService);
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


    public void run(String... args) {
    }
}
