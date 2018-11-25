package org.trade.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.trade.core.dao.Aspect;

import javax.persistence.*;


@Data
@Entity
public class Domain {

	private @Id @GeneratedValue Long sysId;

	private @Version @JsonIgnore Long version;

	private String name;
	private String description;

	private Domain() {}

	public Domain(String name, String description){
		this.name = name;
		this.description = description;
	}
}
