package org.myftp.gattserver.grass3.model.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.myftp.gattserver.grass3.model.service.IEntityService;

public class BasicEntityServiceListener {

	/**
	 * Entity
	 */
	private static List<IEntityService> services = new ArrayList<IEntityService>();

	public static List<IEntityService> getServices() {
		return Collections.unmodifiableList(services);
	}

	public static void bind(IEntityService service) {
		services.add(service);
	}

}