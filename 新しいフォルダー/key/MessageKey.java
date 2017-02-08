package basashi.hpview.key;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageKey implements IMessage {
	public byte key;

	public MessageKey(){}
	public MessageKey(byte keycode){
		key = keycode;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// TODO 自動生成されたメソッド・スタブ
		key = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// TODO 自動生成されたメソッド・スタブ
		buf.writeByte(key);
	}

}
