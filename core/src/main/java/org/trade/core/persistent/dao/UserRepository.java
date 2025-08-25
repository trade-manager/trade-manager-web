package org.trade.core.persistent.dao;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.trade.core.dao.AspectRepository;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@RepositoryRestResource(exported = false)
public interface UserRepository extends AspectRepository<User, Long>, UserRepositoryCustom {

    User findByName(String name);

}

