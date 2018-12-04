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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Entity
@Table(name = "user")
public class User {

	public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private  Integer id;
	private String name;
	private  String password;

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

	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof User)) {
			return false;
		} else {
			User other = (User)o;
			if (!other.canEqual(this)) {
				return false;
			} else {
				Object this$id = this.getId();
				Object other$id = other.getId();
				if (this$id == null) {
					if (other$id != null) {
						return false;
					}
				} else if (!this$id.equals(other$id)) {
					return false;
				}

				Object this$name = this.getName();
				Object other$name = other.getName();
				if (this$name == null) {
					if (other$name != null) {
						return false;
					}
				} else if (!this$name.equals(other$name)) {
					return false;
				}

				Object this$password = this.getPassword();
				Object other$password = other.getPassword();
				if (this$password == null) {
					if (other$password != null) {
						return false;
					}
				} else if (!this$password.equals(other$password)) {
					return false;
				}

				if (!Arrays.deepEquals(this.getRoles(), other.getRoles())) {
					return false;
				} else {
					return true;
				}
			}
		}
	}

	protected boolean canEqual(final Object other) {
		return other instanceof User;
	}

	public int hashCode() {
		int result = 1;
		Object $id = this.getId();
		result = result * 59 + ($id == null ? 43 : $id.hashCode());
		Object $name = this.getName();
		result = result * 59 + ($name == null ? 43 : $name.hashCode());
		Object $password = this.getPassword();
		result = result * 59 + ($password == null ? 43 : $password.hashCode());
		result = result * 59 + Arrays.deepHashCode(this.getRoles());
		return result;
	}

	public String toString() {
		return "Manager(id=" + this.getId() + ", name=" + this.getName() + ", roles=" + Arrays.deepToString(this.getRoles()) + ")";
	}
}


