package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.AbstractButton;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;*/
//? }

public class KeybindSelector extends AbstractButton {
    private static KeybindSelector focusedKeybind = null;
    private static final ResourceLocation NORMAL = Main.id("textures/gui/btns/bedrock_normal_bottom.png");
    private static final ResourceLocation PRESSED = Main.id("textures/gui/btns/bedrock_normal_top.png");

    private String name = TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString();
    private int keyCode;
    private boolean isPressed = false;

    private float yOffset = 0;
    private float targetOffset = 0;
    private final float speed = Main.GLOBAL_ANIMATION_SPEED + 0.2f;

    private int x, y;

    public KeybindSelector(int x, int y, int width, int height) {
        super(x, y, width, height, TextUtils.empty());
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

    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public int getKeyCode() { return this.keyCode; }
    public int getHeight() { return this.height; }
    public boolean isPressed() { return isPressed; }

    public static KeybindSelector getFocusedField() { return focusedKeybind; }

    public static void setFocusedField(KeybindSelector fK) {
        if (focusedKeybind != null) focusedKeybind.setPressed(false);
        focusedKeybind = fK;
    }

    @Override
    public void onPress() {}

    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        boolean isHovered = isMouseOver(mouseX, mouseY);
        Font font = Minecraft.getInstance().font;

        targetOffset = isPressed ? 2 : 0;
        yOffset = Mth.lerp(GUIUtils.clampSpeed(speed * delta), yOffset, targetOffset);
        if (Math.abs(yOffset - targetOffset) < 0.001f) yOffset = targetOffset;

        int maxSymbols = (this.width / 7) - (isPressed ? 4 : 0);
        String cName = calculateName(this.name, (isPressed) ? "> " + this.name + " <" : this.name, maxSymbols);
        String fName = (isPressed) ? "> " + cName + " <" : cName;
        int textWidth = font.width(fName);

        GUIUtils.drawResizableBox(ctx, NORMAL, getX(), getY() + 2, getWidth(), getHeight() - 2, 5, 11,
                (isHovered || isPressed) ? 0xFF07938d : 0xFFFFFFFF);
        GUIUtils.drawResizableBox(ctx, PRESSED, getX(), getY() + Math.round(yOffset), getWidth(), getHeight() - 2, 5, 11,
                (isHovered || isPressed) ? 0xFF07938d : 0xFFFFFFFF);

        GUIUtils.addText(ctx, TextUtils.literal(fName), 0,
                getX() + (width / 2) - (textWidth / 2),
                getY() + Math.round(yOffset) + (height / 2),
                "left", "center", (isHovered || isPressed) ? 0xFFFFFFFF : 0xFF212121, false);
    }

    private String calculateName(String insideName, String fullName, int max) {
        int textCut = Math.min(fullName.length(), max);
        return GUIUtils.truncateString(insideName, textCut);
    }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}
    //?} else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) {}*/
    //?}

    private static GuiEventListener lastClickedWidget = null;

    public static void setLastClickedWidget(GuiEventListener widget) {
        lastClickedWidget = widget;
    }

    public static void controlFocus() {
        if (focusedKeybind == null) return;
        if (lastClickedWidget == focusedKeybind) return;
        focusedKeybind.setFocused(false);
        focusedKeybind.setPressed(false);
        focusedKeybind = null;
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (isMouseOver(mouseX, mouseY)) {
            setLastClickedWidget(this);
            setFocusedField(this);
            this.setPressed(true);
            this.setFocused(true);
        }
        return super.mouseClicked(event, bl);
    }*/
    //? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            setLastClickedWidget(this);
            setFocusedField(this);
            this.setPressed(true);
            this.setFocused(true);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    //? }

    @Override
    //? if >=1.21.9 {
    /*public boolean keyPressed(KeyEvent event) {
        if (focusedKeybind != null) {
            int keyCode = event.key();
            int newKey = (keyCode == GLFW.GLFW_KEY_ESCAPE) ? 0 : keyCode;
            focusedKeybind.setPressed(false);
            focusedKeybind.setKeyCode(newKey);
            KeybindSelector.setFocusedField(null);
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) return true;
        }
        return super.keyPressed(event);
    }*/
    //? } else {
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
    //? }
}