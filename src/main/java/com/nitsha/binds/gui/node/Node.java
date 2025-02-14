package com.nitsha.binds.gui.node;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.GUIUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

public class Node extends AbstractParentElement implements Drawable, Element, Selectable {

    // Textures
    private static final Identifier NODE_ITEM = MainClass.id("textures/gui/nodes/node_item.png");
    private static final Identifier NODE_ITEM_LAST = MainClass.id("textures/gui/nodes/node_item_last.png");
    private static final Identifier CONNECTOR = MainClass.id("textures/gui/nodes/connector.png");
    private static final Identifier BORDER = MainClass.id("textures/gui/nodes/node_active_border.png");

    int x, y, oX, oY;
    int originalX, originalY;
    int index;
    private final Identifier HEADER_TEXTURE;
    private final String NAME;
    private final List<String[]> PARAMS;
    private int NODE_WIDTH = 70;
    private int TOTAL_HEIGHT;
    boolean dragging = false;
    int dragOffsetX, dragOffsetY;

    private boolean isActive = false;

    Node(int index, int x, int y, int oX, int oY, String name, String color, List<String[]> params) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.oX = oX;
        this.oY = oY;
        this.originalX = x + oX;
        this.originalY = y + oY;
        this.HEADER_TEXTURE = MainClass.id("textures/gui/nodes/header_" + color + ".png");
        this.NAME = name;
        this.PARAMS = params;
    }

    void render(DrawContext ctx, int oX1, int oY1) {
        RenderSystem.enableDepthTest();
        ctx.getMatrices().push();
        ctx.getMatrices().translate(0, 0, isActive ? 998 : index + 1);
        int startX = x + (oX - oX1);
        int startY = y + (oY - oY1);
        GUIUtils.drawResizableBox(ctx, HEADER_TEXTURE, startX, startY, NODE_WIDTH, 20, 2, 5);
        GUIUtils.addText(ctx, Text.of(NAME), 0, startX + 6, startY + 6);

        // GENERATING LIST
        int i = 0;
        int itemY = startY + 20;
        TOTAL_HEIGHT = 20;
        for (String[] param : PARAMS) {
            i++;
            GUIUtils.drawResizableBox(ctx, (i == PARAMS.size()) ? NODE_ITEM_LAST : NODE_ITEM, startX, itemY, NODE_WIDTH, (i == PARAMS.size()) ? 18 : 17, 3, 7);
            int textX = startX + 6;
            if (Objects.equals(param[0], "input")) {
                ctx.drawTexture(RenderLayer::getGuiTextured, CONNECTOR, startX + 6, itemY + 6, 0, 0, 5, 5, 5, 5);
                textX += 9;
            }
            if (Objects.equals(param[0], "output")) {
                ctx.drawTexture(RenderLayer::getGuiTextured, CONNECTOR, startX + NODE_WIDTH - 11, itemY + 6, 0, 0, 5, 5, 5, 5);
            }
            GUIUtils.addText(ctx, Text.of(param[1]), 0, textX, itemY + 4);
            itemY += 17;
            TOTAL_HEIGHT += (i == PARAMS.size()) ? 18 : 17;
        }

        // ACTIVE BORDER
        if (isActive) {
            GUIUtils.drawResizableBox(ctx, BORDER, startX - 1, startY - 1, NODE_WIDTH + 2, TOTAL_HEIGHT + 2, 3, 7);
        }
        ctx.getMatrices().pop();
        RenderSystem.disableDepthTest();
    }

    public int getWidth() {
        return NODE_WIDTH;
    }

    public void setWidth(int w) {
        NODE_WIDTH = w;
    }

    public boolean isClicked(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + NODE_WIDTH
                && mouseY >= y && mouseY <= y + 20;
    }

    public void setActive(boolean active) {
        this.isActive = active;
        System.out.println("Node " + (active ? "Activated" : "Deactivated"));
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public List<? extends Element> children() {
        return List.of();
    }

    @Override
    public SelectionType getType() {
        return null;
    }
}