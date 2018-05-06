package cz.gattserver.grass3.songs.model.dto;

import cz.gattserver.grass3.songs.model.domain.Instrument;

public class ChordTO {

	/**
	 * Název
	 */
	private String name;

	/**
	 * Nástroj
	 */
	private Instrument instrument;

	/**
	 * Konfigurace
	 */
	private Integer configuration;

	/**
	 * DB id
	 */
	private Long id;

	public ChordTO() {
		configuration = 0;
	}

	public ChordTO(String name, Instrument instrument, Integer configuration, Long id) {
		super();
		this.name = name;
		this.instrument = instrument;
		this.configuration = configuration;
		if (this.configuration == null)
			this.configuration = 0;
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChordTO))
			return false;
		return ((ChordTO) obj).getId() == getId();
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

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public Integer getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Integer configuration) {
		this.configuration = configuration;
	}

}
