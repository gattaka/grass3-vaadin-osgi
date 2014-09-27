package cz.gattserver.grass3.articles.dto;

import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.UserDTO;

/**
 * Tato třída je určená pro dočasné ukládní rozpracovaných článků - má sloužit
 * tedy pouze jako takový snapshot editoru, ze kterého může být rozpracovaný
 * článek obnoven.
 * 
 * @author gatt
 * 
 */
public class TempArticleDTO {

	/**
	 * Do které kategorie byl článek určen ?
	 */
	private NodeDTO node;

	/**
	 * Kdo byl autorem v době psaní článku ?
	 */
	private UserDTO user;

	/**
	 * Název článku
	 */
	private String name;

	/**
	 * Pokud jde o rozpracovanou verzi již existujícího článku, potřebuju a něj
	 * referenci
	 */
	private ArticleDTO modifiedArticle;

	/**
	 * Obsah článku
	 */
	private String text;

	/**
	 * Klíčová slova
	 */
	private String tags;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public ArticleDTO getModifiedArticle() {
		return modifiedArticle;
	}

	public void setModifiedArticle(ArticleDTO modifiedArticle) {
		this.modifiedArticle = modifiedArticle;
	}

	public NodeDTO getNode() {
		return node;
	}

	public void setNode(NodeDTO node) {
		this.node = node;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TempArticleDTO() {
	}

	public TempArticleDTO(String text) {
		this.text = text;
	}

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

}
