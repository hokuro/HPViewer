package basashi.hpview.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import basashi.hpview.config.ConfigValue;
import basashi.hpview.core.EntityConfigurationEntry;
import basashi.hpview.core.HPViewer;
import basashi.hpview.texture.AbstractSkin;
import basashi.hpview.texture.EnumSkinPart;
import basashi.hpview.texture.Ordering;
import basashi.hpview.texture.SkinRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

public class GuiTool extends GuiIngame {
	public static List<String> enemies = new ArrayList();
	public static Field foundField = null;
	public static GuiTool instance = new GuiTool(Minecraft.getMinecraft());
	public static Map<Class, Integer> mobRenderLists = new HashMap();
	public static Long offset;
	public static int opt = 0;
	public static double rotationCounter = 0.0D;
	public static boolean skinned = true;
	private static final Minecraft field_73839_d = Minecraft.getMinecraft();
	private static ScaledResolution scaledresolution;
	public static DynamicTexture inventoryPNG;
	public static DynamicTexture widgetsPNG;

	public static void drawBackground(AbstractSkin skin, int locX, int locY) {
		int backgroundWidth = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH)).intValue();
		int backgroundHeight = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT)).intValue();
		int backgroundX = locX + ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDX)).intValue();
		int backgroundY = locY + ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDY)).intValue();
		skin.bindTexture(EnumSkinPart.BACKGROUNDID);
		WorldRenderer render = Tessellator.getInstance().getWorldRenderer();
		addVertexWithUV(backgroundX, backgroundY + backgroundHeight, 0, 0.0D, 1.0D,render);
		addVertexWithUV(backgroundX + backgroundWidth, backgroundY + backgroundHeight, 0,1.0D, 1.0D,render);
		addVertexWithUV(backgroundX + backgroundWidth, backgroundY, 0, 1.0D, 0.0D,render);
		addVertexWithUV(backgroundX, backgroundY, 0, 0.0D, 0.0D,render);
	}

	public static void drawFrame(AbstractSkin skin, int locX, int locY) {
		skin.bindTexture(EnumSkinPart.FRAMEID);
		int adjx = locX + ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEX)).intValue();
		int adjy = locY + ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEY)).intValue();
		int backgroundWidth = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEWIDTH)).intValue();
		int backgroundHeight = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEHEIGHT)).intValue();
		WorldRenderer render = Tessellator.getInstance().getWorldRenderer();
		addVertexWithUV(adjx, adjy + backgroundHeight, 0, 0.0D, 1.0D,render);
		addVertexWithUV(adjx + backgroundWidth, adjy + backgroundHeight, 0, 1.0D, 1.0D,render);
		addVertexWithUV(adjx + backgroundWidth, adjy, 0, 1.0D, 0.0D,render);
		addVertexWithUV(adjx, adjy, 0, 0.0D, 0.0D,render);
	}

	private static void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6, float zLevel) {
		float var7 = (par5 >> 24 & 0xFF) / 255.0F;
		float var8 = (par5 >> 16 & 0xFF) / 255.0F;
		float var9 = (par5 >> 8 & 0xFF) / 255.0F;
		float var10 = (par5 & 0xFF) / 255.0F;
		float var11 = (par6 >> 24 & 0xFF) / 255.0F;
		float var12 = (par6 >> 16 & 0xFF) / 255.0F;
		float var13 = (par6 >> 8 & 0xFF) / 255.0F;
		float var14 = (par6 & 0xFF) / 255.0F;
		GL11.glDisable(3553);
		GL11.glDisable(3008);
		GL11.glShadeModel(7425);
		Tessellator tess = Tessellator.getInstance();
		WorldRenderer render = tess.getWorldRenderer();
		render.begin(7,new VertexFormat());
		GlStateManager.color(var8, var9, var10, var7);
		render.addVertexData(new int[]{par3, par2, (int)zLevel});
		render.addVertexData(new int[]{par1, par2, (int)zLevel});
		GlStateManager.color(var12, var13, var14, var11);
		render.addVertexData(new int[]{par1, par4, (int)zLevel});
		render.addVertexData(new int[]{par3, par4, (int)zLevel});
		tess.draw();
		GL11.glShadeModel(7424);
		GL11.glEnable(3008);
		GL11.glEnable(3553);
	}

	public static void drawHealthBar(AbstractSkin skin, int locX, int locY, int health, int maxHealth, int getEntityId) {
		int healthBarWidth = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARWIDTH)).intValue();
		int healthBarHeight = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARHEIGHT)).intValue();
		int healthBarX = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARX)).intValue();
		int healthBarY = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARY)).intValue();

		skin.bindTexture(EnumSkinPart.DAMAGEID);
		WorldRenderer render = Tessellator.getInstance().getWorldRenderer();
		addVertexWithUV(locX + healthBarX, locY + healthBarY + healthBarHeight, 0, 0.0D,1.0D,render);
		addVertexWithUV(locX + healthBarX + healthBarWidth, locY + healthBarY + healthBarHeight,0, 1.0D, 1.0D,render);
		addVertexWithUV(locX + healthBarX + healthBarWidth, locY + healthBarY, 0, 1.0D, 0.0D,render);
		addVertexWithUV(locX + healthBarX, locY + healthBarY, 0, 0.0D, 0.0D,render);
		Tessellator.getInstance().draw();
		float healthbarwidth;
		if (health < maxHealth) {
			float f = health * 1.0F / (maxHealth * 1.0F);
			f = healthBarWidth * f;
			healthbarwidth = f;
			if (healthbarwidth < 0.0F) {
				healthbarwidth = 0.0F;
			}
		} else {
			healthbarwidth = healthBarWidth;
			EntityConfigurationEntry.maxHealthOverride.put(Integer.valueOf(getEntityId), Integer.valueOf(health));
		}
		render.begin(7,  new VertexFormat());
		skin.bindTexture(EnumSkinPart.HEALTHID);
		addVertexWithUV(locX + healthBarX, locY + healthBarY + healthBarHeight, 0, 0.0D,1.0D,render);
		addVertexWithUV((int)(locX + healthBarX + healthbarwidth), locY + healthBarY + healthBarHeight,0, 1.0D, 1.0D,render);
		addVertexWithUV((int)(locX + healthBarX + healthbarwidth), locY + healthBarY, 0, 1.0D, 0.0D,render);
		addVertexWithUV(locX + healthBarX, locY + healthBarY, 0, 0.0D, 0.0D,render);
	}

	public static void drawHealthText(AbstractSkin skin, int locX, int locY, int health, int maxHealth) {
		String Health = String.valueOf(health) + "/" + String.valueOf(maxHealth);
		if (health > maxHealth) {
			Health = String.valueOf(health) + "/" + String.valueOf(health);
		}
		int healthBarWidth = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARWIDTH)).intValue();
		int healthBarHeight = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARHEIGHT)).intValue();
		int healthBarX = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARX)).intValue();
		int healthBarY = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARY)).intValue();
		int packedRGB = Integer.parseInt("FFFFFF", 16);
		try {
			packedRGB = Integer.parseInt((String) skin.getSkinValue(EnumSkinPart.CONFIGTEXTEXTHEALTHCOLOR), 16);
		} catch (Exception ex) {
		}
		if (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 2 > healthBarHeight) {
			GL11.glPushMatrix();
			try {
				GL11.glTranslatef(locX + healthBarX + (healthBarWidth - Minecraft.getMinecraft().fontRendererObj.getStringWidth(Health) * 0.7F) / 2.0F,
						locY + healthBarY + healthBarHeight - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 0.7F - 0.5F,
						0.0F);
				GL11.glScalef(0.7F, 0.7F, 1.0F);
				Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(Health, 0, 0, packedRGB);
			} catch (Throwable ex) {
			}
			GL11.glPopMatrix();
		} else {
			Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(Health,
					locX + healthBarX + (healthBarWidth - Minecraft.getMinecraft().fontRendererObj.getStringWidth(Health)) / 2,
					locY + healthBarY + (healthBarHeight - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT) / 2, packedRGB);
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void drawMobPreview(EntityLivingBase el, AbstractSkin skin, int locX, int locY) {
		int backgroundWidth = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH)).intValue();
		int backgroundHeight = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT)).intValue();
		int MobPreviewOffsetX = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWX)).intValue();
		int MobPreviewOffsetY = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWY)).intValue();
		GL11.glEnable(3089);
		try {
			int boxLocX = MathHelper.floor_double((locX + MobPreviewOffsetX) * scaledresolution.getScaleFactor());
			int boxWidth = MathHelper.floor_double(backgroundWidth * scaledresolution.getScaleFactor());
			int boxHeight = MathHelper.floor_double(backgroundHeight * scaledresolution.getScaleFactor());
			int boxLocY = MathHelper.floor_double((locY + MobPreviewOffsetY) * scaledresolution.getScaleFactor());
			if (!(Minecraft.getMinecraft().currentScreen instanceof AdvancedGui)) {
				boxWidth = (int) (boxWidth * ConfigValue.General.guiScale);
				boxHeight = (int) (boxHeight * ConfigValue.General.guiScale);
			}
			if (el != null) {
				Class entityclass = el.getClass();
				HPViewer.tool.getEntityMap().get(entityclass);
				try {
					GL11.glScissor(boxLocX, Minecraft.getMinecraft().displayHeight - boxLocY - boxHeight, boxWidth,
							boxHeight);
					if ((el != null) && (!el.isDead)) {
						drawTargettedMobPreview(el, locX + MobPreviewOffsetX, locY + MobPreviewOffsetY);
					}
				} catch (Throwable ex) {
				}
			}
		} catch (Throwable ex) {
		}
		GL11.glDisable(3089);
	}

	public static void drawMobTypes(EntityLivingBase el, AbstractSkin skin, int locX, int locY) {
		boolean hostile;
		if ((el instanceof IMob)) {
			hostile = true;
		} else {
			if ((el instanceof EntityPlayer)) {
				if (enemies.contains(((EntityPlayer) el).getName())) {
					hostile = true;
				} else {
					hostile = false;
				}
			} else {
				if (Minecraft.getMinecraft().thePlayer == el.getLastAttacker()) {
					hostile = true;
				} else {
					hostile = false;
				}
			}
		}
		if (((el instanceof EntityTameable)) && (((EntityTameable) el).isTamed())) {
			EntityLivingBase target = ((EntityTameable) el).getAttackTarget();
			if (Minecraft.getMinecraft().thePlayer == target) {
				hostile = true;
			} else if ((target instanceof EntityTameable)) {
				if (((EntityTameable) target).getOwner() == Minecraft.getMinecraft().thePlayer) {
					hostile = true;
				}
			} else {
				hostile = false;
			}
		}
		if (hostile) {
			GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.6F);
			GlStateManager.color(1.0F, 0.0F, 0.0F, 0.6F);
		} else {
			GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.6F);
			GlStateManager.color(0.0F, 1.0F, 0.0F, 0.6F);
		}
		float step = 0.2F;
		float glTexX;
		if ((el instanceof IBossDisplayData)) {
			glTexX = 4.0F * step;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.6F);
		} else {
			if ((el.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) || (el.isEntityUndead())) {
				glTexX = 0.0F * step;
			} else {
				if (el.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
					glTexX = 3.0F * step;
				} else {
					if (((el instanceof EntityPlayer)) || ((el instanceof EntityWitch))
							|| ((el instanceof EntityVillager)) || ((el instanceof EntityIronGolem))) {
						glTexX = 2.0F * step;
					} else {
						glTexX = 1.0F * step;
					}
				}
			}
		}
		float adjX = locX + ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEX)).intValue();
		float adjY = locY + ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEY)).intValue();
		skin.bindTexture(EnumSkinPart.TYPEICONSID);
		WorldRenderer render = Tessellator.getInstance().getWorldRenderer();
		addVertexWithUV(adjX, adjY, 0, glTexX, 0.0D,render);
		addVertexWithUV(adjX, adjY + ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEHEIGHT)).intValue(), 0, glTexX, 1.0D,render);
		addVertexWithUV(
				adjX + ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEWIDTH)).intValue(),
				adjY + ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEHEIGHT)).intValue(), 0, glTexX + step,
				1.0D,render);
		addVertexWithUV(
				adjX + ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEWIDTH)).intValue(), adjY, 0,
				glTexX + step, 0.0D,render);
	}

	public static void drawNamePlate(AbstractSkin skin, int locX, int locY) {
		int NamePlateWidth = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEWIDTH)).intValue();
		int NamePlateHeight = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEHEIGHT)).intValue();
		int NamePlateX = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEX)).intValue();
		int NamePlateY = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEY)).intValue();
		skin.bindTexture(EnumSkinPart.NAMEPLATEID);
		WorldRenderer render = Tessellator.getInstance().getWorldRenderer();
		addVertexWithUV(locX + NamePlateX, locY + NamePlateY, 0, 0.0D, 0.0D,render);
		addVertexWithUV(locX + NamePlateX, locY + NamePlateY + NamePlateHeight, 0, 0.0D,1.0D,render);
		addVertexWithUV(locX + NamePlateX + NamePlateWidth, locY + NamePlateY + NamePlateHeight,0, 1.0D, 1.0D,render);
		addVertexWithUV(locX + NamePlateX + NamePlateWidth, locY + NamePlateY, 0, 1.0D, 0.0D,render);
	}

	public static void drawNameText(AbstractSkin skin, String Name, int locX, int locY) {
		int NamePlateWidth = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEWIDTH)).intValue();
		int NamePlateHeight = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEHEIGHT)).intValue();
		int NamePlateX = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEX)).intValue();
		int NamePlateY = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEY)).intValue();
		int packedRGB = Integer.parseInt("FFFFFF", 16);
		try {
			packedRGB = Integer.parseInt((String) skin.getSkinValue(EnumSkinPart.CONFIGTEXTEXTNAMECOLOR), 16);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(Name,
				locX + NamePlateX + (NamePlateWidth - Minecraft.getMinecraft().fontRendererObj.getStringWidth(Name)) / 2,
				locY + NamePlateY + (NamePlateHeight - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT) / 2, packedRGB);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void DrawPortraitSkinned(int locX, int locY, boolean hostile, String Name, int health, int maxHealth, EntityLivingBase el){
		scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int depthzfun = GL11.glGetInteger(2932);
		boolean depthTest = GL11.glGetBoolean(2929);
		boolean blend = GL11.glGetBoolean(3042);
		//boolean isDrawing = Tessellator.getInstance().isDrawing;
		try
		{
		AbstractSkin skin = AbstractSkin.getActiveSkin();
		//if (isDrawing) {
			Tessellator.getInstance().draw();
		//}
		Ordering[] ordering = (Ordering[])skin.getSkinValue(EnumSkinPart.ORDERING);
		for (int i = 0; i < ordering.length; i++)
		{
			GL11.glDepthFunc(519);
			if (ordering[i] != Ordering.MOBPREVIEW)
			{
				Tessellator.getInstance().getWorldRenderer().begin(7, new VertexFormat());
				Tessellator.getInstance().getWorldRenderer().putBrightness4(240, 240, 240, 240);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
			else
			{
				GL11.glDepthFunc(515);
			}
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
			GL11.glPushMatrix();
			GL11.glEnable(3553);
			GL11.glDisable(3042);
			GL11.glDepthMask(true);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(3553);
			GL11.glEnable(2929);
			GL11.glDisable(2896);
			GL11.glBlendFunc(770, 771);
			try
			{
				int tmp;
				switch (ordering[i].ordinal())
				{
				case 1:
					GL11.glEnable(3042);
					try
					{
						tmp = ((Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH)).intValue();
						if (tmp > 0) {
								drawBackground(skin, locX, locY);
						}
					}
					catch (Throwable ex) {}
					break;
				case 2:
					try
					{
						if ((el != null) && (!el.isDead) && (health > 0))
						{
								tmp = ((Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH)).intValue();
								if (tmp > 0) {
									drawMobPreview(el, skin, locX, locY);
								}
						}
					}
					catch (Throwable ex) {}
					break;
				case 3:
					GL11.glEnable(3042);
					try
					{
					drawNamePlate(skin, locX, locY);
					}
					catch (Throwable ex) {}
					break;
				case 4:
					GL11.glEnable(3042);
					try
					{
						drawHealthBar(skin, locX, locY, health, maxHealth, el != null ? el.getEntityId() : -1);
					}
					catch (Throwable ex) {}
					break;
				case 5:
					GL11.glEnable(3042);
					try
					{
						drawFrame(skin, locX, locY);
					}
					catch (Throwable ex) {}
					break;
				case 6:
					try
					{
						drawMobTypes(el, skin, locX, locY);
					}
					catch (Throwable ex) {}
					break;
				case 7:
					try
					{
						drawPotionBoxes(el);
					}
					catch (Throwable ex) {}
					break;
				case 8:
					try
					{
						drawHealthText(skin, locX, locY, health, maxHealth);
					}
					catch (Throwable ex) {}
					break;
				case 9:
					try
					{
						drawNameText(skin, Name, locX, locY);
					}
					catch (Throwable ex) {}
					break;
				}
			}
			catch (Throwable ex) {}
			if ((ordering[i] != Ordering.MOBPREVIEW))// && (Tessellator.getInstance().isDrawing)) {
			Tessellator.getInstance().draw();
			}
			GL11.glPopMatrix();
		}
		catch (Throwable ex) {}
		GL11.glDepthFunc(515);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthFunc(depthzfun);
		if (depthTest) {
		GL11.glEnable(2929);
		} else {
		GL11.glDisable(2929);
		}
		if (blend) {
		GL11.glEnable(3042);
		} else {
		GL11.glDisable(3042);
		}
		GL11.glClear(256);
		//if (isDrawing) {
		Tessellator.getInstance().getWorldRenderer().begin(7, new VertexFormat());;
		//}
	}

	public static void DrawPortraitUnSkinned(int locX, int locY, boolean hostile, String Name, int health,
			int maxHealth, EntityLivingBase el) {
		scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		skinned = false;
		int depthzfun = GL11.glGetInteger(2932);
		boolean depthTest = GL11.glGetBoolean(2929);
		boolean blend = GL11.glGetBoolean(3042);
		//boolean isDrawing = Tessellator.getInstance().isDrawing;
		//if (isDrawing) {
			Tessellator.getInstance().draw();
		//}
		try {
			try {
				int entityHealth = health;
				String Health = String.valueOf(entityHealth) + "/" + String.valueOf(maxHealth);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				GL11.glEnable(3089);
				try {
					int boxLocX = MathHelper.floor_double(locX * scaledresolution.getScaleFactor());
					int boxWidth = MathHelper.floor_double(50.0F * scaledresolution.getScaleFactor());
					int boxLocY = MathHelper.floor_double(locY * scaledresolution.getScaleFactor());
					if (el != null) {
						Class entityclass = el.getClass();
						HPViewer.tool.getEntityMap().get(entityclass);
						GL11.glEnable(3089);
						try {
							GL11.glScissor(boxLocX, Minecraft.getMinecraft().displayHeight - boxLocY - boxWidth,
									boxWidth, boxWidth);
							if ((el != null) && (!el.isDead)) {
								drawTargettedMobPreview(el, locX, locY);
							}
						} catch (Throwable ex) {
						}
					}
				} catch (Throwable ex) {
				}
				GL11.glDisable(3089);
				GL11.glDepthFunc(519);
				try {
					int healthbarwidth = MathHelper.floor_float(locX + 50 + 85.0F * entityHealth / maxHealth);
					if (entityHealth > maxHealth) {
						healthbarwidth = locX + 50 + 85;
						Health = "Health: " + String.valueOf(entityHealth) + "/" + String.valueOf(entityHealth);
					}
					drawGradientRect(locX + 50, locY, locX + 135, locY + 13, new Color(1.0F, 1.0F, 0.0F, 0.5F).getRGB(),
							new Color(0.2F, 0.2F, 0.0F, 0.5F).getRGB(), 0.0F);
					drawGradientRect(locX + 50, locY + 13, locX + 135, locY + 26,
							new Color(1.0F, 0.0F, 0.0F, 0.5F).getRGB(), new Color(0.2F, 0.0F, 0.0F, 0.5F).getRGB(),
							0.0F);
					drawGradientRect(locX + 50, locY + 13, healthbarwidth, locY + 26,
							new Color(0.0F, 1.0F, 0.0F, 0.5F).getRGB(), new Color(0.0F, 0.2F, 0.0F, 0.5F).getRGB(),
							0.0F);
					drawGradientRect(locX, locY, locX + 2, locY + 50, Color.lightGray.getRGB(),
							Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX, locY, locX + 50, locY + 2, Color.lightGray.getRGB(),
							Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX + 48, locY, locX + 50, locY + 50, Color.lightGray.getRGB(),
							Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX, locY + 48, locX + 50, locY + 50, Color.lightGray.getRGB(),
							Color.lightGray.getRGB(), 0.0F);
					Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(Name,
							locX + 50 + (88 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(Name)) / 2, locY + 2,
							-1426063361);
					Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(Health,
							locX + 50 + (88 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(Health)) / 2, locY + 16,
							-1426063361);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				} catch (Throwable ex) {
				}
			} catch (Throwable ex) {
			}
		} catch (Throwable ex) {
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthFunc(depthzfun);
		if (depthTest) {
			GL11.glEnable(2929);
		} else {
			GL11.glDisable(2929);
		}
		if (blend) {
			GL11.glEnable(3042);
		} else {
			GL11.glDisable(3042);
		}
		GL11.glClear(256);
		//if (isDrawing) {
			Tessellator.getInstance().getWorldRenderer().begin(7,  new VertexFormat());;
		//}
	}

	public static void drawPotionBoxes(EntityLivingBase el) {
		if (inventoryPNG == null) {
			try {
				BufferedImage inventorypng = ImageIO.read(
						Minecraft.class.getResourceAsStream("/assets/minecraft/textures/gui/container/inventory.png"));
				inventoryPNG = new DynamicTexture(inventorypng);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
		AbstractSkin skin = SkinRegistration.getActiveSkin();
		int PotionBoxSidesWidth = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXWIDTH)).intValue();
		int PotionBoxHeight = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXHEIGHT)).intValue();
		int PotionBoxOffsetX = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXX)).intValue();
		int PotionBoxOffsetY = ((Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXY)).intValue();
		try {
			boolean boxdrawn = false;
			if ((ConfigValue.General.enablePotionEffects)
					&& (HPViewer.McEvent.potionEffects.get(Integer.valueOf(el.getEntityId())) != null)
					&& (!((Collection) HPViewer.McEvent.potionEffects.get(Integer.valueOf(el.getEntityId()))).isEmpty())) {
				int position = 0;
				Tessellator.getInstance().draw();
				for (Iterator iterator = ((Collection) HPViewer.McEvent.potionEffects.get(Integer.valueOf(el.getEntityId())))
						.iterator(); iterator.hasNext();) {
					PotionEffect effect = (PotionEffect) iterator.next();
					int potionID = Integer.valueOf(effect.getPotionID()).intValue();
					int Duration = Integer.valueOf(effect.getDuration()).intValue();
					if ((Potion.potionTypes.length - 1 >= potionID) && (Duration > 0)) {
						Potion potion = Potion.potionTypes[potionID];
						if ((potion != null) && (potion.hasStatusIcon()) && (Duration > 10)) {
							GL11.glPushMatrix();
							GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
							GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
							if (!boxdrawn) {
								boxdrawn = true;
								int adjx = ConfigValue.General.locX + PotionBoxOffsetX;
								int adjy = ConfigValue.General.locY + PotionBoxOffsetY;
								skin.bindTexture(EnumSkinPart.LEFTPOTIONID);
								Tessellator.getInstance().getWorldRenderer().begin(7,new VertexFormat());
								addVertexWithUV(adjx, adjy, 0.0D, 0.0D, 0.0D,Tessellator.getInstance().getWorldRenderer());
								addVertexWithUV(adjx, adjy + PotionBoxHeight, 0.0D, 0.0D, 1.0D,Tessellator.getInstance().getWorldRenderer());
								addVertexWithUV(adjx + PotionBoxSidesWidth,adjy + PotionBoxHeight, 0.0D, 1.0D, 1.0D,Tessellator.getInstance().getWorldRenderer());
								addVertexWithUV(adjx + PotionBoxSidesWidth, adjy, 0.0D, 1.0D,0.0D,Tessellator.getInstance().getWorldRenderer());
								Tessellator.getInstance().draw();
							}
							int adjx = ConfigValue.General.locX + PotionBoxOffsetX + position * 20 + PotionBoxSidesWidth;
							int adjy = ConfigValue.General.locY + PotionBoxOffsetY;
							skin.bindTexture(EnumSkinPart.CENTERPOTIONID);
							//if (Tessellator.getInstance().isDrawing) {
								Tessellator.getInstance().draw();
							//}
							Tessellator.getInstance().getWorldRenderer().begin(7,new VertexFormat());
							addVertexWithUV(adjx, adjy, 0.0D, 0.0D, 0.0D,Tessellator.getInstance().getWorldRenderer());
							addVertexWithUV(adjx, adjy + PotionBoxHeight, 0.0D, 0.0D, 1.0D,Tessellator.getInstance().getWorldRenderer());
							addVertexWithUV(adjx + 20, adjy + PotionBoxHeight, 0.0D, 1.0D, 1.0D,Tessellator.getInstance().getWorldRenderer());
							addVertexWithUV(adjx + 20, adjy, 0.0D, 1.0D, 0.0D,Tessellator.getInstance().getWorldRenderer());
							Tessellator.getInstance().draw();
							int iconIndex = potion.getStatusIconIndex();
							Duration = MathHelper.floor_float(Duration / 20.0F);
							String formattedtime = Potion.getDurationString(effect);
							int posx = ConfigValue.General.locX + PotionBoxOffsetX + position * 20 + PotionBoxSidesWidth + 2;
							int posy = ConfigValue.General.locY + PotionBoxOffsetY + 2;
							int ioffx = (0 + iconIndex % 8) * 18;
							int ioffy = (0 + iconIndex / 8) * 18 + 198;
							int width = PotionBoxHeight - 4;
							inventoryPNG.updateDynamicTexture();
							instance.drawTexturedModalRect(posx, posy, ioffx, ioffy, width, width);
							try {
								GL11.glTranslatef(
										ConfigValue.General.locX + PotionBoxOffsetX + position * 20 + PotionBoxSidesWidth + 13
												- Minecraft.getMinecraft().fontRendererObj.getStringWidth(formattedtime) / 2,
										ConfigValue.General.locY + PotionBoxOffsetY + PotionBoxHeight
												- Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 0.815F,
										0.1F);
								GL11.glScalef(0.815F, 0.815F, 0.815F);
								Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(formattedtime, 0, 0,
										new Color(1.0F, 1.0F, 0.5F, 1.0F).getRGB());
								GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
								GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
							} catch (Throwable ex) {
							}
							Tessellator.getInstance().getWorldRenderer().begin(7,new VertexFormat());
							GL11.glPopMatrix();
							position++;
						}
					}
				}
				if (boxdrawn) {
					int adjx = ConfigValue.General.locX + PotionBoxOffsetX + position * 20 + PotionBoxSidesWidth;
					int adjy = ConfigValue.General.locY + PotionBoxOffsetY;
					skin.bindTexture(EnumSkinPart.RIGHTPOTIONID);
					addVertexWithUV(adjx, adjy, 0.0D, 0.0D, 0.0D,Tessellator.getInstance().getWorldRenderer());
					addVertexWithUV(adjx, adjy + PotionBoxHeight, 0.0D, 0.0D, 1.0D,Tessellator.getInstance().getWorldRenderer());
					addVertexWithUV(adjx + PotionBoxSidesWidth, adjy + PotionBoxHeight, 0.0D,1.0D, 1.0D,Tessellator.getInstance().getWorldRenderer());
					addVertexWithUV(adjx + PotionBoxSidesWidth, adjy, 0.0D, 1.0D, 0.0D,Tessellator.getInstance().getWorldRenderer());
				}
			}
		} catch (Throwable ex) {
		}
	}

	public static void drawTargettedMobPreview(EntityLivingBase el, int locX, int locY) {
		EntityConfigurationEntry configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(el.getClass());
		if (configentry == null) {
			configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(EntityVillager.class);
		}
		GL11.glPushMatrix();
		try {
			try {
				if (!ConfigValue.General.skinnedPortrait) {
					drawGradientRect(locX + 1, locY + 1, locX + 49, locY + 49,
							new Color(0.0F, 0.0F, 0.0F, 0.0F).getRGB(), new Color(0.6F, 0.6F, 0.6F, 0.8F).getRGB(),
							0.0F);
				}
				if (el == Minecraft.getMinecraft().thePlayer) {
					GL11.glTranslatef(locX + 25 + configentry.XOffset, locY + 52 + configentry.YOffset - 30.0F, 1.0F);
				} else {
					GL11.glTranslatef(locX + 25 + configentry.XOffset, locY + 52 + configentry.YOffset, 1.0F);
				}
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				float scalemod = (3.0F - el.getEyeHeight()) * configentry.EntitySizeScaling;
				float finalScale = configentry.ScaleFactor + configentry.ScaleFactor * scalemod;
				if (el.isChild()) {
					finalScale = (configentry.ScaleFactor + configentry.ScaleFactor * scalemod)
							* configentry.BabyScaleFactor;
				}
				GL11.glScalef(finalScale * 0.85F, finalScale * 0.85F, 0.1F);
				if (ConfigValue.General.lockPosition) {
					int hurt = el.hurtTime;
					float prevRenderYawOffset = el.prevRenderYawOffset;
					el.hurtTime = 0;
					el.prevRenderYawOffset = (el.renderYawOffset - 360.0F);
					GL11.glRotatef(el.renderYawOffset - 360.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					try {
						renderEntity(el);
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
					el.prevRenderYawOffset = prevRenderYawOffset;
					el.hurtTime = hurt;
				} else {
					int hurt = el.hurtTime;
					el.hurtTime = 0;
					GL11.glRotatef(180.0F - Minecraft.getMinecraft().thePlayer.rotationYaw, 0.0F, -1.0F, 0.0F);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					try {
						renderEntity(el);
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
					el.hurtTime = hurt;
				}
			} catch (Throwable ex) {
			}
		} catch (Throwable ex) {
		}
		GL11.glPopMatrix();
	}

	public static void renderEntity(EntityLivingBase el) {
		GL11.glPushAttrib(8192);
		Tessellator.getInstance().getWorldRenderer().putBrightness4(240, 240, 240, 240);

		Render render = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(el.getClass());
		try {
			GL11.glDisable(3042);
			GL11.glEnable(2929);
			render.doRender(el, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
			GL11.glClear(256);
		} catch (Throwable ex) {
			//if (Tessellator.getInstance().isDrawing) {
				Tessellator.getInstance().draw();
			//}
		}
		GL11.glPopAttrib();
		Tessellator.getInstance().getWorldRenderer().putBrightness4(240, 240, 240, 240);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBlendFunc(770, 771);
	}

	public GuiTool(Minecraft par1Minecraft) {
		super(par1Minecraft);
	}

	public static void addVertexWithUV(int p1, int p2, int p3, double u, double v, WorldRenderer render){
		render.tex(u, v);
		render.addVertexData(new int[]{p1,p2,p3});
	}

	public static void addVertexWithUV(int p1, int p2, int p3, float u, float v, WorldRenderer render){
		addVertexWithUV((int)p1,(int)p2,(int)p3,u, v, render);
	}

	public static void addVertexWithUV(int p1, int p2, double p3, double u, double v, WorldRenderer render){
		addVertexWithUV((int)p1,(int)p2,(int)p3,u, v, render);
	}

	public static void addVertexWithUV(float p1, float p2, double p3, double u, double v, WorldRenderer render){
		addVertexWithUV((int)p1,(int)p2,(int)p3,u, v, render);
	}
}
