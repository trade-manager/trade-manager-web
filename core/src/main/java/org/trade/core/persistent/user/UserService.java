package org.trade.core.persistent.user;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface UserService {

    /**
     * Method findAll.
     *
     * @return List<User>
     */
    List<User> findAll();

    /**
     * Method findUserByName.
     *
     * @param name String
     * @return User
     */
    User findUserByName(String name);

    /**
     * Method findByUsername.
     *
     * @param username String
     * @return User
     */
    User findByUsername(String username);

    /**
     * Method hasWithUsername.
     *
     * @param username String
     * @return boolean
     */
    boolean hasWithUsername(String username);

    /**
     * Method hasWithEmail.
     *
     * @param email String
     * @return boolean
     */
    boolean hasWithEmail(String email);

    /**
     * Method validateAndFindByUsername.
     *
     * @param username String
     * @return User
     */
    User validateAndFindByUsername(String username);

    /**
     * Method validateAndFindUserById.
     *
     * @param id Long
     * @return User
     */
    User validateAndFindUserById(Long id);

    /**
     * Method save.
     *
     * @param user User
     * @return User
     */
    User save(User user);

    /**
     * Method delete.
     *
     * @param user User
     */
    void delete(User user);

    /**
     * Method validUsernameAndPassword.
     *
     * @param username String
     * @param password String
     * @return Optional<User>
     */
    Optional<User> validUsernameAndPassword(String username, String password);
}
