package org.myftp.gattserver.grass3.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.SimpleExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstraktní třída DAO tříd na získávání Entit z databáze nebo přes existující
 * entity.
 * 
 * @author gatt
 * 
 * @param <E>
 *            Entity class
 */
public abstract class AbstractDAO<E> {

	/**
	 * SessionFactoryBuilder
	 */
	@Resource(name = "entityServicesAggregator")
	private EntityServicesAggregator entityServicesAggregator;

	/**
	 * Třída entity - tedy doménového objektu
	 */
	protected Class<?> entityClass;

	/**
	 * Logger
	 */
	private Logger logger;

	/**
	 * Aktuálně používaná {@link Session}
	 */
	protected Session session;

	protected void log(String msg) {
		logger.info(msg);
	}

	public AbstractDAO(Class<?> entityClass) {
		this.entityClass = entityClass;
		logger = LoggerFactory.getLogger(entityClass);
	}

	protected void openSession() {
		session = entityServicesAggregator.getSessionFactory().openSession();
//		session = entityServicesAggregator.getSessionFactory().getCurrentSession();
	}

	/**
	 * <b>Důležité</b>
	 * 
	 * <p>
	 * Tuto metodu je nutné za sebou na konci všech proxy volání zavolat aby se
	 * uzavřela - volá se sama pokud dojde k pádu, ale pokud se dotaz na DB
	 * zdařil, tak se nezavolá, protože se počítá s tím, že budou volány proxy
	 * objekty, které potřebují aby session jejich původce zůstala otevřená
	 * </p>
	 * 
	 * <p>
	 * Dotazem na DB se v tomto případě myslí pouze select dotazy, nikoliv
	 * ukládání a updaty. Tam se nevrací entity ve kterých by mohly být proxy,
	 * jež by později mohly generovat select-y, takže tam je i v případě úspěchu
	 * session automaticky uzavřena.
	 * </p>
	 */
	public void closeSession() {
		if (session.isOpen() == false)
			session.close();
	}

	@SuppressWarnings("unchecked")
	protected List<E> findAllAndCast(Class<?> entityClass) {
		return (List<E>) session.createCriteria(entityClass)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	/**
	 * Najde všechny objekty a vrátí jejich list
	 * 
	 * @return list všech nalezených objektů
	 */
	public List<E> findAll() {
		Transaction tx = null;
		List<E> list = null;
		openSession();
		try {
			tx = session.beginTransaction();
			list = findAllAndCast(entityClass);
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

	@SuppressWarnings("unchecked")
	protected E findByIdAndCast(Class<?> entityClass, Serializable id) {
		return (E) session.load(entityClass, id);
	}

	/**
	 * Získá objekt dle jeho ID
	 * 
	 * @param id
	 *            identifikátor, dle kterého je možné jednoznačně objekt určit
	 * @return hledaný objekt
	 */
	public E findByID(Serializable id) {
		Transaction tx = null;
		E entity = null;
		openSession();
		try {
			tx = session.beginTransaction();
			entity = findByIdAndCast(entityClass, id);
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			closeSession();
			return null;
		}
		return entity;
	}

	/**
	 * Získá objekt dle jeho zadaných pravidel
	 * 
	 * @param expression
	 *            {@link SimpleExpression} výraz dle kterého se bude hledat
	 * 
	 * @return list hledaných objektů
	 */
	@SuppressWarnings("unchecked")
	public List<E> findByRestriction(Criterion criterion, Order order,
			Integer maxResults) {
		Transaction tx = null;
		List<E> list = null;
		openSession();
		try {
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(entityClass);
			if (criterion != null)
				criteria.add(criterion);
			if (order != null)
				criteria.addOrder(order);
			list = (List<E>) criteria.setResultTransformer(
					Criteria.DISTINCT_ROOT_ENTITY).list();
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			closeSession();
			return null;
		}

		/**
		 * Tohle není úplně košér, protože to omezuje výsledky programově až
		 * poté, co jsou všechny vybrány z DB - je to bohužel vlastnost
		 * hibernate, která způsobuje že během outer joinu (collections apod. to
		 * způsobí) se ponechají identické řádky. Max results tady taky
		 * nepomůže, protože DISTINCT_ROOT_ENTITY se aplikuje až na výsledný
		 * omezený počet řádků, což by mělo být naopak.
		 */
		if (maxResults != null) {
			int limit = list.size() <= maxResults ? list.size() : maxResults;
			return list.subList(0, limit > 0 ? limit : 1);
		} else {
			return list;
		}
	}

	/**
	 * Uloží objekt
	 * 
	 * @param entity
	 *            objekt, který bude uložen
	 * @return id objekt, kterým je uložený objekt identifikován
	 */
	public Object save(E entity) {
		Transaction tx = null;
		Object id = null;
		openSession();
		try {
			tx = session.beginTransaction();
			id = session.save(entity);
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			return null;
		} finally {
			closeSession();
		}
		return id;
	}

	/**
	 * Uloží skupinu objektů
	 * 
	 * @param entityList
	 *            list objektů, které budou uloženy
	 * @return ids list objektů, kterými jsou uložené objekty identifikovány
	 */
	public List<Object> save(List<E> entityList) {
		List<Object> ids = new ArrayList<Object>();
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();
			for (E entity : entityList) {
				ids.add(session.save(entity));
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			return null;
		} finally {
			closeSession();
		}
		return ids;
	}

	/**
	 * Updatuje objekt
	 * 
	 * @param entity
	 *            objekt, který bude uložen
	 */
	public boolean merge(E entity) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();
			session.merge(entity);
			tx.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			return false;
		} finally {
			closeSession();
		}
	}

	/**
	 * Updatuje skupinu objektů
	 * 
	 * @param entityList
	 *            list objektů, které budou updatovány
	 */
	public boolean merge(List<E> entityList) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();
			for (E entity : entityList) {
				session.merge(entity);
			}
			tx.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			return false;
		} finally {
			closeSession();
		}
	}

	/**
	 * Smaže objekt
	 * 
	 * @param entity
	 *            objekt, který bude smazán
	 */
	public boolean delete(E entity) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();
			session.delete(entity);
			tx.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			return false;
		} finally {
			closeSession();
		}
	}

}
