package org.myftp.gattserver.grass3.medic.web;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.medic.web.templates.AbstractDetailSubWindow;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

public class MedicalInstitutionDetailWindow extends AbstractDetailSubWindow {

	private static final long serialVersionUID = -1240133390770972624L;

	private IMedicFacade medicalFacade;

	public MedicalInstitutionDetailWindow(final Component triggerComponent,
			Long id) {
		super("Detail instituce", triggerComponent);

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		final MedicalInstitutionDTO medicalInstitutionDTO = medicalFacade
				.getMedicalInstitutionById(id);

		addDetailLine("Název", medicalInstitutionDTO.getName());

		Link link = new Link(medicalInstitutionDTO.getWeb(),
				new ExternalResource(medicalInstitutionDTO.getWeb()));
		link.setTargetName("_blank");
		addDetailLine("Web", link);

		addDetailLine("Adresa", medicalInstitutionDTO.getAddress());

		Label label;
		label = addDetailLine("Hodiny", medicalInstitutionDTO.getHours());
		label.setContentMode(ContentMode.PREFORMATTED);

		setContent(layout);

	}
}
