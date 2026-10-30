package com.nitsha.binds.multiversions;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

//? if >=1.21.9 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
//? }

public abstract class NitshaScreen extends Screen {

    protected NitshaScreen() {
        super(TextUtils.empty());
    }

    protected <T extends AbstractWidget> T addNWidget(T widget) {
        //? if >=1.17 {
        return this.addRenderableWidget(widget);
        //? } else {
        /*return this.addButton(widget);*/
        //? }
    }

    protected <T extends NitshaContainer> T addNWidget(T widget) {
        //? if >=1.17 {
        return this.addRenderableWidget(widget);
        //? } else {
        /*return this.addWidget(widget);*/
        //? }
    }

    protected void removeNWidget(AbstractWidget widget) {
        //? if >=1.17 {
        this.removeWidget(widget);
        //? } else {
        /*
        this.children().remove(widget);
        this.buttons.remove(widget);
        */
        //? }
    }

    //? if >=26.1 {
    @Override
    public final void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        renderFrame(ctx, mouseX, mouseY, delta);
    }
    //? } else {
    /*
    @Override
    public final void render(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        renderFrame(ctx, mouseX, mouseY, delta);
    }
    */
    //? }

    private void renderFrame(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.beginFrame();

        try {
            onRender(ctx, mouseX, mouseY, delta);
            RenderUtils.renderQueuedHoverTooltip(ctx, mouseX, mouseY, width, height);
        } finally {
            RenderUtils.clearHoverTooltip();
        }
    }

    //? if >=1.18.1 {
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    //? } else {
    /*public boolean isPauseScreen() {
        return false;
    }*/
    //? }

    //? if >=1.21.9 {
    @Override
    public final boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (onMouseClicked(event.x(), event.y(), event.button())) {
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }
    //? } else {
    /*
    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (onMouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
    */
    //? }

    //? if >=1.21.9 {
    public boolean mouseReleased(MouseButtonEvent event) {
        if (onMouseReleased(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseReleased(event);
    }
    //? } else {
    /*public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (onMouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    */
    //? }

    //? if >=1.21.9 {
    @Override
    public final boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (onMouseDragged(event.x(), event.y(), event.button(), deltaX, deltaY)) {
            return true;
        }

        return super.mouseDragged(event, deltaX, deltaY);
    }
    //? } else {
    /*
    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (onMouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    */
    //? }

    //? if >=1.21.9 {
    @Override
    public final boolean keyPressed(KeyEvent event) {
        //$ if >=26.3 'int nativeCode = event.keycode();' else 'int nativeCode = event.scancode();'
        int nativeCode = event.keycode();

        if (onKeyPressed(event.key(), nativeCode, event.modifiers())) {
            return true;
        }

        return super.keyPressed(event);
    }
    //? } else {
    /*@Override
    public final boolean keyPressed(int keyCode, int nativeCode, int modifiers) {
        if (onKeyPressed(keyCode, nativeCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, nativeCode, modifiers);
    }
    */
    //? }

    //? if >=1.21.9 {
    @Override
    public final boolean keyReleased(KeyEvent event) {
        if (onKeyReleased(InputConstants.getKey(event), event.modifiers())) {
            return true;
        }

        return super.keyReleased(event);
    }
    //? } else {
    /*
    @Override
    public final boolean keyReleased(int keyCode, int nativeCode, int modifiers) {
        if (onKeyReleased(InputConstants.getKey(keyCode, nativeCode), modifiers)) {
            return true;
        }

        return super.keyReleased(keyCode, nativeCode, modifiers);
    }
    */
    //? }

    //? if >=1.21.9 {
    @Override
    public final boolean charTyped(CharacterEvent event) {
        return onCharTyped(event.codepoint());
    }
    //? } else {
    /*
    @Override
    public final boolean charTyped(char codePoint, int modifiers) {
        return onCharTyped(codePoint);
    }
    */
    //? }

    //? if >=1.20.2 {
    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    //? } else {
    /*
    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (onMouseScrolled(mouseX, mouseY, 0.0, amount)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    */
    //? }

    //? if >=1.20.5 {
    protected void drawNitshaPanorama(GuiGraphicsExtractor ctx, float delta) {
        //? if >=26.1 {
        this.extractPanorama(ctx, delta);
        //? } else {
        /*this.renderPanorama(ctx, delta);*/
        //? }
    }

    protected void drawNitshaBlur(GuiGraphicsExtractor ctx, float delta) {
        //? if >=26.1 {
        this.extractBlurredBackground(ctx);
        //? } else if >=1.21.6 {
        /*this.renderBlurredBackground(ctx);*/
        //? } else if <=1.21.1 {
        /*this.renderBlurredBackground(delta);*/
        //? } else {
        /*this.renderBlurredBackground();*/
        //? }
    }
    //? }

    //? if >=26.1 {
    @Override
    public final void extractBackground(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        onBackground(ctx, mouseX, mouseY, delta);
    }
    //? } else {
    /*
    @Override
    public final void renderBackground(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        onBackground(ctx, mouseX, mouseY, delta);
    }
    */
    //? }

    protected void onBackground(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) { }

    protected void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        //? if >=26.1 {
        super.extractRenderState(ctx, mouseX, mouseY, delta);
        //? } else {
        /*super.render(ctx, mouseX, mouseY, delta);*/
        //? }
    }

    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    protected boolean onMouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    protected boolean onKeyPressed(int keyCode, int nativeCode, int modifiers) {
        return false;
    }

    protected boolean onKeyReleased(InputConstants.Key key, int modifiers) {
        return false;
    }

    protected boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

    protected boolean onCharTyped(int codePoint) {
        return false;
    }
}
