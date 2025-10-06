package org.trade.core.persistent.portfolio;

import org.springframework.stereotype.Repository;
import org.trade.core.dao.AspectRepository;

import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface PortfolioRepository extends AspectRepository<Portfolio, Long> {

    Optional<Portfolio> findByName(String name);
}
