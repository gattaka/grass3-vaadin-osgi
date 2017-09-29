package cz.gattserver.grass3.medic.web;

import cz.gattserver.grass3.medic.dto.PhysicianDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.template.AbstractDetailSubWindow;
import cz.gattserver.web.common.SpringContextHelper;

public class PhysicianDetailWindow extends AbstractDetailSubWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	private MedicFacade medicalFacade;

	public PhysicianDetailWindow(Long id) {
		super("Detail instituce");
		medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		final PhysicianDTO physicianDTO = medicalFacade.getPhysicianById(id);
		addDetailLine("Jméno", physicianDTO.getName());
		setContent(layout);
	}

}
