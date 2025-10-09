package org.trade.core.persistent.user;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface UserRepository extends AspectRepository<User, Long> {


    /**
     * Method findAllByOrderByName.
     *
     * @return List<Role>
     */
    List<User> findAllByOrderByName();

    /**
     * @param name String
     * @return User
     */
    Optional<User> findByName(String name);

    /**
     * @param username String
     * @return Optional<User>
     */
    Optional<User> findByUsername(String username);

    /**
     * @param username String
     * @return boolean
     */
    boolean existsByUsername(String username);

    /**
     * @param email String
     * @return boolean
     */
    boolean existsByEmail(String email);
}

