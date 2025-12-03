package costa.paltrinieri.felipe.fiscalData.service;

import costa.paltrinieri.felipe.core.service.FiscalDataService;
import costa.paltrinieri.felipe.fiscalData.gateway.FiscalDataGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class FiscalDataServiceImpl implements FiscalDataService {

    private final FiscalDataGateway gateway;

    public FiscalDataServiceImpl(FiscalDataGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Optional<BigDecimal> getExchangeRate(final String currency, final LocalDate purchaseDate) {
        return gateway.getExchangeRate(currency, purchaseDate);
    }

}
