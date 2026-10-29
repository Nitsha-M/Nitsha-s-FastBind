package com.nitsha.binds.gui.widget.list;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.modals.SelectSound;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import com.nitsha.binds.utils.AudioPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class SoundItem extends NitshaAbstractWidget {

    private static final Identifier NORMAL = Main.id("textures/gui/btns/smallbtn_normal.png");
    private static final Identifier PLAY_NORMAL = Main.id("textures/gui/sprites/play_normal.png");
    private static final Identifier PLAY_HOVER = Main.id("textures/gui/sprites/play_hover.png");
    private static final Identifier PAUSE_NORMAL = Main.id("textures/gui/sprites/pause_normal.png");
    private static final Identifier PAUSE_HOVER = Main.id("textures/gui/sprites/pause_hover.png");

    private final int index;

    private String value;
    private SelectSound parent;
    private boolean isExternal;

    private boolean isPlaying;

    public SoundItem(SelectSound parent, int x, int y, int width, int height, String value, int index, boolean isExternal) {
        super(x, y, width, height, TextUtils.empty());
        this.index = index;
        this.value = value;
        this.parent = parent;
        this.isExternal = isExternal;
        this.isPlaying = false;
    }


    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        if (index % 2 == 0)
            RenderUtils.drawFill(ctx, getX() - 4, getY(), getX() + getWidth() + 4, getY() + getHeight(), 0x4DFFFFFF);
        if (isMouseOver(mouseX, mouseY))
            RenderUtils.drawFill(ctx, getX() - 4, getY(), getX() + getWidth() + 4, getY() + getHeight(), 0x1AFFFFFF);

        int iX = this.getX() + 4;
        int iW = this.getWidth() - 8;

        Font font = Minecraft.getInstance().font;

        RenderUtils.drawTexture(ctx, (isMouseInsidePlayBtn(mouseX, mouseY)) ? ((isPlaying) ? PAUSE_HOVER : PLAY_HOVER) : ((isPlaying) ? PAUSE_NORMAL : PLAY_NORMAL), iX, this.getY() + 1, 0, 0, 10, 10, 10, 10);

        String category = TextUtils.translatable("nitsha.binds.advances.modals.buttons.external").getString().toLowerCase();
        String name = this.value;
        if (!isExternal) {
            String[] parts = this.value.split(":", 2);
            String[] subparts = parts[1].split("\\.", 2);
            String obj = RenderUtils.truncateString(subparts[0], 15);
            name = subparts.length > 1 ? subparts[1] : subparts[0];
            category = RenderUtils.truncateString(parts[0], 10);

            // obj
            RenderUtils.drawResizableBox(ctx, NORMAL, iX + 12, this.getY() + 1, font.width(obj) + 4, 10, 2, 5, 0xFF316D20);
            RenderUtils.drawText(ctx, TextUtils.literal(obj), 0, iX + 14, this.getY() + 2, "left", "top", 0xFFFFFFFF, false);
        }

        // category
        RenderUtils.drawResizableBox(ctx, NORMAL, iX + iW - font.width(category) - 4, this.getY() + 1, font.width(category) + 4, 10, 2, 5, 0xFF316D20);
        RenderUtils.drawText(ctx, TextUtils.literal(category), 0, iX + iW - 2, this.getY() + 2, "right", "top", 0xFFFFFFFF, false);

        RenderUtils.drawText(ctx, TextUtils.literal(RenderUtils.truncateString(name, 28)),
                0, iX, getY() + getHeight() - 2, "left", "bottom", 0xFF212121, false);
    }

    public boolean isMouseInsidePlayBtn(double mouseX, double mouseY) {
        return mouseX >= this.getX() + 4 && mouseX <= this.getX() + 14
                && mouseY >= this.getY() + 1 && mouseY <= this.getY() + 11;
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || button != InputConstants.MOUSE_BUTTON_LEFT || !isMouseOver(mouseX, mouseY)) {
            return false;
        }

        if (isMouseInsidePlayBtn(mouseX, mouseY)) {
            AudioPlayer.previewSound(9, this.value, 1.0f, 1.0f, this.isExternal, () -> play(true), () -> play(false), () -> play(false));
            return true;
        }

        playDownSound(Minecraft.getInstance().getSoundManager());
        this.parent.onSelect(value);
        return true;
    }

    private void play(boolean status) {
        isPlaying = status;
    }
}