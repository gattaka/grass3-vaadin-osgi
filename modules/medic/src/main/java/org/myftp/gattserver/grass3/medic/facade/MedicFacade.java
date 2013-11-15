package org.myftp.gattserver.grass3.medic.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.medic.dao.MedicalInstitutionRepository;
import org.myftp.gattserver.grass3.medic.dao.MedicalRecordRepository;
import org.myftp.gattserver.grass3.medic.dao.MedicamentRepository;
import org.myftp.gattserver.grass3.medic.dao.PhysicianRepository;
import org.myftp.gattserver.grass3.medic.dao.ScheduledVisitRepository;
import org.myftp.gattserver.grass3.medic.domain.MedicalInstitution;
import org.myftp.gattserver.grass3.medic.domain.MedicalRecord;
import org.myftp.gattserver.grass3.medic.domain.Medicament;
import org.myftp.gattserver.grass3.medic.domain.Physician;
import org.myftp.gattserver.grass3.medic.domain.ScheduledVisit;
import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicamentDTO;
import org.myftp.gattserver.grass3.medic.dto.PhysicianDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitState;
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
	private MedicalRecordRepository recordRepository;

	@Autowired
	private MedicamentRepository medicamentRepository;

	@Autowired
	private PhysicianRepository physicianRepository;

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
		MedicalInstitution institution = new MedicalInstitution();
		institution.setId(dto.getId());
		institution.setAddress(dto.getAddress());
		institution.setHours(dto.getHours());
		institution.setName(dto.getName());
		institution.setWeb(dto.getWeb());
		return institutionRepository.save(institution) != null;
	}

	@Override
	public MedicalInstitutionDTO getMedicalInstitutionById(Long id) {
		return medicMapper.mapMedicalInstitution(institutionRepository
				.findOne(id));
	}

	// Návštěvy

	@Override
	public void deleteScheduledVisit(ScheduledVisitDTO dto) {
		scheduledVisitRepository.delete(dto.getId());
	}

	@Override
	public List<ScheduledVisitDTO> getAllScheduledVisits(boolean planned) {
		return medicMapper.mapScheduledVisits(scheduledVisitRepository
				.findByPlanned(planned));
	}

	@Override
	public ScheduledVisitDTO createPlannedScheduledVisitFromToBePlanned(
			ScheduledVisitDTO dto) {
		ScheduledVisitDTO newDTO = new ScheduledVisitDTO();
		newDTO.setInstitution(dto.getInstitution());
		newDTO.setState(ScheduledVisitState.PLANNED);
		newDTO.setPurpose(dto.getPurpose());
		newDTO.setRecord(dto.getRecord());
		return newDTO;
	}

	@Override
	public boolean saveScheduledVisit(ScheduledVisitDTO dto) {
		ScheduledVisit visit = new ScheduledVisit();
		visit.setId(dto.getId());
		visit.setDate(dto.getDate());
		visit.setPeriod(dto.getPeriod());
		visit.setPurpose(dto.getPurpose());
		visit.setPlanned(dto.isPlanned());

		// pouze pokud jde o save nikoliv o update
		if (visit.getId() == null) {
			// save nemůže mít stav MISSED
			visit.setPlanned(dto.getState().equals(ScheduledVisitState.PLANNED));
		}

		if (dto.getRecord() != null) {
			visit.setRecord(recordRepository.findOne(dto.getRecord().getId()));
		}

		if (dto.getInstitution() != null) {
			visit.setInstitution(institutionRepository.findOne(dto
					.getInstitution().getId()));
		}

		return scheduledVisitRepository.save(visit) != null;
	}

	// Záznamy

	@Override
	public void deleteMedicalRecord(MedicalRecordDTO dto) {
		scheduledVisitRepository.delete(dto.getId());
	}

	@Override
	public List<MedicalRecordDTO> getAllMedicalRecords() {
		return medicMapper.mapMedicalRecords(recordRepository.findAll());
	}

	@Override
	public boolean saveMedicalRecord(MedicalRecordDTO dto) {
		MedicalRecord record = new MedicalRecord();
		record.setId(dto.getId());
		record.setDate(dto.getDate());
		record.setRecord(dto.getRecord());

		if (dto.getPhysician() != null) {
			record.setPhysician(physicianRepository.findOne(dto.getPhysician()
					.getId()));
		}

		if (dto.getInstitution() != null) {
			record.setInstitution(institutionRepository.findOne(dto
					.getInstitution().getId()));
		}

		List<Medicament> medicaments = new ArrayList<>();
		for (MedicamentDTO m : dto.getMedicaments()) {
			Medicament medicament = medicamentRepository.findOne(m.getId());
			medicaments.add(medicament);
		}
		record.setMedicaments(medicaments);

		return recordRepository.save(record) != null;
	}

	// Medikamenty

	@Override
	public void deleteMedicament(MedicamentDTO dto) {
		medicamentRepository.delete(dto.getId());
	}

	@Override
	public Set<MedicamentDTO> getAllMedicaments() {
		return medicMapper.mapMedicaments(medicamentRepository.findAll());
	}

	@Override
	public boolean saveMedicament(MedicamentDTO dto) {
		Medicament medicament = new Medicament();
		medicament.setId(dto.getId());
		medicament.setName(dto.getName());
		medicament.setTolerance(dto.getTolerance());
		return medicamentRepository.save(medicament) != null;
	}

	// Doktoři

	@Override
	public void deletePhysician(PhysicianDTO dto) {
		physicianRepository.delete(dto.getId());
	}

	@Override
	public Set<PhysicianDTO> getAllPhysicians() {
		return medicMapper.mapPhysicians(physicianRepository.findAll());
	}

	@Override
	public boolean savePhysician(PhysicianDTO dto) {
		Physician physician = new Physician();
		physician.setId(dto.getId());
		physician.setName(dto.getName());
		return physicianRepository.save(physician) != null;
	}
}
