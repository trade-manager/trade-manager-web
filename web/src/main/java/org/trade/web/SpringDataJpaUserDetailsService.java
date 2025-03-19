package org.trade.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class SpringDataJpaUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    private static final Logger _log = LogManager.getLogger(SpringDataJpaUserDetailsService.class);

    @Autowired
    public SpringDataJpaUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = this.repository.findByName(name);

        _log.info("User found: " + user.getName() + " " + user.getPassword() + " " + String.join(",", user.getRoles()));

        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRoles()));
    }

}
