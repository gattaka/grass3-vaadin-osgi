package org.myftp.gattserver.grass3.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.ContentTag;
import org.myftp.gattserver.grass3.model.domain.Quote;
import org.myftp.gattserver.grass3.model.domain.User;
import org.myftp.gattserver.grass3.model.service.IEntityService;

/**
 * Sdružuje třídy entit a hromadně je jako služba registruje u model bundle
 * 
 * @author gatt
 * 
 */
public class CoreEntityService implements IEntityService {

	/**
	 * Mělo by být immutable
	 */
	List<Class<?>> domainClasses = new ArrayList<Class<?>>();

	public CoreEntityService() {
		domainClasses.add(User.class);
		domainClasses.add(ContentNode.class);
		domainClasses.add(ContentTag.class);
		domainClasses.add(Quote.class);

		// nakonec zamkni
		domainClasses = Collections.unmodifiableList(domainClasses);
	}

	public List<Class<?>> getDomainClasses() {
		return domainClasses;
	}

}
