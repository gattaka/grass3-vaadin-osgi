package cz.gattserver.grass3.medic.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.medic.dao.MedicalInstitutionRepository;
import cz.gattserver.grass3.medic.dao.MedicalRecordRepository;
import cz.gattserver.grass3.medic.dao.MedicamentRepository;
import cz.gattserver.grass3.medic.dao.PhysicianRepository;
import cz.gattserver.grass3.medic.dao.ScheduledVisitRepository;
import cz.gattserver.grass3.medic.domain.MedicalInstitution;
import cz.gattserver.grass3.medic.domain.MedicalRecord;
import cz.gattserver.grass3.medic.domain.Medicament;
import cz.gattserver.grass3.medic.domain.Physician;
import cz.gattserver.grass3.medic.domain.ScheduledVisit;
import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;
import cz.gattserver.grass3.medic.dto.MedicamentDTO;
import cz.gattserver.grass3.medic.dto.PhysicianDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitState;

@Transactional
@Component
public class MedicFacadeImpl implements MedicFacade {

	@Autowired
	private MedicalInstitutionRepository medicalInstitutionRepository;

	@Autowired
	private ScheduledVisitRepository scheduledVisitRepository;

	@Autowired
	private MedicalRecordRepository medicalRecordRepository;

	@Autowired
	private MedicamentRepository medicamentRepository;

	@Autowired
	private PhysicianRepository physicianRepository;

	@Autowired
	private MedicMapper medicMapper;

	// Instituce

	@Override
	public void deleteMedicalInstitution(MedicalInstitutionDTO institution) {
		medicalInstitutionRepository.deleteById(institution.getId());
	}

	@Override
	public List<MedicalInstitutionDTO> getAllMedicalInstitutions() {
		return medicMapper.mapMedicalInstitutions(medicalInstitutionRepository.findAll());
	}

	@Override
	public void saveMedicalInstitution(MedicalInstitutionDTO dto) {
		MedicalInstitution institution = new MedicalInstitution();
		institution.setId(dto.getId());
		institution.setAddress(dto.getAddress());
		institution.setHours(dto.getHours());
		institution.setName(dto.getName());
		institution.setWeb(dto.getWeb());
		medicalInstitutionRepository.save(institution);
	}

	@Override
	public MedicalInstitutionDTO getMedicalInstitutionById(Long id) {
		return medicMapper.mapMedicalInstitution(medicalInstitutionRepository.findById(id).orElse(null));
	}

	// Návštěvy

	@Override
	public void deleteScheduledVisit(ScheduledVisitDTO dto) {
		scheduledVisitRepository.deleteById(dto.getId());
	}

	@Override
	public List<ScheduledVisitDTO> getAllScheduledVisits(boolean planned) {
		return medicMapper.mapScheduledVisits(scheduledVisitRepository.findByPlanned(planned));
	}

	@Override
	public List<ScheduledVisitDTO> getAllScheduledVisits() {
		return medicMapper.mapScheduledVisits(scheduledVisitRepository.findAll());
	}

	@Override
	public ScheduledVisitDTO createPlannedScheduledVisitFromToBePlanned(ScheduledVisitDTO dto) {
		ScheduledVisitDTO newDTO = new ScheduledVisitDTO();
		newDTO.setInstitution(dto.getInstitution());
		newDTO.setState(ScheduledVisitState.PLANNED);
		newDTO.setPurpose(dto.getPurpose());
		newDTO.setRecord(dto.getRecord());
		return newDTO;
	}

	@Override
	public void saveScheduledVisit(ScheduledVisitDTO dto) {
		ScheduledVisit visit = new ScheduledVisit();
		visit.setId(dto.getId());
		visit.setDate(dto.getDate());
		visit.setPeriod(dto.getPeriod());
		visit.setPurpose(dto.getPurpose());
		visit.setPlanned(dto.isPlanned());

		// pouze pokud jde o save nikoliv o update
		if (visit.getId() == null) {
			// save nemůže mít stav MISSED
			visit.setPlanned(ScheduledVisitState.PLANNED.equals(dto.getState()));
		}

		if (dto.getRecord() != null) {
			visit.setRecord(medicalRecordRepository.findById(dto.getRecord().getId()).orElse(null));
		}

		if (dto.getInstitution() != null) {
			visit.setInstitution(medicalInstitutionRepository.findById(dto.getInstitution().getId()).orElse(null));
		}

		scheduledVisitRepository.save(visit);
	}

	@Override
	public ScheduledVisitDTO getScheduledVisitById(Long id) {
		return medicMapper.mapScheduledVisit(scheduledVisitRepository.findById(id).orElse(null));
	}

	// Záznamy

	@Override
	public void deleteMedicalRecord(MedicalRecordDTO dto) {
		scheduledVisitRepository.deleteById(dto.getId());
	}

	@Override
	public List<MedicalRecordDTO> getAllMedicalRecords() {
		return medicMapper.mapMedicalRecords(medicalRecordRepository.findOrderByDateDesc());
	}

	@Override
	public void saveMedicalRecord(MedicalRecordDTO dto) {
		MedicalRecord record = new MedicalRecord();
		record.setId(dto.getId());
		record.setDate(dto.getDate());
		record.setRecord(dto.getRecord());

		if (dto.getPhysician() != null) {
			record.setPhysician(physicianRepository.findById(dto.getPhysician().getId()).orElse(null));
		}

		if (dto.getInstitution() != null) {
			record.setInstitution(medicalInstitutionRepository.findById(dto.getInstitution().getId()).orElse(null));
		}

		List<Medicament> medicaments = new ArrayList<>();
		for (MedicamentDTO m : dto.getMedicaments()) {
			Medicament medicament = medicamentRepository.findById(m.getId()).orElse(null);
			medicaments.add(medicament);
		}
		record.setMedicaments(medicaments);

		medicalRecordRepository.save(record);
	}

	@Override
	public MedicalRecordDTO getMedicalRecordById(Long id) {
		return medicMapper.mapMedicalRecord(medicalRecordRepository.findById(id).orElse(null));
	}

	// Medikamenty

	@Override
	public void deleteMedicament(MedicamentDTO dto) {
		medicamentRepository.deleteById(dto.getId());
	}

	@Override
	public Set<MedicamentDTO> getAllMedicaments() {
		return medicMapper.mapMedicaments(medicamentRepository.findAll());
	}

	@Override
	public void saveMedicament(MedicamentDTO dto) {
		Medicament medicament = new Medicament();
		medicament.setId(dto.getId());
		medicament.setName(dto.getName());
		medicament.setTolerance(dto.getTolerance());
		medicamentRepository.save(medicament);
	}

	@Override
	public MedicamentDTO getMedicamentById(Long id) {
		return medicMapper.mapMedicament(medicamentRepository.findById(id).orElse(null));
	}

	// Doktoři

	@Override
	public void deletePhysician(PhysicianDTO dto) {
		physicianRepository.deleteById(dto.getId());
	}

	@Override
	public Set<PhysicianDTO> getAllPhysicians() {
		return medicMapper.mapPhysicians(physicianRepository.findAll());
	}

	@Override
	public void savePhysician(PhysicianDTO dto) {
		Physician physician = new Physician();
		physician.setId(dto.getId());
		physician.setName(dto.getName());
		physicianRepository.save(physician);
	}

	@Override
	public PhysicianDTO getPhysicianById(Long id) {
		return medicMapper.mapPhysician(physicianRepository.findById(id).orElse(null));
	}

}
