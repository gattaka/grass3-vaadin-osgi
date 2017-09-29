package cz.gattserver.grass3.medic.web;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

import cz.gattserver.grass3.medic.dto.MedicamentDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.template.AbstractDetailSubWindow;
import cz.gattserver.web.common.SpringContextHelper;

public class MedicamentDetailWindow extends AbstractDetailSubWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	private MedicFacade medicalFacade;

	public MedicamentDetailWindow(Long id) {
		super("Detail instituce");

		medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		final MedicamentDTO medicamentDTO = medicalFacade.getMedicamentById(id);

		addDetailLine("Název", medicamentDTO.getName());

		Label label;
		label = addDetailLine("Reakce", medicamentDTO.getTolerance());
		label.setContentMode(ContentMode.PREFORMATTED);

		setContent(layout);

	}
}
