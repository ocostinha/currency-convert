package costa.paltrinieri.felipe.repository.jpa;

import costa.paltrinieri.felipe.repository.model.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseJpaRepository extends JpaRepository<PurchaseEntity, String> {

}