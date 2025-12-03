package costa.paltrinieri.felipe.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PurchaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreatePurchaseSuccessfully() throws Exception {
        String requestBody = """
                             {
                                 "description": "Coffee purchase",
                                 "transactionDate": "2024-01-15",
                                 "purchaseAmount": 25.50
                             }
                             """;

        mockMvc.perform(post("/api/v1/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.description").value("Coffee purchase"))
            .andExpect(jsonPath("$.transactionDate").value("2024-01-15"))
            .andExpect(jsonPath("$.purchaseAmount").value(25.50));
    }

    @Test
    void shouldGetPurchaseById() throws Exception {
        String requestBody = """
                             {
                                 "description": "Coffee purchase",
                                 "transactionDate": "2024-01-15",
                                 "purchaseAmount": 25.50
                             }
                             """;

        String purchaseId = mockMvc.perform(post("/api/v1/purchases")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestBody))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/v1/purchases/" + purchaseId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(purchaseId))
            .andExpect(jsonPath("$.description").value("Coffee purchase"))
            .andExpect(jsonPath("$.transactionDate").value("2024-01-15"))
            .andExpect(jsonPath("$.originalAmount").value(25.50))
            .andExpect(jsonPath("$.originalCurrency").value("United States-Dollar"))
            .andExpect(jsonPath("$.exchangeRate").value(1.0))
            .andExpect(jsonPath("$.convertedAmount").value(25.50))
            .andExpect(jsonPath("$.targetCurrency").value("United States-Dollar"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Euro Zone-Euro", "Brazil-Real"})
    void shouldGetPurchaseByIdWithCurrencyConversion(String currency) throws Exception {
        String requestBody = """
                             {
                                 "description": "Coffee purchase",
                                 "transactionDate": "2024-01-15",
                                 "purchaseAmount": 25.50
                             }
                             """;

        String purchaseId = mockMvc.perform(post("/api/v1/purchases")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestBody))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/v1/purchases/" + purchaseId)
                            .param("currency", currency))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(purchaseId))
            .andExpect(jsonPath("$.description").value("Coffee purchase"))
            .andExpect(jsonPath("$.transactionDate").value("2024-01-15"))
            .andExpect(jsonPath("$.originalAmount").value(25.50))
            .andExpect(jsonPath("$.originalCurrency").value("United States-Dollar"))
            .andExpect(jsonPath("$.exchangeRate").exists())
            .andExpect(jsonPath("$.convertedAmount").exists())
            .andExpect(jsonPath("$.targetCurrency").value(currency));
    }

    @Test
    void shouldReturnBadRequestWhenDescriptionIsEmpty() throws Exception {
        String requestBody = """
                             {
                                 "description": "",
                                 "transactionDate": "2024-01-15",
                                 "purchaseAmount": 25.50
                             }
                             """;

        mockMvc.perform(post("/api/v1/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenDescriptionExceedsMaxLength() throws Exception {
        String requestBody = """
                             {
                                 "description": "This is a very long description that exceeds fifty characters",
                                 "transactionDate": "2024-01-15",
                                 "purchaseAmount": 25.50
                             }
                             """;

        mockMvc.perform(post("/api/v1/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenAmountIsNegative() throws Exception {
        String requestBody = """
                             {
                                 "description": "Invalid purchase",
                                 "transactionDate": "2024-01-15",
                                 "purchaseAmount": -10.00
                             }
                             """;

        mockMvc.perform(post("/api/v1/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenPurchaseDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/purchases/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenCurrencyIsInvalid() throws Exception {
        String requestBody = """
                             {
                                 "description": "Coffee purchase",
                                 "transactionDate": "2024-01-15",
                                 "purchaseAmount": 25.50
                             }
                             """;

        String purchaseId = mockMvc.perform(post("/api/v1/purchases")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestBody))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/v1/purchases/" + purchaseId)
                            .param("currency", "INVALID"))
            .andExpect(status().isUnprocessableEntity());
    }

}
