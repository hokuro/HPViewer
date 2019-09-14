package basashi.hpview.key;

import basashi.hpview.core.ModCommon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MessageKeyHandler implements IMessageHandler<MessageKey, IMessage> {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModCommon.MOD_CHANEL);

	public static void init(){
		INSTANCE.registerMessage(MessageKeyHandler.class, MessageKey.class, 0, Side.SERVER);
	}

	  @Override
	    public IMessage onMessage(MessageKey message, MessageContext ctx) {
	        PlayerEntity entityPlayer = ctx.getServerHandler().playerEntity;
	        //受け取ったMessageクラスのkey変数の数字をチャットに出力

	        entityPlayer.addChatComponentMessage(new ChatComponentText(String.format("Received byte %d", message.key)));
	        return null;
	    }
}
