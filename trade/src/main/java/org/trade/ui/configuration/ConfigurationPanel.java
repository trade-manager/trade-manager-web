package org.trade.ui.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.base.BaseButton;
import org.trade.base.BasePanel;
import org.trade.base.BaseUIPropertyCodes;
import org.trade.base.TableModel;
import org.trade.base.TextDialog;
import org.trade.core.aspect.Aspect;
import org.trade.core.aspect.Aspects;
import org.trade.core.factory.ClassFactory;
import org.trade.core.lookup.DBTableLookupServiceProvider;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.codetype.CodeType;
import org.trade.core.persistent.codetype.CodeValue;
import org.trade.core.persistent.dao.series.indicator.IndicatorSeries;
import org.trade.core.persistent.portfolio.Portfolio;
import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.valuetype.DAOEntryLimit;
import org.trade.core.valuetype.ReferenceTable;
import org.trade.ui.models.AccountTableModel;
import org.trade.ui.models.AspectTableModel;
import org.trade.ui.models.CodeAttributeTableModel;
import org.trade.ui.models.IndicatorSeriesTableModel;
import org.trade.ui.tables.ConfigurationTable;
import org.trade.ui.widget.ButtonEditor;
import org.trade.ui.widget.ButtonRenderer;
import org.trade.ui.widget.DecodeComboBoxEditor;
import org.trade.ui.widget.DecodeComboBoxRenderer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import static org.trade.core.persistent.TradeService.PERSISTENT_PACKAGE;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class ConfigurationPanel extends BasePanel {

    @Serial
    private static final long serialVersionUID = 8543984162821384818L;

    private final TradeService tradeService;
    private final static Logger _log = LoggerFactory.getLogger(ConfigurationPanel.class);
    private JScrollPane jScrollPane = null;
    private final JScrollPane jScrollPane1 = new JScrollPane();
    private ConfigurationTable configTable = null;
    private AspectTableModel tableModel = null;
    private Aspects aspects = null;
    private ConfigurationTable tableChild = null;
    private TableModel tableModelChild = null;
    private BaseButton propertiesButton = null;
    private DecodeComboBoxEditor refTableEditorComboBox = null;

    /**
     * Constructor
     */

    public ConfigurationPanel(final TradeService tradeService) {

        this.tradeService = tradeService;
        try {

            if (null != getMenu()) {

                getMenu().addMessageListener(this);
            }

            this.setLayout(new BorderLayout());
            /*
             * Initialize the ValueType decode tables. This caused the tables to
             * be cached.
             */
            DAOEntryLimit.newInstance();
            jScrollPane = new JScrollPane();
            propertiesButton = new BaseButton(this, BaseUIPropertyCodes.PROPERTIES, 0);
            propertiesButton.setEnabled(false);
            JLabel refTable = new JLabel("Configuration:");
            refTableEditorComboBox = new DecodeComboBoxEditor(ReferenceTable.newInstance().getCodesDecodes());
            DecodeComboBoxRenderer refTableRenderer = new DecodeComboBoxRenderer();
            refTableEditorComboBox.setRenderer(refTableRenderer);
            refTableEditorComboBox.addItemListener(e -> {

                if (e.getStateChange() == ItemEvent.SELECTED) {

                    if (null != tableChild) {

                        jScrollPane1.getViewport().remove(tableChild);
                    }
                    addReferenceTablePanel(((ReferenceTable) e.getItem()).getCode());
                }
            });

            JPanel jPanel2 = new JPanel(new BorderLayout());
            JPanel jPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel jPanel4 = new JPanel(new BorderLayout());

            jPanel3.add(refTable, null);
            jPanel3.add(refTableEditorComboBox, null);
            jPanel3.setBorder(new BevelBorder(BevelBorder.RAISED));
            JToolBar jToolBar = new JToolBar();
            jToolBar.setLayout(new BorderLayout());
            jToolBar.add(jPanel3, BorderLayout.WEST);

            jPanel4.add(jScrollPane, BorderLayout.CENTER);
            JScrollPane jScrollPane1 = new JScrollPane();
            jScrollPane1.getViewport().add(jPanel4, BorderLayout.NORTH);
            jScrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED));

            jPanel2.add(this.jScrollPane1, BorderLayout.CENTER);
            JSplitPane jSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, jPanel4, jPanel2);
            jSplitPane1.setResizeWeight(0.2d);
            jSplitPane1.setOneTouchExpandable(true);
            this.add(jToolBar, BorderLayout.NORTH);
            this.add(jSplitPane1, BorderLayout.CENTER);

        } catch (Exception ex) {

            this.setErrorMessage("Error during initialization.", ex.getMessage(), ex);
        }
    }

    public void doWindowActivated() {
    }

    /**
     * Method doWindowDeActivated.
     *
     * @return boolean
     */
    public boolean doWindowDeActivated() {

        if (aspects.isDirty()) {

            setStatusBarMessage("Please Save or Refresh as changed are pending", BasePanel.WARNING);
            return false;
        }

        return true;
    }

    /**
     * Method doWindowClose.
     */

    public void doWindowClose() {
    }

    /**
     * Method doWindowOpen.
     */
    public void doWindowOpen() {

        try {

            this.addReferenceTablePanel(ReferenceTable.newInstance().getCode());
        } catch (Exception ex) {

            this.setErrorMessage("Error during initiaization.", ex.getMessage(), ex);
        }
    }

    /**
     * Method doSave.This is fired when the Save button is pressed.
     */

    public void doSave() {

        try {

            this.setStatusBarMessage("Save in progress ...", BasePanel.INFORMATION);
            int selectedRow = configTable.getSelectedRow();
            String className = PERSISTENT_PACKAGE + ((ReferenceTable) Objects.requireNonNull(refTableEditorComboBox.getSelectedItem())).getCode();

            for (ListIterator<Aspect> itemIter = aspects.getAspects().listIterator(); itemIter.hasNext(); ) {

                Aspect item = itemIter.next();

                if (item.isDirty()) {

                    item = tradeService.getAspectService().save(item);

                    /*
                     * Replace the aspect with the mergedAspect then update the
                     * tables and select the row for the saved data.
                     */
                    itemIter.set(item);
                }
            }

            aspects.setDirty(false);
            Aspects aspects = tradeService.getAspectService().findByClassName(className);

            for (Aspect currAspect : aspects.getAspects()) {

                boolean exists = false;

                for (Aspect aspect : this.aspects.getAspects()) {

                    if (currAspect.getId().equals(aspect.getId())) {

                        exists = true;
                        break;
                    }
                }

                if (!exists) {

                    tradeService.getAspectService().delete(currAspect);
                }
            }

            DBTableLookupServiceProvider.clearLookup();
            doRefresh();

            if (selectedRow > -1) {

                configTable.setRowSelectionInterval(selectedRow, selectedRow);
            }

            this.setStatusBarMessage(
                    "Save complete. Note for changed to take effect the Tradingday Tab Search must be re-run.",
                    BasePanel.INFORMATION);
        } catch (Exception ex) {

            this.setErrorMessage("Error saving item.", ex.getMessage(), ex);
        }
    }

    /**
     * Method doSearch This is fired when the Search button is pressed.
     */
    public void doSearch() {
        doRefresh();
    }

    /**
     * Method doRefresh This is fired when the Refresh button is pressed.
     */
    public void doRefresh() {

        try {

            this.addReferenceTablePanel(((ReferenceTable) Objects.requireNonNull(refTableEditorComboBox.getSelectedItem())).getCode());
        } catch (Exception ex) {

            this.setErrorMessage("Error finding item.", ex.getMessage(), ex);
        } finally {

            clearStatusBarMessage();
        }
    }

    /**
     * Method doOpen This is fired when the tool-bar File open button is pressed
     * or the main menu Open File.
     */
    public void doOpen() {

    }

    /**
     * Method doProperties.
     *
     * @param series IndicatorSeries
     */
    public void doProperties(final IndicatorSeries series) {

        try {

            this.clearStatusBarMessage();
            String indicatorName = series.getType().substring(0, series.getType().indexOf("Series"));
            CodeType codeType = tradeService.getCodeTypeService().findByNameAndType(indicatorName,
                    CodeType.IndicatorParameters);

            if (null == codeType) {

                this.setStatusBarMessage("There are no properties for this Indicator ...", BasePanel.INFORMATION);
            } else {

                CodeAttributePanel codeAttributePanel = new CodeAttributePanel(codeType, series.getCodeValues());
                TextDialog dialog = new TextDialog(this.getFrame(), "Indicator Properties", true,
                        codeAttributePanel);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);

                if (!dialog.getCancel()) {

                    /*
                     * Populate the code values from the fields.
                     */
                    for (CodeValue value : codeAttributePanel.getCodeValues()) {

                        series.setDirty(true);

                        if (null == value.getIndicatorSeries()) {

                            value.setIndicatorSeries(series);
                        }
                    }
                }
            }
        } catch (Exception ex) {

            setErrorMessage("Error getting Indicator properties.", ex.getMessage(), ex);
        } finally {

            this.getFrame().setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     *
     */
    private class TableRowListener implements ListSelectionListener {

        /**
         * Method valueChanged.
         *
         * @param event ListSelectionEvent
         * @see ListSelectionListener#valueChanged(ListSelectionEvent)
         */

        public void valueChanged(ListSelectionEvent event) {

            if (!event.getValueIsAdjusting()) {

                ListSelectionModel model = (ListSelectionModel) event.getSource();

                if (model.getLeadSelectionIndex() > -1) {

                    Aspect transferObject = tableModel.getData().getAspects()
                            .get(configTable.convertRowIndexToModel(model.getLeadSelectionIndex()));
                    propertiesButton.setEnabled(false);
                    setChildPanel(transferObject);
                }
            }
        }
    }

    /**
     * Method addReferenceTablePanel.
     *
     * @param refTableClass String
     */
    private void addReferenceTablePanel(String refTableClass) {

        try {

            aspects = tradeService.getAspectService().findByClassName(PERSISTENT_PACKAGE + refTableClass);
            List<Object> params = new ArrayList<>();
            tableModel = (AspectTableModel) ClassFactory
                    .getCreateClass("org.trade.ui.models." + refTableClass + "TableModel", params, this);
            tableModel.setData(aspects);
            configTable = new ConfigurationTable(tableModel);
            configTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
            configTable.setPreferredScrollableViewportSize(new Dimension(300, 200));
            configTable.setFillsViewportHeight(true);
            configTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            configTable.getSelectionModel().addListSelectionListener(new TableRowListener());
            jScrollPane.getViewport().add(configTable, BorderLayout.CENTER);
            jScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
            jScrollPane.addMouseListener(configTable);

            if (!aspects.getAspects().isEmpty()) {

                configTable.setRowSelectionInterval(0, 0);
            }

        } catch (Exception ex) {
            this.setErrorMessage("Error deleting Strategy.", ex.getMessage(), ex);
        }
    }

    /**
     *
     */
    private class IndicatorSeriesTableRowListener implements ListSelectionListener {
        /**
         * Method valueChanged.
         *
         * @param event ListSelectionEvent
         * @see ListSelectionListener#valueChanged(ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent event) {

            if (!event.getValueIsAdjusting()) {

                ListSelectionModel model = (ListSelectionModel) event.getSource();

                if (model.getLeadSelectionIndex() > -1) {

                    IndicatorSeries transferObject = ((IndicatorSeriesTableModel) tableModelChild).getData()
                            .getIndicatorSeries()
                            .get(tableChild.convertRowIndexToModel(model.getLeadSelectionIndex()));
                    propertiesButton.setTransferObject(transferObject);
                    propertiesButton.setEnabled(true);
                }
            }
        }
    }

    /**
     * Method setChildPanel.
     *
     * @param aspect Aspect
     */
    private void setChildPanel(final Aspect aspect) {

        try {

            switch (aspect) {
                case Strategy strategy -> {

                    tableModelChild = new IndicatorSeriesTableModel();
                    ((IndicatorSeriesTableModel) tableModelChild).setData(strategy);
                    tableChild = new ConfigurationTable(tableModelChild);

                    tableChild.getSelectionModel().addListSelectionListener(new IndicatorSeriesTableRowListener());
                    tableChild.setDefaultRenderer(Aspects.class, new ButtonRenderer(BaseUIPropertyCodes.PROPERTIES));
                    tableChild.setDefaultEditor(Aspects.class, new ButtonEditor(propertiesButton));
                }
                case CodeType codeType -> {

                    tableModelChild = new CodeAttributeTableModel();
                    ((CodeAttributeTableModel) tableModelChild).setData(codeType);
                    tableChild = new ConfigurationTable(tableModelChild);
                }
                case Portfolio portfolio -> {

                    tableModelChild = new AccountTableModel();
                    ((AccountTableModel) tableModelChild).setData(portfolio);
                    tableChild = new ConfigurationTable(tableModelChild);
                }
                case null, default -> tableChild = new ConfigurationTable(null);
            }

            tableChild.setFont(new Font("Monospaced", Font.PLAIN, 12));
            tableChild.setPreferredScrollableViewportSize(new Dimension(300, 200));
            tableChild.setFillsViewportHeight(true);
            tableChild.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            jScrollPane1.getViewport().add(tableChild, BorderLayout.CENTER);
            jScrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED));
        } catch (Exception ex) {

            this.setErrorMessage("Error deleting Strategy.", ex.getMessage(), ex);
        }
    }

}
