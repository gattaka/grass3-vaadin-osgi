package cz.gattserver.grass3.medic.web;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.medic.dto.MedicamentDTO;
import cz.gattserver.grass3.medic.facade.IMedicFacade;
import cz.gattserver.grass3.template.AbstractDetailSubWindow;

public class MedicamentDetailWindow extends AbstractDetailSubWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	private IMedicFacade medicalFacade;

	public MedicamentDetailWindow(Long id, final Component... triggerComponent) {
		super("Detail instituce", triggerComponent);

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		final MedicamentDTO medicamentDTO = medicalFacade.getMedicamentById(id);

		addDetailLine("Název", medicamentDTO.getName());

		Label label;
		label = addDetailLine("Reakce", medicamentDTO.getTolerance());
		label.setContentMode(ContentMode.PREFORMATTED);

		setContent(layout);

	}
}
