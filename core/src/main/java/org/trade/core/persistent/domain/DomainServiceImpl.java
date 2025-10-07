package org.trade.core.persistent.domain;

import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Domain> finaAll() {

        return domainRepository.findAllByOrderByName();
    }

    public Domain findByName(String name) {

        return domainRepository.findByName(name).orElse(null);
    }

    public Domain validateAndGet(String name) {

        return domainRepository.findByName(name).orElseThrow(() -> new DomainNotFoundException(String.format("Domain with name %s not found", name)));
    }

    public Domain save(Domain domain) {

        return domainRepository.save(domain);
    }

    public void delete(Domain domain) {

        if (null == domain) {

            return;
        }

        domainRepository.delete(domain);
    }
}