package org.myftp.gattserver.grass3.model.service;

import java.util.List;

import org.myftp.gattserver.grass3.model.service.IEntityService;

public interface IEntityServiceListener {

	public List<IEntityService> getServices();

	public Long getVersion();

}