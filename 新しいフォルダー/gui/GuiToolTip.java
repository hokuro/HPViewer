package basashi.hpview.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.MathHelper;

public class GuiToolTip extends Gui {
	public float alpha = 1.0F;
	public int borderColor = -12320649;
	public int borderWidth = 1;
	public boolean Centered = true;
	public boolean centerVertically = true;
	FontRenderer cfr;
	public int fontColor = -1;
	public int gradientEnd = -16777216;
	public int gradientStart = -16777216;
	public int HEIGHT = 128;
	public int iconIndex = 0;
	public int lineSpacing = 11;
	private final AdvancedGui PARENT;
	public String[] stringLines;
	public String TextureFile = null;
	public boolean useTexture = false;
	public int WIDTH = 48;
	public int xPos = 0;
	public int yPos = 0;

	public GuiToolTip(AdvancedGui parentGui, int width, int height) {
		PARENT = parentGui;
		WIDTH = width;
		HEIGHT = height;
		cfr = Minecraft.getMinecraft().fontRendererObj;
	}

	public void drawCenteredStringNoShadow(FontRenderer par1FontRenderer, String par2Str, int par3, int par4,int par5) {
		cfr.setUnicodeFlag(true);
		if ((par5 >> 24 & 0xFF) > 16) {
			cfr.drawString(par2Str,
					MathHelper.floor_float(par3 - par1FontRenderer.getStringWidth(par2Str) / 2.0F * 0.75F) - 8, par4,
					par5, false);
		}
		cfr.setUnicodeFlag(false);
	}

	public void drawStrings(FontRenderer par1FontRenderer) {
		drawStrings(par1FontRenderer, xPos, yPos);
	}

	public void drawStrings(FontRenderer par1FontRenderer, int x, int y) {
		drawStrings(par1FontRenderer, x, y, borderColor, gradientStart, gradientEnd, fontColor);
	}

	public void drawStrings(FontRenderer par1FontRenderer, int x, int y, int border, int gradStart, int gradEnd,
			int fontcolor) {
		drawStrings(par1FontRenderer, x, y, border, gradStart, gradEnd, fontcolor, stringLines);
	}

	public void drawStrings(FontRenderer par1FontRenderer, int x, int y, int border, int gradStart, int gradEnd,
			int font, boolean centered, String[] lines) {
		GL11.glDepthFunc(519);
		Tessellator tess = Tessellator.getInstance();
		float[] components = new Color(gradientStart).getComponents(new float[4]);
		GL11.glColor4f(components[0], components[1], components[2], components[3]);
		GlStateManager.color(components[0], components[1], components[2], components[3]);
		if (useTexture) {
			int uTex = 0 + iconIndex % 8 * 18;
			int vTex = 198 + iconIndex / 8 * 18;
			PARENT.drawTexturedModalRect(x, y, uTex, vTex, WIDTH, HEIGHT);
		} else {
			PARENT.drawGradientRect(x, y, x + WIDTH, y + HEIGHT, gradStart, gradEnd);
			drawRect(x, y, x + WIDTH, y + borderWidth, border);
			drawRect(x, y + HEIGHT - borderWidth, x + WIDTH, y + HEIGHT, border);
			drawRect(x, y, x + borderWidth, y + HEIGHT, border);
			drawRect(x + WIDTH - borderWidth, y, x + WIDTH, y + HEIGHT, border);
		}
		int lineNumber = 0;
		for (String string : lines) {
			int linecount = lines.length;
			int verticalOffset = MathHelper.floor_float(
					HEIGHT / 2.0F - linecount * (par1FontRenderer.FONT_HEIGHT + 2.0F) * 1.0F / 2.0F);
			if (centered) {
				if (centerVertically) {
					drawCenteredStringNoShadow(par1FontRenderer, string, x + WIDTH / 2,
							y + verticalOffset + lineNumber * (par1FontRenderer.FONT_HEIGHT + 2), font);
				} else {
					drawCenteredStringNoShadow(par1FontRenderer, string, x + WIDTH / 2,
							y + 3 + lineNumber * lineSpacing, font);
				}
			} else {
				par1FontRenderer.drawString(string, x + 3, y + 3 + lineNumber * lineSpacing, font);
			}
			lineNumber++;
		}
		GL11.glDepthFunc(515);
		GL11.glClear(256);
	}

	public void drawStrings(FontRenderer par1FontRenderer, int x, int y, int border, int gradStart, int gradEnd,
			int font, String[] lines) {
		drawStrings(par1FontRenderer, x, y, border, gradStart, gradEnd, font, Centered, lines);
	}

	public void drawStrings(FontRenderer par1FontRenderer, int x, int y, String[] lines) {
		drawStrings(par1FontRenderer, x, y, borderColor, gradientStart, gradientEnd, fontColor,
				lines);
	}

	public void drawStrings(FontRenderer par1FontRenderer, String[] lines) {
		drawStrings(par1FontRenderer, xPos, yPos, lines);
	}

	public void drawStringsWithDifferentColors(FontRenderer par1FontRenderer, int x, int y, int border, int gradStart,
			int gradEnd, boolean centered, String colonDelimetedString, int[] colors) {
		drawStringsWithDifferentColors(par1FontRenderer, x, y, border, gradStart, gradEnd, centered,
				colonDelimetedString.split(":"), colors);
	}

	public void drawStringsWithDifferentColors(FontRenderer par1FontRenderer, int x, int y, int border, int gradStart,
			int gradEnd, boolean centered, String[] lines, int[] colors) {
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, 0.0F, 1800.0F);
		if (lines.length != colors.length) {
			throw new IllegalArgumentException(
					"The number of string lines must be equal to the number of colors passed in");
		}
		if (useTexture) {
			Tessellator tess = Tessellator.getInstance();
			WorldRenderer render = tess.getWorldRenderer();
			float[] components = new Color(gradientStart).getComponents(new float[4]);
			GL11.glColor4f(components[0], components[1], components[2], components[3]);
			GlStateManager.color(components[0], components[1], components[2], components[3]);
			int uTex = 0 + iconIndex % 8 * 18;
			int vTex = 198 + iconIndex / 8 * 18;
			PARENT.drawTexturedModalRect(x, y, uTex, vTex, WIDTH, HEIGHT);
			tess.draw();
			render.begin(7, new VertexFormat());
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			PARENT.drawGradientRect(x, y, WIDTH, HEIGHT, gradStart, gradEnd);
			drawRect(x, y, x + WIDTH, y + borderWidth, border);
			drawRect(x, y + HEIGHT - borderWidth, x + WIDTH, y + HEIGHT, border);
			drawRect(x, y, x + borderWidth, y + HEIGHT, border);
			drawRect(x + WIDTH - borderWidth, y, x + WIDTH, y + HEIGHT, border);
		}
		int lineNumber = 0;
		for (String string : lines) {
			int linecount = lines.length;
			int verticalSpacing = MathHelper.floor_float(HEIGHT / (linecount + 1));
			if (centered) {
				if (centerVertically) {
					drawCenteredString(par1FontRenderer, string, x + WIDTH / 2,
							y + verticalSpacing * lineNumber - par1FontRenderer.FONT_HEIGHT / 2, colors[lineNumber]);
				} else {
					drawCenteredString(par1FontRenderer, string, x + WIDTH / 2, y + 3 + lineNumber * lineSpacing,
							colors[lineNumber]);
				}
			} else {
				drawString(par1FontRenderer, string, x + 3, y + 3 + lineNumber * lineSpacing,
						colors[lineNumber]);
			}
			lineNumber++;
		}
		GL11.glPopMatrix();
	}

	public void drawStringsWithDifferentColors(FontRenderer par1FontRenderer, int[] colors) {
		drawStringsWithDifferentColors(par1FontRenderer, xPos, yPos, borderColor, gradientStart,
				gradientEnd, Centered, stringLines, colors);
	}

	public boolean isCentered() {
		return Centered;
	}

	public boolean isCenterVertically() {
		return centerVertically;
	}

	public boolean isUsingTexture() {
		return useTexture;
	}

	public void setBasicColors(int border, int gradStart, int gradEnd, int font) {
		borderColor = border;
		gradientStart = gradStart;
		gradientEnd = gradEnd;
		fontColor = font;
	}

	public void setBorderWidth(int bborderWidth) {
		borderWidth = bborderWidth;
	}

	public void setCentered(boolean bCentered) {
		Centered = bCentered;
	}

	public void setCenterVertically(boolean bcenterVertically) {
		centerVertically = bcenterVertically;
	}

	public void setDontUseTexture() {
		useTexture = false;
	}

	public void setGlobalAlpha(float trans) {
		alpha = (trans > 0.0F ? trans : trans > 1.0F ? 1.0F : 0.0F);
		Color color = new Color(borderColor);
		float[] temp = color.getColorComponents(new float[3]);
		color = new Color(temp[0], temp[1], temp[2], alpha);
		borderColor = color.getRGB();
		color = new Color(gradientStart);
		temp = color.getColorComponents(new float[3]);
		color = new Color(temp[0], temp[1], temp[2], alpha);
		gradientStart = color.getRGB();
		color = new Color(gradientEnd);
		temp = color.getColorComponents(new float[3]);
		color = new Color(temp[0], temp[1], temp[2], alpha);
		gradientEnd = color.getRGB();
		color = new Color(fontColor);
		temp = color.getColorComponents(new float[3]);
		color = new Color(temp[0], temp[1], temp[2], alpha);
		fontColor = color.getRGB();
	}

	public void setGlobalAlpha(int trans) {
		trans = trans > 0 ? trans : trans > 255 ? 255 : 0;
		setGlobalAlpha(trans / 255.0F * 1.0F);
	}

	public void setLineSpacing(int alineSpacing) {
		lineSpacing = alineSpacing;
	}

	public void setPos(int axPos, int ayPos) {
		xPos = axPos;
		yPos = ayPos;
	}

	public void setStringLines(String[] astringLines) {
		stringLines = astringLines;
	}

	public void setTextLines(String[] alines) {
		stringLines = alines;
	}

	public void setTextureFile(String aTextureFile, int aiconIndex) {
		TextureFile = aTextureFile;
		iconIndex = aiconIndex;
		useTexture = true;
	}

	public void setUpForDraw(int x, int y) {
		setUpForDraw(x, y, borderColor, gradientStart, gradientEnd, fontColor, Centered,
				centerVertically);
	}

	public void setUpForDraw(int x, int y, int borderColor, int baseColor, int gradEndColor, int defaultFontColor,
			boolean centeredHorizontally, boolean centeredVerTically) {
		setUpForDraw(x, y, borderColor, baseColor, gradEndColor, defaultFontColor, centeredHorizontally,
				centeredVerTically, WIDTH, HEIGHT);
	}

	public void setUpForDraw(int x, int y, int borderColor, int baseColor, int gradEndColor, int defaultFontColor,
			boolean centeredHorizontally, boolean centeredVerTically, int newWidth, int newHeight) {
		setUpForDraw(x, y, borderColor, baseColor, gradEndColor, defaultFontColor, centeredHorizontally,
				centeredVerTically, newWidth, newHeight, TextureFile, iconIndex);
	}

	public void setUpForDraw(int x, int y, int borderColor, int baseColor, int gradEndColor, int defaultFontColor,
			boolean centeredHorizontally, boolean centeredVertically, int newWidth, int newHeight, String texture,
			int iconIndex) {
		setUpForDraw(x, y, borderColor, baseColor, gradEndColor, defaultFontColor, centeredHorizontally,
				centeredVertically, newWidth, newHeight, texture, iconIndex, stringLines);
	}

	public void setUpForDraw(int x, int y, int aborderColor, int baseColor, int gradEndColor, int defaultFontColor,
			boolean centeredHorizontally, boolean centeredVertically, int newWidth, int newHeight, String texture,
			int iconIndex, String[] lines) {
		xPos = x;
		yPos = y;
		borderColor = aborderColor;
		gradientStart = baseColor;
		gradientEnd = gradEndColor;
		fontColor = defaultFontColor;
		Centered = centeredHorizontally;
		centerVertically = centeredVertically;
		WIDTH = newWidth;
		HEIGHT = newHeight;
		stringLines = lines;
		if ((texture == null) || ("".equals(texture))) {
			setDontUseTexture();
		} else {
			setTextureFile(texture, iconIndex);
		}
	}

	public void setUpForDraw(int x, int y, String[] lines) {
		stringLines = lines;
		setUpForDraw(x, y, borderColor, gradientStart, gradientEnd, fontColor, Centered,
				centerVertically);
	}
}
