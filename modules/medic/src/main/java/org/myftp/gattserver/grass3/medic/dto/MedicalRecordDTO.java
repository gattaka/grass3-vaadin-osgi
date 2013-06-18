package org.myftp.gattserver.grass3.medic.dto;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

public class MedicalRecordDTO {

	private Long id;

	/**
	 * M�sto o�et�en�
	 */
	@NotNull
	private MedicalInstitutionDTO institution;

	/**
	 * L�ka� - o�et�uj�c�
	 */
	private String doctor;

	/**
	 * Kdy se to stalo
	 */
	@NotNull
	private Date date;

	/**
	 * Z�znam o vy�et�en�
	 */
	@NotNull
	private String record;

	/**
	 * Napsan� l�ky
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

}
