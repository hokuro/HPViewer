package basashi.hpview.gui;

import org.lwjgl.opengl.GL11;

import basashi.hpview.config.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;

public class SkinGui extends GuiScreen {
	private SkinSlot SkinSlot;

	public SkinGui(GuiScreen par1, GameSettings par2) {
	}

	public void initGui() {
		this.SkinSlot = new SkinSlot(this);
		this.buttonList.add(new GuiButton(1, this.width - 24, 4, 20, 20, "X"));
	}

	protected void actionPerformed(GuiButton par1GuiButton) {
		Minecraft.getMinecraft().displayGuiScreen(new RepositionGui());
	}

	public void onGuiClosed() {
		RepositionGui rp = new RepositionGui();
		rp.onGuiClosed();
	}

	public void drawDefaultBackground() {
	}

	protected void drawBackground() {
	}

	public void drawBackground(int par1) {
	}

	public void drawScreen(int par1, int par2, float par3) {
		this.SkinSlot.drawScreen(par1, par2, par3);
		super.drawScreen(par1, par2, par3);
		GL11.glPushAttrib(278529);
		GL11.glPushMatrix();
		new ScaledResolution(Minecraft.getMinecraft());
		GL11.glTranslatef((1.0F - ConfigValue.General.guiScale) * ConfigValue.General.locX,
				(1.0F - ConfigValue.General.guiScale) * ConfigValue.General.locY, 0.0F);
		GL11.glScalef(ConfigValue.General.guiScale, ConfigValue.General.guiScale, 1.0F);
		if (ConfigValue.General.skinnedPortrait == true) {
			GuiTool.DrawPortraitSkinned(10, 10, false, this.mc.thePlayer.getName(),
					(int) Math.ceil(this.mc.thePlayer.getHealth()),
					(int) Math.ceil(this.mc.thePlayer.getMaxHealth()),
					this.mc.thePlayer);
		} else {
			GuiTool.DrawPortraitUnSkinned(10, 10, false, this.mc.thePlayer.getName(),
					(int) Math.ceil(this.mc.thePlayer.getHealth()),
					(int) Math.ceil(this.mc.thePlayer.getMaxHealth()),
					this.mc.thePlayer);
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}