package org.myftp.gattserver.grass3.hw.dao;

import org.hibernate.Transaction;
import org.myftp.gattserver.grass3.hw.domain.ServiceNote;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.springframework.stereotype.Component;

@Component("serviceNoteDAO")
public class ServiceNoteDAO extends AbstractDAO<ServiceNote> {

	public ServiceNoteDAO() {
		super(ServiceNote.class);
	}

	public boolean delete(Long id) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();
			ServiceNote article = findByIdAndCast(entityClass, id);
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
