package cz.gattserver.grass3.print3d.interfaces;

import java.nio.file.Path;

public class Print3dViewItemTO {

	private Path file;
	private String name;

	public String getName() {
		return name;
	}

	public Print3dViewItemTO setName(String name) {
		this.name = name;
		return this;
	}

	public Path getFile() {
		return file;
	}

	public Print3dViewItemTO setFile(Path file) {
		this.file = file;
		return this;
	}

}
