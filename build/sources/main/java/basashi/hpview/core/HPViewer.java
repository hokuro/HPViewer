package basashi.hpview.core;

import basashi.hpview.config.ConfigValue;
import basashi.hpview.core.log.ModLog;
import basashi.hpview.event.McEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ModCommon.MOD_ID, name = ModCommon.MOD_NAME, version = ModCommon.MOD_VERSION)
public class HPViewer {
	public static boolean s_bUpdateMessageSent = false;
	public static String s_sUpdateMessage="";

	@Mod.Instance(ModCommon.MOD_ID)
	public static HPViewer instance;
	@SidedProxy(clientSide = ModCommon.MOD_PACKAGE + ModCommon.MOD_CLIENT_SIDE, serverSide = ModCommon.MOD_PACKAGE + ModCommon.MOD_SERVER_SIDE)
	public static CommonProxy proxy;

	public static Tools tool = new Tools();
	public static int s_nTransparency;
	public static McEventHandler McEvent;


	public HPViewer(){}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		ConfigValue.init(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		McEvent = new McEventHandler();

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(McEvent);
		}
		tool.RegisterRenders();
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event){
		ModLog.log().info("start");
		ModLog.log().info("Server on longer requirs this mod to function");
		//MinecraftServer.getServer().logWarning("Server no longer requires Damage Indicators to function client side!");
	}
}
