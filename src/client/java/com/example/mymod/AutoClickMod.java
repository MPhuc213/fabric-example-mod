package com.example.mymod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

public class AutoClickMod implements ClientModInitializer {
    private final VHT vht = new VHT();
    private int commandStep = 0; // Biến trạng thái để theo dõi lệnh nào đang chạy

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MinecraftClient mc = MinecraftClient.getInstance();

            if (mc.player == null || !vht.isPlayerInSafeZone()) {
                return;
            }

           
            // Xử lý phần click slot nếu màn hình hiện tại là HandledScreen
            Screen currentScreen = mc.currentScreen;
            if (!(currentScreen instanceof HandledScreen<?> handledScreen)) {
                return;
            }

            // Kiểm tra số slot
            if (handledScreen.getScreenHandler().slots.size() <= 53) {
                return;
            }

            // Lấy slot index 12 và kiểm tra item
            Slot slot = handledScreen.getScreenHandler().slots.get(12);
            ItemStack stack = slot.getStack();
            Identifier itemId = Registries.ITEM.getId(stack.getItem());

            // Nếu slot chứa item "minecraft:player_head", thực hiện click
            if ("minecraft:player_head".equals(itemId.toString())) {
                mc.interactionManager.clickSlot(
                    handledScreen.getScreenHandler().syncId,
                    12, 
                    0,  
                    SlotActionType.PICKUP,
                    mc.player
                );
            }
        });
    }
}
