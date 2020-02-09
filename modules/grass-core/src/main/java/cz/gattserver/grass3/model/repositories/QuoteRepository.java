package cz.gattserver.grass3.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.model.domain.Quote;

public interface QuoteRepository extends JpaRepository<Quote, Long>, QuoteRepositoryCustom {

}
