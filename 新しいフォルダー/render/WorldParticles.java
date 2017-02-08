package basashi.hpview.render;

import org.lwjgl.opengl.GL11;

import basashi.hpview.config.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldParticles extends EntityFX {
	public int Damage;
	boolean grow = true;
	boolean heal = false;

	public WorldParticles(World par1World, double par2, double par4, double par6, double par8, double par10,
			double par12, int damage) {
		super(par1World, par2, par4, par6, par8, par10, par12);
		try {
			this.Damage = damage;
			setSize(0.2F, 0.2F);
			this.stepHeight = (this.height * 1.1F);
			setPosition(par2, par4, par6);
			this.motionY = par10;
			this.particleTextureJitterX = 1.5F;
			this.particleTextureJitterY = 1.5F;
			this.particleGravity = ConfigValue.General.Gravity;
			this.particleScale = ConfigValue.General.Size;
			this.particleMaxAge = ConfigValue.General.Lifespan;
			this.particleAge = 0;
			if (this.Damage < 0) {
				this.heal = true;
				this.Damage = Math.abs(this.Damage);
			}
		} catch (Throwable ex) {
			setDead();
		}
	}

	public int func_70537_b() {
		return 3;
	}

	@SideOnly(Side.CLIENT)
	public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6,
			float par7) {
		try {
			this.rotationYaw = (-Minecraft.getMinecraft().getRenderViewEntity().rotationYaw);
			this.rotationPitch = Minecraft.getMinecraft().getRenderViewEntity().rotationYaw;
			float locX = (float) (this.prevPosX + (this.posX - this.prevPosX) * par2
					- interpPosX);
			float locY = (float) (this.prevPosY + (this.posY - this.prevPosY) * par2
					- interpPosY);
			float locZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * par2
					- interpPosZ);
			GL11.glPushMatrix();

			GL11.glDepthFunc(519);
			GL11.glTranslatef(locX, locY, locZ);
			GL11.glRotatef(this.rotationYaw, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.rotationPitch, 1.0F, 0.0F, 0.0F);

			GL11.glScalef(-1.0F, -1.0F, 1.0F);
			GL11.glScaled(this.particleScale * 0.008D, this.particleScale * 0.008D, this.particleScale * 0.008D);
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
			Minecraft.getMinecraft().entityRenderer.disableLightmap();

			fontRenderer.drawStringWithShadow(String.valueOf(this.Damage),
					-MathHelper.floor_float(fontRenderer.getStringWidth(this.Damage + "") / 2.0F),
					-MathHelper.floor_float(fontRenderer.FONT_HEIGHT / 2.0F),
					this.heal ? ConfigValue.General.healColor : ConfigValue.General.Color);

			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			Minecraft.getMinecraft().entityRenderer.enableLightmap();
			GL11.glDepthFunc(515);

			GL11.glPopMatrix();
			if (this.grow) {
				this.particleScale *= 1.08F;
				if (this.particleScale > ConfigValue.General.Size * 3.0D) {
					this.grow = false;
				}
			} else {
				this.particleScale *= 0.96F;
			}
		} catch (Throwable ex) {
			setDead();
		}
	}
}
