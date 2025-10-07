package org.trade.core.persistent.portfolio;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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

    @Transactional
    public Portfolio findById(Long id) {

        Portfolio portfolio = this.portfolioRepository.findById(id).orElse(null);

        if (null != portfolio) {

            portfolio.getTradestrategies().size();
            return portfolio;
        }

        return null;
    }

    @Transactional
    public Portfolio findByName(String name) {

        Portfolio portfolio = this.portfolioRepository.findByName(name).orElse(null);

        if (null != portfolio) {

            portfolio.getAccounts().size();
            return portfolio;
        }

        return null;
    }

    public Portfolio validateAndGet(String name) {

        return portfolioRepository.findByName(name).orElseThrow(() -> new PortfolioNotFoundException(String.format("Portfolio with name %s not found", name)));
    }


    @Transactional
    public void resetDefault(final Portfolio instance) {

        List<Portfolio> items = this.findAll();

        for (Portfolio item : items) {

            item.setIsDefault(Objects.equals(item.getId(), instance.getId()));
            item = this.save(item);
            instance.setIsDefault(item.getIsDefault());
        }
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
    public List<Portfolio> findAll() {

        return portfolioRepository.findAll();
    }

    public Portfolio save(Portfolio portfolio) {

        return portfolioRepository.save(portfolio);
    }

    public void delete(Portfolio portfolio) {

        if (null == portfolio) {

            return;
        }

        portfolioRepository.delete(portfolio);
    }
}