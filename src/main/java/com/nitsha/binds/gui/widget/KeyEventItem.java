package com.nitsha.binds.gui.widget;

import com.nitsha.binds.gui.modals.SelectKeyEvent;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

import java.util.Map;

public class KeyEventItem extends AbstractButton {
    private final int index;
    private final int x, y;

    private String value;
    private SelectKeyEvent parent;

    public KeyEventItem(SelectKeyEvent parent, int x, int y, int width, int height, String value, int index) {
        super(x, y, width, height, TextUtils.empty());
        this.index = index;
        this.value = value;
        this.parent = parent;
        this.x = x;
        this.y = y;
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }

    public int getHeight() {
        return this.height;
    }

    @Override
    public void onPress() {

    }

    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        if (index % 2 == 0)
            GUIUtils.drawFill(ctx, getX() - 4, getY(), getX() + getWidth() + 4, getY() + getHeight(), 0x4DFFFFFF);
        if (isHovered)
            GUIUtils.drawFill(ctx, getX() - 4, getY(), getX() + getWidth() + 4, getY() + getHeight(), 0x1AFFFFFF);

        GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.truncateString(TextUtils.translatable(value).getString(), 26)), 0,
                4, getY() + 3, "top", "left", 0xFF212121, false);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {}

    @Override
    public void onClick(double mouseX, double mouseY) {
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.parent.onSelect(value);
    }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}
    //? } else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) {}*/
    //? }
}