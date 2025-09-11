package org.trade.core.persistent.user;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface UserService {

    /**
     * Method getUsers.
     *
     * @return List<User>
     */
    List<User> getUsers();

    /**
     * Method getUserByUsername.
     *
     * @param username String
     * @return User
     */
    User getUserByUsername(String username);

    /**
     * Method hasUserWithUsername.
     *
     * @param username String
     * @return boolean
     */
    boolean hasUserWithUsername(String username);

    /**
     * Method hasUserWithEmail.
     *
     * @param email String
     * @return boolean
     */
    boolean hasUserWithEmail(String email);

    /**
     * Method validateAndGetUserByUsername.
     *
     * @param username String
     * @return User
     */
    User validateAndGetUserByUsername(String username);

    /**
     * Method validateAndGetUserById.
     *
     * @param id Long
     * @return User
     */
    User validateAndGetUserById(Long id);

    /**
     * Method saveUser.
     *
     * @param user User
     * @return User
     */
    User saveUser(User user);

    /**
     * Method deleteUser.
     *
     * @param user User
     */
    void deleteUser(User user);

    /**
     * Method validUsernameAndPassword.
     *
     * @param username String
     * @param password String
     * @return Optional<User>
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
