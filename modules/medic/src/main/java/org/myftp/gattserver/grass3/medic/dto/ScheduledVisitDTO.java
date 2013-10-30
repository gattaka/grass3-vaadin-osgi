package org.myftp.gattserver.grass3.medic.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ScheduledVisitDTO {

	private Long id;

	/**
	 * Účel návštěvy
	 */
	@NotNull
	@Size(min = 1)
	private String purpose;

	/**
	 * Místo, kam se dostavit
	 */
	@NotNull
	private MedicalInstitutionDTO institution;

	/**
	 * Záznam - návštěva, ze které vzešlo toto datum návštěvy
	 */
	private MedicalRecordDTO record;

	/**
	 * Stav
	 */
	private ScheduledVisitState state;

	/**
	 * Datum kontroly
	 */
	@NotNull
	private Date date;

	/**
	 * Perioda v měsících
	 */
	private int period;

	public ScheduledVisitState getState() {
		return state;
	}

	public void setState(ScheduledVisitState state) {
		this.state = state;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

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
