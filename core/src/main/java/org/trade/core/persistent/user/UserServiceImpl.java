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

    public List<User> getUsers() {

        return userRepository.findAllByOrderByName();
    }

    public User getUserByUsername(String username) {

        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean hasUserWithUsername(String username) {

        return userRepository.existsByUsername(username);
    }

    public boolean hasUserWithEmail(String email) {

        return userRepository.existsByEmail(email);
    }

    public User validateAndGetUserByUsername(String username) {

        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(String.format("User with username %s not found", username)));
    }

    @Transactional
    public User saveUser(User instance) {

        List<Role> roles = new ArrayList<>();

        for (Role role : instance.getRoles()) {

            Optional<Role> current = roleRepository.findByName(role.getName());
            current.ifPresent(roles::add);
        }

        instance.getRoles().clear();
        instance.setRoles(roles);
        return userRepository.save(instance);
    }

    @Override
    public void deleteUser(User user) {

        if (null == user) {

            return;
        }

        userRepository.delete(user);
    }

    @Override
    public Optional<User> validUsernameAndPassword(String username, String password) {

        return userRepository.findByUsername(username).filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    public User findUserByName(String name) {

        return userRepository.findByName(name).orElse(null);
    }

    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

        User user = this.userRepository.findByName(name).orElseThrow(() -> new UserNotFoundException(String.format("User with name %s not found", name)));
        _log.info("User found: {}, password: {}, roles: {}", user.getName(), user.getPassword(), String.join(",", user.getRoleValues()));

        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRoleValues()));
    }
}