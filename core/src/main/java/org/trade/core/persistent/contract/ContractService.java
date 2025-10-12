package org.trade.core.persistent.contract;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface ContractService {

    /**
     * Method findByUniqueKey.
     *
     * @param SECType    String
     * @param symbol     String
     * @param exchange   String
     * @param currency   String
     * @param expiryDate ZonedDateTime
     * @return List<Contract>
     */
    Contract findByUniqueKey(String SECType, String symbol, String exchange, String currency,
                             ZonedDateTime expiryDate);

    /**
     * Method findContractById.
     *
     * @param id Long
     * @return Contract
     */
    Contract findById(Long id);

    /**
     * Method findById.
     *
     * @return Candle
     */
    Contract validateAndGet(Long id);

    /**
     * Method findContractLiteById.
     *
     * @param id Long
     * @return ContractLite
     */
    ContractLite findLiteById(final Long id);

    /**
     * Method findContractBySymbol.
     *
     * @param symbol String
     * @return Optional<Contract>
     */
    Optional<Contract> findBySymbol(String symbol);

    /**
     * Method findAllContracts.
     *
     * @return Iterable<Contract>
     */
    Iterable<Contract> findAll();

}
