package com.nitsha.binds.gui.widget.button;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.multiversions.TextUtils;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemButton extends NitshaAbstractWidget {

    private final Runnable onClick;
    private final int size;
    private boolean selected;
    private int iconOffset = 0;
    private String key;
    private Identifier texture;
    private ItemStack itemStack;

    public ItemButton(int x, int y, int size, ItemStack itemStack, Identifier texture, String key, Runnable onClick) {
        super(x, y, size, size, TextUtils.empty());
        this.itemStack = itemStack;
        this.onClick = onClick;
        this.selected = false;
        this.size = size;
        this.iconOffset = (size - 16) / 2;
        this.texture = texture;
        this.key = key;
    }

    public ItemButton(int x, int y, ItemStack itemStack, Identifier texture, String key, Runnable onClick) {
        this(x, y, 26, itemStack, texture, key, onClick);
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setSelected(boolean cond) {
        this.selected = cond;
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.onRender(ctx, mouseX, mouseY, delta);

        if (!visible) return;

        RenderUtils.matricesUtil(ctx, 0, 0, 1, () -> {
            RenderUtils.drawTexture(ctx, texture, this.getX(), this.getY(), 0, 0, this.size, this.size, this.size * 3, this.size);
            try {
                if (this.itemStack != null) {
                    if (itemStack.is(Items.BARRIER)) {
                        RenderUtils.drawTexture(ctx, Textures.NOT_FOUND_TEXTURE, this.getX() + iconOffset, this.getY() + iconOffset, 0, 0, 16, 16, 16, 16);
                    } else {
                        RenderUtils.drawItem(ctx, this.itemStack, this.getX() + iconOffset, this.getY() + iconOffset);
                    }
                } else {
                    RenderUtils.drawTexture(ctx, Textures.NOT_FOUND_TEXTURE, this.getX() + iconOffset, this.getY() + iconOffset, 0, 0, 16, 16, 16, 16);
                }
            } catch (Exception e) {
                RenderUtils.drawTexture(ctx, Textures.NOT_FOUND_TEXTURE, this.getX() + iconOffset, this.getY() + iconOffset, 0, 0, 16, 16, 16, 16);
            }
        });
        RenderUtils.matricesUtil(ctx, 0, 0, 200, () -> {
            if (this.isHovered()) RenderUtils.drawTexture(ctx, texture, this.getX(), this.getY(), this.size * 2, 0, this.size, this.size, this.size * 3, this.size);
            if (this.selected) RenderUtils.drawTexture(ctx, texture, this.getX(), this.getY(), this.size, 0, this.size, this.size, this.size * 3, this.size);
        });
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || button != InputConstants.MOUSE_BUTTON_LEFT || !isMouseOver(mouseX, mouseY)) {
            return false;
        }

        playDownSound(Minecraft.getInstance().getSoundManager());
        this.onClick.run();
        return true;
    }
}
