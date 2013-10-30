package org.myftp.gattserver.grass3.medic.facade;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.medic.dao.MedicalInstitutionRepository;
import org.myftp.gattserver.grass3.medic.dao.MedicalRecordRepository;
import org.myftp.gattserver.grass3.medic.dao.MedicamentRepository;
import org.myftp.gattserver.grass3.medic.dao.ScheduledVisitRepository;
import org.myftp.gattserver.grass3.medic.domain.MedicalInstitution;
import org.myftp.gattserver.grass3.medic.domain.Medicament;
import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicamentDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("medicFacade")
public class MedicFacade implements IMedicFacade {

	@Autowired
	private MedicalInstitutionRepository institutionRepository;

	@Autowired
	private ScheduledVisitRepository scheduledVisitRepository;

	@Autowired
	private MedicalRecordRepository medicalRecordRepository;

	@Autowired
	private MedicamentRepository medicamentRepository;

	@Resource(name = "medicMapper")
	private MedicMapper medicMapper;

	// Instituce

	@Override
	public void deleteMedicalInstitution(MedicalInstitutionDTO institution) {
		institutionRepository.delete(institution.getId());
	}

	@Override
	public List<MedicalInstitutionDTO> getAllMedicalInstitutions() {
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

	// Návštěvy

	@Override
	public void deleteScheduledVisit(ScheduledVisitDTO dto) {
		scheduledVisitRepository.delete(dto.getId());
	}

	@Override
	public List<ScheduledVisitDTO> getAllScheduledVisits() {
		return medicMapper.mapScheduledVisits(scheduledVisitRepository
				.findAll());
	}

	@Override
	public boolean saveScheduledVisit(ScheduledVisitDTO dto) {
		// TODO Auto-generated method stub
		return false;
	}

	// Záznamy

	@Override
	public void deleteMedicalRecord(MedicalRecordDTO dto) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MedicalRecordDTO> getAllMedicalRecords() {
		return medicMapper.mapMedicalRecords(medicalRecordRepository.findAll());
	}

	@Override
	public boolean saveMedicalRecord(MedicalRecordDTO dto) {
		// TODO Auto-generated method stub
		return false;
	}

	// Medikamenty

	@Override
	public void deleteMedicament(MedicamentDTO dto) {
		medicamentRepository.delete(dto.getId());
	}

	@Override
	public List<MedicamentDTO> getAllMedicaments() {
		return medicMapper.mapMedicaments(medicamentRepository.findAll());
	}

	@Override
	public boolean saveMedicament(MedicamentDTO dto) {
		Medicament medicament = new Medicament();
		medicament.setName(dto.getName());
		medicament.setTolerance(dto.getTolerance());
		return medicamentRepository.save(medicament) != null;
	}
}
