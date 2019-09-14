package basashi.hpview.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basashi.config.Configuration;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.fml.loading.FMLPaths;

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
			//EntityList.classToStringMapping.put(EntityOtherPlayerMP.class, "OtherPlayers");
		} catch (Throwable ex) {
		}
		//searchMapForEntities(EntityList.classToStringMapping);
	}


	private void searchMapForEntities(Map theMap) {
		// TODO: なんかファイル
		File configfolder = new File(FMLPaths.CONFIGDIR.get().toString(), "DIAdvancedCompatibility");
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
			if ((entry != null) && (LivingEntity.class.isAssignableFrom(entry))) {
				entityMap.put(entry, EntityConfigurationEntry.generateDefaultConfiguration(config, null, entry));
			}
		}
		config.save();
	}
}
