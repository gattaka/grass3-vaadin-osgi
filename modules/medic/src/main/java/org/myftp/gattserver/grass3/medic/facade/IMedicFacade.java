package org.myftp.gattserver.grass3.medic.facade;

import java.util.Collection;

import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;

public interface IMedicFacade {

	boolean deleteMedicalInstitution(MedicalInstitutionDTO dto);

	Collection<? extends MedicalInstitutionDTO> getAllMedicalInstitutions();

	boolean saveMedicalInstitution(MedicalInstitutionDTO dto);

}
