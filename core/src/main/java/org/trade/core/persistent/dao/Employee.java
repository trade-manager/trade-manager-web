package org.trade.core.persistent.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.trade.core.dao.Aspect;

import java.io.Serializable;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "employee")
public class Employee extends Aspect implements Serializable, Cloneable {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "description")
    private String description;

    @Column(name = "email")
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = true, updatable = true, nullable = false)
    private User user;

    private Employee() {
    }

    public Employee(String firstName, String lastName, String description, String email, User user) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.email = email;
        this.user = user;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {

        Employee employee = (Employee) super.clone();
        return employee;
    }
}
