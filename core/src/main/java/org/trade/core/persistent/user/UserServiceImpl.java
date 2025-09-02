package org.trade.core.persistent.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class UserServiceImpl implements UserService {

    private final static Logger _log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(final UserRepository userRepository, final RoleRepository roleRepository, final PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getUsers() {

        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    public boolean hasUserWithUsername(String username) {

        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean hasUserWithEmail(String email) {

        return userRepository.existsByEmail(email);
    }

    @Override
    public User validateAndGetUserByUsername(String username) {

        return getUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with username %s not found", username)));
    }

    @Transactional
    public User saveUser(User instance) {

        List<Role> roles = new ArrayList<>();

        for (Role role : instance.getRoles()) {

            Role current = roleRepository.findByName(role.getName());

            roles.add(Objects.requireNonNullElse(current, role));
        }

        instance.getRoles().clear();
        instance.setRoles(roles);
        return userRepository.save(instance);
    }

    @Override
    public void deleteUser(User user) {

        userRepository.delete(user);
    }

    @Override
    public Optional<User> validUsernameAndPassword(String username, String password) {

        return getUserByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    public User findUserByName(String name) {

        return userRepository.findByName(name);
    }

    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

        User user = this.userRepository.findByName(name);
        _log.info("User found: " + user.getName() + " " + user.getPassword() + " " + String.join(",", user.getRoleValues()));

        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRoleValues()));
    }
}