package basashi.hpview.texture;

public enum EnumSkinPart {
	FRAMENAME(null, "/DITextures/Default/DIFramSkin.png"),
	FRAMEID(null, null),
	TYPEICONSNAME(null,"/DITextures/Default/DITypeIcons.png"),
	TYPEICONSID(null, null),
	DAMAGENAME(null,"/DITextures/Default/damage.png"),
	DAMAGEID(null, null),
	HEALTHNAME(null,"/DITextures/Default/health.png"),
	HEALTHID(null, null),
	BACKGROUNDNAME(null,"/DITextures/Default/background.png"),
	BACKGROUNDID(null, null),
	NAMEPLATENAME(null,"/DITextures/Default/NamePlate.png"),
	NAMEPLATEID(null,null),
	LEFTPOTIONNAME(null,"/DITextures/Default/leftPotions.png"),
	LEFTPOTIONID(null,null),
	RIGHTPOTIONNAME(null,"/DITextures/Default/rightPotions.png"),
	RIGHTPOTIONID(null, null),
	CENTERPOTIONNAME(null,"/DITextures/Default/centerPotions.png"),
	CENTERPOTIONID(null,null),
	ORDERING(null,Ordering.values()),
	CONFIGHEALTHBARHEIGHT("HealthBarHeight",Integer.valueOf(17)),
	CONFIGHEALTHBARWIDTH("HealthBarWidth",Integer.valueOf(112)),
	CONFIGHEALTHBARX("HealthBarXOffset",Integer.valueOf(49)),
	CONFIGHEALTHBARY("HealthBarYOffset",Integer.valueOf(13)),
	CONFIGFRAMEHEIGHT("FrameHeight",Integer.valueOf(64)),
	CONFIGFRAMEWIDTH("FrameWidth",Integer.valueOf(178)),
	CONFIGFRAMEX("FrameXOffset",Integer.valueOf(-15)),
	CONFIGFRAMEY("FrameYOffset",Integer.valueOf(-5)),
	CONFIGBACKGROUNDHEIGHT("BackgroundHeight",Integer.valueOf(51)),
	CONFIGBACKGROUNDWIDTH("BackgroundWidth",Integer.valueOf(49)),
	CONFIGBACKGROUNDX("BackgroundXOffset",Integer.valueOf(-4)),
	CONFIGBACKGROUNDY("BackgroundYOffset",Integer.valueOf(-4)),
	CONFIGNAMEPLATEHEIGHT("NamePlateHeight",Integer.valueOf(12)),
	CONFIGNAMEPLATEWIDTH("NamePlateWidth",Integer.valueOf(112)),
	CONFIGNAMEPLATEX("NamePlateXOffset",Integer.valueOf(49)),
	CONFIGNAMEPLATEY("NamePlateYOffset",Integer.valueOf(0)),
	CONFIGMOBTYPEHEIGHT("MobTypeSizeHeight",Integer.valueOf(18)),
	CONFIGMOBTYPEWIDTH("MobTypeSizeWidth",Integer.valueOf(18)),
	CONFIGMOBTYPEX("MobTypeOffsetX",Integer.valueOf(-13)),
	CONFIGMOBTYPEY("MobTypeOffsetY",Integer.valueOf(39)),
	CONFIGPOTIONBOXHEIGHT("PotionBoxHeight",Integer.valueOf(22)),
	CONFIGPOTIONBOXWIDTH("PotionBoxSidesWidth",Integer.valueOf(4)),
	CONFIGPOTIONBOXX("PotionBoxOffsetX",Integer.valueOf(48)),
	CONFIGPOTIONBOXY("PotionBoxOffsetY",Integer.valueOf(31)),
	CONFIGMOBPREVIEWX("MobPreviewOffsetX",Integer.valueOf(-4)),
	CONFIGMOBPREVIEWY("MobPreviewOffsetY",Integer.valueOf(-3)),
	CONFIGTEXTEXTNAMECOLOR("NameTextColor","FFFFFF"),
	CONFIGTEXTEXTHEALTHCOLOR("HealthTextColor","FFFFFF"),
	CONFIGDISPLAYNM("SkinName","Clean"),
	CONFIGAUTHOR("Author","rich1051414"),
	INTERNAL(null,"/DITextures/Default/");

	private final Object ext;
	private final Object extDefault;

	private EnumSkinPart(Object extended, Object configDefault) {
		this.ext = extended;
		this.extDefault = configDefault;
	}

	public final Object getExtended() {
		return this.ext;
	}

	public final Object getConfigDefault() {
		return this.extDefault;
	}
}
