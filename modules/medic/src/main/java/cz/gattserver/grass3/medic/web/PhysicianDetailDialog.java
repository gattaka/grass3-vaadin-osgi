package cz.gattserver.grass3.medic.web;

import com.vaadin.flow.component.dialog.Dialog;

import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.interfaces.PhysicianTO;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.Strong;

public class PhysicianDetailDialog extends Dialog {

	private static final long serialVersionUID = -1240133390770972624L;

	public PhysicianDetailDialog(Long id) {
		final PhysicianTO physicianDTO = SpringContextHelper.getBean(MedicFacade.class).getPhysicianById(id);
		add(new Strong("Jméno"));
		add(physicianDTO.getName());
	}

}
