package org.myftp.gattserver.grass3.medic.facade;

import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicamentDTO;
import org.myftp.gattserver.grass3.medic.dto.PhysicianDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;

public interface IMedicFacade {

	// Doktoři

	void deletePhysician(PhysicianDTO dto);

	Set<PhysicianDTO> getAllPhysicians();

	boolean savePhysician(PhysicianDTO dto);

	PhysicianDTO getPhysicianById(Long id);

	// Instituce

	void deleteMedicalInstitution(MedicalInstitutionDTO dto);

	List<MedicalInstitutionDTO> getAllMedicalInstitutions();

	boolean saveMedicalInstitution(MedicalInstitutionDTO dto);

	MedicalInstitutionDTO getMedicalInstitutionById(Long id);

	// Návštěvy

	void deleteScheduledVisit(ScheduledVisitDTO dto);

	List<ScheduledVisitDTO> getAllScheduledVisits(boolean planned);

	boolean saveScheduledVisit(ScheduledVisitDTO dto);

	ScheduledVisitDTO createPlannedScheduledVisitFromToBePlanned(
			ScheduledVisitDTO dto);

	ScheduledVisitDTO getScheduledVisitById(Long id);

	// Záznamy

	void deleteMedicalRecord(MedicalRecordDTO dto);

	List<MedicalRecordDTO> getAllMedicalRecords();

	boolean saveMedicalRecord(MedicalRecordDTO dto);

	MedicalRecordDTO getMedicalRecordById(Long id);

	// Medikamenty

	void deleteMedicament(MedicamentDTO dto);

	Set<MedicamentDTO> getAllMedicaments();

	boolean saveMedicament(MedicamentDTO dto);

	MedicamentDTO getMedicamentById(Long id);

}
