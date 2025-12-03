package costa.paltrinieri.felipe.core.business;

import costa.paltrinieri.felipe.domain.Purchase;

public interface PurchaseBusiness {

    Purchase createPurchase(final Purchase purchase);

    Purchase getPurchaseWithConversion(final String id, final String currency);

}
