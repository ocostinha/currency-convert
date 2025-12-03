package costa.paltrinieri.felipe.purchase.controller;

import costa.paltrinieri.felipe.core.business.PurchaseBusiness;
import costa.paltrinieri.felipe.purchase.adapter.PurchaseAdapter;
import costa.paltrinieri.felipe.purchase.dto.PurchaseConvertedResponse;
import costa.paltrinieri.felipe.purchase.dto.PurchaseRequest;
import costa.paltrinieri.felipe.purchase.dto.PurchaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseController {

    private static final PurchaseAdapter adapter = PurchaseAdapter.INSTANCE;
    private final PurchaseBusiness purchaseBusiness;

    public PurchaseController(final PurchaseBusiness purchaseBusiness) {
        this.purchaseBusiness = purchaseBusiness;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseResponse createPurchase(@Valid @RequestBody PurchaseRequest request) {
        return adapter.toResponse(
            this.purchaseBusiness.createPurchase(
                adapter.toDomain(request)
            )
        );
    }

    @GetMapping("/{id}")
    public PurchaseConvertedResponse getPurchase(@PathVariable String id,
                                                 @RequestParam(required = false, defaultValue = "United States-Dollar") String currency) {
        return adapter.toConvertedResponse(purchaseBusiness.getPurchaseWithConversion(id, currency));
    }

}
