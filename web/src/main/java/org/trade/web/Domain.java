package org.trade.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;


@Setter
@Getter
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

}
