package basashi.hpview.texture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;

import javax.imageio.ImageIO;

import basashi.hpview.core.HPViewer;
import basashi.hpview.core.log.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class SkinRegistration extends AbstractSkin {
	private static void checkEntry(JarEntry jEntry) {
		ModLog.log().info(jEntry.getName());
		if ((jEntry.getName().contains("DITextures")) && (jEntry.getName().endsWith(".png"))) {
			String thisSkin = jEntry.getName().substring(0, jEntry.getName().lastIndexOf("/"));
			if (!thisSkin.startsWith("/")) {
				thisSkin = "/" + thisSkin;
			}
			if (!thisSkin.endsWith("/")) {
				thisSkin = thisSkin + "/";
			}
			if (!AbstractSkin.AVAILABLESKINS.contains(thisSkin)) {
				AbstractSkin.AVAILABLESKINS.add(thisSkin);
			}
		}
	}

	private static void giveDebuggingInfo(Object test) {
		ModLog.log().info(" ");
		ModLog.log().info(" ");
		ModLog.log().info(" ");
		ModLog.log().info("======================================");
		ModLog.log().info("Java version/OS incompatibility detected!");
		ModLog.log().info("Please post this error block on the Minecraft Forum thread:");
		ModLog.log().info("http://www.minecraftforum.net/topic/1536685-151");
		ModLog.log().info("======================================");
		ModLog.log().info(" ");
		ModLog.log().info(" ");
		ModLog.log().info("--------------------------------------");
		ModLog.log().info(" ");
		ModLog.log().info("Connection method: " + String.valueOf(test));
		ModLog.log().info(" ");
		ModLog.log().info("Java version: " + System.getProperty("java.version"));
		ModLog.log().info("Java vendor: " + System.getProperty("java.vendor"));
		ModLog.log().info("Java url: " + System.getProperty("java.vendor.url"));
		ModLog.log().info("Java class version: " + System.getProperty("java.class.version"));
		ModLog.log().info("Java Architecture: " + System.getProperty("os.arch"));
		ModLog.log().info(" ");
		ModLog.log().info("Java Spec version: " + System.getProperty("java.specification.version"));
		ModLog.log().info("Java Spec vendor: " + System.getProperty("java.specification.vendor"));
		ModLog.log().info("Java Spec name: " + System.getProperty("java.specification.name"));
		ModLog.log().info(" ");
		ModLog.log().info("VM version: " + System.getProperty("java.vm.version"));
		ModLog.log().info("VM vendor: " + System.getProperty("java.vm.vendor"));
		ModLog.log().info("VM name: " + System.getProperty("java.vm.name"));
		ModLog.log().info(" ");
		ModLog.log().info("VM Spec version: " + System.getProperty("java.vm.specification.version"));
		ModLog.log().info("VM Spec vendor: " + System.getProperty("java.vm.specification.vendor"));
		ModLog.log().info("VM Spec name: " + System.getProperty("java.vm.specification.name"));
		ModLog.log().info(" ");
		ModLog.log().info("OS: " + System.getProperty("os.name"));
		ModLog.log().info("OS Version: " + System.getProperty("os.version"));
		ModLog.log().info(" ");
		ModLog.log().info("Spec version: " + System.getProperty("java.vm.specification.version"));
		ModLog.log().info("Spec vendor: " + System.getProperty("java.vm.specification.vendor"));
		ModLog.log().info("Spec name: " + System.getProperty("java.vm.specification.name"));
		ModLog.log().info(" ");
		ModLog.log().info("--------------------------------------");
		ModLog.log().info(" ");
		ModLog.log().info(" ");
		ModLog.log().info(" ");
	}

	public static void scanJarForSkins(Class clazz) {
		AbstractSkin.AVAILABLESKINS.add("/DITextures/Default/");
		AbstractSkin.AVAILABLESKINS.add("/DITextures/WoWLike/");
		AbstractSkin.AVAILABLESKINS.add("/DITextures/Minimal/");
		try {
			URL url = clazz.getResource("/DITextures");
			if (url != null) {
				Object test = url.openConnection();
				if ((test instanceof JarURLConnection)) {
					JarURLConnection juc = (JarURLConnection) test;
					juc.setUseCaches(false);
					juc.setDoInput(true);
					juc.setDoOutput(false);
					juc.setAllowUserInteraction(true);
					juc.connect();
					Enumeration jEnum = juc.getJarFile().entries();
					while (jEnum.hasMoreElements()) {
						System.out.println(jEnum.nextElement());
						try {
							checkEntry((JarEntry) jEnum.nextElement());
						} catch (Exception ex) {
						}
					}
				} else if (World.class.getName().endsWith("World")) {
					ModLog.log().info("Damage Indicators detected deobfuscation.");
				} else {
					giveDebuggingInfo(test);
				}
			}
		} catch (Exception ex) {
		}
	}

	public SkinRegistration(String skinName) {
		setInternalName(skinName);
		setSkinValue(EnumSkinPart.FRAMENAME, skinName + "DIFrameSkin.png");
		setSkinValue(EnumSkinPart.TYPEICONSNAME, skinName + "DITypeIcons.png");
		setSkinValue(EnumSkinPart.DAMAGENAME, skinName + "damage.png");
		setSkinValue(EnumSkinPart.HEALTHNAME, skinName + "health.png");
		setSkinValue(EnumSkinPart.BACKGROUNDNAME, skinName + "background.png");
		setSkinValue(EnumSkinPart.NAMEPLATENAME, skinName + "NamePlate.png");
		setSkinValue(EnumSkinPart.LEFTPOTIONNAME, skinName + "leftPotions.png");
		setSkinValue(EnumSkinPart.RIGHTPOTIONNAME, skinName + "rightPotions.png");
		setSkinValue(EnumSkinPart.CENTERPOTIONNAME, skinName + "centerPotions.png");
		try {
			File file = File.createTempFile("skin", ".tmp");
			try {
				if ((file.exists()) && (!file.delete())) {
					file.deleteOnExit();
					ModLog.log().info("Unable to delete old temp file:");
					ModLog.log().info(file.getCanonicalPath());
					file = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".skin.tmp");
					ModLog.log().info("Creating unique temp file:");
					ModLog.log().info(file.getCanonicalPath());
				}
				if (!file.createNewFile()) {
					ModLog.log().info("Unable to create temp file:");
					ModLog.log().info(file.getCanonicalPath());
					ModLog.log().info("This may cause problems!");
				}
				URL url = Minecraft.class.getResource(skinName + "skin.cfg");
				InputStream cfg = url.openStream();
				FileOutputStream fos = new FileOutputStream(file);
				int bite = cfg.read();
				while (bite != -1) {
					fos.write(bite);
					bite = cfg.read();
				}
				fos.flush();
				fos.close();
				cfg.close();
			} catch (Exception ex) {

			}
			loadConfig(new Configuration(file));
			if (!file.delete()) {
				file.deleteOnExit();
			}
		} catch (Exception ex) {
		}
	}

	private DynamicTexture checkAndReload(EnumSkinPart enumID, EnumSkinPart enumName) {
		DynamicTexture ret = (DynamicTexture) getSkinValue(enumID);
		if (ret == null) {
			try {
				String tmp = (String) getSkinValue(enumName);
				ret = setupTexture(fixDim(ImageIO.read(HPViewer.class.getResourceAsStream(tmp))), enumID);
			} catch (Exception ex) {
			}
		}
		return ret;
	}

	public final void loadSkin() {
		setSkinValue(EnumSkinPart.FRAMEID, checkAndReload(EnumSkinPart.FRAMEID, EnumSkinPart.FRAMENAME));
		setSkinValue(EnumSkinPart.TYPEICONSID, checkAndReload(EnumSkinPart.TYPEICONSID, EnumSkinPart.TYPEICONSNAME));
		setSkinValue(EnumSkinPart.DAMAGEID, checkAndReload(EnumSkinPart.DAMAGEID, EnumSkinPart.DAMAGENAME));
		setSkinValue(EnumSkinPart.HEALTHID, checkAndReload(EnumSkinPart.HEALTHID, EnumSkinPart.HEALTHNAME));
		setSkinValue(EnumSkinPart.BACKGROUNDID, checkAndReload(EnumSkinPart.BACKGROUNDID, EnumSkinPart.BACKGROUNDNAME));
		setSkinValue(EnumSkinPart.NAMEPLATEID, checkAndReload(EnumSkinPart.NAMEPLATEID, EnumSkinPart.NAMEPLATENAME));
		setSkinValue(EnumSkinPart.LEFTPOTIONID, checkAndReload(EnumSkinPart.LEFTPOTIONID, EnumSkinPart.LEFTPOTIONNAME));
		setSkinValue(EnumSkinPart.RIGHTPOTIONID,checkAndReload(EnumSkinPart.RIGHTPOTIONID, EnumSkinPart.RIGHTPOTIONNAME));
		setSkinValue(EnumSkinPart.CENTERPOTIONID,checkAndReload(EnumSkinPart.CENTERPOTIONID, EnumSkinPart.CENTERPOTIONNAME));
	}
}
