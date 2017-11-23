package cz.gattserver.grass3.config;

public abstract class AbstractConfiguration {

	private String prefix;

	public AbstractConfiguration(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

}
