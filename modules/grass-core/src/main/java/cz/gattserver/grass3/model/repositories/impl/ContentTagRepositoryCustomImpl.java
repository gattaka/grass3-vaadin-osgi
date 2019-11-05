package cz.gattserver.grass3.model.repositories.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

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
	public List<String> findByFilter(String filter, int offset, int limit) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QContentTag c = QContentTag.contentTag;
		query.offset(offset);
		query.limit(limit);
		return query.select(c.name).from(c).where(c.name.like("%" + filter + "%")).fetch();
	}

	@Override
	public Integer countByFilter(String filter) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QContentTag c = QContentTag.contentTag;
		return (int) query.select(c).from(c).where(c.name.like("%" + filter + "%")).fetchCount();
	}

}