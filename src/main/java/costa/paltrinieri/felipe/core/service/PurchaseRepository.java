package costa.paltrinieri.felipe.core.service;

import costa.paltrinieri.felipe.domain.Purchase;

import java.util.Optional;

public interface PurchaseRepository {

    Purchase save(final Purchase purchase);

    Optional<Purchase> findById(final String id);

}
