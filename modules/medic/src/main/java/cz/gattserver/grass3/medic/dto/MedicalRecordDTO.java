package cz.gattserver.grass3.medic.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import cz.gattserver.common.Identifiable;

public class MedicalRecordDTO implements Identifiable {

	private Long id;

	/**
	 * Místo ošetření
	 */
	@NotNull
	private MedicalInstitutionDTO institution;

	/**
	 * Lékař - ošetřující
	 */
	@NotNull
	private PhysicianDTO physician;

	/**
	 * Kdy se to stalo
	 */
	@NotNull
	private LocalDate date;

	@NotNull
	private LocalTime time;

	/**
	 * Záznam o vyšetření
	 */
	@NotNull
	@Size(min = 1)
	private String record = "";

	/**
	 * Napsané léky
	 */
	private Set<MedicamentDTO> medicaments = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MedicalInstitutionDTO getInstitution() {
		return institution;
	}

	public void setInstitution(MedicalInstitutionDTO institution) {
		this.institution = institution;
	}

	public PhysicianDTO getPhysician() {
		return physician;
	}

	public void setPhysician(PhysicianDTO physician) {
		this.physician = physician;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public LocalDateTime getDateTime() {
		return date.atTime(time);
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public Set<MedicamentDTO> getMedicaments() {
		return medicaments;
	}

	public void setMedicaments(Set<MedicamentDTO> medicaments) {
		this.medicaments = medicaments;
	}

	@Override
	public String toString() {
		return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + " " + physician.getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MedicalRecordDTO) {
			MedicalRecordDTO dto = (MedicalRecordDTO) obj;
			if (dto.getId() == null)
				return id == null;
			else
				return dto.getId().equals(id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
