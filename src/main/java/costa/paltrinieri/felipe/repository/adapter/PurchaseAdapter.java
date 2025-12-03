package costa.paltrinieri.felipe.repository.adapter;

import costa.paltrinieri.felipe.domain.Purchase;
import costa.paltrinieri.felipe.repository.model.PurchaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurchaseAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseAdapter.class);
    public static final PurchaseAdapter INSTANCE = new PurchaseAdapter();

    private PurchaseAdapter() {
    }

    public PurchaseEntity toEntity(final Purchase purchase) {
        if (purchase == null) {
            LOGGER.warn("Received null Purchase");

            return null;
        }

        LOGGER.debug("Converting Purchase to Entity");

        PurchaseEntity entity = new PurchaseEntity();
        entity.setId(purchase.getId());
        entity.setDescription(purchase.getDescription());
        entity.setTransactionDate(purchase.getTransactionDate());
        entity.setPurchaseAmount(purchase.getPurchaseAmount());

        return entity;
    }

    public Purchase toDomain(final PurchaseEntity purchaseEntity) {
        if (purchaseEntity == null) {
            LOGGER.warn("Received null PurchaseEntity");

            return null;
        }

        LOGGER.debug("Converting PurchaseEntity to Domain");

        Purchase purchase = new Purchase();
        purchase.setId(purchaseEntity.getId());
        purchase.setDescription(purchaseEntity.getDescription());
        purchase.setTransactionDate(purchaseEntity.getTransactionDate());
        purchase.setPurchaseAmount(purchaseEntity.getPurchaseAmount());

        return purchase;
    }

}
