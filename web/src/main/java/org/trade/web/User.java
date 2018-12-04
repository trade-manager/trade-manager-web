package org.trade.web;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Entity
@Table(name = "user")
public class User {

	public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String password;

	private String[] roles;
//	private Domain domain;

	protected User() {
	}

	public User(String name, String password, Domain domain, String... roles) {

		this.name = name;
		this.setPassword(password);
		this.roles = roles;
//		this.domain = domain;
	}


	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "roles")
	public String[] getRoles() { return this.roles; }

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	@JsonIgnore
	@Column(name = "password")
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = PASSWORD_ENCODER.encode(password);
	}

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "domain_id")
//	public Domain getDomain() {
//		return this.domain;
//	}

//	public void setDomain(Domain domain) {
//		this.domain = domain;
//	}

}


