package basashi.hpview.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random ;

import basashi.hpview.config.ConfigValue;
import basashi.hpview.core.HPViewer;
import basashi.hpview.core.Tools;
import basashi.hpview.core.log.ModLog;
import basashi.hpview.gui.GuiOverLayHPView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class McEventHandler{
	public static int LastTargeted = 0;
	private static Minecraft mc = Minecraft.func_71410_x();
	public static Map<Integer, Collection> potionEffects = new HashMap();
	public static boolean searched = false;
	public static int tick = 0;
	public static Map<Class, List<Class>> entityParts = new HashMap();
	public static int dim = -2;
	private static boolean doParticles = false;
	private static HashMap<Integer, Integer> healths = new HashMap();
	private GuiOverLayHPView view = null;

	private long time = -1L;
	private static final java.util.Random rnd = new Random();
	public McEventHandler() {
	}

	@SubscribeEvent
	public void gameOverlayEvent(RenderGameOverlayEvent evt) {
		if (ConfigValue.General.portraitEnabled) {
			if ((evt.getType() == RenderGameOverlayEvent.ElementType.ALL)
					&& ((evt instanceof RenderGameOverlayEvent.Post))) {
				try {
					if (mc.field_71439_g != null) {
						if (mc.field_71474_y.field_74319_N) {
							LastTargeted = 0;
							return;
						}
						if ((mc.field_71474_y.field_74330_P) && (ConfigValue.General.DebugHidesWindow)) {
							LastTargeted = 0;
							return;
						}
						if ((mc.field_71462_r != null)
								&& (!(Minecraft.func_71410_x().field_71462_r instanceof GuiChat))) {
							LastTargeted = 0;
							return;
						}
						if (!searched) {
							ModLog.log().fatal("Damage Indicators performing entity search...");
							HPViewer.tool.scanforEntities();
							searched = true;
						}
						try {
							if (view ==null){
								view = new GuiOverLayHPView(mc);
							}
							updateMouseOversSkinned(evt.getPartialTicks());
							view.renderHPViwe();
						} catch (Throwable ex) {
							ex.printStackTrace();
						}
					}
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static EntityLivingBase getClosestLivingEntity(double parDistance, float tick) {
		try {
			EntityLivingBase viewEntity = (mc.func_175606_aa() instanceof EntityLivingBase)?(EntityLivingBase)mc.func_175606_aa():null;
			EntityLivingBase Return = null;
			double closest = parDistance;
			Vec3d playerPosition;
			Vec3d lookFarCoord;

			// 見ているエンティティがあるかどうか
			if ((viewEntity != null) && (viewEntity instanceof EntityLivingBase)) {
				World worldObj = viewEntity.field_70170_p;
				RayTraceResult objectMouseOver = viewEntity.func_174822_a(parDistance, tick);

				// プレイヤーの位置
				playerPosition = new Vec3d(viewEntity.func_180425_c());
//				if (objectMouseOver != null) {
//					parDistance = getClosestSolidWall(viewEntity, playerPosition, tick, parDistance, 0, 0.0D);
//				}
				// 視線ベクトル
				Vec3d dirVec = viewEntity.func_70040_Z();
				// 視線座標
				lookFarCoord = playerPosition.func_72441_c(dirVec.field_72450_a * parDistance, dirVec.field_72448_b * parDistance, dirVec.field_72449_c * parDistance);

				// 視線が当たっているMobを取得
				List<EntityLivingBase> targettedEntities = worldObj.func_72872_a(EntityLivingBase.class,
						 viewEntity.func_174813_aQ().func_72321_a(dirVec.field_72450_a * parDistance, dirVec.field_72448_b * parDistance, dirVec.field_72449_c * parDistance));
				// 自分自身はMobから外す
				targettedEntities.remove(viewEntity);
				for (EntityLivingBase targettedEntity : targettedEntities) {
					if (targettedEntity != null) {
						// エンティティとの距離
						double precheck = viewEntity.func_70032_d(targettedEntity);
						// 視線が当たっているかどうか確認
						RayTraceResult mopElIntercept = targettedEntity.func_174813_aQ().func_72327_a(playerPosition.func_72441_c(0, viewEntity.func_70047_e(), 0),lookFarCoord);
						if ((mopElIntercept != null) && (precheck < closest)) {
							Return = targettedEntity;
							closest = precheck;
							ModLog.log().debug("Hit " + Return.func_70005_c_());
						}
					}
				}
			}
			if ((Return != null) && (!Return.field_70128_L) && (!Return.func_82150_aj())) {
				return Return;
			}
		} catch (Throwable ex) {
		}
		return null;
	}


	public void updateMouseOversSkinned(float Tick) {
		try {
			if (mc.field_71439_g != null) {
				EntityLivingBase el = getClosestLivingEntity(ConfigValue.General.mouseoverRange, Tick);
				view.setViewEntity(el);
		}
		}catch(Exception ex){}
	}

	  //Called when the client ticks.
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		  EntityPlayer p;
			if ((Minecraft.func_71410_x().field_71462_r == null) || ((Minecraft.func_71410_x().field_71462_r instanceof GuiChat))) {
				p = Minecraft.func_71410_x().field_71439_g;
				if ((p != null) && (p.field_70170_p != null)) {
					World world = p.field_70170_p;
					AxisAlignedBB bb = new AxisAlignedBB(
							p.field_70165_t - ConfigValue.General.mouseoverRange,
							p.field_70163_u - ConfigValue.General.mouseoverRange, p.field_70161_v - ConfigValue.General.mouseoverRange,
							p.field_70165_t + ConfigValue.General.mouseoverRange, p.field_70163_u + ConfigValue.General.mouseoverRange,
							p.field_70161_v + ConfigValue.General.mouseoverRange);
					List<EntityLivingBase> entityList = world.func_72872_a(EntityLivingBase.class, bb);
					if ((entityList != null) && (entityList.size() > 0)) {
						for (EntityLivingBase el : entityList) {
							if (el != null) {
								if (((el instanceof EntityPlayer)) &&
										(Tools.donators.contains(((EntityPlayer) el).func_70005_c_().toLowerCase())) &&
										((el != p) || (Minecraft.func_71410_x().field_71474_y.field_74320_O != 0))) {
									double darkness = rnd.nextDouble() / 4.0D;
								}
								if (ConfigValue.General.popOffsEnabled) {
									int currentHealth = MathHelper.func_76141_d((float) Math.ceil(el.func_110143_aJ()));
									if (healths.containsKey(Integer.valueOf(el.func_145782_y()))) {
										int lastHealth = ((Integer) healths.get(Integer.valueOf(el.func_145782_y()))).intValue();
										if ((lastHealth != 0) && (lastHealth - currentHealth != 0)) {
											int damage = lastHealth - currentHealth;
										}
									}
									healths.put(Integer.valueOf(el.func_145782_y()), Integer.valueOf(currentHealth));
								}
							}
						}
					}
				}
			}
	 }
}
