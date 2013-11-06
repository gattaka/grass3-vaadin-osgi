package org.myftp.gattserver.grass3.medic.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MedicalRecordDTO {

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
	@Size(min = 1)
	private String doctor;

	/**
	 * Kdy se to stalo
	 */
	@NotNull
	private Date date;

	/**
	 * Záznam o vyšetření
	 */
	@NotNull
	@Size(min = 1)
	private String record;

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

	public String getDoctor() {
		return doctor;
	}

	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return dateFormat.format(date) + " " + doctor;
	}

}
