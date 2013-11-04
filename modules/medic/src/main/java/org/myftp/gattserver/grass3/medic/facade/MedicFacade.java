package org.myftp.gattserver.grass3.medic.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.medic.dao.MedicalInstitutionRepository;
import org.myftp.gattserver.grass3.medic.dao.MedicalRecordRepository;
import org.myftp.gattserver.grass3.medic.dao.MedicamentRepository;
import org.myftp.gattserver.grass3.medic.dao.ScheduledVisitRepository;
import org.myftp.gattserver.grass3.medic.domain.MedicalInstitution;
import org.myftp.gattserver.grass3.medic.domain.MedicalRecord;
import org.myftp.gattserver.grass3.medic.domain.Medicament;
import org.myftp.gattserver.grass3.medic.domain.ScheduledVisit;
import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicamentDTO;
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
	public List<ScheduledVisitDTO> getAllScheduledVisits(boolean planned) {
		return medicMapper.mapScheduledVisits(scheduledVisitRepository
				.findByPlanned(planned));
	}

	@Override
	public boolean saveScheduledVisit(ScheduledVisitDTO dto) {
		ScheduledVisit visit = new ScheduledVisit();
		visit.setId(dto.getId());
		visit.setDate(dto.getDate());
		visit.setPeriod(dto.getPeriod());
		visit.setPurpose(dto.getPurpose());

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
		record.setDate(dto.getDate());
		record.setDoctor(dto.getDoctor());
		record.setRecord(dto.getRecord());

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
		medicament.setName(dto.getName());
		medicament.setTolerance(dto.getTolerance());
		return medicamentRepository.save(medicament) != null;
	}
}
