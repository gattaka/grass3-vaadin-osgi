package org.myftp.gattserver.grass3.medic.web;

import java.util.Collection;

import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.medic.web.templates.TableOperationsTab;
import org.myftp.gattserver.grass3.util.SpringInjector;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public class MedicalInstitutionsTab extends
		TableOperationsTab<MedicalInstitutionDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	@Autowired
	private IMedicFacade medicFacade;

	@Override
	protected Collection<MedicalInstitutionDTO> getTableItems() {
		return medicFacade.getAllMedicalInstitutions();
	}

	@Override
	protected Window createCreateWindow(Component... triggerComponent) {
		return new MedicalInstitutionCreateWindow(triggerComponent) {
			private static final long serialVersionUID = 5711665262096833291L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected Window createDetailWindow(Long id, Component... triggerComponent) {
		return new MedicalInstitutionDetailWindow(id, triggerComponent);
	}

	@Override
	protected Window createModifyWindow(MedicalInstitutionDTO dto,
			Component... triggerComponent) {
		return new MedicalInstitutionCreateWindow(dto, triggerComponent) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected void deleteEntity(MedicalInstitutionDTO dto) {
		medicFacade.deleteMedicalInstitution(dto);
	}

	@Override
	protected void init() {
		// SpringInjector.inject(this);
		medicFacade = SpringInjector.getContext().getBean(IMedicFacade.class);
		super.init();
	}

	public MedicalInstitutionsTab() {
		super(MedicalInstitutionDTO.class);
	}
}
