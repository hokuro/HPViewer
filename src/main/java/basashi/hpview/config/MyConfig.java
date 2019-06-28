package basashi.hpview.config;

import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public class MyConfig {

	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final General _general = new General(BUILDER);
	public static final ForgeConfigSpec spec = BUILDER.build();

//	private final static ModConfig config = new ModConfig();;
	//private static String formattedDIColor = "FFAA00";
//	private static String formattedHealColor = "00FF00";
	public static RenderingHints hints;

//	public static void init(FMLPreInitializationEvent event){
//		ModConfig.config.init(new Class<?>[]{General.class}, event);
////		//formattedDIColor = Integer.toHexString(General.Color);
////		formattedHealColor = Integer.toHexString(General.healColor);
//	}

//	public static File CONFIG_FILE(){
//		return config.ConfigFile();
//	}
//
//	public static void reloadConfig(){
//		if( ModConfig.config.reloadConfig()){}
//	}
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
		public final ForgeConfigSpec.ConfigValue<Boolean> lockPosition;
		public final ForgeConfigSpec.ConfigValue<Boolean> skinnedPortrait;
		public final ForgeConfigSpec.ConfigValue<Float> Size;
		public final ForgeConfigSpec.ConfigValue<Integer> portraitLifetime;
		public final ForgeConfigSpec.ConfigValue<Boolean> portraitEnabled;
		public final ForgeConfigSpec.ConfigValue<Boolean> popOffsEnabled;
		public final ForgeConfigSpec.ConfigValue<Integer> mouseoverRange;
		public final ForgeConfigSpec.ConfigValue<Integer> locX;
		public final ForgeConfigSpec.ConfigValue<Integer> locY;
		public final ForgeConfigSpec.ConfigValue<Integer>  Lifespan;
		public final ForgeConfigSpec.ConfigValue<Integer> healColor;
		public final ForgeConfigSpec.ConfigValue<Float> guiScale;
		public final ForgeConfigSpec.ConfigValue<Float>  Gravity;
		public final ForgeConfigSpec.ConfigValue<Boolean> DebugHidesWindow;

		public General(Builder builder) {
			builder.push("General");
			lockPosition = builder.
					comment("When true, mobs in the portrait are locked in place. When false, they turn the same same as they are turning in the world.").
					define("lockPosition", true);
				skinnedPortrait = builder.
					comment("Use the Fancy Skin when drawing the gui. Set to false to use the old one.").
					define("skinnedPortrait", true);
				Size = builder.
					comment("The size of the digits that bounce off mobs, defaults to 3.0").
					define("Size",3.0F);
				portraitLifetime = builder.
					comment("How long the Portrait will stay after you are no longer targetting a mob. -1 to last forever or until the mob is unloaded.").
					define("portraitLifetime", 160);
				portraitEnabled = builder.
					comment(" Set to false to Disable.").
					define("portraitEnabled",true);
				popOffsEnabled = builder.
					comment("Set this to false to disable damage pop offs. Typically Set using the in game gui.('.' Period is the default keybinding)").
					define("popOffsEnabled",true);
				mouseoverRange = builder.
					comment("The range that mouse overing mobs will display their health.").
					define("mouseoverRange",30);
				locX = builder.
					comment("This is typically set from the repositioning Gui in game, but added here for modpacks.").
					define("locX",15);
				locY = builder.
					comment("This is typically set from the repositioning Gui in game, but added here for modpacks.").
					define("locY",15);
				Lifespan = builder
					.comment("How long the damage indicator lasts before disappearing. Defaults to 12. Decimals not accepted, whole numbers only.").
					define("Lifespan",12);
				healColor = builder.
					comment("The color of the digits on heals. Must use a 3 byte or 4 byte(With alpha included) RGB value here, also known as html notation. Defaults to 00FF00(Green).").
					define("healColor",65280);
				guiScale = builder.
					comment("Change the size of the portrait preview.").
					define("guiScale",0.76F);
				Gravity = builder.
					comment("Change this to change the speed that the damage indicators fall, low numbers prevent falling numbers entirely. All positive numbers accepted, 0.8 is default.").
					define("Gravity",0.8F);
				DebugHidesWindow = builder.
					comment("Should the debug window(F3) Hide the portrait window.").
					define("DebugHidesWindow",true);
			builder.pop();
		}
	}

	private RenderingHints populateHints() {
		Map hintsMap = new HashMap();
		hintsMap.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		hintsMap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		return new RenderingHints(hintsMap);
	}
}
