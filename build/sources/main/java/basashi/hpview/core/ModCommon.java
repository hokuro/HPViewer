package basashi.hpview.core;

public final class ModCommon {
	// デバッグモードかどうか
	public static boolean isDebug = false;

	// モッドID
	public static final String MOD_ID = "hpview";
	// モッド名
	public static final String MOD_NAME = "HPViewer";
	public static final String MOD_PACKAGE = "basashi.hpview";
	public static final String MOD_CLIENT_SIDE = ".client.ClientProxy";
	public static final String MOD_SERVER_SIDE = ".core.CommonProxy";
	public static final String MOD_FACTRY = ".client.config.HPViewerGuiFactory";
	// モッドバージョン
	public static final String MOD_VERSION = "@VERSION@";
	// コンフィグファイル名
	public static final String MOD_CONFIG_FILE = "";
	// コンフィグ
	public static final String MOD_CONFIG_LANG = "";
	// コンフィグリロード間隔
	public static final long MOD_CONFIG_RELOAD = 500L;

	// コンフィグ カテゴリー general
	public static final String MOD_CONFIG_CAT_GENELAL = "general";
	public static final String MOD_CHANEL ="Mod_Channel_HPViewer";
}
