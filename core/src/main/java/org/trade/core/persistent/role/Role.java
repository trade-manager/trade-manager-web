package org.trade.core.persistent.role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.trade.core.dao.Aspect;
import org.trade.core.persistent.user.User;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "role")
public class Role extends Aspect implements Serializable, Cloneable {

    public final static String ROLE_ADMIN = "ADMIN";
    public final static String ROLE_MANAGER = "MANAGER";
    public final static String ROLE_USER = "USER";

    @Serial
    private static final long serialVersionUID = 5691902477608387034L;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contained_role_id")
    private Role containedRole;

    // One-to-many relationship with contains (self-reference)
    @OneToMany(mappedBy = "containedRole", cascade = CascadeType.ALL)
    private List<Role> containRoles;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>(0);

    public Role() {
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * @return name
     */
    public String getName() {

        return this.name;
    }

    /**
     * @param name String
     */
    public void setName(String name) {

        this.name = name;
    }

    /**
     * @return description String
     */
    public String getDescription() {

        return this.description;
    }

    /**
     * @param description String
     */
    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * @return containedRole Role
     */
    public Role getContainedRole() {

        return this.containedRole;
    }

    /**
     * @param containedRole Role
     */
    public void setContainedRole(Role containedRole) {

        this.containedRole = containedRole;
    }


    /**
     * Method getContainRoles.
     *
     * @return List<Role>
     */
    public List<Role> getContainRoles() {
        return this.containRoles;
    }

    /**
     * Method setContainRoles.
     *
     * @param containRoles List<Role>
     */
    public void setContainRoles(List<Role> containRoles) {
        this.containRoles = containRoles;
    }

    /**
     * Method getUsers.
     *
     * @return List<User>
     */
    public List<User> getUsers() {
        return this.users;
    }

    /**
     * Method setUsers.
     *
     * @param users List<User>
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {

        Role role = (Role) super.clone();
        List<User> users = new ArrayList<>(0);
        role.setUsers(users);
        return role;
    }
}
