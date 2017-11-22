package cz.gattserver.grass3.medic.web;

import cz.gattserver.common.Identifiable;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.ui.components.GridOperationsTab;
import cz.gattserver.web.common.SpringContextHelper;

public abstract class MedicPageTab<T extends Identifiable> extends GridOperationsTab<T> {

	private static final long serialVersionUID = 2057957439013190170L;

	protected MedicFacade medicFacade;

	public MedicPageTab(Class<T> clazz) {
		super(clazz);
	}

	@Override
	protected void init() {
		medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		super.init();
	}
}
