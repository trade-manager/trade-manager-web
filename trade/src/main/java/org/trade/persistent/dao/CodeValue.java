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
package org.trade.persistent.dao;

import org.trade.core.dao.Aspect;
import org.trade.core.factory.ClassFactory;
import org.trade.strategy.data.IndicatorSeries;
import org.trade.ui.configuration.CodeAttributePanel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Vector;

import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 */
@Entity
@Table(name = "codevalue")
public class CodeValue extends Aspect implements java.io.Serializable {

    private static final long serialVersionUID = 2273276207080568947L;

    private String codeValue;
    @NotNull
    private CodeAttribute codeAttribute;
    private IndicatorSeries indicatorSeries;
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
     * Method getId.
     *
     * @return Integer
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /**
     * Method setId.
     *
     * @param id Integer
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Method getCodeValue.
     *
     * @return String
     */
    @Column(name = "code_value", nullable = false, length = 45)
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_code_attribute", nullable = false)
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_indicator_series", nullable = true)
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tradestrategy", nullable = true)
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
     * Method getVersion.
     *
     * @return Integer
     */
    @Version
    @Column(name = "version")
    public Integer getVersion() {
        return this.version;
    }

    /**
     * Method setVersion.
     *
     * @param version Integer
     */
    public void setVersion(Integer version) {
        this.version = version;
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
     * @throws Exception
     */
    @Transient
    public static Object getValueCode(final String name, final List<CodeValue> codeValues) throws Exception {
        Object codeValue = null;
        for (CodeValue value : codeValues) {
            if (name.equals(value.getCodeAttribute().getName())) {
                Vector<Object> parm = new Vector<Object>();
                parm.add(value.getCodeValue());
                codeValue = ClassFactory.getCreateClass(value.getCodeAttribute().getClassName(), parm,
                        CodeAttributePanel.class);
                return codeValue;
            }
        }
        return codeValue;
    }
}
