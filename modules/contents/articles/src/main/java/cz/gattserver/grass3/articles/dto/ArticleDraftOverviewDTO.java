package cz.gattserver.grass3.articles.dto;

import cz.gattserver.grass3.model.dto.ContentNodeDTO;

/**
 * DTO pro výběr rozpracovaného článku v menu
 * 
 * @author Hynek
 *
 */
public class ArticleDraftOverviewDTO {

	/**
	 * Náhled článku
	 */
	private String text;

	/**
	 * Meta-informace o obsahu
	 */
	private ContentNodeDTO contentNode;

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Je-li draft a má-li rozpracovanou pouze část článku, pak kterou
	 */
	private Integer partNumber;

	public Integer getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(Integer partNumber) {
		this.partNumber = partNumber;
	}

	public ArticleDraftOverviewDTO() {
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ContentNodeDTO getContentNode() {
		return contentNode;
	}

	public void setContentNode(ContentNodeDTO contentNode) {
		this.contentNode = contentNode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
