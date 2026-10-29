package com.nitsha.binds.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.multiversions.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.math.BigDecimal;

public class SteppedSlider extends Slider {
    private static final int KNOB_WIDTH = 10;
    private static final int TRACK_PADDING = 1;

    private final StepRange range;
    private int selectedIndex;

    public SteppedSlider(int x, int y, int width, int height, boolean isEnabled, float min, float max, float step, float value) {
        super(x, y, width, height, isEnabled, min, max, value);
        range = new StepRange(min, max, step);
        setValue(value);
    }

    public float getStep() {
        return (float) range.step;
    }

    public int getDivisionCount() {
        return range.lastIndex + 1;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    public void setValue(float value) {
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException("Slider value must be finite");
        }

        selectedIndex = range.nearestIndex(value);
        super.setValue(range.valueAt(selectedIndex));
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        super.setValue(getValue());
    }

    private int getTravelWidth() {
        return Math.max(0, getWidth() - TRACK_PADDING * 2 - KNOB_WIDTH);
    }

    private void updateFromMouse(double mouseX) {
        int travelWidth = getTravelWidth();

        if (travelWidth == 0) return;

        double progress = (mouseX - getX() - TRACK_PADDING - KNOB_WIDTH / 2.0) / travelWidth;
        selectedIndex = range.indexAtProgress(progress);
        super.setValue(range.valueAt(selectedIndex));
    }

    @Override
    protected void renderDivisions(GuiGraphicsExtractor ctx) {
        if (getHeight() <= 6) return;

        int travelWidth = getTravelWidth();
        int samples = Math.min(range.lastIndex, travelWidth);
        int previousX = Integer.MIN_VALUE;
        int remainingColor = isEnabled() && active ? 0xFF8B8B8B : 0xFF555555;

        for (int sample = 0; sample <= samples; sample++) {
            int index = range.lastIndex <= travelWidth ? sample : range.indexAtProgress(samples == 0 ? 0.0 : sample / (double) samples);
            int divisionX = getX() + TRACK_PADDING + KNOB_WIDTH / 2 + (int) Math.round(range.progressAt(index) * travelWidth);

            if (divisionX == previousX) continue;

            int color = index <= selectedIndex ? 0xFFFFFFFF : remainingColor;
            RenderUtils.drawFill(ctx, divisionX, getY() + 3, divisionX + 1, getY() + getHeight() - 3, color);
            previousX = divisionX;
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return active && visible && (super.isMouseOver(mouseX, mouseY) || isInsideKnob(mouseX, mouseY));
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!active) return false;

        boolean insideKnob = isInsideKnob(mouseX, mouseY);

        if (!super.onMouseClicked(mouseX, mouseY, button)) {
            return false;
        }

        if (!insideKnob) {
            updateFromMouse(mouseX);
        }

        return true;
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!active || !visible || !isEnabled() || !isPressed() || button != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        updateFromMouse(mouseX);
        return true;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (button != InputConstants.MOUSE_BUTTON_LEFT || !isPressed()) {
            return false;
        }

        setPressed(false);
        return true;
    }

    static final class StepRange {
        private final double min;
        private final double max;
        private final double step;
        private final int fullSteps;
        private final int lastIndex;

        StepRange(float min, float max, float step) {
            if (!Float.isFinite(min) || !Float.isFinite(max) || !Float.isFinite(step) || max <= min || step <= 0) {
                throw new IllegalArgumentException("Slider requires finite min < max and a positive step");
            }

            BigDecimal start = new BigDecimal(Float.toString(min));
            BigDecimal end = new BigDecimal(Float.toString(max));
            BigDecimal increment = new BigDecimal(Float.toString(step));
            BigDecimal[] division = end.subtract(start).divideAndRemainder(increment);

            if (division[0].compareTo(BigDecimal.valueOf(Integer.MAX_VALUE - 2L)) > 0) {
                throw new IllegalArgumentException("Slider has too many divisions");
            }

            this.min = start.doubleValue();
            this.max = end.doubleValue();
            this.step = increment.doubleValue();
            fullSteps = division[0].intValueExact();
            lastIndex = fullSteps + (division[1].signum() == 0 ? 0 : 1);
        }

        float valueAt(int index) {
            return (float) preciseValueAt(index);
        }

        private double preciseValueAt(int index) {
            return index == lastIndex ? max : min + step * index;
        }

        int nearestIndex(double value) {
            if (value <= min) return 0;
            if (value >= max) return lastIndex;

            int lower = Math.min(fullSteps, (int) Math.floor((value - min) / step));
            int upper = Math.min(lastIndex, lower + 1);
            double midpoint = (preciseValueAt(lower) + preciseValueAt(upper)) / 2.0;

            return value < midpoint ? lower : upper;
        }

        int indexAtProgress(double progress) {
            if (progress <= 0) return 0;
            if (progress >= 1) return lastIndex;

            return nearestIndex(min + progress * (max - min));
        }

        double progressAt(int index) {
            return (preciseValueAt(index) - min) / (max - min);
        }
    }
}
