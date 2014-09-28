package cz.gattserver.grass3.pg.events;

import cz.gattserver.grass3.events.IResultEvent;

public class PGProcessResultEvent implements IResultEvent {

	private static final long serialVersionUID = -7417313051667964628L;

	private boolean success;
	private String resultDetails;
	private Long galleryId;

	public PGProcessResultEvent() {
		this.success = true;
	}
	
	public PGProcessResultEvent(Long galleryId) {
		this();
		this.galleryId = galleryId;
	}

	public PGProcessResultEvent(boolean success, String resultDetails) {
		this.success = success;
		this.resultDetails = resultDetails;
	}

	public Long getGalleryId() {
		return galleryId;
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
