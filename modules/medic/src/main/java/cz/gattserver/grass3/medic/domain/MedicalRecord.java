package cz.gattserver.grass3.medic.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "MEDICAL_RECORD")
public class MedicalRecord {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Místo ošetření
	 */
	@ManyToOne
	private MedicalInstitution institution;

	/**
	 * Lékař - ošetřující
	 */
	@ManyToOne
	private Physician physician;

	/**
	 * Kdy se to stalo
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	/**
	 * Záznam o vyšetření
	 */
	@Column(columnDefinition = "TEXT")
	private String record;

	/**
	 * Napsané léky
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

	public Physician getPhysician() {
		return physician;
	}

	public void setPhysician(Physician physician) {
		this.physician = physician;
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
