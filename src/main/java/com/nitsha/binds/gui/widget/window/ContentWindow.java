package com.nitsha.binds.gui.widget.window;

import com.google.common.collect.Lists;
import com.nitsha.binds.gui.utils.DrawElement;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.utils.RenderUtils;
import com.nitsha.binds.utils.Renderable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class ContentWindow extends AbstractContainerEventHandler implements Renderable, GuiEventListener /*? if >=1.17 {*/ , NarratableEntry /*?}*/ {
    private final Map<GuiEventListener, Renderable> elementToRenderable = new HashMap<>();
    private final List<GuiEventListener> children = Lists.<GuiEventListener>newArrayList();
    private final List<Renderable> renderables = Lists.<Renderable>newArrayList();
    private final List<DrawElement> drawElementsTop = Lists.<DrawElement>newArrayList();
    private final List<DrawElement> drawElementsBottom = Lists.<DrawElement>newArrayList();

    private int x, y, width, height;
    private boolean visible = false;

    public ContentWindow(int x, int y, int width, int height) {
        clearChildren();;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Add new children
    public void addDrawElement(DrawElement.Drawer drawer, int position) {
        if(position == 0) drawElementsBottom.add(new DrawElement(drawer));
        if(position == 1) drawElementsTop.add(new DrawElement(drawer));
    }

    public void addDrawElement(DrawElement.Drawer drawer) {
        this.addDrawElement(drawer, 0);
    }

    public void addElement(GuiEventListener element) {
        this.children.add(element);
        Renderable r = RenderUtils.wrapRenderable(element);
        if (r != null) {
            this.renderables.add(r);
            this.elementToRenderable.put(element, r);
        }
    }

    // Basic parameters: get/set coords or size
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    //? if >=26.1 {
    // public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
    //? } else {
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
    //? }
        renderWindow(ctx, mouseX, mouseY, delta);
    }

    public void renderWindow(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        int xO = getX();
        int yO = getY();

        if (visible) {
            GUIUtils.matricesUtil(ctx, xO, yO, 2, () -> {
                drawElementsBottom.forEach(element -> element.render(ctx, mouseX - xO, mouseY - yO));
            });

            renderables.forEach(element -> {
                Runnable render = () -> {
                    GUIUtils.matricesUtil(ctx, xO, yO, 2, () -> {
                        //? if >=26.1 {
                        // element.extractRenderState(ctx, mouseX - xO, mouseY - yO, delta);
                        //? } else {
                        element.render(ctx, mouseX - xO, mouseY - yO, delta);
                        //? }
                    });
                };
                if (element instanceof ScrollableWindow) {
                    ScrollableWindow sw = (ScrollableWindow) element;
                    GUIUtils.customScissor(ctx,xO + sw.getX(), yO + sw.getY(), sw.getWidth(), sw.getHeight(), render);
                } else {
                    render.run();
                }
            });

            GUIUtils.matricesUtil(ctx, xO, yO, 2, () -> {
                drawElementsTop.forEach(element -> element.render(ctx, mouseX - xO, mouseY - yO));
            });

        }
    }

    //? if >=1.17 {
    @Override
    public void updateNarration(NarrationElementOutput builder) { }
    //?}

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    public void removeElement(GuiEventListener element) {
        children.remove(element);
        Renderable r = elementToRenderable.remove(element);
        if (r != null) {
            renderables.remove(r);
        }
    }

    public void clearChildren() {
        children.clear();
        renderables.clear();
        drawElementsTop.clear();
        drawElementsBottom.clear();
        elementToRenderable.clear();
    }

    public void removeElementsOfType(Class<?> type) {
        List<GuiEventListener> toKeep = new ArrayList<>();
        for (GuiEventListener child : children) {
            if (!type.isInstance(child)) {
                toKeep.add(child);
            }
        }

        children.clear();
        renderables.clear();

        for (GuiEventListener child : toKeep) {
            children.add(child);
            Renderable r = RenderUtils.wrapRenderable(child);
            if (r != null) {
                renderables.add(r);
            }
        }
    }


    //? if >=1.17 {
    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }
    //?}

    public boolean isMouseInside(double mouseX, double mouseY) {
        if (!visible) return false;
        float windowX = getX();
        float windowY = getY();
        float windowWidth = getWidth();
        float windowHeight = getHeight();

        return mouseX >= windowX &&
                mouseX <= windowX + windowWidth &&
                mouseY >= windowY &&
                mouseY <= windowY + windowHeight;
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (!visible) return false;
        for (GuiEventListener child : children) {
            if (child.mouseClicked(event, bl)) {
                return true;
            }
        }
        return false;
    }*/
    //? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        for (GuiEventListener child : children) {
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }
    //? }
    @Override
    //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
        if (!visible) return false;
        boolean released = false;

        for (GuiEventListener child : children) {
            if (child.mouseReleased(event)) {
                released = true;
            }
        }
        return released;
    }*/
    //? } else {
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        boolean released = false;
        for (GuiEventListener child : children) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                released = true;
            }
        }
        return released;
    }
    //? }
    @Override
    //? if >=1.21.9 {
    /*public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (!visible) return false;
        boolean dragged = false;
        for (GuiEventListener child : children) {
            if (child.mouseDragged(event, deltaX, deltaY)) {
                dragged = true;
            }
        }
        return dragged;
    }*/
    //? } else {
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!visible) return false;
        boolean dragged = false;
        for (GuiEventListener child : children) {
            if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
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

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isVisible() || !isMouseInside(mouseX, mouseY)) return false;

        for (GuiEventListener child : children()) {
            if (child.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    //?} else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!isVisible() || !isMouseInside(mouseX, mouseY)) return false;

        for (GuiEventListener child : children()) {
            if (child.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    *///?}
}