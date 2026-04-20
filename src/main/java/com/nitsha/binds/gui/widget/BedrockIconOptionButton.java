package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

import java.util.ArrayList;
import java.util.List;

public class BedrockIconOptionButton extends BedrockButton {

    public static class Option {
        public String id;
        public String nameKey;
        public ResourceLocation icon;
        public int color1;
        public int color2;
        
        public Option(String id, String nameKey, ResourceLocation icon, int color1, int color2) {
            this.id = id;
            this.nameKey = nameKey;
            this.icon = icon;
            this.color1 = color1;
            this.color2 = color2;
        }
    }

    private int xO = 0;
    private int yO = 0;

    private final List<Option> options = new ArrayList<>();
    private int selectedIndex = 0;

    private static final ResourceLocation TOOLTIP = Main.id("textures/gui/btns/tooltip.png");

    public BedrockIconOptionButton(int x, int y, int width, int height, Runnable onRelease) {
        super("", x, y, width, height, true, onRelease);
        this.xO = (width - 16) / 2;
        this.yO = (height - 16) / 2;
    }

    public BedrockIconOptionButton addOption(String id, String nameKey, ResourceLocation icon, int color1, int color2) {
        this.options.add(new Option(id, nameKey, icon, color1, color2));
        if (options.size() == 1) setupColor();
        return this;
    }

    private void setupColor() {
        if (options.isEmpty()) return;
        Option opt = options.get(selectedIndex);
        this.setColors(opt.color1, opt.color2, 0xFFFFFFFF, 0xFFFFFFFF);
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

    //? if >=1.21.9 {
    /*@Override
    public void onRelease(MouseButtonEvent event) {
        if (isMouseOver(event.x(), event.y()) && isPressed()) {
            if (options.isEmpty()) return;
            selectedIndex = (selectedIndex + 1) % options.size();
            setupColor();
        }
        super.onRelease(event);
    }*/
    //? } else {
    @Override
    public void onRelease(double mouseX, double mouseY) {
        if (isMouseOver(mouseX, mouseY) && isPressed()) {
            if (options.isEmpty()) return;
            selectedIndex = (selectedIndex + 1) % options.size();
            setupColor();
        }
        super.onRelease(mouseX, mouseY);
    }
    //? }

    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        super.renderWidget(ctx, mouseX, mouseY, delta);
        if (options.isEmpty()) return;

        Option opt = options.get(selectedIndex);
        GUIUtils.adaptiveDrawTexture(ctx, opt.icon, this.getX() + xO, this.getY() + yO + Math.round(this.getOffsetY()), 0, 0, 16, 14, 16, 14);
        if (isMouseOver(mouseX, mouseY)) {
            Component tooltip = TextUtils.translatable(opt.nameKey);
            int tooltipWidth = Minecraft.getInstance().font.width(tooltip);
            int tooltipHeight = Minecraft.getInstance().font.lineHeight + 4;

            int tx = mouseX - tooltipWidth - 4;
            int ty = mouseY - tooltipHeight - 2;

            int color = this.getBtnColor();
            int transparentColor = (color & 0x00FFFFFF) | (0xCC << 24);
            GUIUtils.matricesUtil(ctx, tx, ty, 200, () -> {
                GUIUtils.drawResizableBox(ctx, TOOLTIP, -2, -2, tooltipWidth + 6, tooltipHeight, 3, 7, transparentColor);
                GUIUtils.addText(ctx, tooltip, 0, 2, 0, "top", "left", 0xFFFFFFFF, false);
            });
        }
    }
}