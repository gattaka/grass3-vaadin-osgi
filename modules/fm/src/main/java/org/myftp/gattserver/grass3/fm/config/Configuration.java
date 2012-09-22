package org.myftp.gattserver.grass3.fm.config;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.myftp.gattserver.vaadin.Role;

@XmlRootElement
public class Configuration {

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

	/**
	 * Jaké znaky mám zakázané v nových adresářích ?
	 */
	private String invalidDirCharacters = "?*~^";

	private String tmpDir = "tmpUpload";

	public Configuration() {
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

	public String getInvalidDirCharacters() {
		return invalidDirCharacters;
	}

	public void setInvalidDirCharacters(String invalidDirCharacters) {
		this.invalidDirCharacters = invalidDirCharacters;
	}

	public String getTmpDir() {
		return tmpDir;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

}
