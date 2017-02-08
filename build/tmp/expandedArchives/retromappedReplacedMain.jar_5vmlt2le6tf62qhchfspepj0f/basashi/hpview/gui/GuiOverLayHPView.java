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
    public static final FloatBuffer DEPTH = GLAllocation.func_74529_h(32);
    public static final FloatBuffer BLEND = GLAllocation.func_74529_h(32);
    private ScaledResolution scaledresolution;
	public static int LastTargeted = 0;
    public static int tick = 0;
    public static DynamicTexture inventoryPNG;
	public GuiOverLayHPView(Minecraft mc){
		this.mc = mc;
		this.fontRenderer = mc.field_71466_p;
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
			GlStateManager.func_179094_E();
			if (mc.field_71439_g != null) {
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
					} else if ((EntityList.field_75626_c.get(el.getClass()) != null) && (((String) EntityList.field_75626_c.get(el.getClass())).equalsIgnoreCase("Linkbook"))) {
						el = null;
					} else if (configentry.IgnoreThisMob) {
						el = null;
					} else {
						LastTargeted = el.func_145782_y();
					}
				}
				if ((el != null) || ((LastTargeted != 0) && ((ConfigValue.General.portraitLifetime == -1)|| (tick > 0)))) {
					mc.field_71460_t.func_78478_c();
					ScaledResolution scaledresolution = new ScaledResolution(mc);
					if (ConfigValue.General.locX > scaledresolution.func_78326_a() - 135) {
						ConfigValue.General.locX = (scaledresolution.func_78326_a() - 135);
					}
					if (ConfigValue.General.locY > scaledresolution.func_78328_b() - 50) {
						ConfigValue.General.locY = (scaledresolution.func_78328_b() - 50);
					}
					if (ConfigValue.General.locX < 0) {
						ConfigValue.General.locX = 0;
					}
					if (ConfigValue.General.locY < 0) {
						ConfigValue.General.locY = 0;
					}
					GlStateManager.func_179094_E();
					try {
						GlStateManager.func_179109_b((1.0F - ConfigValue.General.guiScale) * ConfigValue.General.locX, (1.0F - ConfigValue.General.guiScale) * ConfigValue.General.locY, 0.0F);
						GlStateManager.func_179152_a(ConfigValue.General.guiScale, ConfigValue.General.guiScale, ConfigValue.General.guiScale);
						if (el == null) {
							tick -= 1;
							try {
								el = (EntityLivingBase) mc.field_71441_e.func_73045_a(LastTargeted);
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
						LastTargeted = el.func_145782_y();
						FontRenderer fontrenderer = mc.field_71466_p;
						Class entityclass = el.getClass();
						EntityConfigurationEntry configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(entityclass);
						if ((configentry.maxHP == -1) || (configentry.eyeHeight == -1.0F)) {
							configentry.eyeHeight = el.func_70047_e();
							configentry.maxHP = MathHelper.func_76128_c(Math.ceil(el.func_110138_aP()));
						}
						if (configentry.maxHP != MathHelper.func_76128_c(Math.ceil(el.func_110138_aP()))) {
							configentry.maxHP = MathHelper.func_76128_c(Math.ceil(el.func_110138_aP()));
						}
						String Name = configentry.NameOverride;
						if ((el instanceof EntityPlayer)) {
							Name = el.func_70005_c_();
						}
						if ((Name == null) || ("".equals(Name))) {
							Name = el.func_70005_c_();
							if (Name.toLowerCase().endsWith(".name")) {
								Name = I18n.func_74838_a(Name);
							}
							if (Name.endsWith(".name")) {
								Name = Name.replace(".name", "");
								Name = Name.substring(Name.lastIndexOf(".") + 1, Name.length());
								Name = Name.substring(0, 1).toUpperCase() + Name.substring(1, Name.length());
								if ((el.func_70631_g_()) && (configentry.AppendBaby)) {
									Name = "Baby " + Name;
								}
							}
						}

						DrawPortraitUnSkinned(ConfigValue.General.locX,  ConfigValue.General.locY, Name, (int) Math.ceil(el.func_110143_aJ()), (int) Math.ceil(el.func_110138_aP()), el);
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
					GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
					//Tessellator.instance.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.func_179121_F();
				}
			}
			GlStateManager.func_179121_F();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public void DrawPortraitUnSkinned(int locX, int locY, String Name, int health, int maxHealth, EntityLivingBase el) {
		scaledresolution = new ScaledResolution(Minecraft.func_71410_x());
		int depthzfun;
		boolean depthTest;
		boolean blend;
		try {
			try{
				GlStateManager.func_179111_a(2932, DEPTH);
				depthzfun = (int)DEPTH.get();
				DEPTH.clear();
				GlStateManager.func_179111_a(2929, BLEND);
				depthTest = BooleanUtils.toBoolean((int)BLEND.get());
				BLEND.clear();
				GlStateManager.func_179111_a(3042, BLEND);
				blend = BooleanUtils.toBoolean((int)BLEND.get());
				BLEND.clear();
			}catch(Exception ex){
				ex.printStackTrace();
				return;
			}

			Tessellator tessellator = Tessellator.func_178181_a();
			VertexBuffer worldrenderer = tessellator.func_178180_c();
			try {
				// 名前
				int entityHealth = health;
				// 体力
				String Health = "Health: " + String.valueOf(entityHealth) + "/" + String.valueOf(maxHealth);
				GlStateManager.func_179147_l();
				GlStateManager.func_179112_b(770, 771);

				GL11.glEnable(3089);
				try {
					int boxLocX = MathHelper.func_76128_c(locX * scaledresolution.func_78325_e());
					int boxWidth = MathHelper.func_76128_c(50.0F * scaledresolution.func_78325_e());
					int boxLocY = MathHelper.func_76128_c(locY * scaledresolution.func_78325_e());
					if (el != null) {
						Class entityclass = el.getClass();
						HPViewer.tool.getEntityMap().get(entityclass);
						GL11.glEnable(3089);
						try {
							GL11.glScissor(boxLocX, Minecraft.func_71410_x().field_71440_d - boxLocY - boxWidth, boxWidth, boxWidth);
							if ((el != null) && (!el.field_70128_L)) {
								drawTargettedMobPreview(el, locX, locY);
							}
						} catch (Throwable ex) {
						}
					}
				} catch (Throwable ex) {
				}
				GL11.glDisable(3089);


				GlStateManager.func_179143_c(519);
				try {
					// 体力バーの長さ
					int healthbarwidth = MathHelper.func_76141_d(locX + 85.0F * entityHealth / maxHealth);
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
					Minecraft.func_71410_x().field_71466_p.func_175063_a(Name, locX+50 + (88 - Minecraft.func_71410_x().field_71466_p.func_78256_a(Name)) / 2, locY + 2, new Color(1.0F, 1.0F, 1.0F, 1.0F).getRGB());
					// 体力数値表示
					Minecraft.func_71410_x().field_71466_p.func_175063_a(Health, locX+50 + (88 - Minecraft.func_71410_x().field_71466_p.func_78256_a(Health)) / 2, locY + 16, new Color(1.0F, 1.0F, 1.0F, 1.0F).getRGB());

					drawGradientRect(locX, locY, locX + 50, locY + 50, new Color(0.2F, 0.2F, 0.2F, 0.3F).getRGB(),new Color(0.1F, 0.1F, 0.1F, 0.3F).getRGB(), 0.0F);
					drawGradientRect(locX, locY, locX + 2, locY + 50, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX, locY, locX + 50, locY + 2, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX + 48, locY, locX + 50, locY + 50, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					drawGradientRect(locX, locY + 48, locX + 50, locY + 50, Color.lightGray.getRGB(),Color.lightGray.getRGB(), 0.0F);
					GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			GlStateManager.func_179082_a(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.func_179143_c(depthzfun);

			if (depthTest) {
				GlStateManager.func_179126_j();
			} else {
				GlStateManager.func_179097_i();
			}
			if (blend) {
				GlStateManager.func_179147_l();
			} else {
				GlStateManager.func_179084_k();
			}
			GlStateManager.func_179117_G();
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
	        GlStateManager.func_179090_x();
	        GlStateManager.func_179147_l();
	        GlStateManager.func_179118_c();
	        GlStateManager.func_179120_a(770, 771, 1, 0);
	        GlStateManager.func_179103_j(7425);
	        Tessellator tessellator = Tessellator.func_178181_a();
	        VertexBuffer worldrenderer = tessellator.func_178180_c();
	        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
	        worldrenderer.func_181662_b((double)right, (double)top, (double)zLevel).func_181666_a(f1, f2, f3, f).func_181675_d();
	        worldrenderer.func_181662_b((double)left, (double)top, (double)zLevel).func_181666_a(f1, f2, f3, f).func_181675_d();
	        worldrenderer.func_181662_b((double)left, (double)bottom, (double)zLevel).func_181666_a(f5, f6, f7, f4).func_181675_d();
	        worldrenderer.func_181662_b((double)right, (double)bottom, (double)zLevel).func_181666_a(f5, f6, f7, f4).func_181675_d();
	        tessellator.func_78381_a();
	        GlStateManager.func_179103_j(7424);
	        GlStateManager.func_179084_k();
	        GlStateManager.func_179141_d();
	        GlStateManager.func_179098_w();
	}



	public void drawTargettedMobPreview(EntityLivingBase el, int locX, int locY) {
		EntityConfigurationEntry configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(el.getClass());
		if (configentry == null) {
			configentry = (EntityConfigurationEntry) HPViewer.tool.getEntityMap().get(EntityVillager.class);
		}
		GlStateManager.func_179094_E();
		try {
			try {
				if (!ConfigValue.General.skinnedPortrait) {
					drawGradientRect(locX + 1, locY + 1, locX + 49, locY + 49,
							new Color(0.0F, 0.0F, 0.0F, 0.0F).getRGB(), new Color(0.6F, 0.6F, 0.6F, 0.8F).getRGB(),
							0.0F);
				}
				if (el == Minecraft.func_71410_x().field_71439_g) {
					GlStateManager.func_179109_b(locX + 25 + configentry.XOffset, locY + 52 + configentry.YOffset - 30.0F, 1.0F);
				} else {
					GlStateManager.func_179109_b(locX + 25 + configentry.XOffset, locY + 52 + configentry.YOffset, 1.0F);
				}
				GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
				float scalemod = (3.0F - el.func_70047_e()) * configentry.EntitySizeScaling;
				float finalScale = configentry.ScaleFactor + configentry.ScaleFactor * scalemod;
				if (el.func_70631_g_()) {
					finalScale = (configentry.ScaleFactor + configentry.ScaleFactor * scalemod)
							* configentry.BabyScaleFactor;
				}
				GlStateManager.func_179152_a(finalScale * 0.85F, finalScale * 0.85F, 0.1F);
				if (ConfigValue.General.lockPosition) {
					int hurt = el.field_70737_aN;
					float prevRenderYawOffset = el.field_70760_ar;
					el.field_70737_aN = 0;
					el.field_70760_ar = (el.field_70761_aq - 360.0F);
					GlStateManager.func_179114_b(el.field_70761_aq - 360.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.func_179114_b(-30.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
					try {
						renderEntity(el);
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
					el.field_70760_ar = prevRenderYawOffset;
					el.field_70737_aN = hurt;
				} else {
					int hurt = el.field_70737_aN;
					el.field_70737_aN = 0;
					GlStateManager.func_179114_b(180.0F - Minecraft.func_71410_x().field_71439_g.field_70177_z, 0.0F, -1.0F, 0.0F);
					GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
					try {
						renderEntity(el);
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
					el.field_70737_aN = hurt;
				}
			} catch (Throwable ex) {
			}
		} catch (Throwable ex) {
		}
		GlStateManager.func_179121_F();
	}

	public void renderEntity(EntityLivingBase el) {
		GlStateManager.func_179123_a();
		//GL11.glPushAttrib(8192);

		Tessellator tessellator = Tessellator.func_178181_a();
		VertexBuffer worldrenderer = tessellator.func_178180_c();
		Render render = this.mc.func_175598_ae().func_78715_a(el.getClass());
		try {
			GlStateManager.func_179084_k();
			GlStateManager.func_179126_j();
			render.func_76986_a(el, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
			GlStateManager.func_179086_m(256);
		} catch (Throwable ex) {
//			if (Tessellator.instance.isDrawing) {
//				Tessellator.instance.draw();
//			}
		}
		GlStateManager.func_179099_b();
//		Tessellator.instance.setBrightness(240);
//		Tessellator.instance.setColorRGBA(255, 255, 255, 255);
		GlStateManager.func_179112_b(770, 771);
	}

}
