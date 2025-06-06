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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.trade.core.dao.Aspect;

import java.io.Serial;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 *
 */
@Entity
@Table(name = "portfolioaccount")
public class PortfolioAccount extends Aspect implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    private Portfolio portfolio;
    private Account account;

    public PortfolioAccount() {
    }

    public PortfolioAccount(Portfolio portfolio, Account account) {
        this.portfolio = portfolio;
        this.account = account;
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
     * Method getPortfolio.
     *
     * @return Portfolio
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "id_portfolio", nullable = false)
    public Portfolio getPortfolio() {
        return this.portfolio;
    }

    /**
     * Method setPortfolio.
     *
     * @param portfolio Portfolio
     */
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    /**
     * Method getAccount.
     *
     * @return Account
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "id_account", nullable = false)
    public Account getAccount() {
        return this.account;
    }

    /**
     * Method setAccount.
     *
     * @param account Account
     */
    public void setAccount(Account account) {
        this.account = account;
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
     * Method equals.
     *
     * @param objectToCompare Object
     * @return boolean
     */
    public boolean equals(Object objectToCompare) {

        if (super.equals(objectToCompare))
            return true;

        if (!(objectToCompare instanceof PortfolioAccount theOtherOrder))
            return false;

        if (!theOtherOrder.getAccount().getAccountNumber().equals((this.getAccount().getAccountNumber()))) {
            return false;
        }

        return theOtherOrder.getPortfolio().getName().equals((this.getPortfolio().getName()));
    }
}
