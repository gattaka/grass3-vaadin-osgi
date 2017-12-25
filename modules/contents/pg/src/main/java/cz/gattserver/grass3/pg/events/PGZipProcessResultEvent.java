package cz.gattserver.grass3.pg.events;

import java.io.File;

import cz.gattserver.grass3.events.ResultEvent;

public class PGZipProcessResultEvent implements ResultEvent {

	private boolean success;
	private String resultDetails;

	private File zipFile;

	public PGZipProcessResultEvent(File zipFile) {
		this.success = true;
		this.zipFile = zipFile;
	}

	public File getZipFile() {
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
