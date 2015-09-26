package cz.gattserver.grass3.pg.util;

import java.awt.image.BufferedImage;
import java.io.File;

import org.jcodec.api.FrameGrab;

public class DecodeAndCaptureFrames {

	public BufferedImage decodeAndCaptureFrames(final String filename) throws Exception {
		BufferedImage frame = FrameGrab.getFrame(new File(filename), 1);
		return frame;
	}

}