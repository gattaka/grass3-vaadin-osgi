package org.myftp.gattserver.grass3.medic.web;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.dto.PhysicianDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.template.AbstractDetailSubWindow;

import com.vaadin.ui.Component;

public class PhysicianDetailWindow extends AbstractDetailSubWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	private IMedicFacade medicalFacade;

	public PhysicianDetailWindow(Long id, final Component... triggerComponent) {
		super("Detail instituce", triggerComponent);

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		final PhysicianDTO physicianDTO = medicalFacade.getPhysicianById(id);

		addDetailLine("Jméno", physicianDTO.getName());

		setContent(layout);

	}
}
