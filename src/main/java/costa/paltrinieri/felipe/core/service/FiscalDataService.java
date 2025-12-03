package costa.paltrinieri.felipe.core.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface FiscalDataService {

    Optional<BigDecimal> getExchangeRate(final String currency, final LocalDate purchaseDate);

}
