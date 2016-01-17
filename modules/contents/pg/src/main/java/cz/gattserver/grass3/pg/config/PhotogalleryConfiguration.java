package cz.gattserver.grass3.pg.config;

import cz.gattserver.grass3.config.AbstractConfiguration;

/**
 * @author Hynek
 * 
 */
public class PhotogalleryConfiguration extends AbstractConfiguration {

	public static final String PHOTOGALLERY_PATH = "/photogallery-files";

	private String rootDir = "rootDir";

	private String miniaturesDir = "foto_mini";

	private String previewsDir = "video_preview";

	private String slideshowDir = "foto_slideshow";

	public PhotogalleryConfiguration() {
		super("cz.gattserver.grass3.pg");
	}

	public String getSlideshowDir() {
		return slideshowDir;
	}

	public void setSlideshowDir(String slideshowDir) {
		this.slideshowDir = slideshowDir;
	}

	public String getMiniaturesDir() {
		return miniaturesDir;
	}

	public void setMiniaturesDir(String miniaturesDir) {
		this.miniaturesDir = miniaturesDir;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public String getPreviewsDir() {
		return previewsDir;
	}

	public void setPreviewsDir(String previewDir) {
		this.previewsDir = previewDir;
	}

}