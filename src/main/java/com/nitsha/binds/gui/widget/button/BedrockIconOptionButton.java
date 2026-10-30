package com.nitsha.binds.gui.widget.button;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BedrockIconOptionButton extends BedrockButton {

    public static class Option {
        public String id;
        public String nameKey;
        public Identifier icon;
        public int color1;
        public int color2;
        public int color3;
        public int color4;

        public Option(String id, String nameKey, Identifier icon, int color1, int color2, int color3, int color4) {
            this.id = id;
            this.nameKey = nameKey;
            this.icon = icon;
            this.color1 = color1;
            this.color2 = color2;
            this.color3 = color3;
            this.color4 = color4;
        }
    }

    private int xO = 0;
    private int yO = 0;

    private final List<Option> options = new ArrayList<>();
    private int selectedIndex = 0;

    public BedrockIconOptionButton(int x, int y, int width, int height, Runnable onRelease) {
        super(TextUtils.empty(), x, y, width, height, true, onRelease);
        this.xO = (width - 16) / 2;
        this.yO = (height - 16) / 2;
        setHoverTooltipEnabled(true);
    }

    public BedrockIconOptionButton addOption(String id, String nameKey, Identifier icon, int color1, int color2, int color3, int color4) {
        this.options.add(new Option(id, nameKey, icon, color1, color2, color3, color4));
        if (options.size() == 1) setupColor();
        return this;
    }

    private void setupColor() {
        if (options.isEmpty()) return;
        Option opt = options.get(selectedIndex);
        this.setColors(opt.color1, opt.color2, opt.color3, opt.color4);
    }

    public String getSelected() {
        return options.isEmpty() ? "" : options.get(selectedIndex).id;
    }

    public String getSelectedName() {
        return options.isEmpty() ? "" : options.get(selectedIndex).nameKey;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    protected Component getHoverTooltip() {
        Component tooltip = super.getHoverTooltip();
        if (tooltip != null) return tooltip;

        return options.isEmpty() ? null : TextUtils.translatable(options.get(selectedIndex).nameKey);
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < options.size()) {
            this.selectedIndex = index;
            setupColor();
        }
    }

    public void setSelected(String value) {
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).id.equals(value)) {
                selectedIndex = i;
                setupColor();
                return;
            }
        }
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.onRender(ctx, mouseX, mouseY, delta);
        if (options.isEmpty()) return;

        Option opt = options.get(selectedIndex);
        RenderUtils.drawTexture(ctx, opt.icon, this.getX() + xO, this.getY() + yO + Math.round(this.getOffsetY()), 0, 0, 16, 14, 16, 14);
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (!isEnabled() || !visible) return false;

        if (button == InputConstants.MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY) && isPressed() && !options.isEmpty()) {
            selectedIndex = (selectedIndex + 1) % options.size();
            setupColor();
        }

        return super.onMouseReleased(mouseX, mouseY, button);
    }
}
