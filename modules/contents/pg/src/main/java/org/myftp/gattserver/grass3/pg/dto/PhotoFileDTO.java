package org.myftp.gattserver.grass3.pg.dto;

import java.io.File;

public class PhotoFileDTO {

	private String name;

	private File tmpFile;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getTmpFile() {
		return tmpFile;
	}

	public void setTmpFile(File tmpFile) {
		this.tmpFile = tmpFile;
	}

}
