package cz.gattserver.grass3.medic.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import cz.gattserver.grass3.model.dto.Identifiable;

public class ScheduledVisitDTO implements Identifiable{

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
	 * Stav - čiště kvůli UI
	 */
	private ScheduledVisitState state;

	/**
	 * Objednán ?
	 */
	private boolean planned;

	/**
	 * Datum kontroly
	 */
	@NotNull
	private Date date;

	/**
	 * Perioda v měsících
	 */
	private int period;

	public boolean isPlanned() {
		return planned;
	}

	public void setPlanned(boolean planned) {
		this.planned = planned;
	}

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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScheduledVisitDTO) {
			ScheduledVisitDTO dto = (ScheduledVisitDTO) obj;
			if (dto.getId() == null)
				return id == null;
			else
				return dto.getId().equals(id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}

}
