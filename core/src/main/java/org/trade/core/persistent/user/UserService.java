package org.trade.core.persistent.user;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface UserService {

    /**
     * @return List<User>
     */
    List<User> getUsers();

    /**
     * @param username String
     * @return User
     */
    User getUserByUsername(String username);

    /**
     * @param username String
     * @return boolean
     */
    boolean hasUserWithUsername(String username);

    /**
     * @param email String
     * @return boolean
     */
    boolean hasUserWithEmail(String email);

    /**
     * @param username String
     * @return User
     */
    User validateAndGetUserByUsername(String username);

    /**
     * @param user User
     * @return User
     */
    User saveUser(User user);

    /**
     * @param user User
     */
    void deleteUser(User user);

    /**
     * @param username String
     * @param password String
     * @return String
     */
    Optional<User> validUsernameAndPassword(String username, String password);

    /**
     * Method findUserByName.
     *
     * @param name String
     * @return User
     */
    User findUserByName(String name);
}
