package com.github.dasilvafg.smartops;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Utility class with methods for images.
 * 
 * @author fabio
 *
 */
public class ImageUtil {

	/**
	 * Creates a text marker to be used in maps.
	 * 
	 * @param text
	 *            The text. Will be truncated if larger than 30 characters.
	 * @param bg
	 *            The background color.
	 * @param fg
	 *            The foreground color.
	 * @return A PNG image.
	 * @throws IOException
	 *             If the image creation fails.
	 */
	public static byte[] createTextMarker(String text, Color bg, Color fg) throws IOException {
		if (text == null) {
			text = "";
		} else {
			text = text.trim().toUpperCase();
		}
		if (text.isEmpty()) {
			text = "[MARKER]";
		} else if (text.length() > 30) {
			text = text.substring(0, 30);
		}

		// Calculate size
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		Font font = new Font("Arial", Font.BOLD, 15);
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
		int w0 = fm.stringWidth(text);
		int h0 = fm.getHeight();
		int w1 = w0 + 10;
		int h1 = h0 + 10;
		g2d.dispose();

		// Create image
		img = new BufferedImage(w1, h1, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setFont(font);
		fm = g2d.getFontMetrics();

		// Draw marker
		int wm = w1 / 20;
		int px1 = w1 / 2;
		int px2 = px1 - wm;
		int px3 = px1 + wm;
		g2d.setColor(bg);
		g2d.fillRoundRect(0, 0, w1, h0, 5, 5);
		g2d.fillPolygon(new int[] {
				px1, px2, px3
		}, new int[] {
				h1, h0, h0
		}, 3);
		g2d.setColor(fg);
		g2d.drawString(text, (w1 - w0) / 2, fm.getAscent());
		g2d.dispose();

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ImageIO.write(img, "png", buffer);
		return buffer.toByteArray();
	}

	/**
	 * Detects the type of an image.
	 * 
	 * <p>
	 * Supported types are:
	 * <ul>
	 * <li>JPEG
	 * <li>PNG
	 * <li>GIF
	 * <li>WebP
	 * </ul>
	 * 
	 * @param img
	 *            The image.
	 * @return The type name or {@code null} if not recognized.
	 */
	public static String detectType(byte[] img) {
		if (img != null && img.length > 4) {
			int n0 = img[0] & 0xFF;
			int n1 = img[1] & 0xFF;
			int n2 = img[2] & 0xFF;
			int n3 = img[3] & 0xFF;
			if (n0 == 0xFF && n1 == 0xD8 && n2 == 0xFF && n3 == 0xE0) {
				return "jpeg";
			}
			if (n0 == 0x89 && n1 == 0x50 && n2 == 0x4E && n3 == 0x47) {
				return "png";
			}
			if (n0 == 0x47 && n1 == 0x49 && n2 == 0x46 && n3 == 0x38) {
				return "gif";
			}
			if (n0 == 0x52 && n1 == 0x49 && n2 == 0x46 && n3 == 0x46) {
				return "webp";
			}
		}
		return null;
	}

}
