package org.myftp.gattserver.grass3.hw.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.myftp.gattserver.grass3.hw.domain.HWItem;
import org.myftp.gattserver.grass3.hw.domain.HWItemType;
import org.myftp.gattserver.grass3.hw.domain.ServiceNote;
import org.myftp.gattserver.grass3.model.service.IEntityService;
import org.springframework.stereotype.Component;

/**
 * Sdružuje třídy entit a hromadně je jako služba registruje u model bundle
 * 
 * @author gatt
 * 
 */
@Component("hwEntityService")
public class HWEntityService implements IEntityService {

	/**
	 * Mělo by být immutable
	 */
	List<Class<?>> domainClasses = new ArrayList<Class<?>>();

	public HWEntityService() {
		domainClasses.add(HWItemType.class);
		domainClasses.add(ServiceNote.class);
		domainClasses.add(HWItem.class);

		// nakonec zamkni
		domainClasses = Collections.unmodifiableList(domainClasses);
	}

	public List<Class<?>> getDomainClasses() {
		return domainClasses;
	}

}
