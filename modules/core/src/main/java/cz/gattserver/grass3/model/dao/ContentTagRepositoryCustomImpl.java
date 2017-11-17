package cz.gattserver.grass3.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.model.domain.QContentTag;
import cz.gattserver.grass3.model.dto.ContentTagCountTO;
import cz.gattserver.grass3.model.dto.QContentTagCountTO;

@Repository
public class ContentTagRepositoryCustomImpl implements ContentTagRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public int countContentTagContents(Long id) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QContentTag c = QContentTag.contentTag;
		return query.select(c.contentNodes.size()).from().where(c.id.eq(id)).fetchOne();
	}

	@Override
	public List<ContentTagCountTO> countContentTagsContents() {
		JPAQuery<ContentTagCountTO> query = new JPAQuery<>(entityManager);
		QContentTag c = QContentTag.contentTag;
		return query.select(new QContentTagCountTO(c.id, c.contentNodes.size())).from().orderBy(c.name.asc()).fetch();
	}

	@Override
	public ContentTagCountTO findTagContentNodesLowestCount() {
		JPAQuery<ContentTagCountTO> query = new JPAQuery<>(entityManager);
		QContentTag c = QContentTag.contentTag;
		return query.select(new QContentTagCountTO(c.id, c.contentNodes.size())).from()
				.orderBy(c.contentNodes.size().asc()).fetchOne();
	}

	@Override
	public ContentTagCountTO findTagContentNodesHighestCount() {
		JPAQuery<ContentTagCountTO> query = new JPAQuery<>(entityManager);
		QContentTag c = QContentTag.contentTag;
		return query.select(new QContentTagCountTO(c.id, c.contentNodes.size())).from()
				.orderBy(c.contentNodes.size().desc()).fetchOne();
	}

}
