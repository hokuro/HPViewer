package basashi.hpview.texture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraftforge.common.config.Configuration;

public class FileSkinRegistration extends AbstractSkin {
	public FileSkinRegistration(String path) {
		String pathName = cleanup(path.replace("file:", ""));
		setInternalName(pathName);
		setSkinValue(EnumSkinPart.FRAMENAME, pathName + "DIFrameSkin.png");
		setSkinValue(EnumSkinPart.TYPEICONSNAME, pathName + "DITypeIcons.png");
		setSkinValue(EnumSkinPart.DAMAGENAME, pathName + "damage.png");
		setSkinValue(EnumSkinPart.HEALTHNAME, pathName + "health.png");
		setSkinValue(EnumSkinPart.BACKGROUNDNAME, pathName + "background.png");
		setSkinValue(EnumSkinPart.NAMEPLATENAME, pathName + "NamePlate.png");
		setSkinValue(EnumSkinPart.LEFTPOTIONNAME, pathName + "leftPotions.png");
		setSkinValue(EnumSkinPart.RIGHTPOTIONNAME, pathName + "rightPotions.png");
		setSkinValue(EnumSkinPart.CENTERPOTIONNAME, pathName + "centerPotions.png");
		File file = new File(pathName + "skin.cfg");
		Configuration config = new Configuration(file);
		loadConfig(config);
		config.save();
	}

	private InputStream getFileInputStream(String path) throws FileNotFoundException {
		return new FileInputStream(path);
	}

	private static String cleanup(String string) {
		String ret = string;
		if (ret.contains(File.separator + "." + File.separator)) {
			ret = ret.replace(File.separator + "." + File.separator, File.separator);
		} else if (ret.contains("\\.\\")) {
			ret = ret.replaceAll("\\\\.\\\\", "\\\\");
		} else if (ret.contains("/./")) {
			ret = ret.replace("/./", "/");
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

	private DynamicTexture checkAndReload(EnumSkinPart enumID, EnumSkinPart enumName) {
		DynamicTexture ret = (DynamicTexture) getSkinValue(enumID);
		if (ret == null) {
			try {
				String tmp = (String) getSkinValue(enumName);
				ret = setupTexture(fixDim(ImageIO.read(getFileInputStream(tmp))), enumID);
			} catch (Exception ex) {
			}
		}
		return ret;
	}

	public static void scanFilesForSkins(File path) {
		try {
			for (File file : path.listFiles()) {
				if (file.isDirectory()) {
					for (File files : file.listFiles()) {
						if (files.getAbsolutePath().endsWith("skin.cfg")) {
							String thisSkin = files.getAbsolutePath().substring(0,
									files.getAbsolutePath().lastIndexOf(File.separator));
							if (!thisSkin.endsWith(File.separator)) {
								thisSkin = thisSkin + File.separator;
							}
							AVAILABLESKINS.add("file:" + thisSkin);
						}
					}
				}
			}
		} catch (Exception ex) {
		}
	}
}
