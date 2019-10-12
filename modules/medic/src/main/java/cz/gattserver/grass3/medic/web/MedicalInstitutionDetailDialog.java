package cz.gattserver.grass3.medic.web;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.WebDialog;

public class MedicalInstitutionDetailDialog extends WebDialog {

	private static final long serialVersionUID = -1240133390770972624L;

	private transient MedicFacade medicFacade;

	public MedicalInstitutionDetailDialog(Long id) {
		final MedicalInstitutionDTO medicalInstitutionDTO = getMedicFacade().getMedicalInstitutionById(id);

		add(new H2("Název"));
		add(medicalInstitutionDTO.getName());

		add(new H2("Web"));
		Anchor link = new Anchor(medicalInstitutionDTO.getWeb());
		link.setTarget("_blank");
		add(link);

		add(new H2("Adresa"));
		add(medicalInstitutionDTO.getAddress());

		add(new H2("Hodiny"));
		Div div = new Div();
		div.setText(medicalInstitutionDTO.getHours());
		div.getStyle().set("white-space", "pre");
		add(div);
	}

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}

}
