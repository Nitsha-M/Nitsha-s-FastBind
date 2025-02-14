package com.nitsha.binds.gui.node;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.GUIUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

public class MenuWidget extends AbstractParentElement implements Drawable, Element, Selectable {
    private static final Identifier BG_TEXTURE = MainClass.id("textures/gui/nodes/menu.png");
    private final int x, y, width, height;
    private final List<ClickableWidget> widgets = Lists.newArrayList();
    private final List<Consumer<DrawContext>> elements = Lists.newArrayList();
    private final List<Element> children = Lists.newArrayList();

    public MenuWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addWidget(ClickableWidget widget) {
        this.widgets.add(widget);
        this.children.add(widget);
    }

    public void addDrawElement(Consumer<DrawContext> element) {
        this.elements.add(element);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        RenderSystem.enableDepthTest();
        ctx.getMatrices().push();

        // 1. Сначала рисуем фон (чуть ниже остальных элементов)
        ctx.getMatrices().translate(0, 0, 999);
        GUIUtils.drawResizableBox(ctx, BG_TEXTURE, x, y, width, height, 3, 7);
        ctx.getMatrices().pop();

        // 2. Затем рисуем элементы
        ctx.getMatrices().push();
        ctx.getMatrices().translate(0, 0, 1000);
        for (Consumer<DrawContext> element : elements) {
            element.accept(ctx);
        }

        for (ClickableWidget widget : widgets) {
            widget.render(ctx, mouseX - x, mouseY - y, delta);
        }

        ctx.getMatrices().pop();
        RenderSystem.disableDepthTest();
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ClickableWidget widget : widgets) {
            if (widget.isMouseOver(mouseX, mouseY) && widget.mouseClicked(mouseX, mouseY, button)) {
                return true; // Если хоть один обработал клик — дальше не идём
            }
        }
        return false; // Клик не обработан
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (Element child : children()) {
            if (child.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Element child : children()) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element child : children()) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (Element child : children()) {
            if (child.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
}