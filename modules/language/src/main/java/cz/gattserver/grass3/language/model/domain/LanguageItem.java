package cz.gattserver.grass3.language.model.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "LANGUAGEITEM")
public class LanguageItem {

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Typ záznamu
	 */
	private ItemType type;

	/**
	 * Jazyk pod který záznam patří
	 */
	@ManyToOne
	private Language language;

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

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
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
