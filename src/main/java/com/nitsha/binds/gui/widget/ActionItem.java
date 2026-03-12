package com.nitsha.binds.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.action.ActionRegistry;
import com.nitsha.binds.action.ActionType;
import com.nitsha.binds.gui.panels.AdvancedOptions;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;*/
//?}
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

import java.util.Map;

public class ActionItem extends AbstractButton {

    private final TexturedButton topBtn;
    private final TexturedButton bottomBtn;
    private final TexturedButton resetBtn;
    private final TexturedButton deleteBtn;

    private static final ResourceLocation TOP          = Main.idSprite("action_top_normal");
    private static final ResourceLocation TOP_HOVER    = Main.idSprite("action_top_hover");
    private static final ResourceLocation BOTTOM       = Main.idSprite("action_bottom_normal");
    private static final ResourceLocation BOTTOM_HOVER = Main.idSprite("action_bottom_hover");
    private static final ResourceLocation RESET        = Main.idSprite("action_reset_normal");
    private static final ResourceLocation RESET_HOVER  = Main.idSprite("action_reset_hover");
    private static final ResourceLocation DELETE       = Main.idSprite("action_delete_normal");
    private static final ResourceLocation DELETE_HOVER = Main.idSprite("action_delete_hover");
    private static final ResourceLocation ARROW_DISABLED = Main.id("textures/gui/test/arrow_disabled.png");

    private final int index;
    private final int x, y;

    private final ActionType action;

    public ActionItem(AdvancedOptions parent, String typeId, int x, int y, int width, int height, Object value, int index) {
        super(x, y, width, height, TextUtils.empty());
        this.index = index;
        this.x = x;
        this.y = y;

        this.action = ActionRegistry.createById(typeId);
        this.action.init(x, y, width, value);

        this.topBtn = GUIUtils.createTexturedBtn(x + width - 9, y + 3, 9, 9,
                new ResourceLocation[]{ TOP, TOP_HOVER },
                button -> { if (index > 0) parent.moveAction(index, -1); });

        this.bottomBtn = GUIUtils.createTexturedBtn(x + width - 9, y + 13, 9, 9,
                new ResourceLocation[]{ BOTTOM, BOTTOM_HOVER },
                button -> { if (BindsEditor.getCBind().actions.size() != index + 1) parent.moveAction(index, 1); });

        this.resetBtn = GUIUtils.createTexturedBtn(x + width - 25, y + 3, 15, 9,
                new ResourceLocation[]{ RESET, RESET_HOVER },
                button -> action.reset());

        this.deleteBtn = GUIUtils.createTexturedBtn(x + width - 25, y + 13, 15, 9,
                new ResourceLocation[]{ DELETE, DELETE_HOVER },
                button -> parent.removeAction(index));
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }

    public Map<String, Object> getValue() {
        return action.getValue();
    }

    public boolean isMouseOverColorButtons(double mouseX, double mouseY) {
        return action.isMouseOverColorButtons(mouseX, mouseY);
    }

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
    //? } else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }*/
    //? } else if >=1.19.4 {
    /*@Override
    public void renderWidget(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }*/
    //? } else {
    /*@Override
    public void renderButton(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }*/
    //? }

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        GuiGraphics c = (GuiGraphics) ctx;
        //? } else {
        /*PoseStack c = (PoseStack) ctx;*/
        //? }

        if (index % 2 == 1)
            GUIUtils.drawFill(c, getX() - 4, getY(), getX() + getWidth() + 4, getY() + getHeight(), 0x4DFFFFFF);

        this.resetBtn.render(c, mouseX, mouseY, delta);
        this.deleteBtn.render(c, mouseX, mouseY, delta);
        GUIUtils.adaptiveDrawTexture(ctx, ARROW_DISABLED, getX() + getWidth() - 9, getY() + 3, 0, 0, 9, 19, 9, 19);

        int aS = BindsEditor.getCBind().actions.size();
        if (aS > 0) {
            if (index > 0)
                this.topBtn.render(c, mouseX, mouseY, delta);
            if (index != (aS - 1))
                this.bottomBtn.render(c, mouseX, mouseY, delta);
        }

        action.render(ctx, mouseX, mouseY, delta);

        GUIUtils.drawFill(ctx, getX() - 3, getY() + 8, getX() - 1, getY() + getHeight() - 8, action.getLineColor());
    }

    @Override
            //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        boolean clicked = action.mouseClicked(event, bl);
        if (this.deleteBtn.mouseClicked(event, bl)) clicked = true;
        if (this.bottomBtn.mouseClicked(event, bl)) clicked = true;
        if (this.resetBtn.mouseClicked(event, bl))  clicked = true;
        if (this.topBtn.mouseClicked(event, bl))    clicked = true;
        return clicked;
    }*/
            //? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clicked = action.mouseClicked(mouseX, mouseY, button);
        if (this.deleteBtn.mouseClicked(mouseX, mouseY, button)) clicked = true;
        if (this.bottomBtn.mouseClicked(mouseX, mouseY, button)) clicked = true;
        if (this.resetBtn.mouseClicked(mouseX, mouseY, button))  clicked = true;
        if (this.topBtn.mouseClicked(mouseX, mouseY, button))    clicked = true;
        return clicked;
    }
    //? }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
        boolean clicked = action.mouseReleased(event);
        if (this.deleteBtn.mouseReleased(event)) clicked = true;
        if (this.bottomBtn.mouseReleased(event)) clicked = true;
        if (this.resetBtn.mouseReleased(event))  clicked = true;
        if (this.topBtn.mouseReleased(event))    clicked = true;
        return clicked;
    }*/
            //? } else {
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean clicked = action.mouseReleased(mouseX, mouseY, button);
        if (this.deleteBtn.mouseReleased(mouseX, mouseY, button)) clicked = true;
        if (this.bottomBtn.mouseReleased(mouseX, mouseY, button)) clicked = true;
        if (this.resetBtn.mouseReleased(mouseX, mouseY, button))  clicked = true;
        if (this.topBtn.mouseReleased(mouseX, mouseY, button))    clicked = true;
        return clicked;
    }
    //? }

    @Override
            //? if >=1.21.9 {
    /*public boolean keyPressed(KeyEvent event) {
        return action.keyPressed(event) || super.keyPressed(event);
    }*/
            //? } else {
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return action.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }
    //? }

    @Override
            //? if >=1.21.9 {
    /*public boolean charTyped(CharacterEvent event) {
        return action.charTyped(event) || super.charTyped(event);
    }*/
            //? } else {
    public boolean charTyped(char codePoint, int modifiers) {
        return action.charTyped(codePoint, modifiers) || super.charTyped(codePoint, modifiers);
    }
    //? }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (action.mouseScrolled(mouseX, mouseY, verticalAmount)) return true;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    //? } else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (action.mouseScrolled(mouseX, mouseY, amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }*/
    //? }

    @Override
    public void playDownSound(SoundManager soundManager) {}

    @Override
            //? if >=1.21.9 {
    /*public void onPress(InputWithModifiers inputWithModifiers) {}*/
            //? } else {
    public void onPress() {}
    //? }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}
    //? } else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) {}*/
    //? }
}