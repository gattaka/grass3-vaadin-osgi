package cz.gattserver.grass3.pg.interfaces;

import java.nio.file.Path;

public class PGSettingsItemTO {

	private Path path;
	private PhotogalleryRESTOverviewTO overviewTO;
	private Long size;
	private Long filesCount;

	public PGSettingsItemTO(Path path, PhotogalleryRESTOverviewTO overviewTO, Long size, Long filesCount) {
		this.path = path;
		this.overviewTO = overviewTO;
		this.size = size;
		this.filesCount = filesCount;
	}

	public Long getFilesCount() {
		return filesCount;
	}

	public void setFilesCount(Long filesCount) {
		this.filesCount = filesCount;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public PhotogalleryRESTOverviewTO getOverviewTO() {
		return overviewTO;
	}

	public void setOverviewTO(PhotogalleryRESTOverviewTO overviewTO) {
		this.overviewTO = overviewTO;
	}

}
