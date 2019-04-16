package cz.gattserver.grass3.medic.web;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

import cz.gattserver.grass3.medic.dto.MedicamentDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.ui.windows.DetailWindow;
import cz.gattserver.web.common.spring.SpringContextHelper;

public class MedicamentDetailWindow extends DetailWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	public MedicamentDetailWindow(Long id) {
		super("Detail instituce");

		final MedicamentDTO medicamentDTO = SpringContextHelper.getBean(MedicFacade.class).getMedicamentById(id);

		addDetailLine("Název", medicamentDTO.getName());

		Label label;
		label = addDetailLine("Reakce", medicamentDTO.getTolerance());
		label.setContentMode(ContentMode.PREFORMATTED);

	}
}
