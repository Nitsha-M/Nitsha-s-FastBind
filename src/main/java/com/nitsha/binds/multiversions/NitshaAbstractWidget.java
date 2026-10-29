package com.nitsha.binds.multiversions;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
//? if >=1.21.9 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
//? }

public class NitshaAbstractWidget extends AbstractWidget {

    public NitshaAbstractWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public int getX() {
        //? if >=1.19.3 {
        return super.getX();
        //? } else {
        /*return this.x;*/
        //? }
    }

    public int getY() {
        //? if >=1.19.3 {
        return super.getY();
        //? } else {
        /*return this.y;*/
        //? }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    protected final void extractWidgetRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        onRender(ctx, mouseX, mouseY, delta);
    }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) { }
    //?} else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) { }*/
    //?}

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
        return onMouseDragged(event.x(), event.y(), event.button(), deltaX, deltaY);
    }
    //? } else {
    /*
    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    */
    //? }

    //? if >=1.21.9 {
    @Override
    public final boolean charTyped(CharacterEvent event) {
        if (onCharTyped(event.codepoint())) {
            return true;
        }

        return super.charTyped(event);
    }
    //? } else {
    /*
    @Override
    public final boolean charTyped(char codePoint, int modifiers) {
        if (onCharTyped(codePoint)) {
            return true;
        }

        return super.charTyped(codePoint, modifiers);
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
    /*
    @Override
    public final boolean keyPressed(int keyCode, int nativeCode, int modifiers) {
        if (onKeyPressed(keyCode, nativeCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, nativeCode, modifiers);
    }
    */
    //? }

    //? if >=1.20.2 {
    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    //? } else {
    /*@Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return onMouseScrolled(mouseX, mouseY, 0.0, amount);
    }
    *///? }

    protected void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        this.isHovered = visible && isMouseOver(mouseX, mouseY);

        //? if >=1.21.6 {
        if (this.isHovered) {
            int screenMouseX = (int) Math.floor(ctx.pose().m00() * mouseX + ctx.pose().m10() * mouseY + ctx.pose().m20());
            int screenMouseY = (int) Math.floor(ctx.pose().m01() * mouseX + ctx.pose().m11() * mouseY + ctx.pose().m21());
            this.isHovered = ctx.containsPointInScissor(screenMouseX, screenMouseY);
        }
        //? }
    }
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    protected boolean onMouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
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
