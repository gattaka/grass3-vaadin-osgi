package cz.gattserver.grass3.language.model.dto;

import java.time.LocalDateTime;

import cz.gattserver.grass3.language.model.domain.ItemType;

public class LanguageItemTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Typ záznamu
	 */
	private ItemType type;

	/**
	 * Jazyk pod který záznam patří
	 */
	private Long language;

	/**
	 * Obsah
	 */
	private String content;

	/**
	 * Překlad
	 */
	private String translation;

	/**
	 * Poslední datum zkoušení
	 */
	private LocalDateTime lastTested;

	/**
	 * Kolikrát již byl záznam zkoušen
	 */
	private Integer tested = 0;

	/**
	 * Úspěšnost při zkoušení
	 */
	private Double successRate = 0.0;

	public Integer getTested() {
		return tested;
	}

	public void setTested(Integer tested) {
		this.tested = tested;
	}

	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}

	public Long getLanguage() {
		return language;
	}

	public void setLanguage(Long language) {
		this.language = language;
	}

	public LocalDateTime getLastTested() {
		return lastTested;
	}

	public void setLastTested(LocalDateTime lastTested) {
		this.lastTested = lastTested;
	}

	public Double getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(Double successRate) {
		this.successRate = successRate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
