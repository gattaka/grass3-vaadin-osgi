package cz.gattserver.grass3.medic.web.tabs;

import java.io.Serializable;
import java.util.Collection;

import cz.gattserver.common.Identifiable;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.ui.components.GridOperationsTab;
import cz.gattserver.web.common.spring.SpringContextHelper;

public abstract class MedicPageTab<T extends Identifiable, C extends Collection<T> & Serializable>
		extends GridOperationsTab<T, C> {

	private static final long serialVersionUID = 2057957439013190170L;

	private transient MedicFacade medicFacade;

	public MedicPageTab(Class<T> clazz) {
		super(clazz);
	}

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}
}
