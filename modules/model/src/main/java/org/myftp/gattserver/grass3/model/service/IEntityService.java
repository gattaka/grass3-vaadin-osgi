package org.myftp.gattserver.grass3.model;

import java.util.List;

/**
 * Interface, přes který se model modulu předávají k registraci třídy entit
 * 
 * @author gatt
 * 
 */
public interface DBUnitService {

	public List<Class<?>> getDomainClasses();

}
