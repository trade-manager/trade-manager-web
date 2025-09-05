package org.trade.core.persistent.domain;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;

    public DomainServiceImpl(final DomainRepository domainRepository) {

        this.domainRepository = domainRepository;
    }

    public Optional<Domain> findDomainByName(String name) {

        return domainRepository.findByName(name);
    }

    public Domain saveDomain(Domain domain) {

        return domainRepository.save(domain);
    }

    public void deleteDomain(Domain domain) {

        domainRepository.delete(domain);
    }
}