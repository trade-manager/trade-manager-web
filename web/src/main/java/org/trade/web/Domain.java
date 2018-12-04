package org.trade.web;

import static javax.persistence.GenerationType.IDENTITY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;

import org.trade.core.dao.Aspect;

import javax.persistence.*;

@EqualsAndHashCode
@Entity
@Table(name = "domain")
public class Domain {


	private Integer id;
	private Integer version;
	private String name;
	private String description;

	private Domain() {}

	public Domain(String name, String description){
		this.name = name;
		this.description = description;
	}

	@Id @GeneratedValue(strategy = IDENTITY)
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

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Version
	@JsonIgnore
	@Column(name = "version")
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
