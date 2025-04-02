package com.example.mymod;

public class ModStateManager {
    private boolean isModEnabled = true;
    private final VHT vht = new VHT();

    /**
     * Kiểm tra và cập nhật trạng thái của mod.
     */
    public void updateModState() {
        if (vht.isPlayerInSafeZone()) {
            isModEnabled = false; // Đặt lại cờ khi người chơi ở safe zone
        }

        if (vht.isAtTargetPosition()) {
            isModEnabled = true; // Đặt lại cờ khi người chơi ở vị trí mục tiêu
        }
    }

    /**
     * Kiểm tra xem mod có được kích hoạt hay không.
     *
     * @return true nếu mod được kích hoạt, ngược lại false.
     */
    public boolean isModEnabled() {
        return isModEnabled;
    }
}
