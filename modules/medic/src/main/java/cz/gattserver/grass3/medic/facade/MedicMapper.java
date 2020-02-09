package cz.gattserver.grass3.medic.facade;

import java.util.List;
import java.util.Set;

import cz.gattserver.grass3.medic.domain.MedicalInstitution;
import cz.gattserver.grass3.medic.domain.MedicalRecord;
import cz.gattserver.grass3.medic.domain.Medicament;
import cz.gattserver.grass3.medic.domain.Physician;
import cz.gattserver.grass3.medic.domain.ScheduledVisit;
import cz.gattserver.grass3.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass3.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass3.medic.interfaces.MedicamentTO;
import cz.gattserver.grass3.medic.interfaces.PhysicianTO;
import cz.gattserver.grass3.medic.interfaces.ScheduledVisitTO;

public interface MedicMapper {

	public MedicalInstitutionTO mapMedicalInstitution(MedicalInstitution e);

	public List<MedicalInstitutionTO> mapMedicalInstitutions(List<MedicalInstitution> e);

	public ScheduledVisitTO mapScheduledVisit(ScheduledVisit e);

	public List<ScheduledVisitTO> mapScheduledVisits(List<ScheduledVisit> e);

	public MedicalRecordTO mapMedicalRecord(MedicalRecord e);

	public List<MedicalRecordTO> mapMedicalRecords(List<MedicalRecord> e);

	public MedicamentTO mapMedicament(Medicament e);

	public Set<MedicamentTO> mapMedicaments(List<Medicament> e);

	public PhysicianTO mapPhysician(Physician e);

	public Set<PhysicianTO> mapPhysicians(List<Physician> e);

}
