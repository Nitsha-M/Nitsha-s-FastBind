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
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//? }

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class OptionButton extends AbstractButton {
    private static final ResourceLocation NORMAL = Main.id("textures/gui/btns/keybind_normal.png");
    private static final ResourceLocation PRESSED = Main.id("textures/gui/btns/keybind_pressed.png");

    private final String[] options;
    private final String[] name;
    private int selectedIndex = 0;
    private int x, y;

    public OptionButton(int x, int y, int width, int height, String[] options, String[] name) {
        super(x, y, width, height, TextUtils.empty());
        this.options = options;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
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
                return;
            }
        }
    }

    //? <1.21.9 {
    @Override
    public void onPress() {
        if (options.length == 0) return;
        selectedIndex = (selectedIndex + 1) % options.length;
    }
    //? } else {
    /*@Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        if (options.length == 0) return;
        selectedIndex = (selectedIndex + 1) % options.length;
    }*/
    //? }

    //? if >=1.21.11 {
    /*@Override
    public void renderContents(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        rndr(ctx, mouseX, mouseY, delta);
    }*/
    //?} else if >1.20.2 {
    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        rndr(ctx, mouseX, mouseY, delta);
    }
    //?} else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///?} else if >=1.19.4 {
    /*@Override
    public void renderWidget(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///?} else {
    /*@Override
    public void renderButton(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    */
    //? }

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        Font font = Minecraft.getInstance().font;
        MutableComponent current = TextUtils.translatable(getSelectedName());
        int textWidth = font.width(current);

        int add = (height % 2 == 0) ? 0 : 1;

        GUIUtils.drawResizableBox(ctx, isHovered ? PRESSED : NORMAL, getX(), getY(), getWidth(), getHeight(), 3, 7);
        GUIUtils.addText(ctx, current, 0,
                this.getX() + ((this.width / 2) - (textWidth / 2)),
                this.getY() + ((this.height / 2) - (font.lineHeight / 2)) + add,
                "top", "left", 0xFFFFFFFF, false);
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

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        return super.mouseClicked(event, bl);
    }*/
    //? } else {
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }
    //? }
}
