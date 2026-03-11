package com.nitsha.binds.gui.panels;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nitsha.binds.Main;
import com.nitsha.binds.action.ActionRegistry;
import com.nitsha.binds.action.ActionType;
import com.nitsha.binds.gui.utils.DrawElement;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.NewActionItem;
import com.nitsha.binds.gui.widget.ScrollableWindow;
import com.nitsha.binds.gui.widget.SmallTextButton;
import com.nitsha.binds.utils.RenderUtils;
import net.minecraft.client.Minecraft;
//? if >=1.20 {
import net.minecraft.client.gui.*;
//?}
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import com.nitsha.binds.utils.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class NewAction extends AbstractContainerEventHandler implements Renderable /*? if >=1.17 {*/ , NarratableEntry /*?}*/ {
    private static final ResourceLocation TOP          = Main.idSprite("top_normal");
    private static final ResourceLocation TOP_HOVER    = Main.idSprite("top_hover");
    private static final ResourceLocation BOTTOM       = Main.idSprite("bottom_normal");
    private static final ResourceLocation BOTTOM_HOVER = Main.idSprite("bottom_hover");
    private static final ResourceLocation SPRESET      = Main.idSprite("select_preset");
    private static final ResourceLocation SPRESET_HOVER = Main.idSprite("select_preset_hover");
    private static final ResourceLocation BACKGROUND   = Main.id("textures/gui/btns/menu.png");
    private static final ResourceLocation ADD_NEW      = Main.id("textures/gui/test/add_new_icon.png");

    private final List<GuiEventListener> children  = Lists.newArrayList();
    private final List<Renderable>       renderables = Lists.newArrayList();
    private final List<DrawElement>      drawElements = Lists.newArrayList();

    private ScrollableWindow actionList;

    private boolean isOpen = false;
    public AdvancedOptions parent;

    private final float speed = Main.GLOBAL_ANIMATION_SPEED - 0.1f;
    private int x, y;
    private float width, height;
    private float targetWidth, targetHeight;
    private long lastUpdateTime;

    private final List<ActionType> entries;
    private int currentAction = 0;

    public NewAction(AdvancedOptions parent, int x, int y, float width, int height) {
        clearChildren();
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = this.targetWidth = width;
        this.height = this.targetHeight = height;
        this.lastUpdateTime = 0;
        this.entries = ActionRegistry.allInstances();

        initUI();
    }

    private ActionType currentEntry() {
        return entries.get(currentAction);
    }

    private void initUI() {
        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.drawFill(ctx, 5, 5, 7, 12, currentEntry().getLineColor());
            GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.truncateString(currentEntry().getDisplayName(), 15)), 0, 9, 4, "top", "left", 0xFFFFFFFF, false);
        });

        this.addElement(GUIUtils.createTexturedBtn(this.getWidth() - 14, 4, 9, 9,
                new ResourceLocation[]{ SPRESET, SPRESET_HOVER },
                button -> openSelector(!isOpen)));

        this.addElement(GUIUtils.createTexturedBtn(this.getWidth() - 25, 4, 9, 9,
                new ResourceLocation[]{ BOTTOM, BOTTOM_HOVER },
                button -> selectActiveAction(1)));

        this.addElement(GUIUtils.createTexturedBtn(this.getWidth() - 36, 4, 9, 9,
                new ResourceLocation[]{ TOP, TOP_HOVER },
                button -> selectActiveAction(-1)));

        this.addElement(new SmallTextButton(
                TextUtils.translatable("nitsha.binds.advances.actions.addNew"),
                this.getWidth() - 38, 4, 0xFF4d9109, "right", ADD_NEW,
                () -> addAction(currentEntry())));

        this.actionList = new ScrollableWindow(2, this.getHeight() - 2, this.getX(), this.getY(), getWidth() - 4, 98, false);
        this.addElement(this.actionList);

        int tY = 0;
        for (ActionType entry : entries) {
            int h = 16;
            NewActionItem item = new NewActionItem(
                    this,
                    entry.getDisplayName(),
                    0, tY,
                    this.getWidth() - 4, h,
                    entry.getId(),
                    entry.getDefaultValue(),
                    entry.getLineColor()
            );
            this.actionList.addElement(item);
            this.actionList.addScrollableArea(h);
            tY += h;
        }
    }

    private void selectActiveAction(int dir) {
        currentAction = (currentAction + dir + entries.size()) % entries.size();
    }

    public void addAction(String typeId, String value) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getId().equals(typeId)) {
                currentAction = i;
                break;
            }
        }
        parent.addAction(typeId, value);
    }

    private void addAction(ActionType entry) {
        parent.addAction(entry.getId(), entry.getDefaultValue());
    }

    public void addDrawElement(DrawElement.Drawer drawer) {
        drawElements.add(new DrawElement(drawer));
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public int getWidth() { return Math.round(this.width); }
    public int getHeight() { return Math.round(this.height); }
    public void setWidth(float newWidth) { this.targetWidth = newWidth; }
    public void setHeight(float newHeight) { this.targetHeight = newHeight; }

    public void addElement(GuiEventListener element) {
        this.children.add(element);
        Renderable drawable = RenderUtils.wrapRenderable(element);
        if (drawable != null) this.renderables.add(drawable);
    }

    public void openSelector(boolean status) {
        isOpen = status;
        this.setHeight(isOpen ? 117 : 17);
    }

    public boolean isOpen() { return isOpen; }

    private void animateValues() {
        long currentTime = System.currentTimeMillis();
        if (lastUpdateTime == 0) { lastUpdateTime = currentTime; return; }
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        float lerpFactor = 1.0f - (float) Math.pow(1.0f - speed, deltaTime / 16.666f);
        this.width  = Mth.lerp(lerpFactor, this.width,  targetWidth);
        this.height = Mth.lerp(lerpFactor, this.height, targetHeight);

        if (Math.abs(this.width  - targetWidth)  < 0.1f) this.width  = targetWidth;
        if (Math.abs(this.height - targetHeight) < 0.1f) this.height = targetHeight;
    }

    @Override
    public void render(
            //? if >=1.20 {
            GuiGraphics ctx
            //?} else {
            /*PoseStack ctx*/
            //?}
            , int mouseX, int mouseY, float delta) {

        int xO = getX();
        int yO = getY();

        GUIUtils.matricesUtil(ctx, xO, yO, 3, () -> {
            GUIUtils.drawResizableBox(ctx, BACKGROUND, 0, 0, getWidth(), getHeight(), 3, 7);
            drawElements.forEach(element -> element.render(ctx, mouseX - xO, mouseY - yO));
        });

        //? if <1.21.4 {
        renderables.forEach(element -> {
            Runnable render = () -> GUIUtils.matricesUtil(ctx, xO, yO, 4, () ->
                    element.render(ctx, mouseX - xO, mouseY - yO, delta));
            if (element instanceof ScrollableWindow) {
                ScrollableWindow sw = (ScrollableWindow) element;
                if (isOpen) GUIUtils.customScissor(ctx,
                        parent.getX() + xO, parent.getY() + yO + 15,
                        xO + sw.getWidth(), sw.getHeight(), render);
            } else {
                render.run();
            }
        });
        //? } else {
        /*renderables.forEach(element -> {
            Runnable render = () -> GUIUtils.matricesUtil(ctx, xO, yO, 4, () ->
                    element.render(ctx, mouseX - xO, mouseY - yO, delta));
            if (element instanceof ScrollableWindow) {
                ScrollableWindow sw = (ScrollableWindow) element;
                if (isOpen) GUIUtils.customScissor(ctx,
                        xO, yO + 15,
                       xO + sw.getWidth(), sw.getHeight(), render);
            } else {
                render.run();
            }
        });*/
        //? }

        animateValues();
    }

    //? if >=1.17 {
    @Override
    public void updateNarration(NarrationElementOutput builder) {}

    @Override
    public NarrationPriority narrationPriority() { return NarrationPriority.NONE; }
    //?}

    @Override
    public List<? extends GuiEventListener> children() { return children; }

    public void clearChildren() {
        children.clear();
        renderables.clear();
        drawElements.clear();
    }

    public void removeElementsOfType(Class<?> type) {
        children.removeIf(type::isInstance);
        renderables.removeIf(type::isInstance);
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        float windowX = this.parent.getX() + this.getX();
        float windowY = this.parent.getY() + this.getY();
        return mouseX >= windowX && mouseX <= windowX + getWidth()
                && mouseY >= windowY && mouseY <= windowY + getHeight();
    }

    public boolean isMouseInsideArea(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width
                && mouseY >= this.y && mouseY <= this.y + 17;
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (isMouseInsideArea(mouseX, mouseY)) {
            selectActiveAction(verticalAmount > 0 ? -1 : 1);
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }
        for (GuiEventListener child : children) {
            if (child.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        }
        return false;
    }
    //?} else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isMouseInsideArea(mouseX, mouseY)) {
            selectActiveAction(amount > 0 ? -1 : 1);
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }
        for (GuiEventListener child : children) {
            if (child.mouseScrolled(mouseX, mouseY, amount)) return true;
        }
        return false;
    }*/
    //?}

    @Override
            //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        double adjustedX = event.x() - getX();
        double adjustedY = event.y() - getY();
        MouseButtonEvent adjustedEvent = new MouseButtonEvent(adjustedX, adjustedY, event.buttonInfo());
        for (GuiEventListener child : children) {
            if (child.mouseClicked(adjustedEvent, bl)) return true;
        }
        return false;
    }*/
            //? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getY();
        for (GuiEventListener child : children) {
            if (child.mouseClicked(adjustedX, adjustedY, button)) return true;
        }
        return false;
    }
    //? }

    @Override
            //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
        double adjustedX = event.x() - getX();
        double adjustedY = event.y() - getY();
        MouseButtonEvent adjustedEvent = new MouseButtonEvent(adjustedX, adjustedY, event.buttonInfo());
        boolean released = false;
        for (GuiEventListener child : children) {
            if (child.mouseReleased(adjustedEvent)) released = true;
        }
        return released;
    }*/
            //? } else {
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getY();
        boolean released = false;
        for (GuiEventListener child : children) {
            if (child.mouseReleased(adjustedX, adjustedY, button)) released = true;
        }
        return released;
    }
    //? }

    @Override
            //? if >=1.21.9 {
    /*public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        double adjustedX = event.x() - getX();
        double adjustedY = event.y() - getY();
        MouseButtonEvent adjustedEvent = new MouseButtonEvent(adjustedX, adjustedY, event.buttonInfo());
        boolean dragged = false;
        for (GuiEventListener child : children) {
            if (child.mouseDragged(adjustedEvent, deltaX, deltaY)) dragged = true;
        }
        return dragged;
    }*/
            //? } else {
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getY();
        boolean dragged = false;
        for (GuiEventListener child : children) {
            if (child.mouseDragged(adjustedX, adjustedY, button, deltaX, deltaY)) dragged = true;
        }
        return dragged;
    }
    //? }
}