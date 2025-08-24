package com.nitsha.binds.gui.widget;

import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
//? if >=1.17 {
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
//? }
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class KeybindSelector extends ClickableWidget {
    private static KeybindSelector focusedKeybind = null;
    private static final Identifier NORMAL = MainClass.id("textures/gui/btns/keybind_normal.png");
    private static final Identifier PRESSED = MainClass.id("textures/gui/btns/keybind_pressed.png");

    private String name = TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString();
    private int keyCode;
    private boolean isPressed = false;

    private int x, y;

    public KeybindSelector(int x, int y, int width, int height) {
        super(x, y, width, height, Text.of(""));
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
                : InputUtil.Type.KEYSYM.createFromCode(kC).getLocalizedText().getString();
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

    //? if >1.20.2 {
    @Override
    public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        rndr(ctx, mouseX, mouseY, delta);
    }
    //? } else if >=1.20 {
    /*@Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///? } else {
    /*@Override
    public void renderButton(MatrixStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///? }

    private String calculateName(String insideName, String fullName, int max) {
        int textCut = Math.min(fullName.length(), max);
        return GUIUtils.truncateString(insideName, textCut);
    }

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int maxSymbols = (this.width / 7) - (isPressed ? 4 : 0);
        String cName = calculateName(this.name, (isPressed) ? "> " + this.name + " <" : this.name, maxSymbols);
        String fName = (isPressed) ? "> " + cName + " <" : cName;
        int textWidth = textRenderer.getWidth(fName);

        GUIUtils.drawResizableBox(ctx, (isPressed || hovered) ? PRESSED : NORMAL, getX(), getY(), getWidth(), getHeight(), 3, 7);
        GUIUtils.addText(ctx, Text.of(fName), 0,
                this.getX() + ((this.width / 2) - (textWidth / 2)),
                this.getY() + ((this.height / 2) - (textRenderer.fontHeight / 2)),
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
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
    //? } else if >=1.17 {
    /*@Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }*/
    //? }

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
