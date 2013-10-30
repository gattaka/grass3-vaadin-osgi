package org.myftp.gattserver.grass3.medic.facade;

import java.util.List;

import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.dto.MedicamentDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;

public interface IMedicFacade {

	// Instituce

	void deleteMedicalInstitution(MedicalInstitutionDTO dto);

	List<MedicalInstitutionDTO> getAllMedicalInstitutions();

	boolean saveMedicalInstitution(MedicalInstitutionDTO dto);

	// Návštěvy

	void deleteScheduledVisit(ScheduledVisitDTO dto);

	List<ScheduledVisitDTO> getAllScheduledVisits();

	boolean saveScheduledVisit(ScheduledVisitDTO dto);

	// Záznamy

	void deleteMedicalRecord(MedicalRecordDTO dto);

	List<MedicalRecordDTO> getAllMedicalRecords();

	boolean saveMedicalRecord(MedicalRecordDTO dto);

	// Medikamenty

	void deleteMedicament(MedicamentDTO dto);

	List<MedicamentDTO> getAllMedicaments();

	boolean saveMedicament(MedicamentDTO dto);

}
