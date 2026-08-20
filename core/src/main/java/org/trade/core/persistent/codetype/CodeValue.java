package org.trade.core.persistent.codetype;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.trade.core.aspect.Aspect;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.strategy.series.indicator.IndicatorSeries;
import org.trade.core.persistent.tradestrategy.Tradestrategy;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "codevalue")
public class CodeValue extends Aspect implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    @Column(name = "code_value", nullable = false, length = 45)
    private String codeValue;

    @Column(name = "code_object_id", columnDefinition = "integer DEFAULT 1", nullable = false)
    private Integer codeObjectId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "code_attribute_id")
    @JsonBackReference
    private CodeAttribute codeAttribute;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "indicator_series_id")
    private IndicatorSeries indicatorSeries;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tradestrategy_id")
    private Tradestrategy tradestrategy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "decodetype_id")
    private DecodeType decodeType;

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
     * Method getCodeObjectId.
     *
     * @return Integer
     */
    public Integer getCodeObjectId() {
        return this.codeObjectId;
    }

    /**
     * Method setCodeObjectId.
     *
     * @param codeObjectId Integer
     */
    public void setCodeObjectId(Integer codeObjectId) {
        this.codeObjectId = codeObjectId;
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
     * Method getDecodeType.
     *
     * @return DecodeType
     */
    public DecodeType getDecodeType() {
        return this.decodeType;
    }

    /**
     * Method setDecodeType.
     *
     * @param decodeType DecodeType
     */
    public void setDecodeType(DecodeType decodeType) {
        this.decodeType = decodeType;
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
