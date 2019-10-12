package cz.gattserver.grass3.medic.web;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;

import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.interfaces.MedicamentTO;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.WebDialog;

public class MedicamentDetailDialog extends WebDialog {

	private static final long serialVersionUID = -1240133390770972624L;

	public MedicamentDetailDialog(Long id) {
		super("Detail instituce");

		final MedicamentTO medicamentDTO = SpringContextHelper.getBean(MedicFacade.class).getMedicamentById(id);

		add(new H2("Název"));
		add(medicamentDTO.getName());

		add(new H2("Reakce"));
		Div div = new Div();
		div.setText(medicamentDTO.getTolerance());
		div.getStyle().set("white-space", "pre");
		add(div);
	}
}
