package com.example.mymod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public class SingleChatMod implements ClientModInitializer {
    private final ModStateManager modStateManager = new ModStateManager();
    private boolean hasSentChat = false;
    private final VHT vht = new VHT();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Kiểm tra nếu client.player chưa được khởi tạo
            if (client.player == null) {
                return;
            }

            // Cập nhật trạng thái mod
            modStateManager.updateModState();
            if (!modStateManager.isModEnabled()) {
                return; // Nếu mod không được kích hoạt, thoát
            }

            // Nếu người chơi ở safe zone, reset cờ gửi tin nhắn
            if (vht.isPlayerInSafeZone()) {
                hasSentChat = false;
            }

            // Xử lý click slot nếu màn hình hiện tại là HandledScreen
            Screen currentScreen = client.currentScreen;
            if (currentScreen instanceof HandledScreen<?> handledScreen) {
                // Kiểm tra số slot (đảm bảo slot index 2 tồn tại)
                if (handledScreen.getScreenHandler().slots.size() < 9) {
                    return;
                }

                Slot slot = handledScreen.getScreenHandler().slots.get(2); // Lấy slot index 2
                ItemStack stack = slot.getStack();
                Identifier itemId = Registries.ITEM.getId(stack.getItem());

                // Nếu slot chứa "minecraft:lime_stained_glass_pane", thực hiện click
                if ("minecraft:lime_stained_glass_pane".equals(itemId.toString())) {
                    client.interactionManager.clickSlot(
                        handledScreen.getScreenHandler().syncId,
                        2,    // slot index
                        0,    // mouse button (0: left click)
                        SlotActionType.PICKUP,
                        client.player
                    );
                }
            }

            // Nếu tin nhắn đã được gửi, không thực hiện thêm
            if (hasSentChat) {
                return;
            }

            // Nếu vị trí người chơi đạt gần tọa độ mục tiêu, gửi tin nhắn và đánh dấu đã gửi
            if (vht.isAtTargetPosition()) {
                hasSentChat = true;
                client.player.networkHandler.sendCommand("autod");
            }
        });
    }
}
