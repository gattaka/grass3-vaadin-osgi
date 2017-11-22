package cz.gattserver.grass3.events;

import cz.gattserver.grass3.events.ResultEvent;

public class MockProcessResultEvent implements ResultEvent {

	private static final long serialVersionUID = -7417313051667964628L;

	private boolean success;
	private String resultDetails;
	private Long galleryId;

	public MockProcessResultEvent(boolean success, String resultDetails) {
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
