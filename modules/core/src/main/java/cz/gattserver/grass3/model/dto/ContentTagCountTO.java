package cz.gattserver.grass3.model.dto;

import com.querydsl.core.annotations.QueryProjection;

public class ContentTagCountTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Počet obsahů
	 */
	private Integer contentsCount;

	@QueryProjection
	public ContentTagCountTO(Long id, Integer contentsCount) {
		this.id = id;
		this.contentsCount = contentsCount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getContentsCount() {
		return contentsCount;
	}

	public void setContentsCount(Integer contentsCount) {
		this.contentsCount = contentsCount;
	}

}
