package org.trade.ui.tradingday;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.tradingday.Tradingday;
import org.trade.core.persistent.tradingday.Tradingdays;
import org.trade.core.valuetype.ValueTypeException;
import org.trade.ui.models.TradingdayTableModel;
import org.trade.ui.tables.TradingdayTable;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Some tests for the  DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class TradingdayPanelIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(TradingdayPanelIT.class);

    private static Tradestrategy tradestrategy;
    private static final String symbol = "IBM-" + TradestrategyBase.getRandomNumber(4);

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() {
        System.setProperty("java.awt.headless", "false");
    }

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        this.deleteRecords();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void replaceTradingday() {

        Tradingdays tradingdays = new Tradingdays();

        Tradingday instance1 = this.tradeService.getTradingdayService()
                .findById(tradestrategy.getTradingday().getId());
        tradingdays.add(instance1);

        TradingdayTableModel tradingdayModel = new TradingdayTableModel();
        tradingdayModel.setData(tradingdays);
        TradingdayTable tradingdayTable = null;
        try {

            tradingdayTable = new TradingdayTable(tradingdayModel);
        } catch (ValueTypeException ex) {

            fail("Failed to create tradingdayTable msg: " + ex.getMessage());
        }
        tradingdayTable.setRowSelectionInterval(0, 0);

        tradestrategy.getContract().setIndustry("Computer");
        Contract result = this.tradeService.getAspectService().save(tradestrategy.getContract());
        assertNotNull(result);
        Tradingday instance2 = this.tradeService.getTradingdayService()
                .findById(tradestrategy.getTradingday().getId());
        tradingdays.replaceTradingday(instance2);
        int selectedRow = tradingdayTable.getSelectedRow();
        tradingdayModel.setData(tradingdays);

        if (selectedRow > -1) {

            tradingdayTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
        org.trade.core.valuetype.Date openDate = (org.trade.core.valuetype.Date) tradingdayModel
                .getValueAt(tradingdayTable.convertRowIndexToModel(0), 0);
        org.trade.core.valuetype.Date closeDate = (org.trade.core.valuetype.Date) tradingdayModel
                .getValueAt(tradingdayTable.convertRowIndexToModel(0), 1);
        Tradingday transferObject = tradingdayModel.getData().getTradingday(openDate.getZonedDateTime(),
                closeDate.getZonedDateTime());
        assertNotNull(transferObject);

        assertNotNull(tradingdays.getTradingday(instance1.getOpen(), instance1.getClose()));
        String industry = transferObject.getTradestrategies().getFirst().getContract().getIndustry();
        assertNotNull("4", industry);
    }
}
