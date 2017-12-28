package cz.gattserver.grass3.pg.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

public class DecodeAndCaptureFrames {

	public BufferedImage decodeAndCaptureFrames(Path file) throws IOException, JCodecException {
		Picture picture = FrameGrab.getFrameFromFile(file.toFile(), 1);
		return AWTUtil.toBufferedImage(picture);
	}

}