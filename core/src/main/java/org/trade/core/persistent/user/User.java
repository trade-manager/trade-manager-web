package org.trade.core.persistent.user;

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
import org.trade.core.dao.Aspect;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleDTO;
import org.trade.core.util.JSONMapper;

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

    @Column(name = "name")
    private String name;

    @Column(name = "user_name")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "domain", nullable = false)
    private Domain domain;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "userrole",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>(0);

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "useraccount",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> accounts = new ArrayList<>(0);

    public User() {
    }

    public User(String username, String password, String name, String email, String role) {

        this.username = username;
        this.setPassword(password);
        this.name = name;
        this.firstName = name;
        this.lastName = name;
        this.email = email;
        roles.add(new Role(role, role));
        this.domain = new Domain(Domain.GLOBAL, Domain.GLOBAL);

    }

    public User(String name, String username, String firstName, String lastName, String email, String password, Domain domain, List<Role> roles) {

        this.name = name;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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

    public String getUsername() {

        return this.username;
    }

    public void setUsername(String username) {

        this.username = username;
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

    public String getPassword() {

        return this.password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public Domain getDomain() {

        return this.domain;
    }

    public void setDomain(Domain domain) {

        this.domain = domain;
    }


    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {

        return this.roles;
    }

    public void setRoles(List<Role> roles) {

        this.roles = roles;
    }

    @Transient
    public Role getRole() {

        return this.roles.getFirst();
    }


    public void addRole(Role role) {

        if (!this.roles.contains(role)) {
            this.roles.add(role);
        }
    }

    public boolean hasRole(Role role) {

        return this.roles.contains(role);
    }

    @Transient
    public String[] getRoleValues() {

        String[] roles = new String[this.getRoles().size()];

        for (int i = 0; i < this.getRoles().size(); i++) {

            roles[i] = this.getRoles().get(i).getName();
        }
        return roles;
    }

    public List<Account> getAccounts() {

        return this.accounts;
    }

    public void setAccounts(List<Account> accounts) {

        this.accounts = accounts;
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {

        User user = (User) super.clone();
        List<Role> roles = new ArrayList<>(0);
        user.setRoles(roles);
        return user;
    }

    /**
     * @return
     */
    @Transient
    public List<RoleDTO> getRoleDTOs() {

        List<RoleDTO> roleDTOs = new ArrayList<>();

        for (Role role : this.getRoles()) {

            RoleDTO roleDTO = JSONMapper.convertEntityToDTO(role, RoleDTO.class);
            roleDTOs.add(roleDTO);
        }

        return roleDTOs;
    }
}


