package org.myftp.gattserver.grass3.articles.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.myftp.gattserver.grass3.model.domain.Node;
import org.myftp.gattserver.grass3.model.domain.User;

@Entity
@Table(name = "TEMPARTICLE")
public class TempArticle {

	/**
	 * Do které kategorie byl článek určen ?
	 */
	@OneToOne
	private Node node;

	/**
	 * Kdo byl autorem v době psaní článku ?
	 */
	@ManyToOne
	private User user;

	/**
	 * Název článku
	 */
	private String name;

	/**
	 * Pokud jde o rozpracovanou verzi již existujícího článku, potřebuju a něj
	 * referenci
	 */
	@OneToOne
	private Article modifiedArticle;

	/**
	 * Obsah článku
	 */
	@Column(columnDefinition = "TEXT")
	private String text;

	/**
	 * Klíčová slova
	 */
	private String tags;

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	public Long getId() {
		return id;
	}

	public Article getModifiedArticle() {
		return modifiedArticle;
	}

	public void setModifiedArticle(Article modifiedArticle) {
		this.modifiedArticle = modifiedArticle;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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
