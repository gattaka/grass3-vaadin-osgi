package cz.gattserver.grass3.pg.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;

public class DecodeAndCaptureFrames {

	public BufferedImage decodeAndCaptureFrames(Path file) throws IOException, JCodecException {
		return FrameGrab.getFrame(file.toAbsolutePath().toFile(), 1);
	}

}