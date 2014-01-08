package org.myftp.gattserver.grass3.grocery.dao;

import org.myftp.gattserver.grass3.grocery.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends
		JpaRepository<Product, Long> {

}
