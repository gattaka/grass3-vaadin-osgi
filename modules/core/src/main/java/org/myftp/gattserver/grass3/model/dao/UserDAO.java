package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.User;
import org.springframework.stereotype.Component;

@Component("userDAO")
public class UserDAO extends AbstractDAO<User> {

	public UserDAO() {
		super(User.class);
	}

	public List<User> findByName(String name) {
		return findByRestriction(Restrictions.eq("name", name), null, null);
	}

	public List<User> findByNameAndPass(String name, String passwordHash) {
		return findByRestriction(
				Restrictions.and(Restrictions.eq("name", name),
						Restrictions.eq("password", passwordHash)), null, null);
	}

	@SuppressWarnings("unchecked")
	private List<User> findAndCast(Criteria criteria) {
		return (List<User>) criteria.setResultTransformer(
				Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	/**
	 * Má daný uživatel obsah v oblíbených ?
	 */
	public boolean hasContentInFavourites(Long nodeId, Long userId) {
		Transaction tx = null;
		List<User> list = null;
		openSession();
		try {
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(entityClass);
			criteria.createAlias("favourites", "content");
			criteria.add(Restrictions.eq("content.id", nodeId));
			criteria.add(Restrictions.eq("id", userId));
			list = findAndCast(criteria);

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			closeSession();
			return false;
		}
		return list.size() != 0;
	}

	/**
	 * Má někdo daný obsah v oblíbených ? Pokud ano, vrať tyto uživatele.
	 */
	public List<User> findByFavouriteContent(Long nodeId) {
		Transaction tx = null;
		List<User> list = null;
		openSession();
		try {
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(entityClass);
			criteria.createAlias("favourites", "content");
			criteria.add(Restrictions.eq("content.id", nodeId));
			list = findAndCast(criteria);

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			closeSession();
			return null;
		}
		return list;
	}

	public boolean addContentToFavourites(Long nodeId, Long userId) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();

			User user = (User) session.load(User.class, userId);
			if (user == null) {
				tx.rollback();
				return false;
			}

			ContentNode node = (ContentNode) session.load(ContentNode.class,
					nodeId);
			if (node == null) {
				tx.rollback();
				return false;
			}

			user.getFavourites().add(node);
			session.merge(user);

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			closeSession();
			return false;
		}
		return true;
	}

	public boolean removeContentFromFavourites(Long nodeId, Long userId) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();

			User user = (User) session.load(User.class, userId);
			if (user == null) {
				tx.rollback();
				return false;
			}

			ContentNode node = (ContentNode) session.load(ContentNode.class,
					nodeId);
			if (node == null) {
				tx.rollback();
				return false;
			}

			user.getFavourites().remove(node);
			session.merge(user);

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			closeSession();
			return false;
		}
		return true;
	}

}
