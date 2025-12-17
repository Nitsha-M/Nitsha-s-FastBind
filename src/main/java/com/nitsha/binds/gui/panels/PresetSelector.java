package com.nitsha.binds.gui.panels;

import com.google.common.collect.Lists;
import com.nitsha.binds.Main;
import com.nitsha.binds.configs.BindsStorage;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.screen.BindsGUI;
import com.nitsha.binds.gui.utils.DrawElement;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.AnimatedWindow;
import com.nitsha.binds.gui.widget.PresetListItem;
import com.nitsha.binds.gui.widget.ScrollableWindow;
import com.nitsha.binds.gui.widget.SmallTextButton;
import com.nitsha.binds.utils.Renderable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import com.nitsha.binds.utils.RenderUtils;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class PresetSelector extends AbstractContainerEventHandler implements Renderable /*? if >=1.17 {*/ , NarratableEntry /*?}*/ {

    private static final ResourceLocation BACKGROUND = Main.id("textures/gui/btns/menu.png");

    private static final ResourceLocation SPRESET = Main.idSprite("select_preset");
    private static final ResourceLocation SPRESET_HOVER = Main.idSprite("select_preset_hover");
    private static final ResourceLocation TOP = Main.idSprite("top_normal");
    private static final ResourceLocation TOP_HOVER = Main.idSprite("top_hover");
    private static final ResourceLocation BOTTOM = Main.idSprite("bottom_normal");
    private static final ResourceLocation BOTTOM_HOVER = Main.idSprite("bottom_hover");

    private static final ResourceLocation ADD_NEW = Main.id("textures/gui/test/add_new_icon.png");

    private final List<GuiEventListener> children = Lists.<GuiEventListener>newArrayList();
    private final List<Renderable> renderables = Lists.<Renderable>newArrayList();
    private final List<DrawElement> drawElements = Lists.<DrawElement>newArrayList();

    private final List<PresetListItem> items = new ArrayList<>();

    private ScrollableWindow presetsList;

    private boolean isOpen = false;
    public BindsEditor screen;

    private final float speed = Main.GLOBAL_ANIMATION_SPEED - 0.1f;
    private int x, y;
    private float width, height;
    private float targetWidth, targetHeight;
    private long lastUpdateTime;

    private long delayMs;
    private float yOffset;
    private float alpha;
    private int globalColor = 0xFFFFFFFF;
    private int currentPreset;

    public PresetSelector(BindsEditor screen, int x, int y, float width, int height, int delay) {
        clearChildren();
        items.clear();
        this.x = x;
        this.y = y;
        this.width = this.targetWidth = width;
        this.height = this.targetHeight = height;
        this.screen = screen;

        this.yOffset = 80;
        this.delayMs = delay;
        this.alpha = 0.0f;
        this.lastUpdateTime = 0;
        currentPreset = BindsEditor.getCurrentPreset();
        initUI(screen);
    }

    private void initUI(BindsEditor screen) {
        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.truncateString(BindsEditor.getPresetName(), 13)), 0, 5, 5, "top", "left", 0xFFFFFFFF, false);
        });

        this.addElement(GUIUtils.createTexturedBtn(113, 4, 9, 9, new ResourceLocation[]{SPRESET, SPRESET_HOVER}, button -> {
            openSelector(!isOpen);
        }));
        this.addElement(GUIUtils.createTexturedBtn(91, 4, 9, 9, new ResourceLocation[]{TOP, TOP_HOVER}, button -> {
            screen.setNewPreset(-1);
        }));
        this.addElement(GUIUtils.createTexturedBtn(102, 4, 9, 9, new ResourceLocation[]{BOTTOM, BOTTOM_HOVER}, button -> {
            screen.setNewPreset(1);
        }));

        this.presetsList = new ScrollableWindow(2, 15, 2, 17, getWidth() - 4, 130, false);
        this.addElement(this.presetsList);

        generatePresetsList();

        this.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.addNew"), 4, 147, 0xFF4d9109, getWidth() - 8, "left", ADD_NEW, ()-> {
            BindsStorage.addPreset("Preset " + (items.size() + 1));
            generatePresetsList();
        }));

        this.open();
    }

    public void generatePresetsList() {
        this.presetsList.clearChildren();
        this.presetsList.setScrollableArea(0);
        this.presetsList.resetScroll();
        items.clear();
        int tY = 0;
        for (int i = 0; i < BindsStorage.presets.size(); i++) {
            int h = 22;
            PresetListItem item = new PresetListItem(this, this.presetsList, BindsStorage.presets.get(i).name, 0, tY, this.getWidth() - 4, h, i);
            this.presetsList.addElement(item);
            this.presetsList.addScrollableArea(h);
            items.add(item);
            tY += h;
        }
    }

    private void selectActivePreset(int dir) {
        currentPreset = (currentPreset + dir + items.size()) % items.size();
        screen.selectPreset(currentPreset);
    }

    public List<PresetListItem> getItems() {
        return items;
    }

    public void saveAll() {
        for (PresetListItem item : items) {
            item.savePreset();
        }
    }

    // Add new children
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
        if (drawable != null) {
            this.renderables.add(drawable);
        }
    }

    public void openSelector(boolean status) {
        isOpen = status;
        this.setHeight(isOpen ? 160 : 17);
    }

    public void open() {
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void tick() {
        long currentTime = System.currentTimeMillis();
        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
            return;
        }
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        if (delayMs > 0) {
            delayMs -= deltaTime;
            if (delayMs < 0) delayMs = 0;
            return;
        }

        float lerpFactor = 1.0f - (float) Math.pow(1.0f - speed, deltaTime / 16.666f);

        animateValues(lerpFactor);
    }


    private void animateValues(float lerpFactor) {
        float easedFactor = 1.0f - (float)Math.pow(1.0f - lerpFactor, 2.0);
        float prevHeight = this.height;

        this.width = Mth.lerp(easedFactor, this.width, targetWidth);
        this.height = Mth.lerp(easedFactor, this.height, targetHeight);
        if (Math.abs(this.width - targetWidth) < 0.1f) this.width = targetWidth;
        if (Math.abs(this.height - targetHeight) < 0.1f) {
            this.height = targetHeight;
            if (!isOpen && prevHeight > 17.1f && this.height <= 17.1f) saveAll();
        }

        this.alpha = Mth.lerp(lerpFactor, this.alpha, 1.0f);
        this.yOffset = Mth.lerp(lerpFactor, this.yOffset, 0);
        if (Math.abs(this.yOffset - 0) < 0.001f) {
            this.yOffset = 0;
            alpha = 1.0f;
        }
    }


    @Override
    public void render(
            //? if >=1.20 {
            GuiGraphics ctx
            //?} else {
            /*PoseStack ctx
             *///?}
            , int mouseX, int mouseY, float delta) {

        int alphaByte = (int)(alpha * 255.0f) & 0xFF;
        globalColor = (alphaByte << 24) | 0xFFFFFFFF;

        int xO = getX();
        int yO = getY() - Math.round(yOffset);

        GUIUtils.matricesUtil(ctx, xO, yO, 499, () -> {
            GUIUtils.drawResizableBox(ctx, BACKGROUND, 0, 0, getWidth(), getHeight(), 3, 7, globalColor);
            drawElements.forEach(element -> element.render(ctx, mouseX - xO, mouseY - yO));
        });

        renderables.forEach(element -> {
            Runnable render = () -> {
                GUIUtils.matricesUtil(ctx, xO, yO, 500, () -> {
                    element.render(ctx, mouseX - xO, mouseY - yO, delta);
                });
            };
            if (element instanceof ScrollableWindow) {
                ScrollableWindow sw = (ScrollableWindow) element;
                if (isOpen) GUIUtils.customScissor(ctx, xO, yO + 15, xO + sw.getWidth(), (isOpen) ? sw.getHeight() : 0, render);
            } else {
                GUIUtils.customScissor(ctx, xO, yO, xO + this.getWidth(), this.getHeight(), render);
            }
        });

        if (this.isOpen()) GUIUtils.drawFill(ctx, xO + 5, yO + 15, xO + getWidth() - 5, yO + 16, 0xFF555555);

        tick();
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
        float windowX = this.x;
        float windowY = this.y;
        float windowWidth = this.width;
        float windowHeight = this.height;

        return mouseX >= windowX &&
                mouseX <= windowX + windowWidth &&
                mouseY >= windowY &&
                mouseY <= windowY + windowHeight;
    }

    public boolean isMouseInsideArea(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width &&
                mouseY >= this.y && mouseY <= this.y + 17;
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isMouseInside(mouseX, mouseY)) return false;
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getY() - yOffset;
        if (isMouseInsideArea(mouseX, mouseY)) {
            int direction = verticalAmount > 0 ? -1 : 1;
            selectActivePreset(direction);
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }

        for (GuiEventListener child : children) {
            if (child.mouseScrolled(adjustedX, adjustedY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }
        return false;
    }
    //?} else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!isMouseInside(mouseX, mouseY)) return false;
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getY() - yOffset;
        if (isMouseInsideArea(mouseX, mouseY)) {
            int direction = amount > 0 ? -1 : 1;
            selectActivePreset(direction);
            Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }
        for (GuiEventListener child : children) {
            if (child.mouseScrolled(adjustedX, adjustedY, amount)) {
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
        double adjustedY = mouseY - getY() - yOffset;
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
            double adjustedY = mouseY - getY() - yOffset;  // Учитываем смещение

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
            double adjustedY = mouseY - getY() - yOffset;
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
            double adjustedY = mouseY - getY() - yOffset;

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
            double adjustedY = mouseY - getY() - yOffset;
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
                double adjustedY = mouseY - getY() - yOffset;

                for (GuiEventListener child : children) {
                    if (child.mouseDragged(adjustedX, adjustedY, button, deltaX, deltaY)) {
                        dragged = true;
                    }
                }

                return dragged;
            }
        //? }

        @Override
        //? if >=1.21.9 {
        /*public boolean keyPressed(KeyEvent event) {
            for (GuiEventListener child : children()) {
                if (child.keyPressed(event)) return true;
            }
            return false;
        }*/
        //? } else {
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                for (GuiEventListener child : children()) {
                    if (child.keyPressed(keyCode, scanCode, modifiers)) return true;
                }
                return false;
            }
        //? }

            @Override
        //? if >=1.21.9 {
        /*public boolean charTyped(CharacterEvent event) {
            for (GuiEventListener child : children()) {
                if (child.charTyped(event)) {
                    return true;
                }
            }
            return false;
        }*/
        //? } else {
            public boolean charTyped(char codePoint, int modifiers) {
                for (GuiEventListener child : children()) {
                    if (child.charTyped(codePoint, modifiers)) {
                        return true;
                    }
                }
                return false;
            }
        //? }

}
