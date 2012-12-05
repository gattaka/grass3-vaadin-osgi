package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.User;

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
		return (List<User>) criteria.list();
	}

	/**
	 * Získá objekt dle jeho zadaných pravidel
	 * 
	 * @param expression
	 *            {@link SimpleExpression} výraz dle kterého se bude hledat
	 * 
	 * @return DTO hledaného objektu
	 */
	public final List<User> findByFavouriteContent(ContentNode contentNode) {
		Transaction tx = null;
		List<User> list = null;
		openSession();
		try {
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(entityClass);
			criteria.createAlias("favourites", "content");
			criteria.add(Restrictions.eq("content.contentID",
					contentNode.getContentId()));
			criteria.add(Restrictions.eq("content.contentReaderID",
					contentNode.getContentReaderId()));

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
}
