package basashi.hpview.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.BooleanUtils;
import org.lwjgl.opengl.GL11;

import basashi.hpview.config.ConfigValue;
import basashi.hpview.core.EntityConfigurationEntry;
import basashi.hpview.core.HPViewer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.Configuration;

public class GuiOverLayHPView extends Gui {
	private final Minecraft mc;
	private final FontRenderer fontRenderer;
	private EntityLivingBase viewEntity;
    public static final FloatBuffer DEPTH = GLAllocation.createDirectFloatBuffer(32);
    public static final FloatBuffer BLEND = GLAllocation.createDirectFloatBuffer(32);
    private ScaledResolution scaledresolution;
	public static int LastTargeted = 0;
    public static int tick = 0;
    public static DynamicTexture inventoryPNG;
	public GuiOverLayHPView(Minecraft mc){
		this.mc = mc;
		this.fontRenderer = mc.fontRendererObj;
	}

	public void setViewEntity(EntityLivingBase view){
		viewEntity = view;
	}

	public void renderHPViwe(){
        render();
    }

	public void render() {
		try {
			if (inventoryPNG == null) {
				try {
					BufferedImage inventorypng = ImageIO.read(Minecraft.class.getResourceAsStream("/assets/minecraft/textures/gui/container/inventory.png"));
					inventoryPNG = new DynamicTexture(inventorypng);
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
			GlStateManager.pushMatrix();
			if (mc.thePlayer != null) {
				// ターゲットの情報を取得
				EntityLivingBase el = viewEntity;
				if (el != null) {
					Class entityclass = el.getClass();
					EntityConfigurationEntry configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(entityclass);
					if (configentry == null) {
						File configfile = new File(new File(ConfigValue.CONFIG_FILE().getParentFile(), "DIAdvancedCompatibility"),"CombinedConfig.cfg");
						Configuration config = new Configuration(configfile);
						HPViewer.tool.getEntityMap().put(entityclass, EntityConfigurationEntry.generateDefaultConfiguration(config, entityclass));
						config.save();
					}
					String c = entityclass.getName().toLowerCase();
					if (c.contains("entitygibs")) {
						el = null;
					} else if ((EntityList.classToStringMapping.get(el.getClass()) != null) && (((String) EntityList.classToStringMapping.get(el.getClass())).equalsIgnoreCase("Linkbook"))) {
						el = null;
					} else if (configentry.IgnoreThisMob) {
						el = null;
					} else {
						LastTargeted = el.getEntityId();
					}
				}
				if ((el != null) || ((LastTargeted != 0) && ((ConfigValue.General.portraitLifetime == -1)|| (tick > 0)))) {
					mc.entityRenderer.setupOverlayRendering();
					ScaledResolution scaledresolution = new ScaledResolution(mc);
					if (ConfigValue.General.locX > scaledresolution.getScaledWidth() - 135) {
						ConfigValue.General.locX = (scaledresolution.getScaledWidth() - 135);
					}
					if (ConfigValue.General.locY > scaledresolution.getScaledHeight() - 50) {
						ConfigValue.General.locY = (scaledresolution.getScaledHeight() - 50);
					}
					if (ConfigValue.General.locX < 0) {
						ConfigValue.General.locX = 0;
					}
					if (ConfigValue.General.locY < 0) {
						ConfigValue.General.locY = 0;
					}
					GlStateManager.pushMatrix();
					try {
						GlStateManager.translate((1.0F - ConfigValue.General.guiScale) * ConfigValue.General.locX, (1.0F - ConfigValue.General.guiScale) * ConfigValue.General.locY, 0.0F);
						GlStateManager.scale(ConfigValue.General.guiScale, ConfigValue.General.guiScale, ConfigValue.General.guiScale);
						if (el == null) {
							tick -= 1;
							try {
								el = (EntityLivingBase) mc.theWorld.getEntityByID(LastTargeted);
							} catch (Throwable ex) {
							}
							if (el == null) {
								LastTargeted = 0;
							}
						} else {
							tick = ConfigValue.General.portraitLifetime;
						}
						if (el == null) {
							return;
						}
						LastTargeted = el.getEntityId();
						FontRenderer fontrenderer = mc.fontRendererObj;
						Class entityclass = el.getClass();
						EntityConfigurationEntry configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(entityclass);
						if ((configentry.maxHP == -1) || (configentry.eyeHeight == -1.0F)) {
							configentry.eyeHeight = el.getEyeHeight();
							configentry.maxHP = MathHelper.floor_double(Math.ceil(el.getMaxHealth()));
						}
						if (configentry.maxHP != MathHelper.floor_double(Math.ceil(el.getMaxHealth()))) {
							configentry.maxHP = MathHelper.floor_double(Math.ceil(el.getMaxHealth()));
						}
						String Name = configentry.NameOverride;
						if ((el instanceof EntityPlayer)) {
							Name = el.getName();
						}
						if ((Name == null) || ("".equals(Name))) {
							Name = el.getName();
							if (Name.toLowerCase().endsWith(".name")) {
								Name = I18n.translateToLocal(Name);
							}
							if (Name.endsWith(".name")) {
								Name = Name.replace(".name", "");
								Name = Name.substring(Name.lastIndexOf(".") + 1, Name.length());
								Name = Name.substring(0, 1).toUpperCase() + Name.substring(1, Name.length());
								if ((el.isChild()) && (configentry.AppendBaby)) {
									Name = "Baby " + Name;
								}
							}
						}

						DrawPortraitUnSkinned(ConfigValue.General.locX,  ConfigValue.General.locY, Name, (int) Math.ceil(el.getHealth()), (int) Math.ceil(el.getMaxHealth()), el);
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					//Tessellator.instance.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.popMatrix();
				}
			}
			GlStateManager.popMatrix();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public void DrawPortraitUnSkinned(int locX, int locY, String Name, int health, int maxHealth, EntityLivingBase el) {
		scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int depthzfun;
		boolean depthTest;
		boolean blend;
		try {
			try{
				GlStateManager.getFloat(2932, DEPTH);
				depthzfun = (int)DEPTH.get();
				DEPTH.clear();
				GlStateManager.getFloat(2929, BLEND);
				depthTest = BooleanUtils.toBoolean((int)BLEND.get());
				BLEND.clear();
				GlStateManager.getFloat(3042, BLEND);
				blend = BooleanUtils.toBoolean((int)BLEND.get());
				BLEND.clear();
			}catch(Exception ex){
				ex.printStackTrace();
				return;
			}

			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer worldrenderer = tessellator.getBuffer();
			try {
				// 名前
				int entityHealth = health;
				// 体力
				String Health = "Health: " + String.valueOf(entityHealth) + "/" + String.valueOf(maxHealth);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(770, 771);

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
							GL11.glScissor(boxLocX, Minecraft.getMinecraft().displayHeight - boxLocY - boxWidth, boxWidth, boxWidth);
							if ((el != null) && (!el.isDead)) {
								drawTargettedMobPreview(el, locX, locY);
							}
						} catch (Throwable ex) {
						}
					}
				} catch (Throwable ex) {
				}
				GL11.glDisable(3089);


				GlStateManager.depthFunc(519);
				try {
					// 体力バーの長さ
					int healthbarwidth = MathHelper.floor_float(locX + 85.0F * entityHealth / maxHealth);
					if (entityHealth > maxHealth) {
						healthbarwidth = locX + 85;
						Health = "Health: " + String.valueOf(entityHealth) + "/" + String.valueOf(entityHealth);
					}
					// 名前バー
					drawGradientRect(locX+50,      locY,      locX + 85+50,      locY + 13, new Color(0.5F, 0.2F, 1.0F, 0.5F).getRGB(), new Color(0.0F, 0.2F, 0.0F, 0.5F).getRGB(), 0.0F);
					// 体力バー 最大値
					drawGradientRect(locX+50,      locY + 13, locX + 85+50,      locY + 26, new Color(1.0F, 0.0F, 0.0F, 0.5F).getRGB(), new Color(0.2F, 0.0F, 0.0F, 0.5F).getRGB(), 0.0F);
					// 体力バー 現在地
					drawGradientRect(locX+50,      locY + 13, healthbarwidth+50, locY + 26, new Color(0.0F, 1.0F, 0.0F, 0.5F).getRGB(), new Color(0.0F, 0.2F, 0.0F, 0.5F).getRGB(),  0.0F);
					// 名前表示
					Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(Name, locX+50 + (88 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(Name)) / 2, locY + 2, new Color(1.0F, 1.0F, 1.0F, 1.0F).getRGB());
					// 体力数値表示
					Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(Health, locX+50 + (88 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(Health)) / 2, locY + 16, new Color(1.0F, 1.0F, 1.0F, 1.0F).getRGB());

					drawGradientRect(locX, locY, locX + 50, locY + 50, new Color(0.2F, 0.2F, 0.2F, 0.3F).getRGB(),new Color(0.1F, 0.1F, 0.1F, 0.3F).getRGB(), 0.0F);
					drawGradientRect(locX, locY, locX + 2, locY + 50, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX, locY, locX + 50, locY + 2, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX + 48, locY, locX + 50, locY + 50, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX, locY + 48, locX + 50, locY + 50, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			GlStateManager.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.depthFunc(depthzfun);

			if (depthTest) {
				GlStateManager.enableDepth();
			} else {
				GlStateManager.disableDepth();
			}
			if (blend) {
				GlStateManager.enableBlend();
			} else {
				GlStateManager.disableBlend();
			}
			GlStateManager.resetColor();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	private static void drawGradientRect(int left, int top, int right, int bottom, int c1Rgb, int c2Rgb, float zLevel) {
	        float f = (float)(c1Rgb >> 24 & 255) / 255.0F;
	        float f1 = (float)(c1Rgb >> 16 & 255) / 255.0F;
	        float f2 = (float)(c1Rgb >> 8 & 255) / 255.0F;
	        float f3 = (float)(c1Rgb & 255) / 255.0F;
	        float f4 = (float)(c2Rgb >> 24 & 255) / 255.0F;
	        float f5 = (float)(c2Rgb >> 16 & 255) / 255.0F;
	        float f6 = (float)(c2Rgb >> 8 & 255) / 255.0F;
	        float f7 = (float)(c2Rgb & 255) / 255.0F;
	        GlStateManager.disableTexture2D();
	        GlStateManager.enableBlend();
	        GlStateManager.disableAlpha();
	        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	        GlStateManager.shadeModel(7425);
	        Tessellator tessellator = Tessellator.getInstance();
	        VertexBuffer worldrenderer = tessellator.getBuffer();
	        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
	        worldrenderer.pos((double)right, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
	        worldrenderer.pos((double)left, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
	        worldrenderer.pos((double)left, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
	        worldrenderer.pos((double)right, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
	        tessellator.draw();
	        GlStateManager.shadeModel(7424);
	        GlStateManager.disableBlend();
	        GlStateManager.enableAlpha();
	        GlStateManager.enableTexture2D();
	}



	public void drawTargettedMobPreview(EntityLivingBase el, int locX, int locY) {
		EntityConfigurationEntry configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(el.getClass());
		if (configentry == null) {
			configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(EntityVillager.class);
		}
		GlStateManager.pushMatrix();
		try {
			try {
				if (!ConfigValue.General.skinnedPortrait) {
					drawGradientRect(locX + 1, locY + 1, locX + 49, locY + 49,
							new Color(0.0F, 0.0F, 0.0F, 0.0F).getRGB(), new Color(0.6F, 0.6F, 0.6F, 0.8F).getRGB(),
							0.0F);
				}
				if (el == Minecraft.getMinecraft().thePlayer) {
					GlStateManager.translate(locX + 25 + configentry.XOffset, locY + 52 + configentry.YOffset - 30.0F, 1.0F);
				} else {
					GlStateManager.translate(locX + 25 + configentry.XOffset, locY + 52 + configentry.YOffset, 1.0F);
				}
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				float scalemod = (3.0F - el.getEyeHeight()) * configentry.EntitySizeScaling;
				float finalScale = configentry.ScaleFactor + configentry.ScaleFactor * scalemod;
				if (el.isChild()) {
					finalScale = (configentry.ScaleFactor + configentry.ScaleFactor * scalemod)
							* configentry.BabyScaleFactor;
				}
				GlStateManager.scale(finalScale * 0.85F, finalScale * 0.85F, 0.1F);
				if (ConfigValue.General.lockPosition) {
					int hurt = el.hurtTime;
					float prevRenderYawOffset = el.prevRenderYawOffset;
					el.hurtTime = 0;
					el.prevRenderYawOffset = (el.renderYawOffset - 360.0F);
					GlStateManager.rotate(el.renderYawOffset - 360.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
					GlStateManager.rotate(180.0F - Minecraft.getMinecraft().thePlayer.rotationYaw, 0.0F, -1.0F, 0.0F);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
		GlStateManager.popMatrix();
	}

	public void renderEntity(EntityLivingBase el) {
		GlStateManager.pushAttrib();
		//GL11.glPushAttrib(8192);

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer worldrenderer = tessellator.getBuffer();
		Render render = this.mc.getRenderManager().getEntityClassRenderObject(el.getClass());
		try {
			GlStateManager.disableBlend();
			GlStateManager.enableDepth();
			render.doRender(el, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
			GlStateManager.clear(256);
		} catch (Throwable ex) {
//			if (Tessellator.instance.isDrawing) {
//				Tessellator.instance.draw();
//			}
		}
		GlStateManager.popAttrib();
//		Tessellator.instance.setBrightness(240);
//		Tessellator.instance.setColorRGBA(255, 255, 255, 255);
		GlStateManager.blendFunc(770, 771);
	}

}
