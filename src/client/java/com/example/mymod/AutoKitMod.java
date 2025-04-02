package com.example.mymod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;


public class AutoKitMod implements ClientModInitializer {
    private final ModStateManager modStateManager = new ModStateManager();
    // Thời gian chờ 5 phút: 5 phút * 60 giây * 20 tick/giây = 6000 ticks
    private static final int WAIT_TICKS = 2400;
    // Thời gian delay giữa các tin nhắn: 1 giây = 20 ticks
    private static final int MESSAGE_DELAY_TICKS = 40;

    // Bộ đếm tick cho chu kỳ chờ
    private int tickCounter = WAIT_TICKS;
    // Cờ báo hiệu trạng thái gửi tin nhắn
    private boolean isSendingMessages = false;
    // Chỉ số tin nhắn hiện tại
    private int messageIndex = 0;
    // Bộ đếm delay giữa các tin nhắn
    private int messageDelayCounter = MESSAGE_DELAY_TICKS;

    private static boolean ModEnabled = false;

    // Mảng chứa các tin nhắn cần gửi
    private final String[] messages = {
        "kit claim coal",
        "kit claim iron",
        "kit claim gold",
        "kit claim diamond",
        "kit claim emerald",
        "kit claim lazuli",
        "kit claim redstone",
        "kit claim vip",
        "kit claim vipplus",
        "feed",
    };
    private final VHT vht = new VHT();

    @Override
    public void onInitializeClient() {

 
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Chỉ xử lý khi người chơi đã được khởi tạo
            if (client.player == null) {
                return;
            }
            modStateManager.updateModState();

            if (!modStateManager.isModEnabled()) {
                return; // Nếu mod không được kích hoạt, thoát
            }
            if (vht.isAtTargetPosition() && ModEnabled == false) {
                ModEnabled = true;
                client.player.sendMessage(Text.literal("Autokit được kích hoạt thành công"), false);
            }

            // Nếu chưa vào giai đoạn gửi tin nhắn, tiếp tục đếm tick
            if (!isSendingMessages && ModEnabled) {
                tickCounter--;
                if (tickCounter <= 0) {
                    // Bắt đầu gửi tin nhắn
                    isSendingMessages = true;
                    messageIndex = 0;
                    messageDelayCounter = 0; // Gửi tin nhắn ngay tick đầu tiên của giai đoạn gửi
                }
            } else {
                // Đang ở giai đoạn gửi tin nhắn, đếm delay giữa các tin nhắn
                messageDelayCounter--;
                if (messageDelayCounter <= 0) {
                    // Nếu còn tin nhắn, gửi tin nhắn hiện tại
                    if (messageIndex < messages.length) {
                        client.player.networkHandler.sendChatCommand(messages[messageIndex]);
                        messageIndex++;
                        // Reset delay cho tin nhắn tiếp theo
                        messageDelayCounter = MESSAGE_DELAY_TICKS;
                    } else {
                        // Đã gửi hết tin nhắn, reset lại chu kỳ chờ
                        isSendingMessages = false;
                        tickCounter = WAIT_TICKS;
                    }
                }
            } 
        });
    }
}
