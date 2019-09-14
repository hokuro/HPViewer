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
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
								&& (!(Minecraft.getInstance().currentScreen instanceof ChatScreen))) {
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

	public static LivingEntity getClosestLivingEntity(double parDistance, float tick) {
		try {
			LivingEntity viewEntity = (mc.getRenderViewEntity() instanceof LivingEntity)?(LivingEntity)mc.getRenderViewEntity():null;
			LivingEntity Return = null;
			double closest = parDistance;
			Vec3d playerPosition;
			Vec3d lookFarCoord;

			// 見ているエンティティがあるかどうか
			if ((viewEntity != null) && (viewEntity instanceof LivingEntity)) {
				World worldObj = viewEntity.world;//　　.worldObj;
				playerPosition = viewEntity.getPositionVec();
				// 視線ベクトル
				Vec3d dirVec = viewEntity.getLookVec();
				// 視線座標
				lookFarCoord = playerPosition.add(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance);

				// 視線が当たっているMobを取得
				List<LivingEntity> targettedEntities = worldObj.getEntitiesWithinAABB(LivingEntity.class,
						 viewEntity.getBoundingBox().expand(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance)
						 .expand(-1 * dirVec.x * parDistance, -1 * dirVec.y * parDistance, -1 * dirVec.z * parDistance));
				// 自分自身はMobから外す
				targettedEntities.remove(viewEntity);
				for (LivingEntity targettedEntity : targettedEntities) {
					if (targettedEntity != null) {
						// エンティティとの距離
						double precheck = viewEntity.getDistance(targettedEntity);
						// 視線が当たっているかどうか確認
						boolean isView = viewEntity.canEntityBeSeen(targettedEntity);
						precheck = viewEntity.getDistance(targettedEntity);
						if (isView && closest > precheck) {
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
				LivingEntity el = getClosestLivingEntity(MyConfig._general.mouseoverRange.get(), Tick);
				view.setViewEntity(el);
		}
		}catch(Exception ex){}
	}

	  //Called when the client ticks.
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		  PlayerEntity p;
			if ((Minecraft.getInstance().currentScreen == null) || ((Minecraft.getInstance().currentScreen instanceof ChatScreen))) {
				p = Minecraft.getInstance().player;
				if ((p != null) && (p.world != null)) {
					World world = p.world;
					AxisAlignedBB bb = new AxisAlignedBB(
							p.posX - MyConfig._general.mouseoverRange.get(),
							p.posY - MyConfig._general.mouseoverRange.get(), p.posZ - MyConfig._general.mouseoverRange.get(),
							p.posX + MyConfig._general.mouseoverRange.get(), p.posY + MyConfig._general.mouseoverRange.get(),
							p.posZ + MyConfig._general.mouseoverRange.get());
					List<LivingEntity> entityList = world.getEntitiesWithinAABB(LivingEntity.class, bb);
					if ((entityList != null) && (entityList.size() > 0)) {
						for (LivingEntity el : entityList) {
							if (el != null) {
								if (((el instanceof PlayerEntity)) &&
										(Tools.donators.contains(((PlayerEntity) el).getName().toString().toLowerCase())) &&
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
