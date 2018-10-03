package cz.gattserver.grass3.campgames.interfaces;

import java.time.LocalDateTime;

public class CampgameFileTO {

	private String name;
	private String size;
	private LocalDateTime lastModified;

	public String getSize() {
		return size;
	}

	public CampgameFileTO setSize(String size) {
		this.size = size;
		return this;
	}

	public String getName() {
		return name;
	}

	public CampgameFileTO setName(String name) {
		this.name = name;
		return this;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public CampgameFileTO setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
		return this;
	}

}
