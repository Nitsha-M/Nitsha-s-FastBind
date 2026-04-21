package com.nitsha.binds.gui.modals;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.IconSelector;
import com.nitsha.binds.gui.widget.KeyEventItem;
import com.nitsha.binds.gui.widget.ModalWindow;
import com.nitsha.binds.gui.widget.ScrollableWindow;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;*/
//? }

public class SelectIcon extends ModalWindow {
//    private static final ResourceLocation TAB2_BG = Main.id("textures/gui/test/scroller.png");

    private final BindsEditor screen;
    private IconSelector selector;

    public SelectIcon(BindsEditor screen, int x, int y, int width, int height, ResourceLocation t1,
                      ResourceLocation t2) {
        super(screen, x, y, width, height, t1, t2, "nitsha.binds.advances.modals.choose_icon");
        this.screen = screen;

        this.selector = new IconSelector(4, 20, 162, 144, (stack, key) -> {
            onSelect(key);
        });

//        this.addDrawElement((ctx, mouseX, mouseY) -> {
//            GUIUtils.drawResizableBox(ctx, TAB2_BG, 4, 20, this.getWidth() - 8, this.getHeight() - 27, 1, 3);
//        });

        this.addElement(this.selector);

    }
    @Override
    public void renderWindow(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        int adjX = mouseX - this.getX();
        int adjY = mouseY - this.getYOffset();

        GUIUtils.matricesUtil(ctx, 0, 0, 200, () -> {
            super.renderWindow(ctx, mouseX, mouseY, delta);
        });
    }
    @Override
    public void open(Runnable onFinish) {
        super.open(onFinish);
    }

    public void onSelect(String key) {
        EventBus.emit("selectIcon.result", key);
        this.close(() -> {});
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                && mouseY >= this.getY() - 16 && mouseY < this.getY() + this.getHeight();
    }
}