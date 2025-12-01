package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiComponent;
 *///?}
import com.mojang.blaze3d.vertex.PoseStack;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Objects;

public class ItemButton extends AbstractWidget {
    private ItemStack icon;
    private final Runnable onClick;
    private final int size;
    private boolean selected;
    private int iconOffset = 0;
    private final String btnKey;
    private ResourceLocation TEXTURE;
    private ResourceLocation NOT_FOUND_TEXTURE = Main.id("textures/gui/not_found.png");

    private int x, y;

    public ItemButton(int x, int y, int size, ItemStack icon, Runnable onClick, ResourceLocation texture, String key) {
        super(x, y, size, size, Component.literal(""));
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

    public ItemButton(int x, int y, ItemStack icon, Runnable onClick, ResourceLocation texture, String key) {
        this(x, y, 26, icon, onClick, texture, key);
    }

    public int getXPos() {
        return this.x;
    }

    public int getYPos() {
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
    protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        rndr(ctx);
    }
    //?} else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        rndr(ctx);
    }
    *///?} else {
    /*@Override
    public void renderWidget(PoseStack ctx, int mouseX, int mouseY, float delta) {
        rndr(ctx);
    }
    *///?}

    private void rndr(Object ctx) {
        //? if >=1.20 {
        GuiGraphics c = (GuiGraphics) ctx;
        //?} else {
        /*PoseStack c = (PoseStack) ctx;
         *///?}
        //? <1.17 {
        // RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
        //?}
        GUIUtils.matricesUtil(c, 0, 0, 1, () -> {
            GUIUtils.adaptiveDrawTexture(c, TEXTURE, this.getXPos(), this.getYPos(), 0, 0, this.size, this.size, this.size * 3, this.size);
            try {
                if (ItemStack.isSameItem(this.icon, new ItemStack(Items.BARRIER))) {
                    GUIUtils.adaptiveDrawTexture(c, NOT_FOUND_TEXTURE, this.getXPos() + iconOffset, this.getYPos() + iconOffset, 0, 0, 16, 16, 16, 16);
                } else {
                    GUIUtils.drawItem(c, this.icon, this.getXPos() + iconOffset, this.getYPos() + iconOffset);
                }
            } catch (Exception e) {
                GUIUtils.adaptiveDrawTexture(c, NOT_FOUND_TEXTURE, this.getXPos() + iconOffset, this.getYPos() + iconOffset, 0, 0, 16, 16, 16, 16);
            }
        });

        GUIUtils.matricesUtil(c, 0, 0, 200, () -> {
            if (this.isHovered()) GUIUtils.adaptiveDrawTexture(c, TEXTURE, this.getXPos(), this.getYPos(), this.size * 2, 0, this.size, this.size, this.size * 3, this.size);
            if (this.selected) GUIUtils.adaptiveDrawTexture(c, TEXTURE, this.getXPos(), this.getYPos(), this.size, 0, this.size, this.size, this.size * 3, this.size);
        });
    }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    //?} else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) {
    }*/
    //?}
}
