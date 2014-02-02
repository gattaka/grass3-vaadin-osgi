package org.myftp.gattserver.grass3.grocery.web;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.grocery.facade.IGroceryFacade;
import org.myftp.gattserver.grass3.model.dto.Identifiable;
import org.myftp.gattserver.grass3.template.TableOperationsTab;

public abstract class GroceryPageTab<T extends Identifiable> extends
		TableOperationsTab<T> {

	private static final long serialVersionUID = 2057957439013190170L;

	protected IGroceryFacade groceryFacade;

	public GroceryPageTab(Class<T> clazz) {
		super(clazz);
	}

	@Override
	protected void init() {
		groceryFacade = SpringContextHelper.getBean(IGroceryFacade.class);
		super.init();
	}
}