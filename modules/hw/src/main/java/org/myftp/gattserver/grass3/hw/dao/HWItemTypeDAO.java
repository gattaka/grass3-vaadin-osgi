package org.myftp.gattserver.grass3.hw.dao;

import org.hibernate.Transaction;
import org.myftp.gattserver.grass3.hw.domain.HWItemType;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.springframework.stereotype.Component;

@Component("hwItemTypeDAO")
public class HWItemTypeDAO extends AbstractDAO<HWItemType> {

	public HWItemTypeDAO() {
		super(HWItemType.class);
	}
	
	public boolean delete(Long id) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();
			HWItemType article = findByIdAndCast(entityClass, id);			
			session.delete(article);
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
