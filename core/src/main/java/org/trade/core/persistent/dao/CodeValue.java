package org.trade.core.persistent.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.trade.core.dao.Aspect;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.dao.series.indicator.IndicatorSeries;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@Entity
@Table(name = "codevalue")
public class CodeValue extends Aspect implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    @Column(name = "code_value", nullable = false, length = 45)
    private String codeValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "code_attribute_id", nullable = false)
    private CodeAttribute codeAttribute;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "indicator_series_id")
    private IndicatorSeries indicatorSeries;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tradestrategy_id")
    private Tradestrategy tradestrategy;

    public CodeValue() {
    }

    /**
     * Constructor for CodeValue.
     *
     * @param codeAttribute CodeAttribute
     * @param codeValue     String
     */
    public CodeValue(CodeAttribute codeAttribute, String codeValue) {

        this.codeValue = codeValue;
        this.codeAttribute = codeAttribute;
    }

    /**
     * Constructor for CodeValue.
     *
     * @param codeAttribute   CodeAttribute
     * @param codeValue       String
     * @param indicatorSeries IndicatorSeries
     */
    public CodeValue(CodeAttribute codeAttribute, String codeValue, IndicatorSeries indicatorSeries) {

        this.codeValue = codeValue;
        this.codeAttribute = codeAttribute;
        this.indicatorSeries = indicatorSeries;
    }

    /**
     * Constructor for CodeValue.
     *
     * @param codeAttribute CodeAttribute
     * @param codeValue     String
     * @param tradestrategy Tradestrategy
     */
    public CodeValue(CodeAttribute codeAttribute, String codeValue, Tradestrategy tradestrategy) {

        this.codeValue = codeValue;
        this.codeAttribute = codeAttribute;
        this.tradestrategy = tradestrategy;
    }

    /**
     * Method getCodeValue.
     *
     * @return String
     */
    public String getCodeValue() {
        return this.codeValue;
    }

    /**
     * Method setCodeValue.
     *
     * @param codeValue String
     */
    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }

    /**
     * Method getCodeAttribute.
     *
     * @return CodeAttribute
     */
    public CodeAttribute getCodeAttribute() {
        return this.codeAttribute;
    }

    /**
     * Method setCodeAttribute.
     *
     * @param codeAttribute CodeAttribute
     */
    public void setCodeAttribute(CodeAttribute codeAttribute) {
        this.codeAttribute = codeAttribute;
    }

    /**
     * Method getIndicatorSeries.
     *
     * @return IndicatorSeries
     */
    public IndicatorSeries getIndicatorSeries() {
        return this.indicatorSeries;
    }

    /**
     * Method setIndicatorSeries.
     *
     * @param indicatorSeries IndicatorSeries
     */
    public void setIndicatorSeries(IndicatorSeries indicatorSeries) {
        this.indicatorSeries = indicatorSeries;
    }

    /**
     * Method getTradestrategy.
     *
     * @return Tradestrategy
     */
    public Tradestrategy getTradestrategy() {
        return this.tradestrategy;
    }

    /**
     * Method setTradestrategy.
     *
     * @param tradestrategy Tradestrategy
     */
    public void setTradestrategy(Tradestrategy tradestrategy) {
        this.tradestrategy = tradestrategy;
    }

    /**
     * Returns the value associated with for the this name attribute name. For
     * String data types you should define an classEditorName in the
     * CodeAttribute table, this should be a
     * org.trade.dictionary.valuetype.Decode These are presented as a combo box
     * in the UI for editing. all other data types use JFormattedField.
     *
     * @param name       the name of the attribute.
     * @param codeValues List<CodeValue>.
     * @return The value of the attribute.
     */
    @Transient
    public static Object getValueCode(final String name, final List<CodeValue> codeValues) throws Exception {

        for (CodeValue value : codeValues) {

            if (name.equals(value.getCodeAttribute().getName())) {

                List<Object> params = new ArrayList<>(0);
                params.add(value.getCodeValue());
                // codeValue = ClassFactory.getCreateClass(value.getCodeAttribute().getClassName(), params, CodeAttributePanel.class);
                return ClassFactory.getCreateClass(value.getCodeAttribute().getClassName(), params,
                        CodeValue.class);
            }
        }
        return null;
    }
}
