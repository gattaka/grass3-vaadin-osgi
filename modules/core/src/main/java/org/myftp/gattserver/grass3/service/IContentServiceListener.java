package org.myftp.gattserver.grass3.service;

import java.util.List;

public interface IContentServiceListener {

	public List<IContentService> getServices();

	public IContentService getContentServiceByName(String contentReaderID);

}
