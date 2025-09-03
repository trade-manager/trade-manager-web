package org.trade.core.persistent.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Transient;
import org.trade.core.dao.Aspect;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.dao.series.indicator.IndicatorSeriesDTO;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class CodeValueDTO extends Aspect implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    private String codeValue;
    private CodeAttributeDTO codeAttribute;
    private IndicatorSeriesDTO indicatorSeries;
    private TradestrategyDTO tradestrategy;

    public CodeValueDTO() {
    }

    /**
     * Constructor for CodeValue.
     *
     * @param codeAttribute CodeAttributeDto
     * @param codeValue     String
     */
    public CodeValueDTO(CodeAttributeDTO codeAttribute, String codeValue) {

        this.codeValue = codeValue;
        this.codeAttribute = codeAttribute;
    }

    /**
     * Constructor for CodeValue.
     *
     * @param codeAttribute   CodeAttributeDto
     * @param codeValue       String
     * @param indicatorSeries IndicatorSeriesDto
     */
    public CodeValueDTO(CodeAttributeDTO codeAttribute, String codeValue, IndicatorSeriesDTO indicatorSeries) {

        this.codeValue = codeValue;
        this.codeAttribute = codeAttribute;
        this.indicatorSeries = indicatorSeries;
    }

    /**
     * Constructor for CodeValue.
     *
     * @param codeAttribute CodeAttributeDto
     * @param codeValue     String
     * @param tradestrategy TradestrategyDto
     */
    public CodeValueDTO(CodeAttributeDTO codeAttribute, String codeValue, TradestrategyDTO tradestrategy) {

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
     * @return CodeAttributeDto
     */
    public CodeAttributeDTO getCodeAttribute() {
        return this.codeAttribute;
    }

    /**
     * Method setCodeAttribute.
     *
     * @param codeAttribute CodeAttributeDto
     */
    public void setCodeAttribute(CodeAttributeDTO codeAttribute) {
        this.codeAttribute = codeAttribute;
    }

    /**
     * Method getIndicatorSeries.
     *
     * @return IndicatorSeriesDto
     */
    public IndicatorSeriesDTO getIndicatorSeries() {
        return this.indicatorSeries;
    }

    /**
     * Method setIndicatorSeries.
     *
     * @param indicatorSeries IndicatorSeriesDto
     */
    public void setIndicatorSeries(IndicatorSeriesDTO indicatorSeries) {
        this.indicatorSeries = indicatorSeries;
    }

    /**
     * Method getTradestrategy.
     *
     * @return TradestrategyDto
     */
    public TradestrategyDTO getTradestrategy() {
        return this.tradestrategy;
    }

    /**
     * Method setTradestrategy.
     *
     * @param tradestrategy TradestrategyDto
     */
    @JsonIgnore
    public void setTradestrategy(TradestrategyDTO tradestrategy) {
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
    public static Object getValueCode(final String name, final List<CodeValueDTO> codeValues) throws Exception {

        for (CodeValueDTO value : codeValues) {

            if (name.equals(value.getCodeAttribute().getName())) {

                List<Object> params = new ArrayList<>(0);
                params.add(value.getCodeValue());
                // codeValue = ClassFactory.getCreateClass(value.getCodeAttribute().getClassName(), params, CodeAttributePanel.class);
                return ClassFactory.getCreateClass(value.getCodeAttribute().getClassName(), params,
                        CodeValueDTO.class);
            }
        }
        return null;
    }
}
