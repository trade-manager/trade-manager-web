/* ===========================================================
 * TradeManager : An application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.ui.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.base.BaseButton;
import org.trade.base.BasePanel;
import org.trade.base.BaseUIPropertyCodes;
import org.trade.base.TableModel;
import org.trade.base.TextDialog;
import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.factory.ClassFactory;
import org.trade.core.lookup.DBTableLookupServiceProvider;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.CodeType;
import org.trade.core.persistent.dao.CodeValue;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.series.indicator.IndicatorSeries;
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
import java.util.ListIterator;
import java.util.Objects;
import java.util.Vector;

/**
 *
 */
public class ConfigurationPanel extends BasePanel {

    @Serial
    private static final long serialVersionUID = 8543984162821384818L;

    private TradeService tradeService;
    private final static Logger _log = LoggerFactory.getLogger(ConfigurationPanel.class);
    private JScrollPane m_jScrollPane = null;
    private final JScrollPane m_jScrollPane1 = new JScrollPane();
    private ConfigurationTable m_table = null;
    private AspectTableModel m_tableModel = null;
    private Aspects m_aspects = null;

    private ConfigurationTable m_tableChild = null;
    private TableModel m_tableModelChild = null;
    private BaseButton propertiesButton = null;
    private DecodeComboBoxEditor refTableEditorComboBox = null;

    /**
     * Constructor
     *
     * @param tradeService TradeService
     */

    public ConfigurationPanel(TradeService tradeService) {
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
            this.tradeService = tradeService;
            m_jScrollPane = new JScrollPane();
            propertiesButton = new BaseButton(this, BaseUIPropertyCodes.PROPERTIES, 0);
            propertiesButton.setEnabled(false);
            JLabel refTable = new JLabel("Configuration:");
            refTableEditorComboBox = new DecodeComboBoxEditor(ReferenceTable.newInstance().getCodesDecodes());
            DecodeComboBoxRenderer refTableRenderer = new DecodeComboBoxRenderer();
            refTableEditorComboBox.setRenderer(refTableRenderer);
            refTableEditorComboBox.addItemListener(e -> {

                if (e.getStateChange() == ItemEvent.SELECTED) {

                    if (null != m_tableChild) {

                        m_jScrollPane1.getViewport().remove(m_tableChild);
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

            jPanel4.add(m_jScrollPane, BorderLayout.CENTER);
            JScrollPane jScrollPane1 = new JScrollPane();
            jScrollPane1.getViewport().add(jPanel4, BorderLayout.NORTH);
            jScrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED));

            jPanel2.add(m_jScrollPane1, BorderLayout.CENTER);
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
        if (m_aspects.isDirty()) {
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
            int selectedRow = m_table.getSelectedRow();
            String className = "org.trade.persistent.dao."
                    + ((ReferenceTable) Objects.requireNonNull(refTableEditorComboBox.getSelectedItem())).getCode();

            for (ListIterator<Aspect> itemIter = m_aspects.getAspect().listIterator(); itemIter.hasNext(); ) {
                Aspect item = itemIter.next();
                if (item.isDirty()) {
                    item = tradeService.persistAspect(item);
                }

                /*
                 * Replace the aspect with the mergedAspect then update the
                 * tables and select the row for the saved data.
                 */
                itemIter.set(item);
            }
            m_aspects.setDirty(false);
            Aspects aspects = tradeService.findAspectsByClassName(className);
            for (Aspect currAspect : aspects.getAspect()) {
                boolean exists = false;
                for (Aspect aspect : m_aspects.getAspect()) {
                    if (currAspect.getId().equals(aspect.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists)
                    tradeService.deleteAspect(currAspect);
            }
            DBTableLookupServiceProvider.clearLookup();
            doRefresh();
            if (selectedRow > -1)
                m_table.setRowSelectionInterval(selectedRow, selectedRow);
            this.setStatusBarMessage(
                    "Save complete. Note for changed to take effect the Tradinday Tab Search must be re-run.",
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
            CodeType codeType = tradeService.findCodeTypeByNameType(indicatorName,
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

                    Aspect transferObject = m_tableModel.getData().getAspect()
                            .get(m_table.convertRowIndexToModel(model.getLeadSelectionIndex()));
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

            m_aspects = tradeService.findAspectsByClassName("org.trade.persistent.dao." + refTableClass);
            Vector<Object> parm = new Vector<>();
            m_tableModel = (AspectTableModel) ClassFactory
                    .getCreateClass("org.trade.ui.models." + refTableClass + "TableModel", parm, this);
            m_tableModel.setData(m_aspects);
            m_table = new ConfigurationTable(m_tableModel);
            m_table.setFont(new Font("Monospaced", Font.PLAIN, 12));
            m_table.setPreferredScrollableViewportSize(new Dimension(300, 200));
            m_table.setFillsViewportHeight(true);
            m_table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            m_table.getSelectionModel().addListSelectionListener(new TableRowListener());
            m_jScrollPane.getViewport().add(m_table, BorderLayout.CENTER);
            m_jScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
            m_jScrollPane.addMouseListener(m_table);

            if (!m_aspects.getAspect().isEmpty()) {

                m_table.setRowSelectionInterval(0, 0);
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

                    IndicatorSeries transferObject = ((IndicatorSeriesTableModel) m_tableModelChild).getData()
                            .getIndicatorSeries()
                            .get(m_tableChild.convertRowIndexToModel(model.getLeadSelectionIndex()));
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
                    m_tableModelChild = new IndicatorSeriesTableModel();
                    ((IndicatorSeriesTableModel) m_tableModelChild).setData(strategy);
                    m_tableChild = new ConfigurationTable(m_tableModelChild);

                    m_tableChild.getSelectionModel().addListSelectionListener(new IndicatorSeriesTableRowListener());
                    m_tableChild.setDefaultRenderer(Aspects.class, new ButtonRenderer(BaseUIPropertyCodes.PROPERTIES));
                    m_tableChild.setDefaultEditor(Aspects.class, new ButtonEditor(propertiesButton));
                }
                case CodeType codeType -> {
                    m_tableModelChild = new CodeAttributeTableModel();
                    ((CodeAttributeTableModel) m_tableModelChild).setData(codeType);
                    m_tableChild = new ConfigurationTable(m_tableModelChild);
                }
                case Portfolio portfolio -> {
                    m_tableModelChild = new AccountTableModel();
                    ((AccountTableModel) m_tableModelChild).setData(portfolio);
                    m_tableChild = new ConfigurationTable(m_tableModelChild);
                }
                case null, default -> m_tableChild = new ConfigurationTable(null);
            }
            m_tableChild.setFont(new Font("Monospaced", Font.PLAIN, 12));
            m_tableChild.setPreferredScrollableViewportSize(new Dimension(300, 200));
            m_tableChild.setFillsViewportHeight(true);
            m_tableChild.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

            m_jScrollPane1.getViewport().add(m_tableChild, BorderLayout.CENTER);
            m_jScrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED));
        } catch (Exception ex) {
            this.setErrorMessage("Error deleting Strategy.", ex.getMessage(), ex);
        }
    }

}
