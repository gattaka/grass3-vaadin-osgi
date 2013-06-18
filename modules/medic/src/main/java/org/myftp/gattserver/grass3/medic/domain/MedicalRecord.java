package org.myftp.gattserver.grass3.medic.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

public class MedicalRecord {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * M�sto o�et�en�
	 */
	@ManyToOne
	private MedicalInstitution institution;

	/**
	 * L�ka� - o�et�uj�c�
	 */
	private String doctor;

	/**
	 * Kdy se to stalo
	 */
	private Date date;

	/**
	 * Z�znam o vy�et�en�
	 */
	@Column(columnDefinition = "TEXT")
	private String record;

	/**
	 * Napsan� l�ky
	 */
	@ManyToMany
	private List<Medicament> medicaments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MedicalInstitution getInstitution() {
		return institution;
	}

	public void setInstitution(MedicalInstitution institution) {
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

	public List<Medicament> getMedicaments() {
		return medicaments;
	}

	public void setMedicaments(List<Medicament> medicaments) {
		this.medicaments = medicaments;
	}

}
