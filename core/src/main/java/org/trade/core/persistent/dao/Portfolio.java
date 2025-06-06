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
package org.trade.core.persistent.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import org.trade.core.dao.Aspect;
import org.trade.core.util.time.TradingCalendar;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;


/**
 *
 */
@Entity
@Table(name = "portfolio")
public class Portfolio extends Aspect implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    @NotNull
    private String name;
    private String alias;
    private String allocationMethod;
    private String description;
    private Boolean isDefault = false;
    private ZonedDateTime lastUpdateDate;
    private List<Tradestrategy> tradestrategies = new ArrayList<>(0);
    private List<PortfolioAccount> portfolioAccounts = new ArrayList<>(0);

    public Portfolio() {
        this.lastUpdateDate = TradingCalendar.getDateTimeNowMarketTimeZone();
    }

    /**
     * Constructor for Portfolio.
     *
     * @param name        String
     * @param description String
     */
    public Portfolio(String name, String description) {
        this.name = name;
        this.description = description;
        this.lastUpdateDate = TradingCalendar.getDateTimeNowMarketTimeZone();
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
     * Method getName.
     *
     * @return String
     */
    @Column(name = "name", nullable = false, length = 45)
    public String getName() {
        return this.name;
    }

    /**
     * Method setName.
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method getAlias.
     *
     * @return String
     */
    @Column(name = "alias", unique = true, length = 45)
    public String getAlias() {
        return this.alias;
    }

    /**
     * Method setAlias.
     *
     * @param alias String
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Method getAllocationMethod.
     *
     * @return String
     */
    @Column(name = "allocation_method", nullable = false, length = 20)
    public String getAllocationMethod() {
        return this.allocationMethod;
    }

    /**
     * Method setAllocationMethod.
     *
     * @param allocationMethod String
     */
    public void setAllocationMethod(String allocationMethod) {
        this.allocationMethod = allocationMethod;
    }

    /**
     * Method getDescription.
     *
     * @return String
     */
    @Column(name = "description", nullable = false, length = 240)
    public String getDescription() {
        return this.description;
    }

    /**
     * Method setDescription.
     *
     * @param description String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method getIsDefault.
     *
     * @return Boolean
     */
    @Column(name = "is_default", nullable = false)
    public Boolean getIsDefault() {
        return this.isDefault;
    }

    /**
     * Method setIsDefault.
     *
     * @param isDefault Boolean
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Method getLastUpdateDate.
     *
     * @return ZonedDateTime
     */
    @Column(name = "last_update_date", nullable = false)
    public ZonedDateTime getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    /**
     * Method setLastUpdateDate.
     *
     * @param lastUpdateDate ZonedDateTime
     */
    public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
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
     * Method getTradestrategies.
     *
     * @return List<Tradestrategy>
     */
    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY)
    public List<Tradestrategy> getTradestrategies() {
        return this.tradestrategies;
    }

    /**
     * Method setTradestrategies.
     *
     * @param tradestrategies List<Tradestrategy>
     */
    public void setTradestrategies(List<Tradestrategy> tradestrategies) {
        this.tradestrategies = tradestrategies;
    }

    /**
     * Method getPortfolioAccounts.
     *
     * @return List<PortfolioAccounts>
     */
    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY, orphanRemoval = true, cascade = {CascadeType.ALL})
    public List<PortfolioAccount> getPortfolioAccounts() {
        return this.portfolioAccounts;
    }

    /**
     * Method setPortfolioAccounts.
     *
     * @param portfolioAccounts List<CodeAttribute>
     */
    public void setPortfolioAccounts(List<PortfolioAccount> portfolioAccounts) {
        this.portfolioAccounts = portfolioAccounts;
    }

    /**
     * Method getIndividualAccount.
     *
     * @return account
     */
    @Transient
    public Account getIndividualAccount() {
        if (this.getPortfolioAccounts().size() == 1) {
            return this.getPortfolioAccounts().getFirst().getAccount();
        }
        return null;
    }

    /**
     * Method isDirty.
     *
     * @return boolean
     */
    @Transient
    public boolean isDirty() {
        for (PortfolioAccount item : this.getPortfolioAccounts()) {
            if (item.isDirty())
                return true;
        }
        return super.isDirty();
    }

    /**
     * Method hashCode.
     * <p>
     * For every field tested in the equals-Method, calculate a hash code c by:
     * <p>
     * If the field f is a boolean: calculate * (f ? 0 : 1);
     * <p>
     * If the field f is a byte, char, short or int: calculate (int)f;
     * <p>
     * If the field f is a long: calculate (int)(f ^ (f >>> 32));
     * <p>
     * If the field f is a float: calculate Float.floatToIntBits(f);
     * <p>
     * If the field f is a double: calculate Double.doubleToLongBits(f) and
     * handle the return value like every long value;
     * <p>
     * If the field f is an object: Use the result of the hashCode() method or 0
     * if f == null;
     * <p>
     * If the field f is an array: See every field as separate element and
     * calculate the hash value in a recursive fashion and combine the values as
     * described next.
     *
     * @return int
     */
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash + (this.getName() == null ? 0 : this.getName().hashCode());
        return hash;
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {

        Portfolio portfolio = (Portfolio) super.clone();
        List<Tradestrategy> tradestrategies = new ArrayList<>(0);
        portfolio.setTradestrategies(tradestrategies);
        return portfolio;
    }
}
