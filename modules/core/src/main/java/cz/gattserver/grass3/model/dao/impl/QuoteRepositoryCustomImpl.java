package cz.gattserver.grass3.model.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.model.dao.QuoteRepositoryCustom;
import cz.gattserver.grass3.model.domain.QQuote;

@Repository
public class QuoteRepositoryCustomImpl implements QuoteRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public String findRandom(long random) {
		JPAQuery<String> query = new JPAQuery<>(entityManager);
		QQuote q = QQuote.quote;
		return query.select(q.name).from(q).offset(random).limit(1).fetchOne();
	}

}
