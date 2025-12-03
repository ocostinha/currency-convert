package costa.paltrinieri.felipe.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Purchase {

    private String id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal purchaseAmount;
    private BigDecimal exchangeRate;
    private BigDecimal convertedAmount;
    private String targetCurrency;

    public Purchase() {
    }

    public Purchase(final String id,
                    final String description,
                    final LocalDate transactionDate,
                    final BigDecimal purchaseAmount,
                    final BigDecimal exchangeRate,
                    final BigDecimal convertedAmount,
                    final String targetCurrency) {
        this.id = id;
        this.description = description;
        this.transactionDate = transactionDate;
        this.purchaseAmount = purchaseAmount;
        this.exchangeRate = exchangeRate;
        this.convertedAmount = convertedAmount;
        this.targetCurrency = targetCurrency;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(final LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(final BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(final BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(final BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(final String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

}
