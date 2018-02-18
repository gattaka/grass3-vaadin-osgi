package cz.gattserver.grass3.medic.facade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

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

@Component
public class MedicMapperImpl implements MedicMapper {

	public MedicalInstitutionDTO mapMedicalInstitution(MedicalInstitution e) {
		if (e == null)
			return null;

		MedicalInstitutionDTO dto = new MedicalInstitutionDTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setAddress(e.getAddress());
		dto.setWeb(e.getWeb());
		dto.setHours(e.getHours());
		return dto;
	}

	public List<MedicalInstitutionDTO> mapMedicalInstitutions(List<MedicalInstitution> e) {
		List<MedicalInstitutionDTO> list = new ArrayList<>();
		for (MedicalInstitution i : e) {
			list.add(mapMedicalInstitution(i));
		}
		return list;
	}

	public ScheduledVisitDTO mapScheduledVisit(ScheduledVisit e) {
		if (e == null)
			return null;

		ScheduledVisitDTO dto = new ScheduledVisitDTO();
		dto.setId(e.getId());
		dto.setDate(e.getDate());
		dto.setInstitution(mapMedicalInstitution(e.getInstitution()));
		dto.setPeriod(e.getPeriod());
		dto.setPurpose(e.getPurpose());
		dto.setRecord(mapMedicalRecord(e.getRecord()));
		dto.setPlanned(e.isPlanned());

		if (LocalDateTime.now().compareTo(dto.getDate()) > 0) {
			dto.setState(ScheduledVisitState.MISSED);
		} else {
			dto.setState(e.isPlanned() ? ScheduledVisitState.PLANNED : ScheduledVisitState.TO_BE_PLANNED);
		}

		return dto;
	}

	public List<ScheduledVisitDTO> mapScheduledVisits(List<ScheduledVisit> e) {
		List<ScheduledVisitDTO> list = new ArrayList<>();
		for (ScheduledVisit i : e) {
			list.add(mapScheduledVisit(i));
		}
		return list;
	}

	public MedicalRecordDTO mapMedicalRecord(MedicalRecord e) {
		if (e == null)
			return null;

		MedicalRecordDTO dto = new MedicalRecordDTO();
		dto.setId(e.getId());
		dto.setDate(e.getDate());
		dto.setInstitution(mapMedicalInstitution(e.getInstitution()));
		dto.setRecord(e.getRecord());
		dto.setPhysician(mapPhysician(e.getPhysician()));
		dto.setMedicaments(mapMedicaments(e.getMedicaments()));
		return dto;
	}

	public List<MedicalRecordDTO> mapMedicalRecords(List<MedicalRecord> e) {
		List<MedicalRecordDTO> list = new ArrayList<>();
		for (MedicalRecord i : e) {
			list.add(mapMedicalRecord(i));
		}
		return list;
	}

	public MedicamentDTO mapMedicament(Medicament e) {
		if (e == null)
			return null;

		MedicamentDTO dto = new MedicamentDTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setTolerance(e.getTolerance());
		return dto;
	}

	public Set<MedicamentDTO> mapMedicaments(List<Medicament> e) {
		Set<MedicamentDTO> set = new HashSet<>();
		for (Medicament i : e) {
			set.add(mapMedicament(i));
		}
		return set;
	}

	public PhysicianDTO mapPhysician(Physician e) {
		if (e == null)
			return null;

		PhysicianDTO dto = new PhysicianDTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		return dto;
	}

	public Set<PhysicianDTO> mapPhysicians(List<Physician> e) {
		Set<PhysicianDTO> set = new HashSet<>();
		for (Physician i : e) {
			set.add(mapPhysician(i));
		}
		return set;
	}

}
