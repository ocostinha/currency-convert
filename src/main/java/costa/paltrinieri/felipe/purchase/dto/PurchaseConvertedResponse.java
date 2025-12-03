package costa.paltrinieri.felipe.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseConvertedResponse {

    private String id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal originalAmount;
    private String originalCurrency = "United States-Dollar";
    private BigDecimal exchangeRate;
    private BigDecimal convertedAmount;
    private String targetCurrency;

    public PurchaseConvertedResponse() {
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

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(final BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getOriginalCurrency() {
        return originalCurrency;
    }

    public void setOriginalCurrency(final String originalCurrency) {
        this.originalCurrency = originalCurrency;
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
