package org.trade.core.persistent.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private RoleDTO containedRole;
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
     * Method getContainedRole.
     *
     * @return containedRole RoleDTO
     */
    public RoleDTO getContainedRole() {

        return this.containedRole;
    }

    /**
     * Method setContainedRole.
     *
     * @param containedRole RoleDTO
     */
    public void setContainedRole(RoleDTO containedRole) {

        this.containedRole = containedRole;
    }

    /**
     * Method getContainRoles.
     *
     * @return List<RoleDTO>
     */
    public List<RoleDTO> getContainRoleDTOs() {
        return this.containRoles;
    }

    /**
     * Method setContainRoles.
     *
     * @param containRoles List<RoleDTO>
     */
    @JsonIgnore
    public void setContainRoleDTOs(List<RoleDTO> containRoles) {
        this.containRoles = containRoles;
    }

    /**
     * Method getUsers.
     *
     * @return List<UserDTO>
     */
    public List<UserDTO> getUserDTOs() {
        return this.users;
    }

    /**
     * Method setUsers.
     *
     * @param users List<UserDTO>
     */
    @JsonIgnore
    public void setUserDTOs(List<UserDTO> users) {
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
        role.setUserDTOs(users);
        return role;
    }
}
