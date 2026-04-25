package com.nitsha.binds.gui.widget.list;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.modals.SelectSound;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.utils.AudioPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class SoundItem extends AbstractButton {

    private static final ResourceLocation NORMAL = Main.id("textures/gui/btns/smallbtn_normal.png");
    private static final ResourceLocation PLAY_NORMAL = Main.id("textures/gui/sprites/play_normal.png");
    private static final ResourceLocation PLAY_HOVER = Main.id("textures/gui/sprites/play_hover.png");
    private static final ResourceLocation PAUSE_NORMAL = Main.id("textures/gui/sprites/pause_normal.png");
    private static final ResourceLocation PAUSE_HOVER = Main.id("textures/gui/sprites/pause_hover.png");

    private final int index;
    private final int x, y;

    private String value;
    private SelectSound parent;
    private boolean isExternal;

    private boolean isPlaying;

    public SoundItem(SelectSound parent, int x, int y, int width, int height, String value, int index, boolean isExternal) {
        super(x, y, width, height, TextUtils.empty());
        this.index = index;
        this.value = value;
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.isExternal = isExternal;
        this.isPlaying = false;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

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
        if (isMouseOver(mouseX, mouseY))
            GUIUtils.drawFill(ctx, getX() - 4, getY(), getX() + getWidth() + 4, getY() + getHeight(), 0x1AFFFFFF);

        int iX = this.getX() + 4;
        int iW = this.getWidth() - 8;

        Font font = Minecraft.getInstance().font;

        GUIUtils.adaptiveDrawTexture(ctx, (isMouseInsidePlayBtn(mouseX, mouseY)) ? ((isPlaying) ? PAUSE_HOVER : PLAY_HOVER) : ((isPlaying) ? PAUSE_NORMAL : PLAY_NORMAL), iX, this.getY() + 1, 0, 0, 10, 10, 10, 10);

        String category = TextUtils.translatable("nitsha.binds.advances.modals.buttons.external").getString().toLowerCase();
        String name = this.value;
        if (!isExternal) {
            String[] parts = this.value.split(":", 2);
            String[] subparts = parts[1].split("\\.", 2);
            String obj = GUIUtils.truncateString(subparts[0], 15);
            name = subparts.length > 1 ? subparts[1] : subparts[0];
            category = GUIUtils.truncateString(parts[0], 10);

            // obj
            GUIUtils.drawResizableBox(ctx, NORMAL, iX + 12, this.getY() + 1, font.width(obj) + 4, 10, 2, 5, 0xFF316D20);
            GUIUtils.addText(ctx, TextUtils.literal(obj), 0, iX + 14, this.getY() + 2, "left", "top", 0xFFFFFFFF, false);
        }

        // category
        GUIUtils.drawResizableBox(ctx, NORMAL, iX + iW - font.width(category) - 4, this.getY() + 1, font.width(category) + 4, 10, 2, 5, 0xFF316D20);
        GUIUtils.addText(ctx, TextUtils.literal(category), 0, iX + iW - 2, this.getY() + 2, "right", "top", 0xFFFFFFFF, false);

        GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.truncateString(name, 28)),
                0, iX, getY() + getHeight() - 2, "left", "bottom", 0xFF212121, false);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.parent.onSelect(value);
    }

    public boolean isMouseInsidePlayBtn(double mouseX, double mouseY) {
        return mouseX >= this.getX() + 4 && mouseX <= this.getX() + 14
                && mouseY >= this.getY() + 1 && mouseY <= this.getY() + 11;
    }

    // ? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    // ? } else if >=1.17 {
    /*
     * @Override
     * public void updateNarration(NarrationElementOutput builder) {}
     */
    // ? }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.buttonInfo().button();

        if (isMouseInsidePlayBtn(mouseX, mouseY)) {
            AudioPlayer.previewSound(9, this.value, 1.0f, 1.0f, this.isExternal, () -> play(true), () -> play(false), () -> play(false));
            return false;
        }

        return super.mouseClicked(event, bl);
    }*/
    //? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseInsidePlayBtn(mouseX, mouseY)) {
            AudioPlayer.previewSound(9, this.value, 1.0f, 1.0f, this.isExternal, () -> play(true), () -> play(false), () -> play(false));
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    //? }

    private void play(boolean status) {
        isPlaying = status;
    }
}