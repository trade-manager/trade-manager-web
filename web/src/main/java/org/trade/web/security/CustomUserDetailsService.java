package org.trade.web.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.trade.core.persistent.user.User;
import org.trade.core.persistent.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        Optional<User> user = userService.getUserByUsername(username);

        if (user.isPresent()) {

            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.get().getRole().getName()));
            return mapUserToCustomUserDetails(user.get(), authorities);
        }

        return null;
    }

    private CustomUserDetails mapUserToCustomUserDetails(User user, List<SimpleGrantedAuthority> authorities) {

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setId(user.getId());
        customUserDetails.setUsername(user.getUsername());
        customUserDetails.setPassword(user.getPassword());
        customUserDetails.setName(user.getName());
        customUserDetails.setEmail(user.getEmail());
        customUserDetails.setAuthorities(authorities);
        return customUserDetails;
    }
}
