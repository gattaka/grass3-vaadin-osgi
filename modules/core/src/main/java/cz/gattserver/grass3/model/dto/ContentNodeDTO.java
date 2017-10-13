package cz.gattserver.grass3.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami
 * 
 * @author gatt
 * 
 */
public class ContentNodeDTO implements Serializable, Authorizable {

	private static final long serialVersionUID = 723375154665160018L;

	/**
	 * ID služby, která daný obsah umí číst
	 */
	private String contentReaderID;

	/**
	 * ID samotného obsahu v rámci dané služby (typu obsahu)
	 */
	private Long contentID;

	/**
	 * Název obsahu
	 */
	private String name;

	/**
	 * nadřazený uzel (kategorie ve které obsah je)
	 */
	private NodeBreadcrumbDTO parent;

	/**
	 * Kdy byl obsah vytvořen
	 */
	private LocalDateTime creationDate;

	/**
	 * Kdy byl naposledy upraven
	 */
	private LocalDateTime lastModificationDate;

	/**
	 * Je obsah ve fázi příprav, nebo už má být publikován ?
	 */
	private Boolean publicated = true;

	/**
	 * Jde o plnohodnotný článek, nebo jde o rozpracovaný obsah?
	 */
	private Boolean draft = false;

	/**
	 * Jde-li o draft upravovaného obsahu, jaké je jeho id
	 */
	private Long draftSourceId;

	/**
	 * Tagy
	 */
	private Set<ContentTagDTO> contentTags;

	/**
	 * Kdo ho vytvořil
	 */
	private UserInfoDTO author;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public Long getDraftSourceId() {
		return draftSourceId;
	}

	public void setDraftSourceId(Long draftSourceId) {
		this.draftSourceId = draftSourceId;
	}

	public Boolean isDraft() {
		return draft == null ? false : draft;
	}

	public void setDraft(Boolean draft) {
		this.draft = draft;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContentReaderID() {
		return contentReaderID;
	}

	public void setContentReaderID(String contentReaderID) {
		this.contentReaderID = contentReaderID;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public LocalDateTime getLastModificationDate() {
		return lastModificationDate;
	}

	public void setLastModificationDate(LocalDateTime lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	public Boolean isPublicated() {
		return publicated;
	}

	public void setPublicated(Boolean publicated) {
		this.publicated = publicated;
	}

	public Long getContentID() {
		return contentID;
	}

	public void setContentID(Long contentID) {
		this.contentID = contentID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeBreadcrumbDTO getParent() {
		return parent;
	}

	public void setParent(NodeBreadcrumbDTO parent) {
		this.parent = parent;
	}

	public UserInfoDTO getAuthor() {
		return author;
	}

	public void setAuthor(UserInfoDTO author) {
		this.author = author;
	}

	public Set<ContentTagDTO> getContentTags() {
		return contentTags;
	}

	public void setContentTags(Set<ContentTagDTO> contentTags) {
		this.contentTags = contentTags;
	}

}
