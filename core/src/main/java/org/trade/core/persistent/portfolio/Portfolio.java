package org.trade.core.persistent.portfolio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.trade.core.dao.Aspect;
import org.trade.core.persistent.account.Account;
import org.trade.core.persistent.dao.Tradestrategy;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "portfolio")
public class Portfolio extends Aspect implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "alias", unique = true, length = 45)
    private String alias;

    @Column(name = "allocation_method", nullable = false, length = 20)
    private String allocationMethod;

    @Column(name = "description", nullable = false, length = 240)
    private String description;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY)
    private List<Tradestrategy> tradestrategies = new ArrayList<>(0);

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "portfolioaccount",
            joinColumns = @JoinColumn(name = "portfolio_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> accounts = new ArrayList<>(0);

    public Portfolio() {

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
    }

    /**
     * Method getName.
     *
     * @return String
     */
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
     * Method getTradestrategies.
     *
     * @return List<Tradestrategy>
     */
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
     * Method getAccounts.
     *
     * @return List<Account>
     */
    public List<Account> getAccounts() {
        return this.accounts;
    }

    /**
     * Method setPortfolioAccounts.
     *
     * @param accounts List<Account>
     */
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * Method getIndividualAccount.
     *
     * @return account
     */
    @Transient
    public Account getIndividualAccount() {

        if (this.getAccounts().size() == 1) {

            return this.getAccounts().getFirst();
        }
        return null;
    }

    /**
     * Method removeAccount.
     *
     * @param account Account
     */
    public boolean removeAccount(Account account) {

        for (ListIterator<Account> itemIter = this.accounts.listIterator(); itemIter.hasNext(); ) {

            Account item = itemIter.next();

            if (item.equals(account)) {

                itemIter.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Method removeTradestrategy.
     *
     * @param tradestrategy Tradestrategy
     */
    public boolean removeTradestrategy(Tradestrategy tradestrategy) {

        for (ListIterator<Tradestrategy> itemIter = this.tradestrategies.listIterator(); itemIter.hasNext(); ) {

            Tradestrategy item = itemIter.next();

            if (item.equals(tradestrategy)) {

                itemIter.remove();
                return true;
            }
        }
        return false;
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
