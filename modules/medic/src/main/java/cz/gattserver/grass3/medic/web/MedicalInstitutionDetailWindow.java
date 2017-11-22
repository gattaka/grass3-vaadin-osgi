package cz.gattserver.grass3.medic.web;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.ui.windows.DetailWindow;
import cz.gattserver.web.common.SpringContextHelper;

public class MedicalInstitutionDetailWindow extends DetailWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	private MedicFacade medicalFacade;

	public MedicalInstitutionDetailWindow(Long id) {
		super("Detail instituce");

		medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		final MedicalInstitutionDTO medicalInstitutionDTO = medicalFacade.getMedicalInstitutionById(id);

		addDetailLine("Název", medicalInstitutionDTO.getName());

		Link link = new Link(medicalInstitutionDTO.getWeb(), new ExternalResource(medicalInstitutionDTO.getWeb()));
		link.setTargetName("_blank");
		addDetailLine("Web", link);

		addDetailLine("Adresa", medicalInstitutionDTO.getAddress());

		Label label;
		label = addDetailLine("Hodiny", medicalInstitutionDTO.getHours());
		label.setContentMode(ContentMode.PREFORMATTED);

		setContent(layout);

	}
}
