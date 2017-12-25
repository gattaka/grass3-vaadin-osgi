package cz.gattserver.grass3.pg.events.impl;

import java.nio.file.Path;

import cz.gattserver.grass3.events.ResultEvent;

public class PGZipProcessResultEvent implements ResultEvent {

	private boolean success;
	private String resultDetails;

	private Path zipFile;

	public PGZipProcessResultEvent(Path zipFile) {
		this.success = true;
		this.zipFile = zipFile;
	}

	public Path getZipFile() {
		return zipFile;
	}

	public PGZipProcessResultEvent(boolean success, String resultDetails) {
		this.success = success;
		this.resultDetails = resultDetails;
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public String getResultDetails() {
		return resultDetails;
	}

}
