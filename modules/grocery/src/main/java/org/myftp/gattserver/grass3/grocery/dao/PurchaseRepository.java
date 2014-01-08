package org.myftp.gattserver.grass3.grocery.dao;

import org.myftp.gattserver.grass3.grocery.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

}
