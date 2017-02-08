package basashi.hpview.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import basashi.hpview.config.ConfigValue;
import basashi.hpview.texture.EnumSkinPart;
import basashi.hpview.texture.SkinRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.MathHelper;

public class RepositionGui extends GuiScreen {
	private float animationTick = 0.0F;
	private DynamicTexture colorBarTex;
	private final BufferedImage colorBar = new BufferedImage(8, 1536, 1);
	private DynamicTexture gradientTex;
	private final BufferedImage Gradient = new BufferedImage(256, 256, 1);
	private GuiTextField gtf;
	public boolean mouseDown = false;
	private boolean setDamageColor = false;
	private boolean setHealColor = false;
	private int textWidth;

	public RepositionGui() {
	}

	protected void actionPerformed(GuiButton par1GuiButton)  throws IOException{
		switch (par1GuiButton.id) {
		case 0:
			((GuiCheckBox) par1GuiButton).setChecked(!((GuiCheckBox) par1GuiButton).isChecked());
			ConfigValue.General.portraitEnabled = ((GuiCheckBox) par1GuiButton).isChecked();
			break;
		case 1:
			((GuiCheckBox) par1GuiButton).setChecked(!((GuiCheckBox) par1GuiButton).isChecked());
			ConfigValue.General.enablePotionEffects = ((GuiCheckBox) par1GuiButton).isChecked();
			break;
		case 2:
			((GuiCheckBox) par1GuiButton).setChecked(!((GuiCheckBox) par1GuiButton).isChecked());
			ConfigValue.General.popOffsEnabled = ((GuiCheckBox) par1GuiButton).isChecked();
			break;
		case 3:
			this.mc.displayGuiScreen(new SkinGui(null, this.mc.gameSettings));
			break;
		case 4:

			//Minecraft.getMinecraft().thePlayer.openGUI(Minecraft.getMinecraft().thePlayer, new AdvancedGui());
			break;
		case 5:
			this.mc.thePlayer.closeScreen();
		}
		super.actionPerformed(par1GuiButton);
	}

	public boolean doesGuiPauseGame() {
		return true;
	}

	private void drawColorbar() {
		if (this.colorBarTex == null) {
			int locx = 0;
			for (int color = 0; color < 6; color++) {
				for (int saturation = 0; saturation < 256; saturation++) {
					int finalColor;
					switch (color) {
					case 0:
						finalColor = 0xFF0000 | saturation;
						break;
					case 1:
						finalColor = (255 - saturation) * 65536 | 0x0 | 0xFF;
						break;
					case 2:
						finalColor = 0x0 | saturation * 256 | 0xFF;
						break;
					case 3:
						finalColor = 0xFF00 | 255 - saturation;
						break;
					case 4:
						finalColor = saturation * 65536 | 0xFF00 | 0x0;
						break;
					default:
						finalColor = 0xFF0000 | (255 - saturation) * 256 | 0x0;
					}
					int pos = locx++;
					for (int i = 0; i < 8; i++) {
						this.colorBar.setRGB(i, pos, finalColor);
					}
				}
			}
			this.colorBarTex = new DynamicTexture(this.colorBar);
		}
	}

	private void drawColorSelector() {
		drawRect(-2, -2, 72, 66, -2236963);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.gradientTex.updateDynamicTexture();
		GL11.glBegin(7);
		GL11.glTexCoord2d(0.0D, 0.0D);
		GL11.glVertex3d(0.0D, 0.0D, this.zLevel);
		GL11.glTexCoord2d(0.0D, 1.0D);
		GL11.glVertex3d(0.0D, 64.0D, this.zLevel);
		GL11.glTexCoord2d(1.0D, 1.0D);
		GL11.glVertex3d(64.0D, 64.0D, this.zLevel);
		GL11.glTexCoord2d(1.0D, 0.0D);
		GL11.glVertex3d(64.0D, 0.0D, this.zLevel);
		GL11.glEnd();
		this.colorBarTex.updateDynamicTexture();
		GL11.glBegin(7);
		GL11.glTexCoord2d(0.0D, 0.0D);
		GL11.glVertex3d(66.0D, 0.0D, this.zLevel);
		GL11.glTexCoord2d(0.0D, 1.0D);
		GL11.glVertex3d(66.0D, 64.0D, this.zLevel);
		GL11.glTexCoord2d(1.0D, 1.0D);
		GL11.glVertex3d(70.0D, 64.0D, this.zLevel);
		GL11.glTexCoord2d(1.0D, 0.0D);
		GL11.glVertex3d(70.0D, 0.0D, this.zLevel);
		GL11.glEnd();
	}

	private void drawGradient(int startRed, int startGreen, int startBlue) {
		if ((startRed >= startBlue) && (startRed >= startGreen)) {
			startRed = 255;
		} else if ((startGreen >= startBlue) && (startGreen >= startRed)) {
			startGreen = 255;
		} else {
			startBlue = 255;
		}
		if ((startRed <= startBlue) && (startRed <= startGreen)) {
			startRed = 0;
		} else if ((startGreen <= startBlue) && (startGreen <= startRed)) {
			startGreen = 0;
		} else {
			startBlue = 0;
		}
		for (int y = 0; y < 256; y++) {
			for (int x = 0; x < 256; x++) {
				this.Gradient.setRGB(x, y,
						0xFF000000 | (startRed + (255 - startRed) * y / 255) * x / 255 * 65536
								| (startGreen + (255 - startGreen) * y / 255) * x / 255 * 256
								| (startBlue + (255 - startBlue) * y / 255) * x / 255);
			}
		}
		this.gradientTex = new DynamicTexture(this.Gradient);
	}

	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		GL11.glPushMatrix();
		if (!ConfigValue.General.portraitEnabled) {
			((GuiCheckBox) this.buttonList.get(1)).enabled = false;
		} else {
			((GuiCheckBox) this.buttonList.get(1)).enabled = true;
			GL11.glPushMatrix();
			new ScaledResolution(Minecraft.getMinecraft());
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((1.0F - ConfigValue.General.guiScale) * ConfigValue.General.locX,
					(1.0F - ConfigValue.General.guiScale) * ConfigValue.General.locY, 0.0F);
			GL11.glScalef(ConfigValue.General.guiScale, ConfigValue.General.guiScale, 1.0F);
			GL11.glPushAttrib(8192);
			if (ConfigValue.General.skinnedPortrait) {
				GuiTool.DrawPortraitSkinned(ConfigValue.General.locX, ConfigValue.General.locY, true,
						this.mc.thePlayer.getName(),
						(int) Math.ceil(this.mc.thePlayer.getHealth()),
						(int) Math.ceil(this.mc.thePlayer.getMaxHealth()),
						this.mc.thePlayer);
			} else {
				GuiTool.DrawPortraitUnSkinned(ConfigValue.General.locX, ConfigValue.General.locY, true,
						this.mc.thePlayer.getName(),
						(int) Math.ceil(this.mc.thePlayer.getHealth()),
						(int) Math.ceil(this.mc.thePlayer.getMaxHealth()),
						this.mc.thePlayer);
			}
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		if (this.animationTick >= 1.0F) {
			this.animationTick = -5.0F;
		}
		this.animationTick += 0.01F;
		GL11.glTranslatef(this.width / 2 + 30 - this.textWidth / 2, this.height / 2 - 30, 0.0F);
		drawRect(0, 0, 30, 20, 1996488704);
		drawRect(0, 2, 30, 0, -1441726384);
		drawRect(0, 22, 30, 20, -1441726384);
		drawRect(0, 0, 2, 22, -1441726384);
		drawRect(28, 0, 30, 22, -1441726384);
		GL11.glTranslatef(32.0F, 25.0F, 0.0F);
		drawRect(0, 0, 15, 13, 0xFF000000 | ConfigValue.General.Color);
		if (this.setDamageColor) {
			drawRect(0, 2, 15, 0, -2236963);
			drawRect(0, 15, 15, 13, -2236963);
			drawRect(0, 0, 2, 15, -2236963);
			drawRect(13, 0, 15, 15, -2236963);
			GL11.glTranslatef(17.0F, -31.0F, 0.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawColorSelector();
			GL11.glTranslatef(-17.0F, 31.0F, 0.0F);
		}
		GL11.glTranslatef(0.0F, 20.0F, 0.0F);
		drawRect(0, 0, 15, 15, 0xFF000000 | ConfigValue.General.healColor);
		if (this.setHealColor) {
			drawRect(0, 2, 15, 0, -2236963);
			drawRect(0, 15, 15, 13, -2236963);
			drawRect(0, 0, 2, 15, -2236963);
			drawRect(13, 0, 15, 15, -2236963);
			GL11.glTranslatef(17.0F, -51.0F, 0.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawColorSelector();
			GL11.glTranslatef(-17.0F, 51.0F, 0.0F);
		}
		GL11.glPopMatrix();
		boolean mouseOver = false;
		if ((par1 > ConfigValue.General.locX)
				&& (par1 < ConfigValue.General.locX
						+ ((Integer) SkinRegistration.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGFRAMEWIDTH))
								.intValue())
				&& (par2 > ConfigValue.General.locY)
				&& (par2 < ConfigValue.General.locY
						+ ((Integer) SkinRegistration.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGFRAMEHEIGHT))
								.intValue())) {
			mouseOver = true;
		}
		if (this.mouseDown) {
			mouseOver = false;
			ConfigValue.General.locX = par1;
			ConfigValue.General.locY = par2;
		}
		this.fontRendererObj.drawStringWithShadow("Gui Scale:",
				this.width / 2 - (this.textWidth + this.gtf.getWidth() + 8) / 2,
				this.height / 2 - 24, 16777215);
		this.fontRendererObj.drawStringWithShadow("Damage Color:",
				this.width / 2 - this.fontRendererObj.getStringWidth("Damage Color:") / 2,
				this.height / 2 - 2, 16777215);
		this.fontRendererObj.drawStringWithShadow("Heal Color:",
				this.width / 2 - this.fontRendererObj.getStringWidth("Heal Color:") / 2,
				this.height / 2 + 18, 16777215);
		this.gtf.drawTextBox();
		this.fontRendererObj.drawStringWithShadow("%",
				this.width / 2 + 42 - this.textWidth / 2 - 6 + this.gtf.getWidth(),
				this.height / 2 - 24, 16777215);
		((GuiCheckBox) this.buttonList.get(0)).setChecked(ConfigValue.General.portraitEnabled);
		((GuiCheckBox) this.buttonList.get(1)).setChecked(ConfigValue.General.enablePotionEffects);
		((GuiCheckBox) this.buttonList.get(2)).setChecked(ConfigValue.General.popOffsEnabled);
		super.drawScreen(par1, par2, par3);
		GL11.glDepthFunc(519);
		if (mouseOver) {
			GL11.glPushMatrix();
			this.fontRendererObj.getStringWidth("<Drag Me>");
			GL11.glTranslatef(par1, par2, 0.0F);
			drawRect(0, 0, 60, 20, 1996488704);
			drawRect(0, 2, 60, 0, -1441726384);
			drawRect(0, 22, 60, 20, -1441726384);
			drawRect(0, 0, 2, 22, -1441726384);
			drawRect(58, 0, 60, 22, -1441726384);
			this.fontRendererObj.drawString("<Drag Me>", 7, 7, -1429418804);
			GL11.glPopMatrix();
		}
		GL11.glDepthFunc(515);
		GL11.glEnable(2929);
	}

	public void initGui() {
		super.initGui();
		int enablePortrait = this.fontRendererObj.getStringWidth("Enable Portrait") + 12;
		this.buttonList.add(new GuiCheckBox(0, this.width / 2 - enablePortrait / 2,
				this.height / 2 - 66, enablePortrait, 16, "Enable Portrait"));
		((GuiCheckBox) this.buttonList.get(0)).setChecked(ConfigValue.General.portraitEnabled);
		int enablePotionEffects = this.fontRendererObj.getStringWidth("Enable PotionEffects") + 12;
		this.buttonList.add(new GuiCheckBox(1, this.width / 2 - enablePotionEffects / 2,
				this.height / 2 - 52, enablePotionEffects, 16, "Enable PotionEffects"));
		((GuiCheckBox) this.buttonList.get(1)).setChecked(true);
		int enablePopOffsWidth = this.fontRendererObj.getStringWidth("Enable PopOffs") + 12;
		this.buttonList.add(new GuiCheckBox(2, this.width / 2 - enablePopOffsWidth / 2,
				this.height / 2 - 38, enablePopOffsWidth, 16, "Enable PopOffs"));
		((GuiCheckBox) this.buttonList.get(2)).setChecked(true);
		int enableSkinWidth = this.fontRendererObj.getStringWidth("Select Skin...") + 8;
		this.buttonList.add(new GuiButton(3, this.width / 2 - enableSkinWidth / 2,
				this.height / 2 + 34, enableSkinWidth, 20, "Select Skin..."));
		((GuiButton) this.buttonList.get(3)).enabled = true;
		((GuiButton) this.buttonList.get(3)).visible = true;
		int AdvancedWidth = this.fontRendererObj.getStringWidth("Advanced") + 8;
		this.buttonList.add(new GuiButton(4, this.width - AdvancedWidth - 4, this.height - 24,
				AdvancedWidth, 20, "Advanced"));
		this.buttonList.add(new GuiButton(5, this.width - 24, 4, 20, 20, "X"));
		this.textWidth = (this.fontRendererObj.getStringWidth("Gui Scale") + 8);
		this.gtf = new GuiTextField(0,this.fontRendererObj, this.width / 2 + 40 - this.textWidth / 2,
				this.height / 2 - 24, 30, 20);
		this.gtf.setText(String.valueOf(MathHelper.floor_float(ConfigValue.General.guiScale * 100.0F)));
		this.gtf.setMaxStringLength(3);
		this.gtf.setEnableBackgroundDrawing(false);
		this.gtf.setVisible(true);
		drawColorbar();
		drawGradient(255, 0, 255);
		GL11.glClear(256);
	}

	protected void keyTyped(char par1, int par2) throws IOException{
		if ((par2 == 14) || (par2 == 211)) {
			this.gtf.textboxKeyTyped(par1, par2);
			super.keyTyped(par1, par2);
			if (this.gtf.getText().length() == 0) {
				this.gtf.setText("0");
				this.gtf.setCursorPositionZero();
				this.gtf.setSelectionPos(1);
			}
		} else if (Character.isDigit(par1)) {
			this.gtf.textboxKeyTyped(par1, par2);
			int setVal = Integer.valueOf(this.gtf.getText()).intValue();
			if (setVal > 200) {
				int p = this.gtf.getCursorPosition();
				this.gtf.setText("200");
				this.gtf.setCursorPosition(p);
			}
		}
		ConfigValue.General.guiScale = (Float.valueOf(this.gtf.getText()).floatValue() / 100.0F);
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int par1, int par2, int par3) throws IOException {
		if (par3 == 0) {
			try {
				if ((par2 >= this.height / 2 - 36) && (par2 <= this.height / 2 + 28)) {
					if (this.setDamageColor) {
						if ((par1 >= this.width / 2 + 53) && (par1 <= this.width / 2 + 116)) {
							int x = par1 - (this.width / 2 + 53);
							int y = par2 - (this.height / 2 - 36);
							int pixelcolor = this.Gradient.getRGB(x * 4, y * 4);
							ConfigValue.General.Color = pixelcolor;
							this.setDamageColor = false;
							return;
						}
						if ((par1 >= this.width / 2 + 119) && (par1 <= this.width / 2 + 123)) {
							int x = 1;
							int y = par2 - (this.height / 2 - 36);
							int pixelcolor = this.colorBar.getRGB(x, y * (this.colorBar.getHeight() / 64));
							drawGradient(pixelcolor >> 16 & 0xFF, pixelcolor >> 8 & 0xFF, pixelcolor & 0xFF);
						}
					} else if (this.setHealColor) {
						if ((par1 >= this.width / 2 + 53) && (par1 <= this.width / 2 + 116)) {
							int x = par1 - (this.width / 2 + 53);
							int y = par2 - (this.height / 2 - 36);
							int pixelcolor = this.Gradient.getRGB(x * 4, y * 4);
							ConfigValue.General.healColor = pixelcolor;
							this.setHealColor = false;
							return;
						}
						if ((par1 >= this.width / 2 + 119) && (par1 <= this.width / 2 + 123)) {
							int x = 1;
							int y = par2 - (this.height / 2 - 36);
							int pixelcolor = this.colorBar.getRGB(x, y * (this.colorBar.getHeight() / 64));
							drawGradient(pixelcolor >> 16 & 0xFF, pixelcolor >> 8 & 0xFF, pixelcolor & 0xFF);
							return;
						}
					}
				}
				if ((par1 >= this.width / 2 + 30 - this.textWidth / 2 + 30)
						&& (par1 <= this.width / 2 + 30 - this.textWidth / 2 + 30 + 15)) {
					if ((par2 >= this.height / 2 - 5) && (par2 <= this.height / 2 + 10)) {
						this.setDamageColor = true;
						this.setHealColor = false;
						drawGradient(ConfigValue.General.Color >> 16 & 0xFF, ConfigValue.General.Color >> 8 & 0xFF,
								ConfigValue.General.Color & 0xFF);
					} else if ((par2 >= this.height / 2 - 25) && (par2 <= this.height / 2 + 30)) {
						this.setHealColor = true;
						this.setDamageColor = false;
						drawGradient(ConfigValue.General.healColor >> 16 & 0xFF, ConfigValue.General.healColor >> 8 & 0xFF,
								ConfigValue.General.healColor & 0xFF);
					}
				}
			} catch (Throwable ex) {
			}
			if ((par1 >= ConfigValue.General.locX - 1) && (par1 <= ConfigValue.General.locX + 137)
					&& (par2 >= ConfigValue.General.locY - 1) && (par2 <= ConfigValue.General.locY + 52)) {
				this.mouseDown = true;
			}
		}
		this.gtf.mouseClicked(par1, par2, par3);
		super.mouseClicked(par1, par2, par3);
	}

	protected void mouseClickMove(int par1, int par2, int par3) throws IOException{
		if (par3 == 0) {
			this.mouseDown = false;
		}
		super.mouseClickMove(par1, par2, par3,0);
	}

	public void onGuiClosed() {
		super.onGuiClosed();
	}

	public void updateScreen() {
		this.gtf.updateCursorCounter();
		super.updateScreen();
	}
}