package cz.gattserver.grass3.pg.events.impl;

import java.nio.file.Path;

import cz.gattserver.grass3.events.ResultEvent;

public class PGZipProcessResultEvent implements ResultEvent {

	private boolean success;
	private String resultDetails;
	private Throwable resultException;

	private Path zipFile;

	public PGZipProcessResultEvent(Path zipFile) {
		this.success = true;
		this.zipFile = zipFile;
	}

	public PGZipProcessResultEvent(boolean success, String resultDetails) {
		this.success = success;
		this.resultDetails = resultDetails;
	}

	public PGZipProcessResultEvent(String resultDetails, Throwable exception) {
		this.success = false;
		this.resultDetails = resultDetails;
		this.resultException = exception;
	}

	public Path getZipFile() {
		return zipFile;
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public String getResultDetails() {
		return resultDetails;
	}

	public Throwable getResultException() {
		return resultException;
	}

}
