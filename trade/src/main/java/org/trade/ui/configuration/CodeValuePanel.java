package org.trade.ui.configuration;

import org.trade.core.persistent.codetype.CodeValue;
import org.trade.core.persistent.codetype.DecodeType;
import org.trade.core.valuetype.Decode;
import org.trade.ui.widget.DecodeComboBoxEditor;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.awt.*;
import java.io.Serial;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class CodeValuePanel extends JPanel {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 5972331201407363985L;
    private final Hashtable<String, JComponent> fields = new Hashtable<>();
    private final DecodeType decodeType;
    private final List<CodeValue> currentCodeValues;

    /**
     * Constructor for CodeAttributesPanel.
     *
     * @param decodeType        DecodeType
     * @param currentCodeValues List<CodeValue>
     */
    public CodeValuePanel(DecodeType decodeType, List<CodeValue> currentCodeValues) throws Exception {

        this.decodeType = decodeType;
        this.currentCodeValues = currentCodeValues;
        GridBagLayout gridBagLayout1 = new GridBagLayout();
        JPanel jPanel1 = new JPanel(gridBagLayout1);
        this.setLayout(new BorderLayout());

        int i = 0;
        for (CodeValue codeValue : this.decodeType.getCodeValues()) {

            JLabel jLabel = new JLabel(codeValue.getCodeValue() + ": ");
            jLabel.setToolTipText(codeValue.getCodeValue());
            jLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            JComponent field = new JFormattedTextField();
            field.setInputVerifier(new CodeValuePanel.FormattedTextFieldVerifier());

            for (CodeValue value : this.currentCodeValues) {

                if (value.getCodeAttribute().getName().equals(codeValue.getCodeValue())) {
                    ((JFormattedTextField) field)
                            .setValue(CodeValue.getValueCode(codeValue.getCodeValue(), this.currentCodeValues));
                    break;
                }
            }

            if (null == ((JFormattedTextField) field).getValue()) {

                ((JFormattedTextField) field).setValue(codeValue);
            }

            fields.put(codeValue.getCodeValue(), field);
            jPanel1.add(jLabel, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 20, 5));
            jPanel1.add(field, new GridBagConstraints(1, i, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 20), 20, 5));
            i++;
        }

        this.add(jPanel1);
    }

    public List<CodeValue> getCodeValues() {

        for (CodeValue codeValue : this.decodeType.getCodeValues()) {

            if (((FormattedTextFieldVerifier) this.fields.get(codeValue.getCodeValue()).getInputVerifier()).isValid()) {

                JComponent field = this.fields.get(codeValue.getCodeValue());
                String newValue = null;
                if (field instanceof JFormattedTextField) {
                    newValue = (((JFormattedTextField) this.fields.get(codeValue.getCodeValue())).getText());
                } else if (field instanceof DecodeComboBoxEditor) {
                    newValue = ((Decode) Objects.requireNonNull(((DecodeComboBoxEditor) this.fields.get(codeValue.getCodeValue()))
                            .getSelectedItem())).getCode();
                }
            }
        }
        return this.currentCodeValues;
    }

    static class FormattedTextFieldVerifier extends InputVerifier {

        private boolean valid = true;

        /**
         * Method verify.
         *
         * @param input JComponent
         * @return boolean
         */
        public boolean verify(JComponent input) {

            if (input instanceof JFormattedTextField ftf) {

                AbstractFormatter formatter = ftf.getFormatter();

                if (formatter != null) {

                    String text = ftf.getText();

                    try {

                        formatter.stringToValue(text);
                        ftf.setBackground(null);
                        valid = true;
                    } catch (ParseException pe) {

                        ftf.setBackground(Color.red);
                        valid = false;
                    }
                }
            }

            return valid;
        }

        /**
         * Method shouldYieldFocus.
         *
         * @param input JComponent
         * @return boolean
         */
        public boolean shouldYieldFocus(JComponent input) {
            return verify(input);
        }

        /**
         * Method isValid.
         *
         * @return boolean
         */
        public boolean isValid() {
            return valid;
        }
    }
}