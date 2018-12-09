package org.trade.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Data
@Entity
@Table(name = "employee")
public class Employee {

    private @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    private String firstName;
    private String lastName;
    private String description;

    private @Version
    @JsonIgnore
    Long version;

    private @ManyToOne
    Manager manager;

    private Employee() {
    }

    public Employee(String firstName, String lastName, String description, Manager manager) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.manager = manager;
    }
}
// end::code[]