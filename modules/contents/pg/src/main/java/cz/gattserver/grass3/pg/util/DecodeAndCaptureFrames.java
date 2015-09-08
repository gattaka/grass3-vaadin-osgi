package cz.gattserver.grass3.pg.util;

import java.awt.image.BufferedImage;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class DecodeAndCaptureFrames {

	private boolean waiting = true;
	private BufferedImage bi;

	public BufferedImage decodeAndCaptureFrames(final String filename) {
		IMediaReader reader = ToolFactory.makeReader(filename);
		reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		reader.addListener(new MediaListenerAdapter() {

			public void onVideoPicture(IVideoPictureEvent event) {
				try {
					bi = event.getImage();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					waiting = false;
				}
			}
		});

		while (waiting) {
			reader.readPacket();
		}

		return bi;
	}

}