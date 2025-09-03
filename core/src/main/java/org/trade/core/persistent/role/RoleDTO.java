package org.trade.core.persistent.role;

import org.trade.core.dao.Aspect;
import org.trade.core.persistent.user.UserDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class RoleDTO extends Aspect implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 5691902477608387034L;

    private String name;
    private String description;
    private Long containedRoleId;
    private List<RoleDTO> containRoles;
    private List<UserDTO> users = new ArrayList<>(0);

    public RoleDTO() {
    }

    public RoleDTO(String name, String description) {
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
     * Method getContainedRoleId.
     *
     * @return containedRoleId Long
     */
    public Long getContainedRoleId() {

        return this.containedRoleId;
    }

    /**
     * Method setContainedRoleId.
     *
     * @param containedRoleId Long
     */
    public void setContainedRoleId(Long containedRoleId) {

        this.containedRoleId = containedRoleId;
    }

    /**
     * Method getContainRoles.
     *
     * @return List<RoleDTO>
     */
    public List<RoleDTO> getContainRoles() {
        return this.containRoles;
    }

    /**
     * Method setContainRoles.
     *
     * @param containRoles List<RoleDTO>
     */
    public void setContainRoles(List<RoleDTO> containRoles) {
        this.containRoles = containRoles;
    }

    /**
     * Method getUsers.
     *
     * @return List<UserDTO>
     */
    public List<UserDTO> getUsers() {
        return this.users;
    }

    /**
     * Method setUsers.
     *
     * @param users List<UserDTO>
     */
    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {

        RoleDTO role = (RoleDTO) super.clone();
        List<UserDTO> users = new ArrayList<>(0);
        role.setUsers(users);
        return role;
    }
}
