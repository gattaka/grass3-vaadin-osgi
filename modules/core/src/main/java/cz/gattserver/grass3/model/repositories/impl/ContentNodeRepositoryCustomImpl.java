package cz.gattserver.grass3.model.repositories.impl;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass3.interfaces.QContentNodeOverviewTO;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.QContentNode;
import cz.gattserver.grass3.model.domain.QContentTag;
import cz.gattserver.grass3.model.domain.QNode;
import cz.gattserver.grass3.model.domain.QUser;
import cz.gattserver.grass3.model.repositories.ContentNodeRepositoryCustom;
import cz.gattserver.grass3.model.util.QuerydslUtil;

@Repository
public class ContentNodeRepositoryCustomImpl implements ContentNodeRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createByUserAccessPredicate(Long userId, boolean admin) {
		QContentNode c = QContentNode.contentNode;
		QUser u = QUser.user;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(ExpressionUtils.anyOf(c.draft.isFalse(), c.draft.isNull()));
		if (!admin) {
			if (userId != null)
				builder.and(ExpressionUtils.anyOf(c.publicated.isTrue(), u.id.eq(userId)));
			else
				builder.and(c.publicated.isTrue());
		}

		return builder.getValue();
	}

	@Override
	public QueryResults<ContentNodeOverviewTO> findByUserAccess(Long userId, boolean admin, Pageable pageable) {
		JPAQuery<ContentNode> query = new JPAQuery<>(entityManager);
		QContentNode c = QContentNode.contentNode;
		QNode n = QNode.node;
		QUser u = QUser.user;
		QuerydslUtil.applyPagination(pageable, query);
		return query.from(c).innerJoin(c.parent, n).innerJoin(c.author, u)
				.where(createByUserAccessPredicate(userId, admin))
				.select(new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id, c.creationDate,
						c.lastModificationDate, c.publicated, u.name, u.id, c.id))
				.fetchResults();
	}

	@Override
	public QueryResults<ContentNodeOverviewTO> findByTagAndUserAccess(Long tagId, Long userId, boolean admin,
			Pageable pageable) {
		JPAQuery<ContentNode> query = new JPAQuery<>(entityManager);
		QContentNode c = QContentNode.contentNode;
		QNode n = QNode.node;
		QUser u = QUser.user;
		QContentTag t = QContentTag.contentTag;
		QuerydslUtil.applyPagination(pageable, query);
		return query.from(t).innerJoin(t.contentNodes, c).innerJoin(c.parent, n).innerJoin(c.author, u)
				.where(createByUserAccessPredicate(userId, admin), c.id.eq(tagId))
				.select(new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id, c.creationDate,
						c.lastModificationDate, c.publicated, u.name, u.id, c.id))
				.orderBy(new OrderSpecifier<LocalDateTime>(Order.DESC, c.creationDate)).fetchResults();
	}

	@Override
	public QueryResults<ContentNodeOverviewTO> findByUserFavouritesAndUserAccess(Long favouritesUserId, Long userId,
			boolean admin, Pageable pageable) {
		JPAQuery<ContentNode> query = new JPAQuery<>(entityManager);
		QContentNode c = QContentNode.contentNode;
		QNode n = QNode.node;
		QUser u = QUser.user;
		QuerydslUtil.applyPagination(pageable, query);
		return query.from(u).innerJoin(u.favourites, c).innerJoin(c.parent, n).innerJoin(c.author, u)
				.where(createByUserAccessPredicate(userId, admin), u.id.eq(favouritesUserId))
				.select(new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id, c.creationDate,
						c.lastModificationDate, c.publicated, u.name, u.id, c.id))
				.orderBy(new OrderSpecifier<LocalDateTime>(Order.DESC, c.creationDate)).fetchResults();
	}

	@Override
	public QueryResults<ContentNodeOverviewTO> findByNodeAndUserAccess(Long nodeId, Long userId, boolean admin,
			Pageable pageable) {
		JPAQuery<ContentNode> query = new JPAQuery<>(entityManager);
		QContentNode c = QContentNode.contentNode;
		QNode n = QNode.node;
		QUser u = QUser.user;
		QuerydslUtil.applyPagination(pageable, query);
		return query.from(c).innerJoin(c.parent, n).innerJoin(c.author, u)
				.where(createByUserAccessPredicate(userId, admin), n.id.eq(nodeId))
				.select(new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id, c.creationDate,
						c.lastModificationDate, c.publicated, u.name, u.id, c.id))
				.orderBy(new OrderSpecifier<LocalDateTime>(Order.DESC, c.creationDate)).fetchResults();
	}

	@Override
	public QueryResults<ContentNodeOverviewTO> findByNameAndUserAccess(String name, Long userId, boolean admin,
			Pageable pageable) {
		JPAQuery<ContentNode> query = new JPAQuery<>(entityManager);
		QContentNode c = QContentNode.contentNode;
		QNode n = QNode.node;
		QUser u = QUser.user;
		QuerydslUtil.applyPagination(pageable, query);
		return query.from(c).innerJoin(c.parent, n).innerJoin(c.author, u)
				.where(createByUserAccessPredicate(userId, admin), c.name.toLowerCase().like(name.toLowerCase()))
				.select(new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id, c.creationDate,
						c.lastModificationDate, c.publicated, u.name, u.id, c.id))
				.orderBy(new OrderSpecifier<LocalDateTime>(Order.DESC, c.creationDate)).fetchResults();
	}

}
