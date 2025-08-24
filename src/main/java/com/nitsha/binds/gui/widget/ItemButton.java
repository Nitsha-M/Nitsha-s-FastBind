package com.nitsha.binds.gui.widget;

import com.nitsha.binds.gui.utils.GUIUtils;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
import net.minecraft.client.util.math.MatrixStack;
//? if >=1.17 {
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
//? }
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.systems.RenderSystem;

public class ItemButton extends ClickableWidget {
    private ItemStack icon;
    private final Runnable onClick;
    private final int size;
    private boolean selected;
    private int iconOffset = 0;
    private final String btnKey;
    private Identifier TEXTURE;

    private int x, y;

    public ItemButton(int x, int y, int size, ItemStack icon, Runnable onClick, Identifier texture, String key) {
        super(x, y, size, size, Text.of(""));
        this.icon = icon;
        this.onClick = onClick;
        this.selected = false;
        this.size = size;
        this.iconOffset = (size - 16) / 2;
        this.TEXTURE = texture;
        this.btnKey = key;
        this.x = x;
        this.y = y;
    }

    public ItemButton(int x, int y, ItemStack icon, Runnable onClick, Identifier texture, String key) {
        this(x, y, 26, icon, onClick, texture, key);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public String getKey() {
        return btnKey;
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
    public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        rndr(ctx);
    }
    //? } else if >=1.20 {
    /*@Override
    public void renderButton(DrawContext ctx, int mouseX, int mouseY, float delta) {
        rndr(ctx);
    }
    *///? } else {
    /*@Override
    public void renderButton(MatrixStack ctx, int mouseX, int mouseY, float delta) {
        rndr(ctx);
    }
    *///? }

    private void rndr(Object ctx) {
        //? if >=1.20 {
        DrawContext c = (DrawContext) ctx;
        //? } else {
        /*MatrixStack c = (MatrixStack) ctx;
         *///? }
        //? <1.17 {
        // RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
        //? }
        GUIUtils.matricesUtil(c, 0, 0, 1, () -> {
            GUIUtils.adaptiveDrawTexture(c, TEXTURE, this.getX(), this.getY(), 0, 0, this.size, this.size, this.size * 3, this.size);
            GUIUtils.drawItem(c, this.icon, this.getX() + iconOffset, this.getY() + iconOffset);
        });

        GUIUtils.matricesUtil(c, 0, 0, 200, () -> {
            if (this.hovered) GUIUtils.adaptiveDrawTexture(c, TEXTURE, this.getX(), this.getY(), this.size * 2, 0, this.size, this.size, this.size * 3, this.size);
            if (this.selected) GUIUtils.adaptiveDrawTexture(c, TEXTURE, this.getX(), this.getY(), this.size, 0, this.size, this.size, this.size * 3, this.size);
        });
    }

    //? if >=1.19.3 {
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
    //? } else if >=1.17 {
    /*@Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }*/
    //? }
}
