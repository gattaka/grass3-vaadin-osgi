package org.myftp.gattserver.grass3.model.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO tagů obsahů pro jejich snadnější hledání
 * 
 * @author gatt
 * 
 */
public class ContentTagDTO implements Serializable {

	private static final long serialVersionUID = -5140560394792756648L;

	/**
	 * Název tagu
	 */
	private String name;

	/**
	 * Obsahy tagu
	 */
	private Set<Long> contentNodeIds;

	/**
	 * DB identifikátor
	 */
	private Long Id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Set<Long> getContentNodeIds() {
		return contentNodeIds;
	}

	public void setContentNodeIds(Set<Long> contentNodeIds) {
		this.contentNodeIds = contentNodeIds;
	}

	public ContentTagDTO(String name) {
		this.name = name;
	}

	public ContentTagDTO() {
	}

	@Override
	public int hashCode() {
		return 7 + 31 * (Id == null ? 0 : Id.hashCode())
				+ (name == null ? 0 : name.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ContentTagDTO) {
			ContentTagDTO tag = (ContentTagDTO) obj;
			return tag.getId() == Id
					&& ((tag.getName() == null && name == null) || tag
							.getName().equals(name));
		} else
			return false;
	}

}
