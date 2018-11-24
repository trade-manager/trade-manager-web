package org.trade.web;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Data
@Entity
public class Employee {

	private @Id @GeneratedValue Long id;
	private String firstName;
	private String lastName;
	private String description;

	private @Version @JsonIgnore Long version;

	private @ManyToOne
    User user;

	private Employee() {}

	public Employee(String firstName, String lastName, String description, User user) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.description = description;
		this.user = user;
	}
}
