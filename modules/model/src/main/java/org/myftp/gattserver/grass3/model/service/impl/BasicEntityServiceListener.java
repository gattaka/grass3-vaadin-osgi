package org.myftp.gattserver.grass3.model.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.myftp.gattserver.grass3.model.service.IEntityServiceListener;
import org.myftp.gattserver.grass3.model.service.IEntityService;


public class BasicEntityServiceListener implements IEntityServiceListener {

	/**
	 * Verze "sestavení" entit. Došlo k nějaké změně ? Má se přegenerovat
	 * SessionFactory ?
	 */
	private Long version = 1L;

	/**
	 * Entity
	 */
	private List<IEntityService> services = Collections
			.synchronizedList(new ArrayList<IEntityService>());

	public synchronized List<IEntityService> getServices() {
		return services;
	}

	public synchronized void setServices(List<IEntityService> sectionServices) {
		this.services = sectionServices;
	}

	public synchronized void bind(IEntityService section) {
		System.out.println("Registred DBUnitService");
		version++;
	}

	public synchronized void unbind(IEntityService section) {
		System.out.println("UnRegistred DBUnitService");
		version++;
	}

	public synchronized Long getVersion() {
		return version;
	}

}