package org.trade.web;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "domain")
public class Domain {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Version
    @JsonIgnore
    @Column(name = "version")
    private Long version;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    private Domain() {
    }

    public Domain(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
