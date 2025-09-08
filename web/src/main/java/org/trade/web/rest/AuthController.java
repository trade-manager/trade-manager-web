package org.trade.web.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.trade.core.persistent.user.DuplicatedUserInfoException;
import org.trade.core.persistent.user.User;
import org.trade.core.persistent.user.UserRecord;
import org.trade.core.persistent.user.UserService;
import org.trade.web.rest.request.LoginRequest;
import org.trade.web.rest.response.AuthResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final DomainService domainService;
    private final RoleService roleService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(final DomainService domainService, final RoleService roleService, final UserService userService, final PasswordEncoder passwordEncoder) {

        this.domainService = domainService;
        this.roleService = roleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        Optional<User> userOptional = userService.validUsernameAndPassword(loginRequest.username(), loginRequest.password());

        if (userOptional.isPresent()) {

            User user = userOptional.get();
            return ResponseEntity.ok(new AuthResponse(user.getId(), user.getName(), user.getRole().getName()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public AuthResponse signUp(@Valid @RequestBody UserRecord userRecord) {

        if (userService.hasUserWithUsername(userRecord.username())) {

            throw new DuplicatedUserInfoException(String.format("Username %s is already been used", userRecord.username()));
        }

        if (userService.hasUserWithEmail(userRecord.email())) {

            throw new DuplicatedUserInfoException(String.format("Email %s is already been used", userRecord.email()));
        }

        Domain domain = domainService.findDomainByName(userRecord.domain().name());
        List<Role> roles = new ArrayList<>();

        for (RoleRecord roleRecord : userRecord.roles()) {

            Role role = roleService.findRoleByName(roleRecord.name());
            roles.add(role);
        }

        User user = UserController.from(userRecord, this.passwordEncoder.encode(userRecord.password()), domain, roles);
        user = userService.saveUser(user);
        return new AuthResponse(user.getId(), user.getName(), user.getRole().getName());
    }
}
