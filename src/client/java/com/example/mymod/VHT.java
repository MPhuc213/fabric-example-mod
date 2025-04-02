package com.example.mymod;

import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;

public class VHT {

    private static final double SAFE_MIN = -1;
    private static final double SAFE_MAX = 1;
    private static final double SAFE_Y = 65.00000;

    private static final double TARGET_X = -50.483;
    private static final double TARGET_Y = 43.00000;
    private static final double TARGET_Z = 0.470;
    private static final double TOLERANCE = 0.1; // Dung sai để xác định vị trí gần đúng

    private final MinecraftClient client = MinecraftClient.getInstance();

    public VHT() {
        // Check if the player is null during initialization
        if (client.player == null) {
            // Handle the null player case (e.g., log or initialize safely)
            return; // or perform other safe handling
        }
    }

    public boolean isAtTargetPosition() {
        if (client.player == null) {
            return false; // Avoid NullPointerException if player is null
        }
        Vec3d pos = client.player.getPos();
        return Math.abs(pos.x - TARGET_X) < TOLERANCE &&
               Math.abs(pos.y - TARGET_Y) < TOLERANCE &&
               Math.abs(pos.z - TARGET_Z) < TOLERANCE;
    }

    public boolean isPlayerInSafeZone() {
        if (client.player == null) {
            return false; // Avoid NullPointerException if player is null
        }
        Vec3d pos = client.player.getPos();
        return pos.x >= SAFE_MIN && pos.x <= SAFE_MAX
            && pos.y == SAFE_Y
            && pos.z >= SAFE_MIN && pos.z <= SAFE_MAX;
    }
}

