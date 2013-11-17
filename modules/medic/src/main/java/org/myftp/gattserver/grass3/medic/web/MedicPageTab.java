package org.myftp.gattserver.grass3.medic.web;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.model.dto.Identifiable;
import org.myftp.gattserver.grass3.template.TableOperationsTab;

public abstract class MedicPageTab<T extends Identifiable> extends
		TableOperationsTab<T> {

	private static final long serialVersionUID = 2057957439013190170L;

	protected IMedicFacade medicFacade;

	public MedicPageTab(Class<T> clazz) {
		super(clazz);
	}

	@Override
	protected void init() {
		medicFacade = SpringContextHelper.getBean(IMedicFacade.class);
		super.init();
	}
}
