package basashi.hpview.core;

import basashi.hpview.config.MyConfig;
import basashi.hpview.event.McEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(ModCommon.MOD_ID)
public class HPViewer {
	public static boolean s_bUpdateMessageSent = false;
	public static String s_sUpdateMessage="";

	public static Tools tool = new Tools();
	public static int s_nTransparency;
	public static McEventHandler McEvent;


    public HPViewer() {
    	ModLoadingContext.get().
        registerConfig(
        		net.minecraftforge.fml.config.ModConfig.Type.COMMON,
        		MyConfig.spec);

    	//event_instance = new EventHook();
        McEvent = new McEventHandler();
    	MinecraftForge.EVENT_BUS.register(McEvent);
    	tool.RegisterRenders();
    	FMLPaths.CONFIGDIR.toString();
    }
}
