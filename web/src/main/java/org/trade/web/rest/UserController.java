package org.trade.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.domain.DomainService;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleRecord;
import org.trade.core.persistent.role.RoleService;
import org.trade.core.persistent.user.User;
import org.trade.core.persistent.user.UserRecord;
import org.trade.core.persistent.user.UserService;
import org.trade.web.service.CustomUserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.trade.web.config.SwaggerConfig.BASIC_AUTH_SECURITY_SCHEME;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final DomainService domainService;
    private final RoleService roleService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(final DomainService domainService, final RoleService roleService, final UserService userService, final PasswordEncoder passwordEncoder) {

        this.domainService = domainService;
        this.roleService = roleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @GetMapping("/me")
    public UserRecord getCurrentUser(@AuthenticationPrincipal CustomUserDetails currentUser) {

        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        return UserRecord.from(user);
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @GetMapping
    public List<UserRecord> getUsers() {

        List<User> users = userService.getUsers();
        return userService.getUsers().stream().map(UserRecord::from).collect(Collectors.toList());
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @GetMapping("/{username}")
    public UserRecord getUser(@PathVariable String username) {

        User user = userService.validateAndGetUserByUsername(username);
        return UserRecord.from(user);
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserRecord createUser(@Valid @RequestBody UserRecord userRecord) {

        Domain domain = domainService.findDomainByName(userRecord.domain().name());
        List<Role> roles = new ArrayList<>();

        for (RoleRecord roleRecord : userRecord.roles()) {

            Role role = roleService.findRoleByName(roleRecord.name());
            roles.add(role);
        }

        User user = UserController.from(userRecord, this.passwordEncoder.encode(userRecord.password()), domain, roles);
        user = userService.saveUser(user);
        return UserRecord.from(user);
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @DeleteMapping("/{username}")
    public UserRecord deleteUser(@PathVariable String username) {

        User user = userService.validateAndGetUserByUsername(username);
        userService.deleteUser(user);
        return UserRecord.from(user);
    }

    public static User from(UserRecord userRecord, String password, Domain domain, List<Role> roles) {

        return new User(userRecord.name(), userRecord.username(), userRecord.name(), userRecord.name(), userRecord.email(), password, domain, roles);
    }
}
