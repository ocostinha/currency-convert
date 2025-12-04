package costa.paltrinieri.felipe.purchase.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurchaseRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWithValidData() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(new BigDecimal("10.50"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenDescriptionIsNull() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription(null);
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(new BigDecimal("10.50"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenDescriptionIsBlank() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("   ");
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(new BigDecimal("10.50"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenDescriptionExceeds50Characters() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("A".repeat(51));
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(new BigDecimal("10.50"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldPassWhenDescriptionIs50Characters() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("A".repeat(50));
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(new BigDecimal("10.50"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenTransactionDateIsNull() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(null);
        request.setPurchaseAmount(new BigDecimal("10.50"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("transactionDate", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenTransactionDateIsInFuture() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(LocalDate.now().plusDays(1));
        request.setPurchaseAmount(new BigDecimal("10.50"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("transactionDate", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldPassWhenTransactionDateIsToday() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(new BigDecimal("10.50"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassWhenTransactionDateIsInPast() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(LocalDate.now().minusDays(1));
        request.setPurchaseAmount(new BigDecimal("10.50"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenPurchaseAmountIsNull() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(null);

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("purchaseAmount", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenPurchaseAmountIsZero() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(BigDecimal.ZERO);

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("purchaseAmount", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenPurchaseAmountIsNegative() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(new BigDecimal("-10.50"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("purchaseAmount", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldPassWhenPurchaseAmountIsMinimum() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(new BigDecimal("0.01"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRoundWhenPurchaseAmountHasMoreThan2DecimalPlaces() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(new BigDecimal("10.126"));

        assertEquals(new BigDecimal("10.13"), request.getPurchaseAmount());
    }

    @Test
    void shouldPassWhenPurchaseAmountHas2DecimalPlaces() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(new BigDecimal("10.99"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassWhenPurchaseAmountHasNoDecimalPlaces() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Valid description");
        request.setTransactionDate(LocalDate.now());
        request.setPurchaseAmount(new BigDecimal("10"));

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenMultipleFieldsAreInvalid() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription(null);
        request.setTransactionDate(null);
        request.setPurchaseAmount(null);

        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        assertEquals(3, violations.size());
    }

}
