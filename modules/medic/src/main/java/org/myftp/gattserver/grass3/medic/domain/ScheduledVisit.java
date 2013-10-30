package org.myftp.gattserver.grass3.medic.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitState;

@Entity
@Table(name = "MEDICAL_VISIT")
public class ScheduledVisit {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Účel návštěvy
	 */
	@NotNull
	private String purpose;

	/**
	 * Místo, kam se dostavit
	 */
	@ManyToOne
	private MedicalInstitution institution;

	/**
	 * Záznam - návštěva, ze které vzešlo toto datum návštěvy
	 */
	@OneToOne
	private MedicalRecord record;

	/**
	 * Stav
	 */
	private ScheduledVisitState state;

	/**
	 * Datum kontroly
	 */
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

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

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

	public MedicalRecord getRecord() {
		return record;
	}

	public void setRecord(MedicalRecord record) {
		this.record = record;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
