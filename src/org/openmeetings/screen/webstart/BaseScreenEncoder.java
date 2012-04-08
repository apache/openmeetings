package org.openmeetings.screen.webstart;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public abstract class BaseScreenEncoder implements IScreenEncoder {

	public BufferedImage resize(BufferedImage _img, Rectangle size) {
		BufferedImage img = _img;
		if (_img.getWidth() != size.width || _img.getHeight() != size.height) {
			img = new BufferedImage(size.width, size.height,
					BufferedImage.TYPE_INT_RGB);

			Graphics2D graphics2D = img.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics2D.drawImage(_img, 0, 0, size.width, size.height, null);
			graphics2D.dispose();
		}
		return img;
	}
	
}
