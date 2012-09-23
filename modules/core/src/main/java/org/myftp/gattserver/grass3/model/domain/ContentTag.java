package org.myftp.gattserver.grass3.model.domain;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CONTENT_TAG")
public class ContentTag {

	/**
	 * Název tagu
	 */
	private String name;

	/**
	 * Obsahy tagu
	 */
	private List<Long> contentNodeIDs;

	/**
	 * DB identifikátor
	 */
	private Long id;

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ElementCollection
	@CollectionTable(name = "contentnode_content_tag", joinColumns = @JoinColumn(name = "contenttags_id"))
	@Column(name = "contentnode_id")
	public List<Long> getContentNodeIDs() {
		return contentNodeIDs;
	}

	public void setContentNodeIDs(List<Long> contentNodeIDs) {
		this.contentNodeIDs = contentNodeIDs;
	}

}
