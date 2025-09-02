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
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.user.DuplicatedUserInfoException;
import org.trade.core.persistent.user.User;
import org.trade.core.persistent.user.UserService;
import org.trade.web.rest.dto.AuthResponse;
import org.trade.web.rest.dto.LoginRequest;
import org.trade.web.rest.dto.SignUpRequest;

import java.util.Optional;

import static org.trade.core.persistent.role.Role.ROLE_USER;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(final UserService userService, final PasswordEncoder passwordEncoder) {

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
    public AuthResponse signUp(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (userService.hasUserWithUsername(signUpRequest.username())) {

            throw new DuplicatedUserInfoException(String.format("Username %s is already been used", signUpRequest.username()));
        }

        if (userService.hasUserWithEmail(signUpRequest.email())) {

            throw new DuplicatedUserInfoException(String.format("Email %s is already been used", signUpRequest.email()));
        }

        User user = userService.saveUser(this.mapSignUpRequestToUser(signUpRequest));
        return new AuthResponse(user.getId(), user.getName(), user.getRole().getName());
    }

    private User mapSignUpRequestToUser(SignUpRequest signUpRequest) {

        User user = new User();
        user.setUsername(signUpRequest.username());
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        user.setName(signUpRequest.name());
        user.setEmail(signUpRequest.email());
        user.addRole(new Role(ROLE_USER, ROLE_USER));
        return user;
    }
}
