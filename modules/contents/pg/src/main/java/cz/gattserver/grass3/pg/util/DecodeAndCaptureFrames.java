package cz.gattserver.grass3.pg.util;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

import org.jcodec.api.FrameGrab;

public class DecodeAndCaptureFrames {

	public BufferedImage decodeAndCaptureFrames(Path file) throws Exception {
		BufferedImage frame = FrameGrab.getFrame(file.toAbsolutePath().toFile(), 1);
		return frame;
	}

}