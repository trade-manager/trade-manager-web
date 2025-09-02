package org.trade.core.persistent.user;

import org.springframework.stereotype.Repository;
import org.trade.core.dao.AspectRepository;

import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface UserRepository extends AspectRepository<User, Long> {

    /**
     * @param name String
     * @return User
     */
    User findByName(String name);

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

