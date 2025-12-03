package costa.paltrinieri.felipe.purchase.adapter;

import costa.paltrinieri.felipe.domain.Purchase;
import costa.paltrinieri.felipe.purchase.dto.PurchaseConvertedResponse;
import costa.paltrinieri.felipe.purchase.dto.PurchaseRequest;
import costa.paltrinieri.felipe.purchase.dto.PurchaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PurchaseAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseAdapter.class);
    public static final PurchaseAdapter INSTANCE = new PurchaseAdapter();

    private PurchaseAdapter() {
    }

    public Purchase toDomain(final PurchaseRequest purchaseRequest) {
        if (purchaseRequest == null) {
            LOGGER.warn("Received null PurchaseRequest");

            return null;
        }

        LOGGER.debug("Converting PurchaseRequest to Domain");

        Purchase purchase = new Purchase();
        purchase.setId(UUID.randomUUID().toString());
        purchase.setDescription(purchaseRequest.getDescription());
        purchase.setTransactionDate(purchaseRequest.getTransactionDate());
        purchase.setPurchaseAmount(purchaseRequest.getPurchaseAmount());

        return purchase;
    }

    public PurchaseResponse toResponse(final Purchase purchase) {
        if (purchase == null) {
            LOGGER.warn("Received null Purchase");

            return null;
        }

        LOGGER.debug("Converting Purchase to Response");

        PurchaseResponse response = new PurchaseResponse();
        response.setId(purchase.getId());
        response.setDescription(purchase.getDescription());
        response.setTransactionDate(purchase.getTransactionDate());
        response.setPurchaseAmount(purchase.getPurchaseAmount());

        return response;
    }

    public PurchaseConvertedResponse toConvertedResponse(final Purchase purchase) {
        if (purchase == null) {
            LOGGER.warn("Received null Purchase");

            return null;
        }

        LOGGER.debug("Converting Purchase to ConvertedResponse");

        PurchaseConvertedResponse response = new PurchaseConvertedResponse();
        response.setId(purchase.getId());
        response.setDescription(purchase.getDescription());
        response.setTransactionDate(purchase.getTransactionDate());
        response.setOriginalAmount(purchase.getPurchaseAmount());
        response.setOriginalCurrency("United States-Dollar");
        response.setExchangeRate(purchase.getExchangeRate());
        response.setConvertedAmount(purchase.getConvertedAmount());
        response.setTargetCurrency(purchase.getTargetCurrency());

        return response;
    }

}
