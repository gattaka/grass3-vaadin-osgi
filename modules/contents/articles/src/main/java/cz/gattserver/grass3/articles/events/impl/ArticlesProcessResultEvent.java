package cz.gattserver.grass3.articles.events.impl;

import java.util.UUID;

import cz.gattserver.grass3.events.ResultEvent;

public class ArticlesProcessResultEvent implements ResultEvent {

	private boolean success;
	private String resultDetails;
	private UUID operationId;

	public ArticlesProcessResultEvent(UUID operationId) {
		this.operationId = operationId;
		this.success = true;
	}

	public ArticlesProcessResultEvent(UUID operationId, boolean success, String resultDetails) {
		this(operationId);
		this.success = success;
		this.resultDetails = resultDetails;
	}

	public UUID getOperationId() {
		return operationId;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setResultDetails(String resultDetails) {
		this.resultDetails = resultDetails;
	}

	public void setOperationId(UUID operationId) {
		this.operationId = operationId;
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
