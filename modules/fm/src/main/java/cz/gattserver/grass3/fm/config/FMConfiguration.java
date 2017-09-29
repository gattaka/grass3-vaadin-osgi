package cz.gattserver.grass3.fm.config;

import java.util.HashSet;
import java.util.Set;

import cz.gattserver.grass3.config.AbstractConfiguration;
import cz.gattserver.grass3.security.Role;

public class FMConfiguration extends AbstractConfiguration {

	public static final String FM_PATH = "/fm-files";

	/**
	 * Kořenový adresář FM
	 */
	private String rootDir = "files";

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
	private Set<Role> roles = new HashSet<Role>();

	private String tmpDir = "tmpUpload";

	public FMConfiguration() {
		super("cz.gattserver.grass3.fm");
		roles.add(Role.ADMIN);
		roles.add(Role.FRIEND);
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		if (rootDir != null && !rootDir.isEmpty())
			this.rootDir = rootDir;
	}

	public Integer getMaxSimUploads() {
		return maxSimUploads;
	}

	public void setMaxSimUploads(Integer maxSimUploads) {
		if (maxSimUploads != null)
			this.maxSimUploads = maxSimUploads;
	}

	public Long getMaxKBytesUploadSize() {
		return maxKBytesUploadSize;
	}

	public void setMaxKBytesUploadSize(Long maxKBytesUploadSize) {
		if (maxKBytesUploadSize != null)
			this.maxKBytesUploadSize = maxKBytesUploadSize;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getTmpDir() {
		return tmpDir;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

}
