package costa.paltrinieri.felipe.purchase.repository;

import costa.paltrinieri.felipe.core.service.PurchaseRepository;
import costa.paltrinieri.felipe.domain.Purchase;
import costa.paltrinieri.felipe.repository.adapter.PurchaseAdapter;
import costa.paltrinieri.felipe.repository.jpa.PurchaseJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PurchaseRepositoryImpl implements PurchaseRepository {

    private static final PurchaseAdapter adapter = PurchaseAdapter.INSTANCE;
    private final PurchaseJpaRepository repository;

    public PurchaseRepositoryImpl(PurchaseJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Purchase save(final Purchase purchase) {
        return adapter.toDomain(
            repository.save(
                adapter.toEntity(purchase)
            )
        );
    }

    @Override
    public Optional<Purchase> findById(final String id) {
        return repository.findById(id).map(adapter::toDomain);
    }

}
