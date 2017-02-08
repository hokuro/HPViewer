package basashi.hpview.config;

import java.awt.RenderingHints;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigValue {
	private final static ModConfig config = new ModConfig();;
	//private static String formattedDIColor = "FFAA00";
//	private static String formattedHealColor = "00FF00";
	public static RenderingHints hints;

	public static void init(FMLPreInitializationEvent event){
		ModConfig.config.init(new Class<?>[]{General.class}, event);
//		//formattedDIColor = Integer.toHexString(General.Color);
//		formattedHealColor = Integer.toHexString(General.healColor);
	}

	public static File CONFIG_FILE(){
		return config.ConfigFile();
	}

	public static void reloadConfig(){
		if( ModConfig.config.reloadConfig()){}
	}
	public static class General{
//
//
//		@ConfigProperty(comment="This sets how powerful the initial bounce is. All positive numbers accepted, 1.5 is default.")
//		public static float BounceStrength = 1.5F;
//		@ConfigProperty(comment="The Currently Selected Skin Path.")
//		public static String selectedSkin = "/DITextures/Default/";
//		@ConfigProperty(comment="Apply the Scale Smoothing filter to the font. Accepts any number between 2.0 and 4.0. All other numbers disable the filter. Defaults to 0.0(disabled). The Scale filter induces a smoother font, but can introduce fuzziness as a consequence. High values may increase startup time significantly when applied to high resolution fonts, or run java out of memory entirely.")
//		public static float ScaleFilter = 0.0F;
//		@ConfigProperty(comment="Number between 1.0 and 0.0 that controls how transparent the digits are. 1.0 means no transparency.")
//		public static float transparency = 1.0F;
//		@ConfigProperty(comment="The color of the digits. Must use a 3 byte or 4 byte(With alpha included) RGB value here, also known as html notation. Defaults to FFFF00(Yellow).")
//		public static int Color = 16755200;
//
//		public static boolean CustomFont = true;
//		@ConfigProperty(comment="This sets how far to send damage alerts to clients.")
//		public static int packetrange = 30;
//		@ConfigProperty(comment="Set to false to Disable.")
//		public static boolean alwaysRender = false;
//
//
//		@ConfigProperty(comment="How long the Portrait will stay after you are no longer targetting a mob. -1 to last forever or until the mob is unloaded.")
//		public static boolean enablePotionEffects = true;
//
//
//
//
//
		@ConfigProperty(comment="When true, mobs in the portrait are locked in place. When false, they turn the same same as they are turning in the world.")
		public static boolean lockPosition = true;
		@ConfigProperty(comment="Use the Fancy Skin when drawing the gui. Set to false to use the old one.")
		public static boolean skinnedPortrait = true;
//
//
		@ConfigProperty(comment="The size of the digits that bounce off mobs, defaults to 3.0")
		public static float Size = 3.0F;
		@ConfigProperty(comment="How long the Portrait will stay after you are no longer targetting a mob. -1 to last forever or until the mob is unloaded.")
		public static int portraitLifetime = 160;
		@ConfigProperty(comment=" Set to false to Disable.")
		public static boolean portraitEnabled = true;
		@ConfigProperty(comment="Set this to false to disable damage pop offs. Typically Set using the in game gui.('.' Period is the default keybinding)")
		public static boolean popOffsEnabled = true;
		@ConfigProperty(comment="The range that mouse overing mobs will display their health.")
		public static int mouseoverRange = 30;
		@ConfigProperty(comment="This is typically set from the repositioning Gui in game, but added here for modpacks.")
		public static int locX = 15;
		@ConfigProperty(comment="This is typically set from the repositioning Gui in game, but added here for modpacks.")
		public static int locY = 15;
		@ConfigProperty(comment="How long the damage indicator lasts before disappearing. Defaults to 12. Decimals not accepted, whole numbers only.")
		public static int Lifespan = 12;
		@ConfigProperty(comment="The color of the digits on heals. Must use a 3 byte or 4 byte(With alpha included) RGB value here, also known as html notation. Defaults to 00FF00(Green).")
		public static int healColor = 65280;
		@ConfigProperty(comment="Change the size of the portrait preview.")
		public static float guiScale = 0.76F;
		@ConfigProperty(comment="Change this to change the speed that the damage indicators fall, low numbers prevent falling numbers entirely. All positive numbers accepted, 0.8 is default.")
		public static float Gravity = 0.8F;
		@ConfigProperty(comment="Should the debug window(F3) Hide the portrait window.")
		public static boolean DebugHidesWindow = true;
	}

	private RenderingHints populateHints() {
		Map hintsMap = new HashMap();
		hintsMap.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		hintsMap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		return new RenderingHints(hintsMap);
	}
}
