package cz.gattserver.grass3.songs.model.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "CHORD")
public class Chord {

	/**
	 * Název
	 */
	private String name;

	/**
	 * Konfigurace
	 */
	private Long configuration;

	/**
	 * DB id
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Chord))
			return false;
		return ((Chord) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Long configuration) {
		this.configuration = configuration;
	}

}
