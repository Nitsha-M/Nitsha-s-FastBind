package com.nitsha.binds.multiversions;

import com.nitsha.binds.gui.utils.DrawElement;
import com.nitsha.binds.utils.Renderable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//? if >=1.17 {
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
//? }

//? if >=1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;
//? }

public class NitshaContainer extends AbstractContainerEventHandler implements Renderable /*? if >=1.17 {*/ , NarratableEntry /*?}*/ {

    private final List<GuiEventListener> children = new ArrayList<>();
    private final Map<GuiEventListener, Renderable> renderables = new HashMap<>();
    private final List<DrawElement> drawElementsBottom = new ArrayList<>();
    private final List<DrawElement> drawElementsTop = new ArrayList<>();

    protected float x;
    protected float y;
    protected float width;
    protected float height;
    private boolean visible;

    private int mouseModifiers;
    private boolean doubleClick;

    public NitshaContainer() {
    }

    public NitshaContainer(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return Math.round(x);
    }

    public int getY() {
        return Math.round(y);
    }

    public int getWidth() {
        return Math.round(width);
    }

    public int getHeight() {
        return Math.round(height);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected int getContentX() {
        return getX();
    }

    protected int getContentY() {
        return getY();
    }

    protected boolean canClick() {
        return isVisible();
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        if (!isVisible()) return false;

        return mouseX >= getContentX()
                && mouseX < getContentX() + getWidth()
                && mouseY >= getContentY()
                && mouseY < getContentY() + getHeight();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!isVisible()) return false;
        if (isMouseInside(mouseX, mouseY)) return true;

        double localX = mouseX - getContentX();
        double localY = mouseY - getContentY();

        for (GuiEventListener child : children) {
            if (child.isMouseOver(localX, localY)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    public void addElement(GuiEventListener element) {
        if (children.contains(element)) return;

        children.add(element);

        Renderable renderable = com.nitsha.binds.utils.RenderUtils.wrapRenderable(element);

        if (renderable != null) {
            renderables.put(element, renderable);
        }
    }

    public void removeElement(GuiEventListener element) {
        if (getFocused() == element) {
            setFocused(null);
            setDragging(false);
        }

        children.remove(element);
        renderables.remove(element);
    }

    public void removeElementsOfType(Class<?> type) {
        for (int i = children.size() - 1; i >= 0; i--) {
            GuiEventListener child = children.get(i);

            if (type.isInstance(child)) {
                removeElement(child);
            }
        }
    }

    public void clearChildren() {
        setFocused(null);
        setDragging(false);
        children.clear();
        renderables.clear();
        drawElementsBottom.clear();
        drawElementsTop.clear();
    }

    public void addDrawElement(DrawElement.Drawer drawer, int position) {
        if (position == 0) {
            drawElementsBottom.add(new DrawElement(drawer));
        } else if (position == 1) {
            drawElementsTop.add(new DrawElement(drawer));
        }
    }

    public void addDrawElement(DrawElement.Drawer drawer) {
        addDrawElement(drawer, 0);
    }

    //? if >=26.1 {
    @Override
    public final void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        onRender(ctx, mouseX, mouseY, delta);
    }
    //? } else {
    /*
    @Override
    public final void render(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        onRender(ctx, mouseX, mouseY, delta);
    }
    */
    //? }

    protected void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        renderWindow(ctx, mouseX, mouseY, delta);
    }

    public void renderWindow(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        if (!isVisible()) return;

        int contentX = getContentX();
        int contentY = getContentY();
        int localX = mouseX - contentX;
        int localY = mouseY - contentY;

        RenderUtils.matricesUtil(ctx, contentX, contentY, 2, () -> {
            renderBackground(ctx, localX, localY, delta);

            for (DrawElement element : drawElementsBottom) {
                element.render(ctx, localX, localY);
            }
        });

        for (GuiEventListener child : children) {
            Renderable renderable = renderables.get(child);

            if (renderable != null) {
                renderChild(ctx, child, renderable, contentX, contentY, localX, localY, delta);
            }
        }

        RenderUtils.matricesUtil(ctx, contentX, contentY, 2, () -> {
            for (DrawElement element : drawElementsTop) {
                element.render(ctx, localX, localY);
            }
        });
    }

    protected void renderBackground(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
    }

    protected void renderChild(GuiGraphicsExtractor ctx, GuiEventListener child, Renderable renderable, int contentX, int contentY, int mouseX, int mouseY, float delta) {
        RenderUtils.matricesUtil(ctx, contentX, contentY, 2, () -> {
            //? if >=26.1 {
            renderable.extractRenderState(ctx, mouseX, mouseY, delta);
            //? } else {
            /*renderable.render(ctx, mouseX, mouseY, delta);*/
            //? }
        });
    }

    //? if >=1.21.9 {
    @Override
    public final boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int previousModifiers = mouseModifiers;
        boolean previousDoubleClick = this.doubleClick;

        mouseModifiers = event.buttonInfo().modifiers();
        this.doubleClick = doubleClick;

        try {
            return onMouseClicked(event.x(), event.y(), event.button());
        } finally {
            mouseModifiers = previousModifiers;
            this.doubleClick = previousDoubleClick;
        }
    }

    @Override
    public final boolean mouseReleased(MouseButtonEvent event) {
        int previousModifiers = mouseModifiers;
        mouseModifiers = event.buttonInfo().modifiers();

        try {
            return onMouseReleased(event.x(), event.y(), event.button());
        } finally {
            mouseModifiers = previousModifiers;
        }
    }

    @Override
    public final boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        int previousModifiers = mouseModifiers;
        mouseModifiers = event.buttonInfo().modifiers();

        try {
            return onMouseDragged(event.x(), event.y(), event.button(), deltaX, deltaY);
        } finally {
            mouseModifiers = previousModifiers;
        }
    }
    //? } else {
    /*
    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        return onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        return onMouseReleased(mouseX, mouseY, button);
    }

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    */
    //? }

    //? if >=1.20.2 {
    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    //? } else {
    /*
    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return onMouseScrolled(mouseX, mouseY, 0.0, amount);
    }
    */
    //? }

    //? if >=1.21.9 {
    @Override
    public final boolean keyPressed(KeyEvent event) {
        //$ if >=26.3 'int nativeCode = event.keycode();' else 'int nativeCode = event.scancode();'
        int nativeCode = event.keycode();

        return onKeyPressed(event.key(), nativeCode, event.modifiers());
    }

    @Override
    public final boolean charTyped(CharacterEvent event) {
        return onCharTyped(event.codepoint());
    }
    //? } else {
    /*
    @Override
    public final boolean keyPressed(int keyCode, int nativeCode, int modifiers) {
        return onKeyPressed(keyCode, nativeCode, modifiers);
    }

    @Override
    public final boolean charTyped(char codePoint, int modifiers) {
        return onCharTyped(codePoint);
    }
    */
    //? }

    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!canClick()) return false;

        return NitshaInput.mouseClicked(children, mouseX - getContentX(), mouseY - getContentY(), button, mouseModifiers, doubleClick);
    }

    protected boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (!isVisible()) return false;

        return NitshaInput.mouseReleased(children, mouseX - getContentX(), mouseY - getContentY(), button, mouseModifiers);
    }

    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isVisible()) return false;

        return NitshaInput.mouseDragged(children, mouseX - getContentX(), mouseY - getContentY(), button, mouseModifiers, deltaX, deltaY);
    }

    protected boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isMouseInside(mouseX, mouseY)) return false;

        return NitshaInput.mouseScrolled(children, mouseX - getContentX(), mouseY - getContentY(), horizontalAmount, verticalAmount);
    }

    protected boolean onKeyPressed(int keyCode, int nativeCode, int modifiers) {
        if (!isVisible()) return false;

        return NitshaInput.keyPressed(children, keyCode, nativeCode, modifiers);
    }

    protected boolean onCharTyped(int codePoint) {
        if (!isVisible()) return false;

        return NitshaInput.charTyped(children, codePoint);
    }

    //? if >=1.17 {
    @Override
    public void updateNarration(NarrationElementOutput builder) {
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }
    //? }
}