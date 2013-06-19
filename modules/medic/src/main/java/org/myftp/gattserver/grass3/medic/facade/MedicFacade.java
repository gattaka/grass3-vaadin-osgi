package org.myftp.gattserver.grass3.medic.facade;

import java.util.Collection;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.medic.dao.MedicalInstitutionRepository;
import org.myftp.gattserver.grass3.medic.domain.MedicalInstitution;
import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("medicFacade")
public class MedicFacade implements IMedicFacade {

	@Autowired
	private MedicalInstitutionRepository institutionRepository;

	@Resource(name = "medicMapper")
	private MedicMapper medicMapper;

	@Override
	public boolean deleteMedicalInstitution(MedicalInstitutionDTO institution) {
		institutionRepository.delete(institution.getId());
		return true;
	}

	@Override
	public Collection<? extends MedicalInstitutionDTO> getAllMedicalInstitutions() {
		return medicMapper.mapMedicalInstitutions(institutionRepository
				.findAll());
	}

	@Override
	public boolean saveMedicalInstitution(MedicalInstitutionDTO dto) {
		MedicalInstitution entity = new MedicalInstitution();
		entity.setAddress(dto.getAddress());
		entity.setHours(dto.getHours());
		entity.setName(dto.getName());
		entity.setWeb(dto.getWeb());
		return institutionRepository.save(entity) != null;
	}
}
