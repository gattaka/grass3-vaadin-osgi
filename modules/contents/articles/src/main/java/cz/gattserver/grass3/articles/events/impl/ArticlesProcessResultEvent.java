package cz.gattserver.grass3.articles.events.impl;

import cz.gattserver.grass3.events.ResultEvent;

public class ArticlesProcessResultEvent implements ResultEvent {

	private boolean success;
	private String resultDetails;
	private Long galleryId;

	public ArticlesProcessResultEvent() {
		this.success = true;
	}

	public ArticlesProcessResultEvent(Long galleryId) {
		this();
		this.galleryId = galleryId;
	}

	public ArticlesProcessResultEvent(boolean success, String resultDetails) {
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
