package org.myftp.gattserver.grass3.hw.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Údaj o opravě, změně součástí apod.
 */
@Entity
@Table(name = "SERVICE_NOTE")
public class ServiceNote {

	/**
	 * Identifikátor změny
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private long id;

	/**
	 * Popis změny
	 */
	@Column(columnDefinition = "TEXT")
	private String description;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
