package org.trade.core.persistent.user;

import org.trade.core.dao.Aspect;
import org.trade.core.persistent.dao.AccountDTO;
import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.domain.DomainDTO;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class UserDTO extends Aspect implements Serializable, Cloneable {

    private String name;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private DomainDTO domain;
    private List<RoleDTO> roles = new ArrayList<>(0);
    private List<AccountDTO> accounts = new ArrayList<>(0);

    public UserDTO() {
    }

    public UserDTO(String username, String password, String name, String email, String role) {

        this.username = username;
        this.password = password;
        this.name = name;
        this.firstName = name;
        this.lastName = name;
        this.email = email;
        roles.add(new RoleDTO(role, role));
        this.domain = new DomainDTO(Domain.GLOBAL, Domain.GLOBAL);

    }

    public UserDTO(String name, String username, String firstName, String lastName, String email, String password, DomainDTO domain, List<RoleDTO> roles) {

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

    public DomainDTO getDomain() {

        return this.domain;
    }

    public void setDomain(DomainDTO domain) {

        this.domain = domain;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RoleDTO> getRoleDTOs() {

        return this.roles;
    }

    public void setRoleDTOs(List<RoleDTO> roles) {

        this.roles = roles;
    }


    public void addRole(RoleDTO role) {

        if (!this.roles.contains(role)) {
            this.roles.add(role);
        }
    }

    public boolean hasRole(Role role) {

        return this.roles.contains(role);
    }

    public List<AccountDTO> getAccountDTOs() {

        return this.accounts;
    }

    public void setAccountDTOs(List<AccountDTO> accounts) {

        this.accounts = accounts;
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {

        UserDTO user = (UserDTO) super.clone();
        List<RoleDTO> roles = new ArrayList<>(0);
        user.setRoleDTOs(roles);
        return user;
    }
}


