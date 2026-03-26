package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class ModalWindow extends AnimatedWindow {

    private static final ResourceLocation CLOSE = Main.idSprite("close_modal");
    private static final ResourceLocation CLOSE_HOVER = Main.idSprite("close_modal_hover");

    private final BindsEditor screen;
    private AbstractWidget closeBtn;

    private float bgAlpha = 0f;
    private float bgAlphaTarget = 0f;
    private static final float BG_MAX_ALPHA = 0.5f;
    private static final float BG_SPEED = 0.2f;

    private String title = "Default";

    public ModalWindow(BindsEditor screen, int x, int y, int width, int height, ResourceLocation t1,
                          ResourceLocation t2, String title) {
        super(x, y, width, height, t1, t2, 0);
        this.screen = screen;

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.truncateString(TextUtils.translatable(title).getString(), 20)), 0,
                    7, 10, "left", "center", 0xFF212121, false);
//            GUIUtils.drawFill(ctx, 4, 19, width - 4, 20, 0xFF212121);
        }, 1);

        this.closeBtn = GUIUtils.createTexturedBtn(width - 17, 5, 11, 11, new ResourceLocation[]{CLOSE, CLOSE_HOVER}, button -> {
            this.close(()->{});
        });

        this.addElement(closeBtn);
    }

    @Override
    public void renderWindow(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        int adjX = mouseX - this.getX();
        int adjY = mouseY - this.getYOffset();

        bgAlpha += (bgAlphaTarget - bgAlpha) * Math.min(1f, BG_SPEED * delta);

        int alpha = (int)(bgAlpha * 255) << 24;
        int bgColor = alpha | 0x00000000;

        GUIUtils.matricesUtil(ctx, 0, 0, 200, () -> {
            GUIUtils.drawFill(ctx, 0, 0, 10000, 10000, bgColor);
            super.renderWindow(ctx, mouseX, mouseY, delta);
        });
    }

    @Override
    public void open(Runnable onFinish) {
        bgAlphaTarget = BG_MAX_ALPHA;
        super.open(onFinish);
    }

    @Override
    public void close(Runnable onFinish) {
        bgAlphaTarget = 0f;
        super.close(onFinish);
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                && mouseY >= this.getY() - 16 && mouseY < this.getY() + this.getHeight();
    }
}