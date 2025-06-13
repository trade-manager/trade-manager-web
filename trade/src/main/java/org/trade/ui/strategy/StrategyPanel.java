/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
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
package org.trade.ui.strategy;

import de.sciss.syntaxpane.DefaultSyntaxKit;
import org.trade.base.BaseButton;
import org.trade.base.BasePanel;
import org.trade.base.BaseUIPropertyCodes;
import org.trade.base.StreamEditorPane;
import org.trade.base.Tree;
import org.trade.base.UIPropertyCodes;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.ServiceException;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Rule;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.series.indicator.CandleDataset;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.strategy.IStrategyRule;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.DynamicCode;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.SECType;
import org.trade.core.valuetype.ValueTypeException;
import org.trade.ui.models.StrategyTreeModel;
import org.trade.ui.tables.renderer.StrategyTreeCellRenderer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serial;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 *
 */
public class StrategyPanel extends BasePanel implements TreeSelectionListener {

    @Serial
    private static final long serialVersionUID = 4053737356695023777L;

    private static final String TEMP_DIR = "temp";

    private Tree m_tree = null;
    private JEditorPane sourceText = null;
    private JTextArea commentText = null;
    private StreamEditorPane messageText = null;
    private BaseButton compileButton = null;
    private BaseButton newButton = null;
    private StrategyTreeModel strategyTreeModel = null;
    private TradeService tradeService;
    private String m_strategyDir = null;
    private DynamicCode dynacode = null;
    private List<Strategy> strategies = null;
    private Rule currentRule = null;
    private SimpleAttributeSet colorRedAttr = null;


    public StrategyPanel(TradeService tradeService) {
        try {

            if (null != getMenu()) {
                getMenu().addMessageListener(this);
            }

            this.setLayout(new BorderLayout());
            this.tradeService = tradeService;
            colorRedAttr = new SimpleAttributeSet();
            StyleConstants.setForeground(colorRedAttr, Color.RED);
            m_strategyDir = ConfigProperties.getPropAsString("trade.strategy.default.dir");
            String fileDir = TEMP_DIR + "/" + IStrategyRule.PACKAGE.replace('.', '/');
            File srcDirFile = new File(fileDir);
            srcDirFile.mkdirs();
            srcDirFile.deleteOnExit();
            this.dynacode = new DynamicCode();
            this.dynacode.addSourceDir(new File(m_strategyDir));
            this.strategies = this.tradeService.findStrategies();
            strategyTreeModel = new StrategyTreeModel(this.strategies);
            compileButton = new BaseButton(this, UIPropertyCodes.newInstance(UIPropertyCodes.COMPILE));
            newButton = new BaseButton(this, BaseUIPropertyCodes.NEW);
            newButton.setToolTipText("Load Template");

            JPanel jPanel1 = new JPanel(new FlowLayout());
            jPanel1.add(newButton);
            jPanel1.add(compileButton);
            jPanel1.setBorder(new BevelBorder(BevelBorder.RAISED));
            JToolBar jToolBar = new JToolBar();
            jToolBar.setLayout(new BorderLayout());
            jToolBar.add(jPanel1, BorderLayout.WEST);

            // create the message panel first so we can send messages to it...
            messageText = new StreamEditorPane("text/rtf");
            messageText.setFont(new Font("dialog", Font.PLAIN, 12));

            JPanel messagePanel = new JPanel(new BorderLayout());
            JScrollPane jScrollPane = new JScrollPane(messageText);
            messagePanel.add(jScrollPane, BorderLayout.CENTER);
            messagePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Messages"),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)));

            DefaultSyntaxKit.initKit();
            sourceText = new JEditorPane();
            JScrollPane jScrollPane1 = new JScrollPane(sourceText);
            sourceText.setContentType("text/java");
            sourceText.setFont(new Font("monospaced", Font.PLAIN, 12));
            sourceText.setBackground(Color.white);
            sourceText.setForeground(Color.black);
            sourceText.setSelectedTextColor(Color.black);
            sourceText.setSelectionColor(Color.red);
            sourceText.setEditable(true);
            commentText = new JTextArea();
            JScrollPane jScrollPane3 = new JScrollPane(commentText);
            JPanel commentPanel = new JPanel(new BorderLayout());
            commentPanel.add(jScrollPane3, BorderLayout.CENTER);
            commentPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Comments"),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)));

            JPanel sourcePanel = new JPanel(new BorderLayout());
            sourcePanel.add(jScrollPane1, BorderLayout.CENTER);
            sourcePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Source"),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)));
            // use the new JSplitPane to dynamically resize...
            JSplitPane splitSource = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, sourcePanel, commentPanel);
            splitSource.setOneTouchExpandable(true);
            splitSource.setResizeWeight(0.7d);

            // create the JTree and scroll pane.
            JPanel treePanel = new JPanel(new BorderLayout());
            m_tree = new Tree(strategyTreeModel);
            m_tree.setCellRenderer(new StrategyTreeCellRenderer());
            m_tree.addTreeSelectionListener(this);
            ToolTipManager.sharedInstance().registerComponent(m_tree);

            JScrollPane jScrollPane2 = new JScrollPane(m_tree);
            treePanel.add(jScrollPane2, BorderLayout.CENTER);
            treePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Rules"),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)));

            // use the new JSplitPane to dynamically resize...
            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, treePanel, splitSource);
            split.setOneTouchExpandable(true);
            split.setResizeWeight(0.05d);

            JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, split, messagePanel);
            mainSplitPane.setResizeWeight(0.7d);
            mainSplitPane.setOneTouchExpandable(true);
            this.add(jToolBar, BorderLayout.NORTH);
            this.add(mainSplitPane, BorderLayout.CENTER);

            loadStrategiesFromFileSystem(this.strategies);
            strategyTreeModel.setData(this.strategies);
            // Expand the tree
            for (int i = 0; i < m_tree.getRowCount(); i++) {

                m_tree.expandRow(i);
            }
        } catch (Exception ex) {
            this.setErrorMessage("Error during initialization.", ex.getMessage(), ex);
        }
    }

    public void valueChanged(TreeSelectionEvent e) {

        try {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();

            if (node.getUserObject() instanceof Strategy) {

                newButton.setTransferObject(node.getUserObject());
                messageText.setText(null);
            }

            if (node.getUserObject() instanceof Rule rule) {

                if (null != currentRule) {

                    if (currentRule.getRule().length > 0) {

                        if (!(new String(currentRule.getRule())).equals(getContent())) {

                            currentRule.setRule(getContent().getBytes());
                            currentRule.setDirty(true);
                        }
                    }

                    if (null != currentRule.getComment() && !currentRule.getComment().equals(commentText.getText())) {

                        currentRule.setComment(commentText.getText());
                        currentRule.setDirty(true);
                    }
                }
                compileButton.setTransferObject(rule);
                setContent(null);
                messageText.setText(null);

                try {

                    Class<?> thisClass = this.dynacode
                            .loadClass(IStrategyRule.PACKAGE + rule.getStrategy().getClassName());
                    setMessageText(null, false, false, null);
                    addClassDefinition(thisClass, "Methods for class: " + thisClass.getName(),
                            messageText.getDocument());
                    addClassDefinition(thisClass.getSuperclass(),
                            "Methods for super class: " + thisClass.getSuperclass().getName(),
                            messageText.getDocument());
                } catch (Throwable ex) {

                    this.setStatusBarMessage("Strategy definition could not be loaded Msg:" + ex.getMessage()
                            + " Please compile and save the strategy.", BasePanel.INFORMATION);

                }
                setContent(new String(rule.getRule()));
                commentText.setText(rule.getComment());
                commentText.setCaretPosition(0);
                messageText.setCaretPosition(0);
                currentRule = rule;
            }
        } catch (Exception ex) {
            setErrorMessage("Error finding Strategy code.", ex.getMessage(), ex);
        }
    }

    public void doWindowOpen() {
    }

    public void doWindowClose() {
        File dir = new File(TEMP_DIR);
        deleteDir(dir);
    }

    public void doWindowActivated() {
        doRefresh();
    }

    public boolean doWindowDeActivated() {
        return true;
    }

    public void doCompile(final Rule rule) {

        try {

            setMessageText(null, false, false, null);
            String fileName = TEMP_DIR + "/" + IStrategyRule.PACKAGE.replace('.', '/');
            fileName = fileName + rule.getStrategy().getClassName() + ".java";
            doSaveFile(fileName, this.getContent());

            Vector<Object> parm = new Vector<>(0);
            IBrokerModel brokerManagerModel = (IBrokerModel) ClassFactory.getServiceForInterface(IBrokerModel._brokerTest,
                    this);
            CandleDataset candleDataset = new CandleDataset();
            CandleSeries candleSeries = new CandleSeries("Test",
                    new Contract(SECType.STOCK, "Test", Exchange.SMART, Currency.USD, null, null), BarSize.FIVE_MIN,
                    TradingCalendar.getDateTimeNowMarketTimeZone(), TradingCalendar.getDateTimeNowMarketTimeZone());
            candleDataset.addSeries(candleSeries);
            StrategyData strategyData = new StrategyData(rule.getStrategy(), candleDataset);
            parm.add(brokerManagerModel);
            parm.add(strategyData);
            parm.add(0);
            DynamicCode dynacode = new DynamicCode();
            dynacode.addSourceDir(new File(TEMP_DIR));
            dynacode.newProxyInstance(IStrategyRule.class, IStrategyRule.PACKAGE + rule.getStrategy().getClassName(),
                    parm);

            this.setStatusBarMessage("File compiled.", BasePanel.INFORMATION);
        } catch (Exception ex) {
            setMessageText("Error compiling strategy: " + rule.getStrategy().getName() + ex.getMessage(), false, true,
                    colorRedAttr);
        }
    }

    public void doOpen() {

        try {

            JFileChooser fileView = new JFileChooser();
            fileView.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileView.addChoosableFileFilter(new JavaFilter());
            fileView.setAcceptAllFileFilterUsed(false);

            if (null == m_strategyDir) {

                fileView.setCurrentDirectory(new File(System.getProperty("user.dir")));
            } else {

                String dir = m_strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/');
                fileView.setCurrentDirectory(new File(dir));
            }

            int returnVal = fileView.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {

                String fileName = fileView.getSelectedFile().getPath();

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) Objects.requireNonNull(m_tree.getSelectionPath())
                        .getLastPathComponent();
                setContent(readFile(fileName));
                commentText.setText(null);

                if (node.getUserObject() instanceof Strategy) {

                    createRule((Strategy) node.getUserObject());
                }
            }
        } catch (Exception ex) {
            this.setErrorMessage("Exception while reading csv file.", ex.getMessage(), ex);
        }
    }

    public void doSave() {

        try {
            /*
             * Check to see if the rule has change and its not a new rule.
             */
            if (this.currentRule == null) {
                this.setStatusBarMessage("Please select a rule to be saved.", BasePanel.INFORMATION);
                return;
            }

            if (this.currentRule.getRule().length > 0) {

                if ((new String(this.currentRule.getRule())).equals(getContent())) {

                    if (null != this.currentRule.getComment() && this.currentRule.getComment().equals(getComments())) {

                        if (null != this.currentRule.getId()) {
                            return;
                        }
                    }
                }
            }

            String fileName = m_strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/');
            String fileNameSource = fileName + this.currentRule.getStrategy().getClassName() + ".java";
            String fileNameComments = fileName + this.currentRule.getStrategy().getClassName() + ".txt";
            int result = JOptionPane.NO_OPTION;

            if (null != this.currentRule.getId()) {

                result = JOptionPane.showConfirmDialog(this.getFrame(), "Do you want to version this strategy",
                        "Information", JOptionPane.YES_NO_OPTION);
            }

            if (result == JOptionPane.YES_OPTION) {

                Integer version = this.tradeService.findRuleByMaxVersion(this.currentRule.getStrategy());
                Rule nextRule = new Rule(this.currentRule.getStrategy(), (version + 1), commentText.getText(),
                        TradingCalendar.getDateTimeNowMarketTimeZone(), getContent().getBytes(),
                        TradingCalendar.getDateTimeNowMarketTimeZone());
                this.currentRule.getStrategy().add(nextRule);
                this.tradeService.saveAspect(nextRule);
                doSaveFile(fileNameSource, getContent());
                doSaveFile(fileNameComments, getComments());
                /*
                 * Now find and reset the original rule back to before the
                 * changes made.
                 */

                Rule orginalRule = tradeService.findRuleById(this.currentRule.getId());
                this.currentRule.setComment(orginalRule.getComment());
                this.currentRule.setCreateDate(orginalRule.getCreateDate());
                this.currentRule.setRule(orginalRule.getRule());

                this.setContent(new String(this.currentRule.getRule()));
                commentText.setText(this.currentRule.getComment());
            } else {

                if (!getComments().isEmpty()) {

                    this.currentRule.setComment(getComments());
                }
                this.currentRule.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                this.currentRule.setRule(getContent().getBytes());
                this.currentRule = this.tradeService.saveAspect(this.currentRule);
                doSaveFile(fileNameSource, getContent());
                doSaveFile(fileNameComments, getComments());
            }
            refreshTree();
            this.currentRule.setDirty(false);
        } catch (Exception ex) {
            setErrorMessage("Error saving strategy", ex.getMessage(), ex);
        } finally {
            this.getFrame().setCursor(Cursor.getDefaultCursor());
        }
    }

    public void doNew(final Strategy strategy) {
        try {

            String templateName = ConfigProperties.getPropAsString("trade.strategy.template");
            String fileName = m_strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/') + templateName + ".java";

            commentText.setText(null);
            setContent(readFile(fileName));
            setContent((getContent().replaceAll(templateName, strategy.getClassName())));
            createRule(strategy);

        } catch (Exception ex) {
            setErrorMessage("Error loading template strategy", ex.getMessage(), ex);
        }
    }

    public void doDelete() {
        try {
            if (this.currentRule == null) {
                this.setStatusBarMessage("Please select a rule to be deleted.", BasePanel.INFORMATION);
                return;
            }

            int result = JOptionPane.showConfirmDialog(this.getFrame(), "Do you want to delete selected rule?",
                    "Information", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                for (Strategy strategy : this.strategies) {
                    if (strategy.getId().equals(this.currentRule.getStrategy().getId())) {
                        strategy.getRules().remove(this.currentRule);
                        this.tradeService.deleteAspect(this.currentRule);
                    }
                }
                Integer version = this.tradeService.findRuleByMaxVersion(this.currentRule.getStrategy());

                if (Objects.equals(version, this.currentRule.getVersion()) && version > 1) {

                    setMessageText("File system is out of sync with DB please re deploy the latest version.", false,
                            true, colorRedAttr);
                }
                refreshTree();
            }
        } catch (Exception ex) {
            setErrorMessage("Error saving rule", ex.getMessage(), ex);
        }
    }

    public void doRefresh() {

        try {

            this.clearStatusBarMessage();
            this.strategies = this.tradeService.findStrategies();
            refreshTree();
        } catch (Exception ex) {
            this.setErrorMessage("Error finding rule.", ex.getMessage(), ex);
        }
    }

    public synchronized String readFile(String fileName) throws IOException {
        FileReader fileReader = null;
        try {

            if ((fileName == null) || fileName.isEmpty()) {
                return null;
            }
            fileReader = new FileReader(fileName);
            return readInputStream(fileReader);
        } finally {
            if (null != fileReader)
                fileReader.close();

        }
    }

    public synchronized String readResource(String fileName) throws IOException {
        try (InputStream inputStream = this.getClass().getResourceAsStream(fileName)) {
            InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream));
            return readInputStream(inputStreamReader);
        }
    }

    private void loadStrategiesFromFileSystem(List<Strategy> strategies) {
        try {
            this.setMessageText(null, false, false, null);
            for (Strategy strategy : strategies) {
                String fileNameCode = m_strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/')
                        + strategy.getClassName() + ".java";
                String fileNameComments = m_strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/')
                        + strategy.getClassName() + ".txt";

                try {
                    String content = readFile(fileNameCode);
                    String comments = readFile(fileNameComments);
                    if (strategy.getRules().isEmpty()) {
                        Rule nextRule = new Rule(strategy, 1, comments, TradingCalendar.getDateTimeNowMarketTimeZone(),
                                content.getBytes(), TradingCalendar.getDateTimeNowMarketTimeZone());
                        strategy.add(nextRule);
                        this.tradeService.saveAspect(nextRule);
                    } else {
                        Integer version = this.tradeService.findRuleByMaxVersion(strategy);
                        for (Rule rule : strategy.getRules()) {
                            if (rule.getVersion().equals(version)) {
                                /*
                                 * Load and save the file in the DB if there is
                                 * no content for this rule. i.e. initial start
                                 * up. Else make sure the rule in the DB is the
                                 * same as the rule in the file system.
                                 */
                                if (null == rule.getRule() && null != content) {
                                    rule.setRule(content.getBytes());
                                    rule = this.tradeService.saveAspect(rule);
                                } else {
                                    String ruleDB = new String(rule.getRule());
                                    if (!ruleDB.equals(content)) {
                                        setMessageText(
                                                "DB strategy not in sync with file system strategy: " + fileNameCode
                                                        + " file length: " + Objects.requireNonNull(content).length() + " Strategy "
                                                        + rule.getStrategy().getName() + " length: " + ruleDB.length(),
                                                true, true, colorRedAttr);
                                    }
                                }
                                if (null == rule.getComment() && null != comments) {

                                    rule.setComment(comments);
                                    this.tradeService.saveAspect(rule);

                                } else {

                                    String commentsDB = rule.getComment();
                                    if (!commentsDB.equals(comments)) {

                                        setMessageText("DB strategy not in sync with file system strategy: "
                                                        + fileNameComments + " file length: " + Objects.requireNonNull(comments).length() + " Strategy "
                                                        + rule.getStrategy().getName() + " length: " + commentsDB.length(),
                                                true, true, colorRedAttr);
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    // Do nothing.
                }
            }
            if (!getMessageText().isEmpty()) {
                setMessageText("Re deploy rule to fix this problem.", true, true, colorRedAttr);
            }

        } catch (ServiceException ex) {
            this.setErrorMessage("Error saving rule.", ex.getMessage(), ex);
        }
    }

    private void addClassDefinition(Class<?> theClass, String title, Document doc) {

        try {
            SimpleAttributeSet bold = new SimpleAttributeSet();
            StyleConstants.setBold(bold, true);
            setMessageText(title, true, true, bold);

            Method[] method = theClass.getDeclaredMethods();
            for (Method element : method) {
                if (element.getModifiers() == Modifier.PUBLIC) {
                    Class<?> returnType = element.getReturnType();
                    String methodName = element.getName();
                    String methodAttribute = returnType.getName();
                    setMessageText(methodAttribute + " ", true, false, null);
                    setMessageText(methodName, true, false, bold);
                    setMessageText(" (", true, false, null);
                    Class<?>[] parms = element.getParameterTypes();
                    Object[] o = new Object[parms.length];
                    methodAttribute = "";
                    for (int j = 0; j < parms.length; j++) {

                        Object obj = parms[j];
                        o[j] = obj;
                        methodAttribute = methodAttribute + o[j].toString();
                        if (j < parms.length - 1) {
                            methodAttribute = methodAttribute + ", ";
                        }
                    }
                    setMessageText(methodAttribute + ")", true, true, null);
                }
            }
            setMessageText("", true, true, null);
        } catch (Exception ex) {
            setMessageText("Error compiling strategy: " + theClass.getName() + ex.getMessage(), false, true,
                    colorRedAttr);
        }
    }


    private synchronized String readInputStream(InputStreamReader inputStreamReader)
            throws IOException {

        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String newLine = "\n";
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append(newLine);
            }
            return sb.toString();
        } finally {
            if (null != inputStreamReader)
                inputStreamReader.close();
        }
    }

    private void doSaveFile(String fileName, String content) {

        try (OutputStream out = new FileOutputStream(fileName)) {
            out.write(content.getBytes());
            out.flush();
        } catch (final Exception ex) {
            this.setErrorMessage("Error saving rule.", ex.getMessage(), ex);
        }
    }

    private void refreshTree() throws ValueTypeException {
        DefaultMutableTreeNode treeNodeSelected = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
        strategyTreeModel.setData(this.strategies);
        // Expand the tree
        for (int i = 0; i < m_tree.getRowCount(); i++) {
            m_tree.expandRow(i);
        }
        if (null == treeNodeSelected)
            return;

        TreePath path = m_tree.findTreePathByObject(treeNodeSelected.getUserObject());

        if (null != path) {
            m_tree.setSelectionPath(path);
            m_tree.scrollPathToVisible(path);
        }
    }

    private void setContent(String content) {
        sourceText.setText(null);
        if (null != content) {
            sourceText.setText(content);
            sourceText.setCaretPosition(0);
        }
    }

    private void setMessageText(String content, boolean append, boolean newLine, SimpleAttributeSet attrSet) {
        if (!append)
            messageText.setText(null);
        if (null != content) {
            Document doc = messageText.getDocument();
            try {
                doc.insertString(doc.getLength(), content, attrSet);
                if (newLine)
                    doc.insertString(doc.getLength(), "\n", null);
            } catch (BadLocationException ex1) {
                this.setErrorMessage("Exception setting messge: ", ex1.getMessage(), ex1);
            }
        }
    }

    private String getMessageText() {
        return messageText.getText();
    }

    private String getContent() {
        return sourceText.getText();
    }

    private String getComments() {
        return commentText.getText();
    }

    protected static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : Objects.requireNonNull(children)) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    private void createRule(final Strategy strategy) throws ServiceException, ValueTypeException {

        Integer version = this.tradeService.findRuleByMaxVersion(strategy);
        Rule nextRule = new Rule(strategy, (version + 1), commentText.getText(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), getContent().getBytes(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        strategy.add(nextRule);
        refreshTree();
        TreePath path = m_tree.findTreePathByObject(nextRule);
        if (null != path) {
            m_tree.setSelectionPath(path);
            m_tree.scrollPathToVisible(path);
        }
    }

    public static class JavaFilter extends FileFilter {

        public final static String java = "java";

        // Accept all directories and all csv files.

        /**
         * Method accept.
         *
         * @param f File
         * @return boolean
         */
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);
            if (extension != null) {
                return extension.equals(java);
            }
            return false;
        }

        public String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if ((i > 0) && (i < (s.length() - 1))) {
                ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
        }

        public String getDescription() {
            return "Java Files";
        }
    }
}
