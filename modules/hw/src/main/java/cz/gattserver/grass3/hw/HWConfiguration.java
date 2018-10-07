package cz.gattserver.grass3.hw;

import java.util.HashSet;
import java.util.Set;

import cz.gattserver.grass3.config.AbstractConfiguration;
import cz.gattserver.grass3.security.CoreRole;

public class HWConfiguration extends AbstractConfiguration {

	/**
	 * HTTP cesta k souborům
	 */
	public static final String HW_PATH = "hw-files";
	
	/**
	 * Kořenový adresář FM
	 */
	private String rootDir = "files/hw";

	/**
	 * Adresář pro ukládání obrázků
	 */
	private String imagesDir = "images";

	/**
	 * Adresář pro ukládání dokumentace
	 */
	private String documentsDir = "documents";

	/**
	 * Kolik souborů zároveň se smí poslat na server
	 */
	private Integer maxSimUploads = 50;

	/**
	 * Maximální velikost upload souboru v KB
	 */
	private Long maxKBytesUploadSize = 100000L; // 100MB

	/**
	 * Jakým rolím má být modul přístupný - defaultně jenom adminovi
	 */
	private Set<CoreRole> roles = new HashSet<>();

	public HWConfiguration() {
		super("cz.gattserver.grass3.hw");
		roles.add(CoreRole.ADMIN);
		roles.add(CoreRole.FRIEND);
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public Integer getMaxSimUploads() {
		return maxSimUploads;
	}

	public void setMaxSimUploads(Integer maxSimUploads) {
		this.maxSimUploads = maxSimUploads;
	}

	public Long getMaxKBytesUploadSize() {
		return maxKBytesUploadSize;
	}

	public void setMaxKBytesUploadSize(Long maxKBytesUploadSize) {
		this.maxKBytesUploadSize = maxKBytesUploadSize;
	}

	public Set<CoreRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<CoreRole> roles) {
		this.roles = roles;
	}

	public String getImagesDir() {
		return imagesDir;
	}

	public void setImagesDir(String imagesDir) {
		this.imagesDir = imagesDir;
	}

	public String getDocumentsDir() {
		return documentsDir;
	}

	public void setDocumentsDir(String documentsDir) {
		this.documentsDir = documentsDir;
	}

}
