package org.trade.security;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(exported = false)
public interface DomainRepository extends Repository<Domain, Long> {

    Domain save(Domain domain);

    Domain findByName(String name);

}

