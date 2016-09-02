package org.warp.engine;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.warp.device.PIDisplay;
import org.warp.device.PIFrame;
import org.warp.picalculator.Main;

public class Display {

	private static PIFrame INSTANCE = new PIFrame();
	public static int[] size = new int[] { 1, 1 };
	public static BufferedImage g = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
	public static int[] canvas2d = new int[1];
	public static int color = 0xFF000000;
	private static volatile Startable refresh;
	private static boolean initialized = false;

	public static void setTitle(String title) {
		INSTANCE.setTitle(title);
	}

	public static void setResizable(boolean r) {
		INSTANCE.setResizable(r);
		if (!r)
			INSTANCE.setUndecorated(true);
	}

	public static void setDisplayMode(final int ww, final int wh) {
		INSTANCE.setSize(ww, wh);
		size = new int[] { ww, wh };
		canvas2d = new int[ww * wh];
		g = new BufferedImage(ww, wh, BufferedImage.TYPE_INT_ARGB);
		INSTANCE.wasResized = false;
	}

	public static void create() {
		INSTANCE.setVisible(true);
		initialized = true;
	}

	public static boolean initialized() {
		return initialized;
	}
	
	public static boolean wasResized() {
		if (INSTANCE.wasResized) {
			size = new int[] { INSTANCE.getWidth(), INSTANCE.getHeight() };
			canvas2d = new int[size[0] * size[1]];
			g = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
			INSTANCE.wasResized = false;
			return true;
		}
		return false;
	}

	public static int getWidth() {
		return INSTANCE.getWidth()-Main.screenPos[0];
	}

	public static int getHeight() {
		return INSTANCE.getHeight()-Main.screenPos[1];
	}

	public static void destroy() {
		initialized = false;
		INSTANCE.setVisible(false);
		INSTANCE.dispose();
	}

	public static void start(Startable refresh) {
		Display.refresh = refresh;
	}

	@Deprecated()
	public static void refresh() {
		if (PIDisplay.screen == null || PIDisplay.loading || (PIDisplay.error != null && PIDisplay.error.length() > 0) || PIDisplay.screen == null || PIDisplay.screen.mustBeRefreshed()) {
			Display.INSTANCE.c.repaint(false);
		}
	}

	public static void refresh(boolean force) {
		Display.INSTANCE.c.repaint(force);
	}
	
	public static void update(Graphics g, boolean forcerefresh) {
		if (refresh != null) {
			refresh.force = forcerefresh;
			refresh.run();

	        final int[] a = ((DataBufferInt) Display.g.getRaster().getDataBuffer()).getData();
	        System.arraycopy(canvas2d, 0, a, 0, canvas2d.length);
			g.clearRect(0, 0, size[0], size[1]);
			g.drawImage(Display.g, 0, 0, null);
		}
	}

	public static abstract class Startable {
		public Startable() {
			this.force = false;
		}

		public Startable(boolean force) {
			this.force = force;
		}

		public boolean force = false;

		public abstract void run();
	}

	public static class Render {
		public static int clearcolor = 0xFFCCE7D4;
		public static RAWFont currentFont;

		public static void glColor3f(int r, int gg, int b) {
			glColor4f(r, gg, b, 255);
		}

		public static void glColor(int c) {
			color = c & 0xFFFFFFFF;
		}

		public static void glClearColor(int c) {
			clearcolor = c & 0xFFFFFFFF;
		}

		public static void glColor4f(int red, int green, int blue, int alpha) {
			color = (alpha << 24) + (red << 16) + (green << 8) + (blue);
		}

		public static void glClearColor(int red, int green, int blue, int alpha) {
			clearcolor = (alpha << 24) + (red << 16) + (green << 8) + (blue);
		}

		public static void glClear() {
			for (int x = 0; x < size[0]; x++) {
				for (int y = 0; y < size[1]; y++) {
					canvas2d[x + y * size[0]] = clearcolor;
				}
			}
		}

		public static void glDrawSkin(int skinwidth, int[] skin, int x0, int y0, int s0, int t0, int s1, int t1, boolean transparent) {
			x0+=Main.screenPos[0];
			y0+=Main.screenPos[1];
			if (x0 >= size[0] || y0 >= size[0]) {
				return;
			}
			if (x0 + (s1-s0) >= size[0]) {
				s1 = size[0] - x0 + s0;
			}
			if (y0 + (t1-t0) >= size[1]) {
				t1 = size[1] - y0 + t0;
			}
			int oldColor;
			int newColor;
			for (int texx = 0; texx < s1 - s0; texx++) {
				for (int texy = 0; texy < t1 - t0; texy++) {
					newColor = skin[(s0 + texx) + (t0 + texy) * skinwidth];
					if (transparent) {
						oldColor = canvas2d[(x0 + texx) + (y0 + texy) * size[0]];
						float a2 = ((float)(newColor >> 24 & 0xFF)) / 255f;
						float a1 = 1f-a2;
						int r = (int) ((oldColor >> 16 & 0xFF) * a1 + (newColor >> 16 & 0xFF) * a2);
						int g = (int) ((oldColor >> 8 & 0xFF) * a1 + (newColor >> 8 & 0xFF) * a2);
						int b = (int) ((oldColor & 0xFF) * a1 + (newColor & 0xFF) * a2);
						newColor = 0xFF000000 | r << 16 | g << 8 | b;
					}
					canvas2d[(x0 + texx) + (y0 + texy) * size[0]] = newColor;
				}
			}
		}

		public static void glDrawLine(int x0, int y0, int x1, int y1) {
			x0+=Main.screenPos[0];
			x1+=Main.screenPos[0];
			y0+=Main.screenPos[1];
			y1+=Main.screenPos[1];
			if (x0 >= size[0] || y0 >= size[0]) {
				return;
			}
			if (y0 == y1) {
				for (int x = 0; x <= x1 - x0; x++) {
					canvas2d[x0 + x + y0 * size[0]] = color;
				}
			} else if (x0 == x1) {
				for (int y = 0; y <= y1 - y0; y++) {
					canvas2d[x0 + (y0 + y) * size[0]] = color;
				}
			} else {
				int m = (y1 - y0) / (x1 - x0);
				for (int texx = 0; texx <= x1 - x0; texx++) {
					if (x0 + texx < size[0] && y0 + (m * texx) < size[1]) {
						canvas2d[(x0 + texx) + (y0 + (m * texx)) * size[0]] = color;
					}
				}
			}
		}

		public static void glFillRect(int x0, int y0, int w1, int h1) {
			x0+=Main.screenPos[0];
			y0+=Main.screenPos[1];
			int x1 = x0+w1;
			int y1 = y0+h1;
			if (x0 >= size[0] || y0 >= size[0]) {
				return;
			}
			if (x1 >= size[0]) {
				x1 = size[0];
			}
			if (y1 >= size[1]) {
				y1 = size[1];
			}
			final int sizeW = size[0];
			for (int x = x0; x < x1; x++) {
				for (int y = y0; y < y1; y++) {
					canvas2d[(x) + (y) * sizeW] = color;
				}
			}
		}

		public static int[] getMatrixOfImage(BufferedImage bufferedImage) {
			int width = bufferedImage.getWidth(null);
			int height = bufferedImage.getHeight(null);
			int[] pixels = new int[width * height];
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					pixels[i + j * width] = bufferedImage.getRGB(i, j);
				}
			}

			return pixels;
		}

		public static void glDrawStringLeft(int x, int y, String text) {
			x+=Main.screenPos[0];
			y+=Main.screenPos[1];
			final int[] chars = currentFont.getCharIndexes(text);
			currentFont.drawText(canvas2d, size, x, y, chars, color);
		}

		public static void glDrawStringCenter(int x, int y, String text) {
			glDrawStringLeft(x - (getStringWidth(text) / 2), y, text);
		}

		public static void glDrawStringRight(int x, int y, String text) {
			glDrawStringLeft(x - getStringWidth(text), y, text);
		}

		public static void setFont(RAWFont font) {
			if (currentFont != font) {
				currentFont = font;
			}
		}

		public static int getStringWidth(String text) {
			int w =(currentFont.charW+1)*text.length();
			if (text.length() > 0) {
				return w-1;
			} else {
				return 0;
			}
			// return text.length()*6;
		}

		public static int getWidth(FontMetrics fm, String text) {
			return fm.stringWidth(text);
		}

	}
}
