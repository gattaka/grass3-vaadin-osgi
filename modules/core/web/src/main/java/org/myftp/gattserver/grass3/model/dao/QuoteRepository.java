package org.myftp.gattserver.grass3.model.dao;

import org.myftp.gattserver.grass3.model.domain.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, Long> {

}
