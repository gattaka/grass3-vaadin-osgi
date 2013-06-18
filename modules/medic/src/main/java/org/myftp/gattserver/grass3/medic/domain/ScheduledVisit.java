package org.myftp.gattserver.grass3.medic.domain;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

public class ScheduledVisit {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Místo, kam se dostavit
	 */
	@ManyToOne
	private MedicalInstitution institution;

	/**
	 * Záznam - návštìva, ze které vzešlo toto datum návštìvy
	 */
	@OneToOne
	private MedicalRecord record;

	/**
	 * Datum kontroly
	 */
	private Date date;

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
