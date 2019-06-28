package basashi.hpview.core;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class CommonProxy {
	public EntityPlayer getEntityPlayerInstance(){return null;}

	static class Client extends CommonProxy{
		public EntityPlayer getEntityPlayerInstance(){
			return Minecraft.getInstance().player;
		}
	}

	static class Server extends CommonProxy{
		public EntityPlayer getEntityPlayerInstance(){return null;}
	}
}
