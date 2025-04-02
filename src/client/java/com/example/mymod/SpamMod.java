package com.example.mymod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;


public class SpamMod implements ClientModInitializer {
    // Định nghĩa vùng an toàn (safe zone)
    private final VHT vht = new VHT();
    private final ModStateManager modStateManager = new ModStateManager();
    // Biến bật mod
    private boolean isModEnabled = true;
    // Delay ticks để gửi lệnh /home khi ở tọa độ mục tiêu
    private int delayTicks = 20; 

    // Tọa độ home
    private double homeX = 0;
    private double homeY = 0;
    private double homeZ = 0;
    // Khi người chơi rời khỏi tọa độ home, flag này được bật và đếm ngược 1 phút (1200 ticks)
    private boolean waitingToReturn = false;
    private int tickCounter = 0;
    private static final int WAIT_TICKS = 1200; // 1 phút (1200 ticks)
    // Ngưỡng khoảng cách xác định người chơi đã rời khỏi tọa độ home
    private static final double LEAVE_THRESHOLD = 1.0;

    private boolean hasSentChat = false;
    private boolean VHT = false; // Biến đánh dấu ở tọa độ mục tiêu

    @Override
    public void onInitializeClient() {

  
        // Đăng ký lệnh togglehome để bật/tắt mod
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("togglehome")
                .executes(context -> {
                    isModEnabled = !isModEnabled;
                    if (isModEnabled) {
                        waitingToReturn = false;
                        delayTicks = 20; // Reset delay khi bật lại mod
                        hasSentChat = false;
                        context.getSource().sendFeedback(Text.literal("Set home toggle §2on"));
                    } else {
                        context.getSource().sendFeedback(Text.literal("Set home toggle §4off"));
                    }
                    return 1;
                }));
        });

          


        // Lắng nghe sự kiện tick của client
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Kiểm tra client.player để tránh NullPointerException
            if (!isModEnabled || client.player == null) return;
            modStateManager.updateModState();

            if (!modStateManager.isModEnabled()) {
                return; // Nếu mod không được kích hoạt, thoát
            }
            // Nếu ở safe zone thì reset các cờ và không xử lý tiếp
            if (vht.isPlayerInSafeZone()) {
                hasSentChat = false;
                VHT = false;
                return;
            }

            // Cập nhật trạng thái VHT: nếu ở tọa độ mục tiêu thì true, ngược lại false
            if (vht.isAtTargetPosition()) {
                VHT = true;
            } 

            // Nếu không đang trong trạng thái quay về (waitingToReturn)
            if (!waitingToReturn) {
                // Nếu ở tọa độ mục tiêu và chưa gửi lệnh /home
                if (VHT && !hasSentChat) {
                    delayTicks--;
                    if (delayTicks <= 0) {
                        client.player.networkHandler.sendChatCommand("home");
                        homeX = client.player.getX();
                        homeY = client.player.getY();
                        homeZ = client.player.getZ();
                        hasSentChat = true;
                        // Sau khi gửi lệnh, bạn có thể giữ delayTicks = 0 hoặc reset nếu cần cho chu kỳ mới
                    }
                } else if (homeCoordinatesSet()) {
                    // Kiểm tra nếu người chơi rời khỏi tọa độ home
                    double currentX = client.player.getX();
                    double currentY = client.player.getY();
                    double currentZ = client.player.getZ();
                    double distance = Math.sqrt(
                            Math.pow(currentX - homeX, 2) +
                            Math.pow(currentY - homeY, 2) +
                            Math.pow(currentZ - homeZ, 2)
                    );
                    if (distance > LEAVE_THRESHOLD) {
                        client.player.sendMessage(Text.literal("§f§lĐã rời khỏi tọa độ"), false);
                        waitingToReturn = true;
                        tickCounter = WAIT_TICKS;
                    }
                }
            } else {
                // Nếu đang chờ quay lại, đếm ngược tick
                tickCounter--;
                if (tickCounter <= 0) {
                    client.player.networkHandler.sendChatCommand("home");
                    homeX = client.player.getX();
                    homeY = client.player.getY();
                    homeZ = client.player.getZ();
                    waitingToReturn = false;
                    // Reset các cờ cho chu kỳ mới
                    delayTicks = 20;
                    hasSentChat = false;
                }
            }
        });
    }


 

    /**
     * Kiểm tra xem tọa độ home đã được thiết lập hay chưa.
     *
     * @return true nếu homeX, homeY, homeZ khác 0 (giả định ban đầu là 0), ngược lại false.
     */
    private boolean homeCoordinatesSet() {
        return homeX != 0 || homeY != 0 || homeZ != 0;
    }
}
