package cz.gattserver.grass3.articles.interfaces;

import cz.gattserver.grass3.interfaces.ContentNodeTO;

/**
 * DTO pro výběr rozpracovaného článku v menu
 * 
 * @author Hynek
 *
 */
public class ArticleDraftOverviewTO {

	/**
	 * Náhled článku
	 */
	private String text;

	/**
	 * Meta-informace o obsahu
	 */
	private ContentNodeTO contentNode;

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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ContentNodeTO getContentNode() {
		return contentNode;
	}

	public void setContentNode(ContentNodeTO contentNode) {
		this.contentNode = contentNode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
