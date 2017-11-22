package cz.gattserver.grass3.model.repositories.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.model.domain.QContentTag;
import cz.gattserver.grass3.model.repositories.ContentTagRepositoryCustom;

@Repository
public class ContentTagRepositoryCustomImpl implements ContentTagRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public int countContentTagContents(Long id) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QContentTag c = QContentTag.contentTag;
		return query.select(c.contentNodes.size()).from(c).where(c.id.eq(id)).fetchOne();
	}

	@Override
	public List<Tuple> countContentTagsContents() {
		JPAQuery<Tuple> query = new JPAQuery<>(entityManager);
		QContentTag c = QContentTag.contentTag;
		return query.select(c.id, c.contentNodes.size()).from(c).groupBy(c.id).orderBy(c.contentNodes.size().asc()).fetch();
	}

}
