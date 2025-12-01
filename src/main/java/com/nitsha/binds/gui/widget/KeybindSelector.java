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
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.client.gui.components.AbstractButton;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class KeybindSelector extends AbstractButton {
    private static KeybindSelector focusedKeybind = null;
    private static final ResourceLocation NORMAL = Main.id("textures/gui/btns/keybind_normal.png");
    private static final ResourceLocation PRESSED = Main.id("textures/gui/btns/keybind_pressed.png");

    private String name = TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString();
    private int keyCode;
    private boolean isPressed = false;

    private int x, y;

    public KeybindSelector(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal(""));
        this.x = x;
        this.y = y;
    }

    public void setPressed(boolean pressed) {
        this.isPressed = pressed;
    }

    public void setKeyCode(int kC) {
        this.keyCode = kC;
        this.name = (kC == 0)
                ? TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString()
                : InputConstants.Type.KEYSYM.getOrCreate(kC).getDisplayName().getString();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public static KeybindSelector getFocusedField() {
        return focusedKeybind;
    }

    public static void setFocusedField(KeybindSelector fK) {
        if (focusedKeybind != null) {
            focusedKeybind.setPressed(false);
        }
        focusedKeybind = fK;
    }

    @Override
    public void onPress() {
    }

    //? if >1.20.2 {
    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        rndr(ctx, mouseX, mouseY, delta);
    }
    //?} else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///?} else {
    /*@Override
    public void renderWidget(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///?}

    private String calculateName(String insideName, String fullName, int max) {
        int textCut = Math.min(fullName.length(), max);
        return GUIUtils.truncateString(insideName, textCut);
    }

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        Font font = Minecraft.getInstance().font;
        int maxSymbols = (this.width / 7) - (isPressed ? 4 : 0);
        String cName = calculateName(this.name, (isPressed) ? "> " + this.name + " <" : this.name, maxSymbols);
        String fName = (isPressed) ? "> " + cName + " <" : cName;
        int textWidth = font.width(fName);

        GUIUtils.drawResizableBox(ctx, (isPressed || isHovered()) ? PRESSED : NORMAL, getX(), getY(), getWidth(), getHeight(), 3, 7);
        GUIUtils.addText(ctx, Component.literal(fName), 0,
                this.getX() + ((this.width / 2) - (textWidth / 2)),
                this.getY() + ((this.height / 2) - (font.lineHeight / 2)),
                "top", "left", 0xFFFFFFFF, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        controlFocus(mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void controlFocus(double mouseX, double mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            setFocusedField(this);
            this.setPressed(true);
        } else {
            setFocusedField(null);
        }
    }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    //?} else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) {
    }*/
    //?}

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedKeybind != null) {
            int newKey = (keyCode == GLFW.GLFW_KEY_ESCAPE) ? 0 : keyCode;
            focusedKeybind.setPressed(false);
            focusedKeybind.setKeyCode(newKey);
            KeybindSelector.setFocusedField(null);
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}