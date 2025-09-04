package org.trade.core.persistent.employee;

import org.trade.core.dao.Aspect;
import org.trade.core.persistent.user.UserDTO;

import java.io.Serializable;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class EmployeeDTO extends Aspect implements Serializable, Cloneable {

    private String name;
    private String firstName;
    private String lastName;
    private String description;
    private String email;
    private UserDTO user;

    private EmployeeDTO() {
    }

    public EmployeeDTO(String name, String firstName, String lastName, String description, String email, UserDTO user) {

        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.email = email;
        this.user = user;
    }

    public EmployeeDTO(Long id, String name, String firstName, String lastName, String description, String email) {

        this.setId(id);
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.email = email;
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

    public UserDTO getUser() {
        return this.user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {

        return (EmployeeDTO) super.clone();
    }
}
