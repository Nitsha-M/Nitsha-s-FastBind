package com.nitsha.binds.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.gui.GUIUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemButton extends ClickableWidget {
    private ItemStack icon;
    private final Runnable onClick;
    private final int size;
    private boolean selected;
    private int iconOffset = 0;
    private final String btnKey;
    private Identifier TEXTURE;

    public ItemButton(int x, int y, int size, ItemStack icon, Runnable onClick, Identifier texture, String key) {
        super(x, y, size, size, Text.empty());
        this.icon = icon;
        this.onClick = onClick;
        this.selected = false;
        this.size = size;
        this.iconOffset = (size - 16) / 2;
        this.TEXTURE = texture;
        this.btnKey = key;
    }

    public ItemButton(int x, int y, ItemStack icon, Runnable onClick, Identifier texture, String key) {
        this(x, y, 26, icon, onClick, texture, key);
    }

    public String getKey() {
        return btnKey;
    }

    public boolean isSelected() {
        return super.isSelected();
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void setSelected(boolean cond) {
        this.selected = cond;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onClick.run();
    }

    //? if >1.20.2 {
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackground(context);
        context.drawItem(this.icon, this.getX() + iconOffset, this.getY() + iconOffset);
        //? if <1.21.6 {
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 200);
        //? }
        if (this.isHovered()) {
            this.drawSelectionBox(context);
        }
        if (this.selected) GUIUtils.adaptiveDrawTexture(context, TEXTURE, this.getX(), this.getY(), this.size, 0, this.size, this.size, this.size * 3, this.size);
        //? if <1.21.6 {
        context.getMatrices().pop();
        //? }
    }
    //? } else {
        /*@Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            this.drawBackground(context);
            context.drawItem(this.icon, this.getX() + iconOffset, this.getY() + iconOffset);
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 200);
            if (this.isHovered()) {
                this.drawSelectionBox(context);
            }
            if (this.selected) GUIUtils.adaptiveDrawTexture(context, TEXTURE, this.getX(), this.getY(), this.size, 0, this.size, this.size, this.size * 3, this.size);
            context.getMatrices().pop();
        }
    *///? }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }


    private void drawBackground(DrawContext context) {
        GUIUtils.adaptiveDrawTexture(context, TEXTURE, this.getX(), this.getY(), 0, 0, this.size, this.size, this.size * 3, this.size);
    }

    private void drawSelectionBox(DrawContext context) {
        GUIUtils.adaptiveDrawTexture(context, TEXTURE, this.getX(), this.getY(), this.size * 2, 0, this.size, this.size, this.size * 3, this.size);
    }
}
