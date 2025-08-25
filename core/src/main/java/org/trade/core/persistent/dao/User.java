package org.trade.core.persistent.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.trade.core.dao.Aspect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "user")
public class User extends Aspect implements Serializable, Cloneable {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public final static String ROLE_ADMIN = "ADMINISTRATOR";
    public final static String ROLE_MANAGER = "MANAGER";
    public final static String ROLE_USER = "USER";

    @Column(name = "name")
    private String name;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "domain_id", insertable = true, updatable = true, nullable = false)
    private Domain domain;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "userrole",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>(0);

    public User() {
    }

    public User(String name, String password, Domain domain, List<Role> roles) {

        this.name = name;
        this.setPassword(password);
        this.roles = roles;
        this.domain = domain;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
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

    public List<Role> getRoles() {

        return this.roles;
    }

    public void setRoles(List<Role> roles) {

        this.roles = roles;
    }

    public String getPassword() {

        return this.password;
    }

    public void setPassword(String password) {

        this.password = PASSWORD_ENCODER.encode(password);
    }

    public Domain getDomain() {

        return this.domain;
    }

    public void setDomain(Domain domain) {

        this.domain = domain;
    }

    @Transient
    public String[] getRoleValues() {

        String[] roles = new String[this.getRoles().size()];

        for (int i = 0; i < this.getRoles().size(); i++) {

            roles[i] = this.getRoles().get(i).getName();
        }
        return roles;
    }

        /**
         * Method clone.
         *
         * @return Object
         */
        public Object clone () throws CloneNotSupportedException {

            User user = (User) super.clone();
            List<Role> roles = new ArrayList<>(0);
            user.setRoles(roles);
            return user;
        }
    }


