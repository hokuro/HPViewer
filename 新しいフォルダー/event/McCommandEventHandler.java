package basashi.hpview.event;

import basashi.hpview.client.ClientProxy;
import basashi.hpview.key.MessageKey;
import basashi.hpview.key.MessageKeyHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class McCommandEventHandler {
    //キーが“押された時”に呼ばれる。“押しっぱなし”の判定は別途用意する必要あり。
    @SubscribeEvent
    public void KeyHandlingEvent(KeyInputEvent event) {
        if (ClientProxy.keyCode.isPressed()) {
        	MessageKeyHandler.INSTANCE.sendToServer(new MessageKey((byte)1));//1をサーバーに送る。
        }
    }
}
