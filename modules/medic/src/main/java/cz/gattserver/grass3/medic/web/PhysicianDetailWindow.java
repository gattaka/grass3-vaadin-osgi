package cz.gattserver.grass3.medic.web;

import cz.gattserver.grass3.medic.dto.PhysicianDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.ui.windows.DetailWindow;
import cz.gattserver.web.common.spring.SpringContextHelper;

public class PhysicianDetailWindow extends DetailWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	public PhysicianDetailWindow(Long id) {
		super("Detail instituce");

		final PhysicianDTO physicianDTO = SpringContextHelper.getBean(MedicFacade.class).getPhysicianById(id);
		addDetailLine("Jméno", physicianDTO.getName());
	}

}
