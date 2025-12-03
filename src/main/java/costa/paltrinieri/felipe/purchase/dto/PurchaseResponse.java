package costa.paltrinieri.felipe.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseResponse {

    private String id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal purchaseAmount;

    public PurchaseResponse() {
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
        this.purchaseAmount = purchaseAmount;
    }

}
