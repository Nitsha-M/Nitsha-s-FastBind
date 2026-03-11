package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
 *///?}
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class BedrockIconOptionButton extends BedrockButton {
    private final ResourceLocation ICON;
    private boolean itemIcon = false;
    private int xO = 0;
    private int yO = 0;

    private final String[] options;
    private final String[] name;
    private int selectedIndex = 0;

    private static final ResourceLocation KEY_PRESS = Main.id("textures/gui/sprites/key_press.png");
    private static final ResourceLocation KEY_HOLD = Main.id("textures/gui/sprites/key_hold.png");
    private static final ResourceLocation TOOLTIP = Main.id("textures/gui/btns/tooltip.png");

    public BedrockIconOptionButton(int x, int y, int width, int height) {
        super("", x, y, width, height, true, ()->{});
        this.xO = (width - 16) / 2;
        this.yO = (height - 16) / 2;
        this.options = new String[]{"press", "hold"};
        this.name = new String[]{"nitsha.binds.advances.actions.option.press", "nitsha.binds.advances.actions.option.hold"};
        this.ICON = null;
        this.itemIcon = false;
    }

    private void setupColor() {
        boolean isHold = selectedIndex == 1;
        this.setColors(
            isHold ? 0xFF9cc708 : 0xFF07938d,
            isHold ? 0xFFafda19 : 0xFF0fb2ab,
            0xFFFFFFFF, 0xFFFFFFFF
        );
    }

    public String getSelected() {
        return (options.length == 0) ? "" : options[selectedIndex];
    }

    public String getSelectedName() {
        return (name.length == 0) ? "" : name[selectedIndex];
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < options.length) {
            this.selectedIndex = index;
        }
    }

    public void setSelected(String value) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(value)) {
                selectedIndex = i;
                setupColor();
                return;
            }
        }
    }
    @Override
    //? <1.21.9 {
    public void onPress() {
        super.onPress();
    //? } else {
    // public void onPress(InputWithModifiers inputWithModifiers) {
    // super.onPress(inputWithModifiers);
    //? }
        if (options.length == 0) return;
        selectedIndex = (selectedIndex + 1) % options.length;
        setupColor();
    }

    //? if >=1.21.11 {
    /*@Override
    public void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderContents(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }*/
    //? } else if >1.20.2 {
    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    //? } else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    *///? } else if >=1.19.4 {
    /*@Override
    public void render(PoseStack context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    *///?} else {
    /*@Override
    public void render(PoseStack context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    */
    //? }

    protected void renderOverlay(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        GuiGraphics context = (GuiGraphics) ctx;
        //?} else {
        /*PoseStack context = (PoseStack) ctx;*/
        //?}
        boolean isHold = selectedIndex == 1;
        ResourceLocation icon = isHold ? KEY_HOLD : KEY_PRESS;
        GUIUtils.adaptiveDrawTexture(ctx, icon, this.getX() + xO, this.getY() + yO + Math.round(this.getOffsetY()), 0, 0, 16, 14, 16, 14);
        if (isMouseOver(mouseX, mouseY)) {
            Component tooltip = TextUtils.translatable(name[selectedIndex]);
            int tooltipWidth = Minecraft.getInstance().font.width(tooltip);
            int tooltipHeight = Minecraft.getInstance().font.lineHeight + 4;

            int tx = mouseX - tooltipWidth - 4;
            int ty = mouseY - tooltipHeight - 2;

            final int ftx = tx, fty = ty;
            int color = this.getBtnColor();
            int transparentColor = (color & 0x00FFFFFF) | (0xCC << 24);
            GUIUtils.matricesUtil(ctx, 0, 0, 52000, () -> {
                GUIUtils.drawResizableBox(ctx, TOOLTIP, ftx - 2, fty - 2, tooltipWidth + 6, tooltipHeight, 3, 7, transparentColor);
                GUIUtils.addText(ctx, tooltip, 0, ftx + 2, fty, "top", "left", 0xFFFFFFFF, false);
            });
        }
    }
}