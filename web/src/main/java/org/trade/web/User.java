package org.trade.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Entity
@Table(name = "user")
public class User {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Setter
    @Column(name = "name")
    private String name;

    @Setter
    @Column(name = "first_name")
    private String firstName;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Setter
    @Column(name = "roles")
    private String[] roles;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "domain_id", nullable = false)
    private Domain domain;

    protected User() {
    }

    public User(String name, String password, Domain domain, String... roles) {

        this.name = name;
        this.setPassword(password);
        this.roles = roles;
        this.domain = domain;
    }


    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

}


