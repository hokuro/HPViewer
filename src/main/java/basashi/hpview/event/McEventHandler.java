package basashi.hpview.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random ;

import basashi.hpview.config.MyConfig;
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
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class McEventHandler{
	public static int LastTargeted = 0;
	private static Minecraft mc = Minecraft.getInstance();
	public static Map<Integer, Collection> potionEffects = new HashMap<Integer, Collection>();
	public static boolean searched = false;
	public static int tick = 0;
	public static Map<Class, List<Class>> entityParts = new HashMap<Class, List<Class>>();
	public static int dim = -2;
	private static boolean doParticles = false;
	private static HashMap<Integer, Integer> healths = new HashMap<Integer, Integer>();
	private GuiOverLayHPView view = null;

	private long time = -1L;
	private static final java.util.Random rnd = new Random();
	public McEventHandler() {
	}

	@SubscribeEvent
	public void gameOverlayEvent(RenderGameOverlayEvent evt) {
		if (MyConfig._general.portraitEnabled.get()) {
			if ((evt.getType() == RenderGameOverlayEvent.ElementType.ALL)
					&& ((evt instanceof RenderGameOverlayEvent.Post))) {
				try {
					if (mc.player != null) {
						if (mc.gameSettings.hideGUI) {
							LastTargeted = 0;
							return;
						}
						if ((mc.gameSettings.showDebugInfo) && (MyConfig._general.DebugHidesWindow.get())) {
							LastTargeted = 0;
							return;
						}
						if ((mc.currentScreen != null)
								&& (!(Minecraft.getInstance().currentScreen instanceof GuiChat))) {
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
			EntityLivingBase viewEntity = (mc.getRenderViewEntity() instanceof EntityLivingBase)?(EntityLivingBase)mc.getRenderViewEntity():null;
			EntityLivingBase Return = null;
			double closest = parDistance;
			Vec3d playerPosition;
			Vec3d lookFarCoord;

			// 見ているエンティティがあるかどうか
			if ((viewEntity != null) && (viewEntity instanceof EntityLivingBase)) {
				World worldObj = viewEntity.world;//　　.worldObj;
				RayTraceResult objectMouseOver = viewEntity.rayTrace(parDistance, tick, RayTraceFluidMode.ALWAYS);

				// プレイヤーの位置
				playerPosition = new Vec3d(viewEntity.getPosition());
//				if (objectMouseOver != null) {
//					parDistance = getClosestSolidWall(viewEntity, playerPosition, tick, parDistance, 0, 0.0D);
//				}
				// 視線ベクトル
				Vec3d dirVec = viewEntity.getLookVec();
				// 視線座標
				lookFarCoord = playerPosition.add(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance);

				// 視線が当たっているMobを取得
				List<EntityLivingBase> targettedEntities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
						 viewEntity.getBoundingBox().expand(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance));
				// 自分自身はMobから外す
				targettedEntities.remove(viewEntity);
				for (EntityLivingBase targettedEntity : targettedEntities) {
					if (targettedEntity != null) {
						// エンティティとの距離
						double precheck = viewEntity.getDistance(targettedEntity);
						// 視線が当たっているかどうか確認
						RayTraceResult mopElIntercept = targettedEntity.getBoundingBox().calculateIntercept(playerPosition.add(0, viewEntity.getEyeHeight(), 0),lookFarCoord);
						if ((mopElIntercept != null) && (precheck < closest)) {
							Return = targettedEntity;
							closest = precheck;
							ModLog.log().debug("Hit " + Return.getName());
						}
					}
				}
			}
			if ((Return != null) && (Return.isAlive()) && (!Return.isInvisible())) {
				return Return;
			}
		} catch (Throwable ex) {
		}
		return null;
	}


	public void updateMouseOversSkinned(float Tick) {
		try {
			if (mc.player != null) {
				EntityLivingBase el = getClosestLivingEntity(MyConfig._general.mouseoverRange.get(), Tick);
				view.setViewEntity(el);
		}
		}catch(Exception ex){}
	}

	  //Called when the client ticks.
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		  EntityPlayer p;
			if ((Minecraft.getInstance().currentScreen == null) || ((Minecraft.getInstance().currentScreen instanceof GuiChat))) {
				p = Minecraft.getInstance().player;
				if ((p != null) && (p.world != null)) {
					World world = p.world;
					AxisAlignedBB bb = new AxisAlignedBB(
							p.posX - MyConfig._general.mouseoverRange.get(),
							p.posY - MyConfig._general.mouseoverRange.get(), p.posZ - MyConfig._general.mouseoverRange.get(),
							p.posX + MyConfig._general.mouseoverRange.get(), p.posY + MyConfig._general.mouseoverRange.get(),
							p.posZ + MyConfig._general.mouseoverRange.get());
					List<EntityLivingBase> entityList = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
					if ((entityList != null) && (entityList.size() > 0)) {
						for (EntityLivingBase el : entityList) {
							if (el != null) {
								if (((el instanceof EntityPlayer)) &&
										(Tools.donators.contains(((EntityPlayer) el).getName().toString().toLowerCase())) &&
										((el != p) || (Minecraft.getInstance().gameSettings.thirdPersonView != 0))) {
									double darkness = rnd.nextDouble() / 4.0D;
								}
								if (MyConfig._general.popOffsEnabled.get()) {
									int currentHealth = MathHelper.floor((float) Math.ceil(el.getHealth()));
									if (healths.containsKey(Integer.valueOf(el.getEntityId()))) {
										int lastHealth = ((Integer) healths.get(Integer.valueOf(el.getEntityId()))).intValue();
										if ((lastHealth != 0) && (lastHealth - currentHealth != 0)) {
											int damage = lastHealth - currentHealth;
										}
									}
									healths.put(Integer.valueOf(el.getEntityId()), Integer.valueOf(currentHealth));
								}
							}
						}
					}
				}
			}
	 }
}
