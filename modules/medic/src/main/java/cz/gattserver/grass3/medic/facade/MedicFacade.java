package cz.gattserver.grass3.medic.facade;

import java.util.List;
import java.util.Set;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;
import cz.gattserver.grass3.medic.dto.MedicamentDTO;
import cz.gattserver.grass3.medic.dto.PhysicianDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;

public interface MedicFacade {

	// Doktoři

	void deletePhysician(PhysicianDTO dto);

	Set<PhysicianDTO> getAllPhysicians();

	void savePhysician(PhysicianDTO dto);

	PhysicianDTO getPhysicianById(Long id);

	// Instituce

	void deleteMedicalInstitution(MedicalInstitutionDTO dto);

	List<MedicalInstitutionDTO> getAllMedicalInstitutions();

	void saveMedicalInstitution(MedicalInstitutionDTO dto);

	MedicalInstitutionDTO getMedicalInstitutionById(Long id);

	// Návštěvy

	void deleteScheduledVisit(ScheduledVisitDTO dto);

	List<ScheduledVisitDTO> getAllScheduledVisits(boolean planned);

	List<ScheduledVisitDTO> getAllScheduledVisits();

	void saveScheduledVisit(ScheduledVisitDTO dto);

	ScheduledVisitDTO createPlannedScheduledVisitFromToBePlanned(ScheduledVisitDTO dto);

	ScheduledVisitDTO getScheduledVisitById(Long id);

	// Záznamy

	void deleteMedicalRecord(MedicalRecordDTO dto);

	List<MedicalRecordDTO> getAllMedicalRecords();

	void saveMedicalRecord(MedicalRecordDTO dto);

	MedicalRecordDTO getMedicalRecordById(Long id);

	// Medikamenty

	void deleteMedicament(MedicamentDTO dto);

	Set<MedicamentDTO> getAllMedicaments();

	void saveMedicament(MedicamentDTO dto);

	MedicamentDTO getMedicamentById(Long id);

}
