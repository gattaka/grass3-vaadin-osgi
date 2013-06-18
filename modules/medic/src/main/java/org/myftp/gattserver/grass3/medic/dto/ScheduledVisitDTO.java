package org.myftp.gattserver.grass3.medic.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class ScheduledVisitDTO {

	private Long id;

	/**
	 * M�sto, kam se dostavit
	 */
	@NotNull
	private MedicalInstitutionDTO institution;

	/**
	 * Z�znam - n�v�t�va, ze kter� vze�lo toto datum n�v�t�vy
	 */
	private MedicalRecordDTO record;

	/**
	 * Datum kontroly
	 */
	@NotNull
	private Date date;

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

	public MedicalRecordDTO getRecord() {
		return record;
	}

	public void setRecord(MedicalRecordDTO record) {
		this.record = record;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
