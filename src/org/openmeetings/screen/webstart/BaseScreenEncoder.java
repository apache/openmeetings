package org.openmeetings.screen.webstart;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class BaseScreenEncoder implements IScreenEncoder {

	public BufferedImage resize(BufferedImage _img, Rectangle size) {
		BufferedImage img = _img;
		if (_img.getWidth() != size.width || _img.getHeight() != size.height) {
			img = (BufferedImage)_img.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
		}
		return img;
	}
	
}
