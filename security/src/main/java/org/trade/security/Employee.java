package org.trade.security;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Data
@Entity
@Table(name = "employee")
public class Employee {

	private @Id @GeneratedValue(strategy = IDENTITY) Long id;
	private String firstName;
	private String lastName;
	private String description;

	private @Version @JsonIgnore Long version;

	private @ManyToOne Manager manager;

	private Employee() {}

	public Employee(String firstName, String lastName, String description, Manager manager) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.description = description;
		this.manager = manager;
	}
}
// end::code[]