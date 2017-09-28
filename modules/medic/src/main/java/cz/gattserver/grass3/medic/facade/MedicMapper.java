package cz.gattserver.grass3.medic.facade;

import java.util.List;
import java.util.Set;

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

public interface MedicMapper {

	public MedicalInstitutionDTO mapMedicalInstitution(MedicalInstitution e);

	public List<MedicalInstitutionDTO> mapMedicalInstitutions(List<MedicalInstitution> e);

	public ScheduledVisitDTO mapScheduledVisit(ScheduledVisit e);

	public List<ScheduledVisitDTO> mapScheduledVisits(List<ScheduledVisit> e);

	public MedicalRecordDTO mapMedicalRecord(MedicalRecord e);

	public List<MedicalRecordDTO> mapMedicalRecords(List<MedicalRecord> e);

	public MedicamentDTO mapMedicament(Medicament e);

	public Set<MedicamentDTO> mapMedicaments(List<Medicament> e);

	public PhysicianDTO mapPhysician(Physician e);

	public Set<PhysicianDTO> mapPhysicians(List<Physician> e);

}
