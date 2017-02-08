package basashi.hpview.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class GuiCheckBox extends GuiButton {
	public boolean checked = false;

	public GuiCheckBox(int id, int x, int y, int w, int h, String Message) {
		super(id, x, y, w, h, Message);
		this.enabled = true;
		this.visible = true;
		this.displayString = Message;
	}

	public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
		int offset = par1Minecraft.fontRendererObj.getStringWidth(this.displayString) + 5;
		if (GuiTool.widgetsPNG == null) {
			try {
				BufferedImage widgetspng = ImageIO
						.read(Minecraft.class.getResourceAsStream("/assets/minecraft/textures/gui/widgets.png"));
				GuiTool.widgetsPNG = new DynamicTexture(widgetspng);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
		if (this.enabled) {
			par1Minecraft.fontRendererObj.drawStringWithShadow(this.displayString, this.xPosition, this.yPosition,
					Color.white.getRGB());
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			par1Minecraft.fontRendererObj.drawStringWithShadow(this.displayString, this.xPosition, this.yPosition,
					Color.GRAY.getRGB());
			GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
		}
		GuiTool.widgetsPNG.updateDynamicTexture();
		if (!this.checked) {
			drawTexturedModalRect128(this.xPosition + offset, this.yPosition, 240, 0, 8, 8);
		} else {
			drawTexturedModalRect128(this.xPosition + offset, this.yPosition, 232, 0, 8, 8);
		}
	}

	public void drawTexturedModalRect128(int par1, int par2, int par3, int par4, int par5, int par6) {
		float var7 = 0.007813F;
		float var8 = 0.007813F;
		WorldRenderer render = Tessellator.getInstance().getWorldRenderer();
		render.begin(7,new VertexFormat());
		GuiTool.addVertexWithUV(par1 + 0, par2 + par6, (int)this.zLevel, (par3 + 0) * var7, (par4 + par6) * var8,render);
		GuiTool.addVertexWithUV(par1 + par5, par2 + par6, (int)this.zLevel, (par3 + par5) * var7, (par4 + par6) * var8,render);
		GuiTool.addVertexWithUV(par1 + par5, par2 + 0, (int)this.zLevel, (par3 + par5) * var7, (par4 + 0) * var8,render);
		GuiTool.addVertexWithUV(par1 + 0, par2 + 0, (int)this.zLevel, (par3 + 0) * var7, (par4 + 0) * var8,render);
		Tessellator.getInstance().draw();
	}

	public boolean isChecked() {
		return this.checked;
	}

	public void mouseReleased(int par1, int par2) {
		this.checked = (!this.checked);
		super.mouseReleased(par1, par2);
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean toggle() {
		this.checked = (!this.checked);
		return this.checked;
	}
}
