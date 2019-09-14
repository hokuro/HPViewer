package basashi.hpview.core;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public class CommonProxy {
	public PlayerEntity getPlayerEntityInstance(){return null;}

	static class Client extends CommonProxy{
		public PlayerEntity getPlayerEntityInstance(){
			return Minecraft.getInstance().player;
		}
	}

	static class Server extends CommonProxy{
		public PlayerEntity getPlayerEntityInstance(){return null;}
	}
}
