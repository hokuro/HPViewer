package basashi.hpview.client;

import basashi.hpview.core.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ClientProxy extends CommonProxy {
	public EntityPlayer getEntityPlayerInstance(){
		return Minecraft.func_71410_x().field_71439_g;
	}
}
