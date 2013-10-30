package org.myftp.gattserver.grass3.medic.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

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
	private String record;

	/**
	 * Napsané léky
	 */
	private List<MedicamentDTO> medicaments;

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

	public List<MedicamentDTO> getMedicaments() {
		return medicaments;
	}

	public void setMedicaments(List<MedicamentDTO> medicaments) {
		this.medicaments = medicaments;
	}

	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return dateFormat.format(date) + " " + doctor;
	}

}
