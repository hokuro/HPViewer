package basashi.hpview.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import basashi.hpview.config.ConfigValue;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityConfigurationEntry {
	public static HashMap<Integer, Integer> maxHealthOverride = new HashMap(200);
	public final boolean AppendBaby;
	public final float BabyScaleFactor;
	public final Class Clazz;
	public final float EntitySizeScaling;
	public float eyeHeight;
	public final boolean IgnoreThisMob;
	public int maxHP;
	public final String NameOverride;
	public final float ScaleFactor;
	public final float XOffset;
	public final float YOffset;

	public static EntityConfigurationEntry generateDefaultConfiguration(Configuration config, Class entry) {
		boolean ignore = false;
		boolean appendBabyName = true;
		float scaleFactor = 22.0F;
		float xOffset = 0.0F;
		float yOffset = -5.0F;
		float SizeModifier = 0.0F;
		float BabyScaleFactor = 2.0F;
		if (entry == EntityIronGolem.class) {
			scaleFactor = 16.0F;
		} else if ((entry == EntitySlime.class) || (entry == EntityMagmaCube.class)) {
			scaleFactor = 5.0F;
			SizeModifier = 2.0F;
			yOffset = -5.0F;
		} else if (entry == EntityEnderman.class) {
			scaleFactor = 15.0F;
		} else if (entry == EntityGhast.class) {
			scaleFactor = 7.0F;
			yOffset = -20.0F;
		} else if (entry == EntitySquid.class) {
			yOffset = -17.0F;
		} else if (entry == EntityOcelot.class) {
			scaleFactor = 25.0F;
			yOffset = -5.0F;
		} else if (entry == EntityWither.class) {
			scaleFactor = 15.0F;
			yOffset = 5.0F;
		} else if (entry.getName().equalsIgnoreCase("thaumcraft.common.entities.EntityWisp")) {
			yOffset = -14.0F;
		} else if (entry.getName().equalsIgnoreCase("drzhark.mocreatures.MoCEntityWerewolf")) {
			scaleFactor = 20.0F;
			yOffset = -4.0F;
		} else if (entry.getName().equalsIgnoreCase("drzhark.mocreatures.MoCEntityOgre")) {
			scaleFactor = 12.0F;
		} else if (entry.getName().equalsIgnoreCase("xolova.blued00r.divinerpg.mobs.EntityCyclops")) {
			scaleFactor = 10.0F;
		} else if (entry.getName().equalsIgnoreCase("xolova.blued00r.divinerpg.mobs.EntityEnergyGolem")) {
			scaleFactor = 10.0F;
		} else if (entry.getName().equalsIgnoreCase("xolova.blued00r.divinerpg.mobs.EntityCaveclops")) {
			scaleFactor = 10.0F;
		} else if ((EntityList.getKey(entry) != null)
				&& (((String)  EntityList.getClassFromID(EntityList.getID(entry)).getName()).equalsIgnoreCase("Linkbook"))) {
			ignore = true;
		}
		return loadEntityConfig(config, new EntityConfigurationEntry(entry, scaleFactor, xOffset, yOffset, SizeModifier,
				BabyScaleFactor, appendBabyName, "", ignore, 20, 1.5F));
	}

	public static EntityConfigurationEntry loadEntityConfig(Configuration config, EntityConfigurationEntry ece) {
		return loadEntityConfig(config, ece, null);
	}

	public static EntityConfigurationEntry loadEntityConfig(Configuration config, EntityConfigurationEntry ece,
			EntityLiving el) {
		Class entry = ece.Clazz;
		String mod = "Vanilla";
		EntityRegistry.EntityRegistration er = EntityRegistry.instance().lookupModSpawn(ece.Clazz, true);
		if (er != null) {
			try {
				mod = er.getContainer().getMetadata().name;
			} catch (Throwable ex) {
			}
		}
		String ConfigName;
		if (EntityList.getKey(entry) != null) {
			ConfigName = I18n.translateToLocal((String)  EntityList.getClassFromID(EntityList.getID(entry)).getName()) + " - " + mod;
		} else {
			ConfigName = entry.getName() + " - " + mod;
		}
		ConfigName = ConfigName.replace(".", "/");
		config.addCustomCategoryComment(ConfigName,
				"These settings are to help other modders and users to make custom mobs fit correctly in the preview window.");
		config.addCustomCategoryComment(ConfigName,
				"These settings are to help other modders and users to make custom mobs fit correctly in the preview window.");
		Property prop = config.get(ConfigName, "Scale_Factor", String.valueOf(ece.ScaleFactor));
		prop.setComment("How much to upscale the mob. 0 will cause it to disappear entirely. 22 is OK for most mobs");
		float scaleFactor;
		try {
			scaleFactor = Float.valueOf(prop.getString()).floatValue();
		} catch (Throwable ex) {
			System.err.println("Invalid or malformed configuration entry for " + prop.getName());
			scaleFactor = ece.ScaleFactor;
			prop.set(String.valueOf(22.0F));
		}
		prop = config.get(ConfigName, "Name", "");
		prop.setComment("Entities Name to use if overriden.");
		String entityName = prop.getString();
		prop = config.get(ConfigName, "Append_Baby_Name", ece.AppendBaby);
		prop.setComment("Append Baby to this entity type if it is a baby.");
		boolean appendBabyName = prop.getBoolean(ece.AppendBaby);
		prop = config.get(ConfigName, "Ignore_This_Mob", ece.IgnoreThisMob);
		prop.setComment("Should the portrait display should ignore this entity entirely?");
		boolean ignore = prop.getBoolean(ece.IgnoreThisMob);
		prop = config.get(ConfigName, "X_Offset", String.valueOf(ece.XOffset));
		prop.setComment("How much to nudge the mob display horizontally. 0 is default, negative numbers to move left, positive for right.");
		float xOffset;
		try {
			xOffset = Float.valueOf(prop.getString()).floatValue();
		} catch (Throwable ex) {
			System.err.println("Invalid or malformed configuration entry for " + prop.getName());
			prop.set(String.valueOf(ece.XOffset));
			xOffset = ece.XOffset;
		}
		prop = config.get(ConfigName, "Y_Offset", String.valueOf(ece.YOffset));
		prop.setComment("How much to nudge the mob display vertically. 0 is default, negative numbers to move up, positive for down.");
		float yOffset;
		try {
			yOffset = Float.valueOf(prop.getString()).floatValue();
		} catch (Throwable ex) {
			System.err.println("Invalid or malformed configuration entry for " + prop.getName());
			prop.set(String.valueOf(ece.YOffset));
			yOffset = ece.YOffset;
		}
		prop = config.get(ConfigName, "Size_Modifier", String.valueOf(ece.EntitySizeScaling));
		prop.setComment("If and how much to scale the mob is it has a size modifier(like slimes)");
		float SizeModifier;
		try {
			SizeModifier = Float.valueOf(prop.getString()).floatValue();
		} catch (Throwable ex) {
			System.err.println("Invalid or malformed configuration entry for " + prop.getName());
			prop.set(String.valueOf(ece.EntitySizeScaling));
			SizeModifier = ece.EntitySizeScaling;
		}
		prop = config.get(ConfigName, "Baby_Scale_Modifier", ece.BabyScaleFactor);
		prop.setComment("If and how much to scale the mob is a baby.");
		float babyScaleFactor;
		try {
			babyScaleFactor = Float.valueOf(prop.getString()).floatValue();
		} catch (Throwable ex) {
			System.err.println("Invalid or malformed configuration entry for " + prop.getName());
			prop.set(String.valueOf(ece.BabyScaleFactor));
			babyScaleFactor = ece.BabyScaleFactor;
		}
		EntityConfigurationEntry tmp = new EntityConfigurationEntry(entry, scaleFactor, xOffset, yOffset, SizeModifier,
				babyScaleFactor, appendBabyName, entityName, ignore, ece.maxHP, ece.eyeHeight);
		return tmp;
	}

	public static void saveEntityConfig(EntityConfigurationEntry ece) {
		File configfolder = new File(ConfigValue.CONFIG_FILE().getParentFile(), "DIAdvancedCompatibility");
		configfolder.mkdir();
		Class entry = ece.Clazz;
		String FullConfigFileName = "CombinedConfig.cfg";
		File configfile = new File(configfolder, FullConfigFileName);
		String mod = "Vanilla";
		EntityRegistry.EntityRegistration er = EntityRegistry.instance().lookupModSpawn(ece.Clazz, true);
		if (er != null) {
			try {
				mod = er.getContainer().getMetadata().name;
			} catch (Throwable ex) {
			}
		}
		String ConfigName;
		if (EntityList.getKey(entry) != null) {
			ConfigName = I18n.translateToLocal((String) EntityList.getClassFromID(EntityList.getID(entry)).getName()) + " - " + mod;
		} else {
			ConfigName = entry.getName() + " - " + mod;
		}
		ConfigName = ConfigName.replace(".", "/");
		try {
			configfile.createNewFile();
		} catch (IOException ex) {
		}
		Configuration config = new Configuration(configfile);
		config.addCustomCategoryComment(ConfigName,
				"These settings are to help other modders and users to make custom mobs fit correctly in the preview window.");
		config.get(ConfigName, "Scale_Factor", String.valueOf(ece.ScaleFactor)).set(String.valueOf(ece.ScaleFactor));
		if ((ece.NameOverride == null) || ("".equals(ece.NameOverride))) {
			config.get(ConfigName, "Name", ece.NameOverride).set("");
		} else {
			config.get(ConfigName, "Name", ece.NameOverride).set(ece.NameOverride);
		}
		config.get(ConfigName, "Append_Baby_Name", ece.AppendBaby).set(String.valueOf(ece.AppendBaby));
		config.get(ConfigName, "X_Offset", String.valueOf(ece.XOffset)).set(String.valueOf(ece.XOffset));
		config.get(ConfigName, "Y_Offset", String.valueOf(ece.YOffset)).set(String.valueOf(ece.YOffset));
		config.get(ConfigName, "Size_Modifier", String.valueOf(ece.EntitySizeScaling))
				.set(String.valueOf(ece.EntitySizeScaling));
		config.get(ConfigName, "Baby_Scale_Modifier", String.valueOf(ece.BabyScaleFactor))
				.set(String.valueOf(ece.BabyScaleFactor));
		config.save();
	}

	public EntityConfigurationEntry(Class clazz, float scale, float xoffset, float yoffset, float sizeScaling,
			float babyscale, boolean appendBaby, boolean ignoreThisMob, int maxHP, float eyeHeight) {
		this(clazz, scale, xoffset, yoffset, sizeScaling, babyscale, appendBaby, "", ignoreThisMob, maxHP, eyeHeight);
	}

	public EntityConfigurationEntry(Class clazz, float scale, float xoffset, float yoffset, float sizeScaling,
			float babyscale, boolean appendBaby, String nameOverride, boolean ignoreThisMob, int maxHP,
			float eyeHeight) {
		this.IgnoreThisMob = ignoreThisMob;
		this.Clazz = clazz;
		this.ScaleFactor = scale;
		this.XOffset = xoffset;
		this.YOffset = yoffset;
		this.EntitySizeScaling = sizeScaling;
		this.BabyScaleFactor = babyscale;
		this.AppendBaby = appendBaby;
		if (nameOverride != null) {
			this.NameOverride = nameOverride;
		} else {
			this.NameOverride = "";
		}
		this.maxHP = maxHP;
		this.eyeHeight = eyeHeight;
	}

	public boolean equals(Object obj) {
		return obj.toString().intern() == toString().intern();
	}

	public void SetInfo(int maxh, float eyeh) {
		this.maxHP = maxh;
		this.eyeHeight = eyeh;
	}

	public String toString() {
		String eol = System.getProperty("line.separator");
		StringBuilder output = new StringBuilder();
		output.append(eol).append("---------------------------------").append(eol).append("Class Name: ")
				.append(this.Clazz.getName()).append(eol).append("ScaleFactor: ")
				.append(String.valueOf(this.ScaleFactor)).append(eol).append("Name Override: ")
				.append(this.NameOverride).append(eol).append("AppendBabyName: ")
				.append(String.valueOf(this.AppendBaby)).append(eol).append("X Offset: ")
				.append(String.valueOf(this.XOffset)).append(eol).append("Y Offset: ")
				.append(String.valueOf(this.YOffset)).append(eol).append("Size Modifier: ")
				.append(String.valueOf(this.EntitySizeScaling)).append(eol).append("Baby Scale Modifier: ")
				.append(String.valueOf(this.BabyScaleFactor)).append(eol).append("Ignored: ")
				.append(String.valueOf(this.IgnoreThisMob)).append(eol).append("---------------------------------")
				.append(eol);
		return output.toString();
	}
}
