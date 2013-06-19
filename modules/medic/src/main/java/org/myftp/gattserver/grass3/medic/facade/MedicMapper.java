package org.myftp.gattserver.grass3.medic.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.myftp.gattserver.grass3.medic.domain.MedicalInstitution;
import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.springframework.stereotype.Component;

@Component("medicMapper")
public class MedicMapper {

	public MedicalInstitutionDTO mapMedicalInstitution(MedicalInstitution e) {
		if (e == null)
			return null;

		MedicalInstitutionDTO dto = new MedicalInstitutionDTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setAddress(e.getAddress());
		dto.setWeb(e.getWeb());
		return dto;
	}

	public Collection<? extends MedicalInstitutionDTO> mapMedicalInstitutions(
			List<MedicalInstitution> e) {
		if (e == null)
			return null;

		List<MedicalInstitutionDTO> list = new ArrayList<MedicalInstitutionDTO>();
		for (MedicalInstitution i : e) {
			list.add(mapMedicalInstitution(i));
		}

		return list;
	}

}
