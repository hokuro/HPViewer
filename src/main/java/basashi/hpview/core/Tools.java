package basashi.hpview.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basashi.hpview.config.ConfigValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.config.Configuration;

public class Tools {
	public static final Set<String> donators = new HashSet();
	private static HashMap<Class, EntityConfigurationEntry> entityMap = new HashMap();
	public static int timeTillFlush = 500;
	public List<Object[]> unloadeEntities;
	public static Thread updateCheck;

	public Tools(){
		this.unloadeEntities = new ArrayList();
	}

	public HashMap<Class,EntityConfigurationEntry> getEntityMap(){
		return entityMap;
	}

	public void RegisterRenders(){
		scanforEntities();
	}

	public void scanforEntities() {
		try {
			EntityList.classToStringMapping.put(EntityOtherPlayerMP.class, "OtherPlayers");
		} catch (Throwable ex) {
		}
		searchMapForEntities(EntityList.classToStringMapping);
	}


	private void searchMapForEntities(Map theMap) {
		File configfolder = new File(ConfigValue.CONFIG_FILE().getParentFile(), "DIAdvancedCompatibility");
		configfolder.mkdirs();
		String FullConfigFileName = "CombinedConfig.cfg";
		File configfile = new File(configfolder, FullConfigFileName);
		try {
			configfile.createNewFile();
		} catch (IOException ex) {
		}
		Configuration config = new Configuration(configfile);
		Set<Class> set = theMap.keySet();
		for (Class entry : set) {
			if ((entry != null) && (EntityLiving.class.isAssignableFrom(entry))) {
				entityMap.put(entry, EntityConfigurationEntry.generateDefaultConfiguration(config, entry));
			}
		}
		config.save();
	}
}
