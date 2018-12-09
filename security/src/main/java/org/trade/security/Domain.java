package org.trade.security;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "domain")
public class Domain {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long version;
    private String name;
    private String description;

    private Domain() {
    }

    public Domain(String name, String description) {
        this.name = name;
        this.description = description;
    }


    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
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
    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
