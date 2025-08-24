package com.nitsha.binds.gui.widget;

import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.panels.AdvancedOptions;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.utils.FastbindParser;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
import net.minecraft.client.gui.widget.PressableWidget;
//? if >=1.17 {
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
//? }
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;

public class ActionItem extends ClickableWidget {
    private final PressableWidget topBtn;
    private final PressableWidget bottomBtn;
    private final PressableWidget resetBtn;
    private final PressableWidget deleteBtn;
    private static final Identifier TOP = MainClass.idSprite("action_top_normal");
    private static final Identifier TOP_HOVER = MainClass.idSprite("action_top_hover");
    private static final Identifier BOTTOM = MainClass.idSprite("action_bottom_normal");
    private static final Identifier BOTTOM_HOVER = MainClass.idSprite("action_bottom_hover");
    private static final Identifier RESET = MainClass.idSprite("action_reset_normal");
    private static final Identifier RESET_HOVER = MainClass.idSprite("action_reset_hover");
    private static final Identifier DELETE = MainClass.idSprite("action_delete_normal");
    private static final Identifier DELETE_HOVER = MainClass.idSprite("action_delete_hover");
    private static final Identifier ARROW_DISABLED = MainClass.id("textures/gui/test/arrow_disabled.png");

    private int type, index;

    private final BindsEditor.TextField command;
    private final BindsEditor.TextField delay;
    private final KeybindSelector keybind;

    private int x, y;

    public ActionItem(AdvancedOptions parent, int type, int x, int y, int width, String value, int index) {
        super(x, y, width, 26, Text.of(""));
        this.index = index;
        this.type = type;
        this.x = x;
        this.y = y;

        this.command = new BindsEditor.TextField(MinecraftClient.getInstance().textRenderer, x, y + 3, width - 26,19, Integer.MAX_VALUE, value, TextUtils.translatable("nitsha.binds.advances.actions.commandLine").getString());
        this.delay = new BindsEditor.TextField(MinecraftClient.getInstance().textRenderer, x + 90, y + 3, width - 116,19, 6, value, TextUtils.translatable("nitsha.binds.advances.actions.delayLine").getString(), true);
        this.keybind = new KeybindSelector(x + width - 26 - 68, y + 3, 68, 19);
        if (this.type == 3) this.keybind.setKeyCode(Integer.parseInt(value));

        this.topBtn = GUIUtils.createTexturedBtn(x + width - 9, y + 3, 9, 9, new Identifier[]{TOP, TOP_HOVER}, button -> {
            if (index > 0) parent.moveAction(index, -1);
        });

        this.bottomBtn = GUIUtils.createTexturedBtn(x + width - 9, y + 13, 9, 9, new Identifier[]{BOTTOM, BOTTOM_HOVER}, button -> {
            if (BindsEditor.getCBind().actions.size() != index + 1) parent.moveAction(index, 1);
        });

        this.resetBtn = GUIUtils.createTexturedBtn(x + width - 25, y + 3, 15, 9, new Identifier[]{RESET, RESET_HOVER}, button -> {
            switch (this.type) {
                case 1 -> {
                    this.command.setText("");
                }
                case 2 -> {
                    this.delay.setText("100");
                }
                case 3 -> {
                    this.keybind.setKeyCode(0);
                    this.keybind.setPressed(false);
                }
            }
        });

        this.deleteBtn = GUIUtils.createTexturedBtn(x + width - 25, y + 13, 15, 9, new Identifier[]{DELETE, DELETE_HOVER}, button -> {
            parent.removeAction(index);
        });
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public String getValue() {
        String val = switch (this.type) {
            case 1 -> this.command.getText();
            case 2 -> this.delay.getText();
            case 3 -> String.valueOf(this.keybind.getKeyCode());
            default -> "";
        };
        return FastbindParser.toAction(this.type, val);
    }

    //? if >1.20.2 {
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
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

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        DrawContext c = (DrawContext) ctx;
        //? } else {
        /*MatrixStack c = (MatrixStack) ctx;
         *///? }
        int lineColor = 0xFFFFFFFF;
        GUIUtils.drawFill(c, getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight(), 0x33FFFFFF);
        this.resetBtn.render(c, mouseX, mouseY, delta);
        this.deleteBtn.render(c, mouseX, mouseY, delta);
        GUIUtils.adaptiveDrawTexture(ctx, ARROW_DISABLED, getX() + getWidth() - 9, getY() + 3, 0, 0, 9, 19, 9, 19);
        int aS = BindsEditor.getCBind().actions.size();
        if (aS > 0) {
            if (index > 0)this.topBtn.render(c, mouseX, mouseY, delta);
            if (index != (aS - 1)) this.bottomBtn.render(c, mouseX, mouseY, delta);
        }
        switch (this.type) {
            case 1 -> {
                lineColor = 0xFFc99212;
                this.command.render(c, mouseX, mouseY, delta);
            }
            case 2 -> {
                lineColor = 0xFF4e8605;
                this.delay.render(c, mouseX, mouseY, delta);
                GUIUtils.addText(c, TextUtils.translatable("nitsha.binds.advances.actions.delayDesс"), 0, getX() + 2, getY() + 8, "top", "left", 0xFF212121, false);
            }
            case 3 -> {
                lineColor = 0xFF790e06;
                this.keybind.render(c, mouseX, mouseY, delta);
                GUIUtils.addText(c, TextUtils.translatable("nitsha.binds.advances.actions.pressKey"), 0, getX() + 2, getY() + 8, "top", "left", 0xFF212121, false);
            }
        }
        GUIUtils.drawFill(ctx, getX() - 3, getY() + 8, getX() - 1, getY() + getHeight() - 8, lineColor);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clicked = false;
        switch (this.type) {
            case 1 -> {
                if (this.command.mouseClicked(mouseX, mouseY, button)) clicked = true;
            }
            case 2 -> {
                if (this.delay.mouseClicked(mouseX, mouseY, button)) clicked = true;
            }
            case 3 -> {
                if (this.keybind.mouseClicked(mouseX, mouseY, button)) clicked = true;
            }
        }
        if (this.deleteBtn.mouseClicked(mouseX, mouseY, button) ||
            this.bottomBtn.mouseClicked(mouseX, mouseY, button) ||
            this.resetBtn.mouseClicked(mouseX, mouseY, button) ||
            this.topBtn.mouseClicked(mouseX, mouseY, button)) {
            clicked = true;
        }
        return clicked;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (this.type) {
            case 1 -> {
                if (this.command.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
            case 2 -> {
                if (this.delay.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
            case 3 -> {
                if (keybind.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        switch (this.type) {
            case 1 -> {
                if (this.command.charTyped(codePoint, modifiers)) {
                    return true;
                }
            }
            case 2 -> {
                if (this.delay.charTyped(codePoint, modifiers)) {
                    return true;
                }
            }
            case 3 -> {
                if (keybind.charTyped(codePoint, modifiers)) {
                    return true;
                }
            }
        }
        return super.charTyped(codePoint, modifiers);
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
}
