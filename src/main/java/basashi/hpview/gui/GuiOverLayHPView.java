package basashi.hpview.gui;

import java.awt.Color;
import java.io.File;
import java.nio.FloatBuffer;

import org.apache.commons.lang3.BooleanUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import basashi.config.Configuration;
import basashi.hpview.config.MyConfig;
import basashi.hpview.core.EntityConfigurationEntry;
import basashi.hpview.core.HPViewer;
import basashi.hpview.core.log.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.loading.FMLPaths;

public class GuiOverLayHPView extends Screen {
	private final Minecraft mc;
	private final FontRenderer fontRenderer;
	private LivingEntity viewEntity;
    public static final FloatBuffer DEPTH = GLAllocation.createDirectFloatBuffer(32);
    public static final FloatBuffer BLEND = GLAllocation.createDirectFloatBuffer(32);
   //private ScaledResolution scaledresolution;
	public static int LastTargeted = 0;
    public static int tick = 0;
    public static DynamicTexture inventoryPNG;
	public GuiOverLayHPView(Minecraft mc){
		super(new StringTextComponent(""));
		this.mc = mc;
		this.fontRenderer = mc.fontRenderer;
	}

	public void setViewEntity(LivingEntity view){
		viewEntity = view;
		if (view != null)
		ModLog.log().debug("view:"+view.getName());
	}

	public void renderHPViwe(){
        render();
    }

	public void render() {
		try {
			if (inventoryPNG == null) {
				try {
					inventoryPNG = new DynamicTexture(NativeImage.read(Minecraft.class.getResourceAsStream("/assets/minecraft/textures/gui/container/inventory.png")));
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
			GlStateManager.pushMatrix();
			if (mc.player != null) {
				// ターゲットの情報を取得
				LivingEntity el = viewEntity;
				if (el != null) {
					Class entityclass = el.getClass();
					EntityConfigurationEntry configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(entityclass);
					if (configentry == null) {
						// TODO なんかファイルつくる
						File configfile = new File(new File(FMLPaths.CONFIGDIR.get().toString(), "DIAdvancedCompatibility"),"CombinedConfig.cfg");
						Configuration config = new Configuration(configfile);
						HPViewer.tool.getEntityMap().put(entityclass, EntityConfigurationEntry.generateDefaultConfiguration(config, el,entityclass));
						config.save();
					}
					String c = entityclass.getName().toLowerCase();
					if (c.contains("entitygibs")) {
						el = null;
					} else if (((Registry.ENTITY_TYPE.getOrDefault(el.getType().getRegistryName())) != null) &&
					(el.getType().getRegistryName().getPath().equalsIgnoreCase("Linkbook"))) {
						el = null;
//					} else if (configentry.IgnoreThisMob) {
//						el = null;
					} else {
						LastTargeted = el.getEntityId();
					}
				}
				if ((el != null) || ((LastTargeted != 0) && ((MyConfig._general.portraitLifetime.get() == -1)|| (tick > 0)))) {
					//mc.entityRenderer.setupOverlayRendering();
					//ScaledResolution scaledresolution = new ScaledResolution(mc);
					int scHeight = mc.mainWindow.getHeight();
					int scWidth = mc.mainWindow.getWidth();
					int locX = 0;
					int locY = 0;
					if (MyConfig._general.locX.get() > scHeight - 135) {
						locX = (scWidth - 135);
					}
					if (MyConfig._general.locY.get() > scHeight - 50) {
						locY = (scWidth - 50);
					}
					if (MyConfig._general.locX.get() < 0) {
						locX = 0;
					}
					if (MyConfig._general.locY.get() < 0) {
						locY = 0;
					}
					GlStateManager.pushMatrix();
					try {
						GlStateManager.translated((1.0D - MyConfig._general.guiScale.get()) * locX, (1.0D - MyConfig._general.guiScale.get()) * locY, 0.0D);
						GlStateManager.scaled(MyConfig._general.guiScale.get(), MyConfig._general.guiScale.get(), MyConfig._general.guiScale.get());
						if (el == null) {
							tick -= 1;
							try {
								el = (LivingEntity) mc.world.getEntityByID(LastTargeted);
							} catch (Throwable ex) {
							}
							if (el == null) {
								LastTargeted = 0;
							}
						} else {
							tick = MyConfig._general.portraitLifetime.get();
						}
						if (el == null) {
							return;
						}
						LastTargeted = el.getEntityId();
						FontRenderer fontrenderer = mc.fontRenderer;
						Class entityclass = el.getClass();
						EntityConfigurationEntry configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(entityclass);
						if ((configentry.maxHP == -1) || (configentry.eyeHeight == -1.0F)) {
							configentry.eyeHeight = el.getEyeHeight();
							configentry.maxHP = MathHelper.floor(Math.ceil(el.getMaxHealth()));
						}
						if (configentry.maxHP != MathHelper.floor(Math.ceil(el.getMaxHealth()))) {
							configentry.maxHP = MathHelper.floor(Math.ceil(el.getMaxHealth()));
						}
						String Name = null;//configentry.NameOverride;
						if ((el instanceof PlayerEntity)) {
							Name = el.getName().toString();
						}
						if ((Name == null) || ("".equals(Name))) {
							Name = el.getDisplayName().getString();
							if (Name.toLowerCase().endsWith(".name")) {
								Name = I18n.format(Name);
							}
							if (Name.endsWith(".name")) {
								Name = Name.replace(".name", "");
								Name = Name.substring(Name.lastIndexOf(".") + 1, Name.length());
								Name = Name.substring(0, 1).toUpperCase() + Name.substring(1, Name.length());
								if ((el.isChild())){// && (configentry.AppendBaby)) {
									Name = "Baby " + Name;
								}
							}
						}

						DrawPortraitUnSkinned(locX,  locY, Name, (int) Math.ceil(el.getHealth()), (int) Math.ceil(el.getMaxHealth()), el);
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					//Tessellator.instance.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.popMatrix();
				}
			}
			GlStateManager.popMatrix();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public void DrawPortraitUnSkinned(int locX, int locY, String Name, int health, int maxHealth, LivingEntity el) {
		double scFactor = Minecraft.getInstance().mainWindow.getGuiScaleFactor();

		int depthzfun;
		boolean depthTest;
		boolean blend;
		try {
			try{
				GlStateManager.getMatrix(2932, DEPTH);
				depthzfun = (int)DEPTH.get();
				DEPTH.clear();
				GlStateManager.getMatrix(2929, BLEND);
				depthTest = BooleanUtils.toBoolean((int)BLEND.get());
				BLEND.clear();
				GlStateManager.getMatrix(3042, BLEND);
				blend = BooleanUtils.toBoolean((int)BLEND.get());
				BLEND.clear();
			}catch(Exception ex){
				ex.printStackTrace();
				return;
			}

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder worldrenderer = tessellator.getBuffer();
			try {
				// 名前
				int entityHealth = health;
				// 体力
				String Health = "Health: " + String.valueOf(entityHealth) + "/" + String.valueOf(maxHealth);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(770, 771);

				GL11.glEnable(3089);
				try {
					int boxLocX = MathHelper.floor(locX * scFactor);
					int boxWidth = MathHelper.floor(50.0F * scFactor);
					int boxLocY = MathHelper.floor(locY * scFactor);
					if (el != null) {
						Class entityclass = el.getClass();
						HPViewer.tool.getEntityMap().get(entityclass);
						GL11.glEnable(3089);
						try {
							GL11.glScissor(boxLocX, Minecraft.getInstance().mainWindow.getHeight() - boxLocY - boxWidth, boxWidth, boxWidth);
							if ((el != null) && (el.isAlive())) {
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
					int healthbarwidth = MathHelper.floor(locX + 85.0F * entityHealth / maxHealth);
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
					Minecraft.getInstance().fontRenderer.drawStringWithShadow(Name, locX+50 + (88 - Minecraft.getInstance().fontRenderer.getStringWidth(Name)) / 2, locY + 2, new Color(1.0F, 1.0F, 1.0F, 1.0F).getRGB());
					// 体力数値表示
					Minecraft.getInstance().fontRenderer.drawStringWithShadow(Health, locX+50 + (88 - Minecraft.getInstance().fontRenderer.getStringWidth(Health)) / 2, locY + 16, new Color(1.0F, 1.0F, 1.0F, 1.0F).getRGB());

					drawGradientRect(locX, locY, locX + 50, locY + 50, new Color(0.2F, 0.2F, 0.2F, 0.3F).getRGB(),new Color(0.1F, 0.1F, 0.1F, 0.3F).getRGB(), 0.0F);
					drawGradientRect(locX, locY, locX + 2, locY + 50, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX, locY, locX + 50, locY + 2, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX + 48, locY, locX + 50, locY + 50, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX, locY + 48, locX + 50, locY + 50, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			GlStateManager.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.depthFunc(depthzfun);

			if (depthTest) {
				GlStateManager.enableDepthTest();
			} else {
				GlStateManager.disableDepthTest();
			}
			if (blend) {
				GlStateManager.enableBlend();
			} else {
				GlStateManager.disableBlend();
			}
			GlStateManager.clearCurrentColor();
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
	        GlStateManager.disableTexture();
	        GlStateManager.enableBlend();
	        GlStateManager.disableAlphaTest();
	        GlStateManager.blendFuncSeparate(770, 771, 1, 0);
	        GlStateManager.shadeModel(7425);
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder worldrenderer = tessellator.getBuffer();
	        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
	        worldrenderer.pos((double)right, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
	        worldrenderer.pos((double)left, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
	        worldrenderer.pos((double)left, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
	        worldrenderer.pos((double)right, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
	        tessellator.draw();
	        GlStateManager.shadeModel(7424);
	        GlStateManager.disableBlend();
	        GlStateManager.enableAlphaTest();
	        GlStateManager.enableTexture();
	}



	public void drawTargettedMobPreview(LivingEntity el, int locX, int locY) {
		EntityConfigurationEntry configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(el.getClass());
		if (configentry == null) {
			configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(VillagerEntity.class);
		}
		GlStateManager.pushMatrix();
		try {
			try {
				if (!MyConfig._general.skinnedPortrait.get()) {
					drawGradientRect(locX + 1, locY + 1, locX + 49, locY + 49,
							new Color(0.0F, 0.0F, 0.0F, 0.0F).getRGB(), new Color(0.6F, 0.6F, 0.6F, 0.8F).getRGB(),
							0.0F);
				}
				if (el == Minecraft.getInstance().player) {
					GlStateManager.translated(locX + 25 + configentry.XOffset, locY + 52 + configentry.YOffset - 30.0F, 1.0F);
				} else {
					GlStateManager.translated(locX + 25 + configentry.XOffset, locY + 52 + configentry.YOffset, 1.0F);
				}
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				float scalemod = (3.0F - el.getEyeHeight()) * configentry.EntitySizeScaling;
				float finalScale = configentry.ScaleFactor + configentry.ScaleFactor * scalemod;
				if (el.isChild()) {
					finalScale = (configentry.ScaleFactor + configentry.ScaleFactor * scalemod)
							* configentry.BabyScaleFactor;
				}
				GlStateManager.scaled(finalScale * 0.85F, finalScale * 0.85F, 0.1F);
				if (MyConfig._general.lockPosition.get()) {
					int hurt = el.hurtTime;
					float prevRenderYawOffset = el.prevRenderYawOffset;
					el.hurtTime = 0;
					el.prevRenderYawOffset = (el.renderYawOffset - 360.0F);
					GlStateManager.rotatef(el.renderYawOffset - 360.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotatef(-30.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
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
					GlStateManager.rotatef(180.0F - Minecraft.getInstance().player.rotationYaw, 0.0F, -1.0F, 0.0F);
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
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

	public void renderEntity(LivingEntity el) {
		GlStateManager.pushLightingAttributes();
		//GL11.glPushAttrib(8192);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder worldrenderer = tessellator.getBuffer();
		EntityRenderer<Entity> render = this.mc.getRenderManager().getRenderer(el.getClass());
		try {
			GlStateManager.disableBlend();
			GlStateManager.enableDepthTest();
			render.doRender(el, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
			GlStateManager.clear(256, false);
		} catch (Throwable ex) {
//			if (Tessellator.instance.isDrawing) {
//				Tessellator.instance.draw();
//			}
		}
		GlStateManager.popAttributes();
//		Tessellator.instance.setBrightness(240);
//		Tessellator.instance.setColorRGBA(255, 255, 255, 255);
		GlStateManager.blendFunc(770, 771);
	}

}
