package cz.gattserver.grass3.print3d.interfaces;

import java.nio.file.Path;

public class Print3dViewItemTO {

	private Path file;
	private String onlyName;
	private String extension;
	private Print3dItemType type;

	public Print3dViewItemTO(Path file, String onlyName, String extension, Print3dItemType type) {
		super();
		this.file = file;
		this.onlyName = onlyName;
		this.extension = extension;
		this.type = type;
	}

	public Print3dViewItemTO() {
	}

	public String getOnlyName() {
		return onlyName;
	}

	public Print3dViewItemTO setOnlyName(String onlyName) {
		this.onlyName = onlyName;
		return this;
	}

	public String getExtension() {
		return extension;
	}

	public Print3dViewItemTO setExtension(String extension) {
		this.extension = extension;
		return this;
	}

	public Print3dItemType getType() {
		return type;
	}

	public Print3dViewItemTO setType(Print3dItemType type) {
		this.type = type;
		return this;
	}

	public String getName() {
		if (onlyName == null)
			return null;
		if (extension == null)
			return onlyName;
		return onlyName + "." + extension;
	}

	public Path getFile() {
		return file;
	}

	public Print3dViewItemTO setFile(Path file) {
		this.file = file;
		return this;
	}

}
