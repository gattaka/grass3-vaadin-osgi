package org.myftp.gattserver.grass3.config;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Základní konfigurace, její umístění je pevně dané, je v ní pro potvrzení údaj
 * o jméně aplikace a její verzi. Podstatná informace je ale pak v
 * {@code configurationPath}, což je cesta k adresáři s nastaveními všech
 * pluginů apod., tento adresář se dá měnit, proto je zde ta jedna doplňková
 * reference, protože odněkud musím na jeho path přijít - tak je zde.
 * </p>
 * 
 * <p>
 * Tento systém umožňuje přepínat rychle celé svazky konfigurací, což je
 * elegantnější než ruční střídání různých souborů
 * <p>
 * 
 * @author gatt
 * 
 */
@XmlRootElement
public class DispatcherConfiguration {

	private String configurationPath = "./grass_configurations";
	public final String grassVersion = AppInfo.GRASS_VERSION;
	public final String grassName = AppInfo.GRASS_NAME;

	public String getConfigurationPath() {
		return configurationPath;
	}

	public void setConfigurationPath(String configurationPath) {
		this.configurationPath = configurationPath;
	}

	public String getGrassVersion() {
		return grassVersion;
	}

	public String getGrassName() {
		return grassName;
	}

}
