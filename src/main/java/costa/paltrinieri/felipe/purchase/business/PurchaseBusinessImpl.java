package costa.paltrinieri.felipe.purchase.business;

import costa.paltrinieri.felipe.core.business.PurchaseBusiness;
import costa.paltrinieri.felipe.core.exceptions.ConvertException;
import costa.paltrinieri.felipe.core.exceptions.NotFoundException;
import costa.paltrinieri.felipe.core.service.FiscalDataService;
import costa.paltrinieri.felipe.core.service.PurchaseRepository;
import costa.paltrinieri.felipe.domain.Purchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PurchaseBusinessImpl implements PurchaseBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseBusinessImpl.class);
    private final static String DEFAULT_COUNTRY_CURRENCY = "United States-Dollar";

    private final PurchaseRepository purchaseRepository;
    private final FiscalDataService fiscalDataService;

    public PurchaseBusinessImpl(final PurchaseRepository purchaseRepository, final FiscalDataService fiscalDataService) {
        this.purchaseRepository = purchaseRepository;
        this.fiscalDataService = fiscalDataService;
    }

    @Override
    public Purchase createPurchase(final Purchase purchase) {
        LOGGER.info("Creating purchase: {}", purchase.getDescription());

        Purchase saved = purchaseRepository.save(purchase);

        LOGGER.info("Purchase created with ID: {}", saved.getId());

        return saved;
    }

    @Override
    public Purchase getPurchaseWithConversion(final String id, final String currency) {
        LOGGER.info("Getting purchase with ID: {} for currency: {}", id, currency);

        final Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> {
            LOGGER.error("Purchase not found with ID: {}", id);

            return new NotFoundException("Purchase not found");
        });

        if (DEFAULT_COUNTRY_CURRENCY.equals(currency)) {
            LOGGER.info("No conversion needed, using default currency: {}", DEFAULT_COUNTRY_CURRENCY);

            purchase.setExchangeRate(BigDecimal.ONE);
            purchase.setConvertedAmount(purchase.getPurchaseAmount());
            purchase.setTargetCurrency(currency);

            return purchase;
        }

        LOGGER.info("Converting purchase amount to currency: {}", currency);

        BigDecimal exchangeRate = fiscalDataService.getExchangeRate(currency, purchase.getTransactionDate())
            .orElseThrow(() -> {
                LOGGER.error("Cannot convert to currency: {}", currency);

                return new ConvertException("Purchase cannot be converted to the target currency");
            });

        purchase.setTargetCurrency(currency);
        purchase.setExchangeRate(exchangeRate);
        purchase.setConvertedAmount(
            purchase.getPurchaseAmount()
                .multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP)
        );

        LOGGER.info("Conversion completed. Rate: {}, Converted amount: {}", exchangeRate, purchase.getConvertedAmount());

        return purchase;
    }

}
