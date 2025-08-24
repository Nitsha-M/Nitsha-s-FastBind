package com.nitsha.binds.utils;

public class EasterEgg {
    private final int requiredClicks;
    private final long maxTimeBetweenClicksMs;
    private final Runnable action;

    private int clickCount = 0;
    private long lastClickTime = 0;

    public EasterEgg(int requiredClicks, long maxTimeBetweenClicksMs, Runnable action) {
        this.requiredClicks = requiredClicks;
        this.maxTimeBetweenClicksMs = maxTimeBetweenClicksMs;
        this.action = action;
    }

    public boolean handleClick(boolean isInsideArea) {
        if (!isInsideArea) {
            reset();
            return false;
        }
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastClickTime > maxTimeBetweenClicksMs) {
            clickCount = 1;
        } else {
            clickCount++;
        }

        lastClickTime = currentTime;

        if (clickCount >= requiredClicks) {
            action.run();
            reset();
        }
        return true;
    }

    public void reset() {
        this.clickCount = 0;
        this.lastClickTime = 0;
    }

    public int getClickCount() {
        return clickCount;
    }
}