package org.myftp.gattserver.grass3.model.dao;

import java.util.Random;

import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.domain.Quote;
import org.springframework.stereotype.Component;

@Component("quoteDAO")
public class QuoteDAO extends AbstractDAO<Quote> {

	public QuoteDAO() {
		super(Quote.class);
	}

	/**
	 * Získá počet entit typu Quote v db
	 * 
	 * @return počet nalezených entit nebo {@code null} pokud došlo k chybě
	 */
	public Long count() {
		Long result;
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();
			result = (Long) session.createCriteria(Quote.class)
					.setProjection(Projections.rowCount()).uniqueResult();
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
		return result == null ? 0 : result;
	}

	public Quote chooseRandomQuote() {
		Quote result = null;
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();

			Long count = (Long) session.createCriteria(Quote.class)
					.setProjection(Projections.rowCount()).uniqueResult();

			if (count != 0) {
				Random generator = new Random();
				Long randomId = Math.abs(generator.nextLong()) % count + 1;
				result = (Quote) session.load(Quote.class, randomId);
			}
			tx.commit();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			return null;
		} finally {
			closeSession();
		}
	}
}
