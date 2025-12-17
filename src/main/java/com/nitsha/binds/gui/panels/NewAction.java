package com.nitsha.binds.gui.panels;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nitsha.binds.Main;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class NewAction extends AbstractContainerEventHandler implements Renderable /*? if >=1.17 {*/ , NarratableEntry /*?}*/ {
    private static final ResourceLocation TOP = Main.idSprite("top_normal");
    private static final ResourceLocation TOP_HOVER = Main.idSprite("top_hover");
    private static final ResourceLocation BOTTOM = Main.idSprite("bottom_normal");
    private static final ResourceLocation BOTTOM_HOVER = Main.idSprite("bottom_hover");
    private static final ResourceLocation SPRESET = Main.idSprite("select_preset");
    private static final ResourceLocation SPRESET_HOVER = Main.idSprite("select_preset_hover");

    private static final ResourceLocation BACKGROUND = Main.id("textures/gui/btns/menu.png");
    private static final ResourceLocation ADD_NEW = Main.id("textures/gui/test/add_new_icon.png");

    private final List<GuiEventListener> children = Lists.<GuiEventListener>newArrayList();
    private final List<Renderable> renderables = Lists.<Renderable>newArrayList();
    private final List<DrawElement> drawElements = Lists.<DrawElement>newArrayList();

    private ScrollableWindow actionList;

    private boolean isOpen = false;
    public AdvancedOptions parent;

    private final float speed = Main.GLOBAL_ANIMATION_SPEED - 0.1f;
    private int x, y;
    private float width, height;
    private float targetWidth, targetHeight;
    private long lastUpdateTime;

    private final Object[][] actionsEntry = new Object[][] {
            { TextUtils.translatable("nitsha.binds.advances.actions.command").getString(), 1, "", 0xFF4e8605 },
            { TextUtils.translatable("nitsha.binds.advances.actions.delay").getString(), 2, "100", 0xFFc99212 },
            { TextUtils.translatable("nitsha.binds.advances.actions.pressKey").getString(), 3, "0", 0xFF790e06 },
            { TextUtils.translatable("nitsha.binds.advances.actions.showMessage").getString(), 4, "", 0xFF174eff },
            { TextUtils.translatable("nitsha.binds.advances.actions.showTitle").getString(), 5, "", 0xFF5facfa }
    };
    private int currentAction = 0;
    private String currentActionName = (String) actionsEntry[currentAction][0];
    private int currentActionColor = (int) actionsEntry[currentAction][3];

    public NewAction(AdvancedOptions parent, int x, int y, float width, int height) {
        clearChildren();
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = this.targetWidth = width;
        this.height = this.targetHeight = height;
        this.lastUpdateTime = 0;
        initUI();
    }

    private void initUI() {
        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.drawFill(ctx, 5, 5, 7, 12, currentActionColor);
            GUIUtils.addText(ctx, TextUtils.literal(currentActionName), 0, 9, 4, "top", "left", 0xFFFFFFFF, false);
        });

        this.addElement(GUIUtils.createTexturedBtn(this.getWidth() - 14, 4, 9, 9, new ResourceLocation[]{SPRESET, SPRESET_HOVER}, button -> {
            openSelector(!isOpen);
        }));
        this.addElement(GUIUtils.createTexturedBtn(this.getWidth() - 25, 4, 9, 9, new ResourceLocation[]{BOTTOM, BOTTOM_HOVER}, button -> {
            selectActiveAction(1);
        }));
        this.addElement(GUIUtils.createTexturedBtn(this.getWidth() - 36, 4, 9, 9, new ResourceLocation[]{TOP, TOP_HOVER}, button -> {
            selectActiveAction(-1);
        }));
        this.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.advances.actions.addNew"), this.getWidth() - 38, 4, 0xFF4d9109, "right", ADD_NEW, ()-> {
            addAction(currentActionName, (int) actionsEntry[currentAction][1], (String) actionsEntry[currentAction][2]);
        }));

        this.actionList = new ScrollableWindow(2, this.getHeight() - 2, this.getX(), this.getY(), getWidth() - 4, 98, false);
        this.addElement(this.actionList);

        int tY = 0;
        for (Object[] entry : actionsEntry) {
            String title = (String) entry[0];
            int type = (int) entry[1];
            String value = (String) entry[2];
            int color = (int) entry[3];

            int h = 16;
            NewActionItem item = new NewActionItem(this, title, 0, tY, this.getWidth() - 4, h, type, value, color);
            this.actionList.addElement(item);
            this.actionList.addScrollableArea(h);

            tY += h;
        }
    }

    private void selectActiveAction(int dir) {
        currentAction = (currentAction + dir + actionsEntry.length) % actionsEntry.length;
        currentActionName = (String) actionsEntry[currentAction][0];
        currentActionColor = (int) actionsEntry[currentAction][3];
    }

    public void addAction(String title, int type, String value) {
        currentActionName = title;
        currentAction = type - 1;
        currentActionColor = (int) actionsEntry[currentAction][3];
        parent.addAction(type, value);
    }

    // Add new children
    public void addDrawElement(DrawElement.Drawer drawer) {
        drawElements.add(new DrawElement(drawer));
    }

    // Basic parameters: get/set coords or size
    public int getX() { return this.x; }
    public int getY() { return this.y; }

    public int getWidth() { return Math.round(this.width); }
    public int getHeight() { return Math.round(this.height); }

    public void setWidth(float newWidth) { this.targetWidth = newWidth; }
    public void setHeight(float newHeight) { this.targetHeight = newHeight; }

    public void addElement(GuiEventListener element) {
        this.children.add(element);
        Renderable drawable = RenderUtils.wrapRenderable(element);
        if (drawable != null) {
            this.renderables.add(drawable);
        }
    }

    public void openSelector(boolean status) {
        isOpen = status;
        this.setHeight(isOpen ? 117 : 17);
    }

    public boolean isOpen() {
        return isOpen;
    }

    private void animateValues() {
        long currentTime = System.currentTimeMillis();
        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
            return;
        }
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        float lerpFactor = 1.0f - (float) Math.pow(1.0f - speed, deltaTime / 16.666f);
        this.width = Mth.lerp(lerpFactor, this.width, targetWidth);
        this.height = Mth.lerp(lerpFactor, this.height, targetHeight);

        if (Math.abs(this.width - targetWidth) < 0.1f) this.width = targetWidth;
        if (Math.abs(this.height - targetHeight) < 0.1f) this.height = targetHeight;
    }

    @Override
    public void render(
            //? if >=1.20 {
            GuiGraphics ctx
            //?} else {
            /*PoseStack ctx
             *///?}
            , int mouseX, int mouseY, float delta) {

        int xO = getX();
        int yO = getY();

        GUIUtils.matricesUtil(ctx, xO, yO, 3, () -> {
            GUIUtils.drawResizableBox(ctx, BACKGROUND, 0, 0, getWidth(), getHeight(), 3, 7);
            drawElements.forEach(element -> element.render(ctx, mouseX - xO, mouseY - yO));
        });


        //? if <1.21.4 {
        renderables.forEach(element -> {
            Runnable render = () -> {
                GUIUtils.matricesUtil(ctx, xO, yO, 4, () -> {
                    element.render(ctx, mouseX - xO, mouseY - yO, delta);
                });
            };
            if (element instanceof ScrollableWindow) {
                ScrollableWindow sw = (ScrollableWindow) element;
                if (isOpen) GUIUtils.customScissor(ctx, parent.getX() + xO, parent.getY() + yO + 15, xO + sw.getWidth(), (isOpen) ? sw.getHeight() : 0, render);
            } else {
                render.run();
            }
        });
        //? } else {
        renderables.forEach(element -> {
            Runnable render = () -> {
                GUIUtils.matricesUtil(ctx, xO, yO, 4, () -> {
                    element.render(ctx, mouseX - xO, mouseY - yO, delta);
                });
            };
            if (element instanceof ScrollableWindow) {
                ScrollableWindow sw = (ScrollableWindow) element;
                if (isOpen)
                    GUIUtils.matricesUtil(ctx, xO, yO, 4, () -> {
                        sw.render(ctx, mouseX - xO, mouseY - yO, delta);
                    });
            } else {
                render.run();
            }
        });
        //? }

        animateValues();
    }

    //? if >=1.17 {
    @Override
    public void updateNarration(NarrationElementOutput builder) {

    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }
    //?}

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

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
        float windowWidth = this.getWidth();
        float windowHeight = this.getHeight();

        return mouseX >= windowX &&
                mouseX <= windowX + windowWidth &&
                mouseY >= windowY &&
                mouseY <= windowY + windowHeight;
    }

    public boolean isMouseInsideArea(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + 17;
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (isMouseInsideArea(mouseX, mouseY)) {
            int direction = verticalAmount > 0 ? -1 : 1;
            selectActiveAction(direction);
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }
        for (GuiEventListener child : children) {
            if (child.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }
        return false;
    }
    //?} else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isMouseInsideArea(mouseX, mouseY)) {
            int direction = amount > 0 ? -1 : 1;
            selectActiveAction(direction);
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }
        for (GuiEventListener child : children) {
            if (child.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        return false;
    }
    *///?}


    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        double mouseX = event.x();
        double mouseY = event.y();
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getY();

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(
            adjustedX,
            adjustedY,
            event.buttonInfo()
        );

        for (GuiEventListener child : children) {
            if (child.mouseClicked(adjustedEvent, bl)) {
                return true;
            }
        }

        return false;
    }*/
    //? } else {
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double adjustedX = mouseX - getX();
            double adjustedY = mouseY - getY();

            for (GuiEventListener child : children) {
                if (child.mouseClicked(adjustedX, adjustedY, button)) {
                    return true;
                }
            }

            return false;
        }
    //? }

        @Override
    //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
        boolean released = false;
        double mouseX = event.x();
        double mouseY = event.y();
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getY();

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(
            adjustedX,
            adjustedY,
            event.buttonInfo()
        );

        for (GuiEventListener child : children) {
            if (child.mouseReleased(adjustedEvent)) {
                released = true;
            }
        }

        return released;
    }*/
    //? } else {
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            boolean released = false;
            double adjustedX = mouseX - getX();
            double adjustedY = mouseY - getY();

            for (GuiEventListener child : children) {
                if (child.mouseReleased(adjustedX, adjustedY, button)) {
                    released = true;
                }
            }

            return released;
        }
    //? }

        @Override
    //? if >=1.21.9 {
    /*public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        boolean dragged = false;
        double mouseX = event.x();
        double mouseY = event.y();
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getY();

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(
            adjustedX,
            adjustedY,
            event.buttonInfo()
        );

        for (GuiEventListener child : children) {
            if (child.mouseDragged(adjustedEvent, deltaX, deltaY)) {
                dragged = true;
            }
        }

        return dragged;
    }*/
    //? } else {
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            boolean dragged = false;
            double adjustedX = mouseX - getX();
            double adjustedY = mouseY - getY();

            for (GuiEventListener child : children) {
                if (child.mouseDragged(adjustedX, adjustedY, button, deltaX, deltaY)) {
                    dragged = true;
                }
            }

            return dragged;
        }
    //? }
}