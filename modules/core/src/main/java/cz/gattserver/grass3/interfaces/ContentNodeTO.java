package cz.gattserver.grass3.interfaces;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ContentNodeTO {

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
	private NodeOverviewTO parent;

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
	private boolean publicated = true;

	/**
	 * Kdo ho vytvořil
	 */
	private UserInfoTO author;

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Jde o plnohodnotný článek, nebo jde o rozpracovaný obsah?
	 */
	private boolean draft = false;

	/**
	 * Jde-li o draft upravovaného obsahu, jaké je jeho id
	 */
	private Long draftSourceId;

	/**
	 * Tagy
	 */
	private Set<ContentTagOverviewTO> contentTags;

	public String getContentReaderID() {
		return contentReaderID;
	}

	public void setContentReaderID(String contentReaderID) {
		this.contentReaderID = contentReaderID;
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

	public NodeOverviewTO getParent() {
		return parent;
	}

	public void setParent(NodeOverviewTO parent) {
		this.parent = parent;
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

	public boolean isPublicated() {
		return publicated;
	}

	public void setPublicated(boolean publicated) {
		this.publicated = publicated;
	}

	public UserInfoTO getAuthor() {
		return author;
	}

	public void setAuthor(UserInfoTO author) {
		this.author = author;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDraft(boolean draft) {
		this.draft = draft;
	}

	public boolean isDraft() {
		return draft;
	}

	public Long getDraftSourceId() {
		return draftSourceId;
	}

	public void setDraftSourceId(Long draftSourceId) {
		this.draftSourceId = draftSourceId;
	}

	public Set<ContentTagOverviewTO> getContentTags() {
		return contentTags;
	}

	public Set<String> getContentTagsAsStrings() {
		Set<String> set = new HashSet<>();
		contentTags.forEach(c -> set.add(c.getName()));
		return set;
	}

	public void setContentTags(Set<ContentTagOverviewTO> contentTags) {
		this.contentTags = contentTags;
	}

}
