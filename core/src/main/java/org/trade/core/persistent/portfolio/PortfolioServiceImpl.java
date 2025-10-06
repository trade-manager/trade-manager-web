package org.trade.core.persistent.portfolio;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class PortfolioServiceImpl implements PortfolioService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PortfolioRepository portfolioRepository;

    public PortfolioServiceImpl(final PortfolioRepository portfolioRepository) {

        this.portfolioRepository = portfolioRepository;
    }

    public Portfolio findPortfolioById(Long id) {

        return this.portfolioRepository.findById(id).orElse(null);
    }

    public Portfolio findPortfolioByName(String name) {

        return this.portfolioRepository.findByName(name).orElse(null);
    }

    public Portfolio validateAndGetPortfolio(String name) {

        return portfolioRepository.findByName(name).orElseThrow(() -> new PortfolioNotFoundException(String.format("Portfolio with name %s not found", name)));
    }

    /**
     * Method findDefault.
     *
     * @return Portfolio
     */
    public Portfolio findDefault() {

        Portfolio portfolio = null;
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Portfolio> query = builder.createQuery(Portfolio.class);
        Root<Portfolio> from = query.from(Portfolio.class);
        query.select(from);
        List<Portfolio> items = entityManager.createQuery(query).getResultList();

        for (Portfolio item : items) {

            if (item.getIsDefault()) {

                // item.getPortfolioAccounts().size();
                portfolio = item;
                break;
            }
        }

        return portfolio;
    }

    /**
     * Method findAllPortfolios.
     *
     * @return Portfolio
     */
    public List<Portfolio> findAllPortfolios() {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Portfolio> query = builder.createQuery(Portfolio.class);
        Root<Portfolio> from = query.from(Portfolio.class);
        query.select(from);
        return entityManager.createQuery(query).getResultList();
    }

    public Portfolio savePortfolio(Portfolio portfolio) {

        return portfolioRepository.save(portfolio);
    }

    public void deletePortfolio(Portfolio portfolio) {

        if (null == portfolio) {

            return;
        }

        portfolioRepository.delete(portfolio);
    }

}