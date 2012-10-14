package org.myftp.gattserver.grass3.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.myftp.gattserver.grass3.service.IContentService;
import org.myftp.gattserver.grass3.service.IContentServiceListener;

public class BasicContentServiceListener implements IContentServiceListener {

	private List<IContentService> services = Collections
			.synchronizedList(new ArrayList<IContentService>());

	public synchronized List<IContentService> getServices() {
		return services;
	}

	public synchronized void setServices(List<IContentService> sectionServices) {
		services = sectionServices;
	}

	public synchronized IContentService getContentServiceByName(
			String contentReaderID) {
		for (IContentService contentService : services) {
			if (contentService.getContentID().equals(contentReaderID))
				return contentService;
		}
		return null;
	}
}
