package com.example.mymod;

public class TimerManager {
    private final int waitTicks; // Số tick cần chờ
    private int tickCounter;    // Bộ đếm tick hiện tại

    /**
     * Khởi tạo TimerManager với số tick cần chờ.
     *
     * @param waitTicks Số tick cần chờ.
     */
    public TimerManager(int waitTicks) {
        this.waitTicks = waitTicks;
        this.tickCounter = waitTicks; // Đặt bộ đếm ban đầu bằng số tick cần chờ
    }

    /**
     * Đếm ngược tick. Gọi phương thức này mỗi tick.
     */
    public void tick() {
        if (tickCounter > 0) {
            tickCounter--;
        }
    }

    /**
     * Kiểm tra xem thời gian đã đến hay chưa.
     *
     * @return true nếu thời gian đã đến, ngược lại false.
     */
    public boolean isTimeUp() {
        return tickCounter <= 0;
    }

    /**
     * Reset bộ đếm về giá trị ban đầu.
     */
    public void reset() {
        tickCounter = waitTicks;
    }

    /**
     * Lấy số tick còn lại.
     *
     * @return Số tick còn lại.
     */
    public int getRemainingTicks() {
        return tickCounter;
    }
}