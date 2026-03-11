package com.nitsha.binds.gui.modals;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
 *///?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;*/
//? }

public class SelectKeyEvent extends ModalWindow {
    private static final ResourceLocation TAB2_BG = Main.id("textures/gui/test/scroller.png");

    private final List<TabButton> tabsBtn = new ArrayList<>();

    private final BindsEditor screen;
    private ScrollableWindow eventsList;

    public SelectKeyEvent(BindsEditor screen, int x, int y, int width, int height, ResourceLocation t1,
                          ResourceLocation t2) {
        super(screen, x, y, width, height, t1, t2, "nitsha.binds.advances.modals.selectKeyEvent");
        this.screen = screen;

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.drawResizableBox(ctx, TAB2_BG, 4, 20, this.getWidth() - 8, this.getHeight() - 27, 1, 3);
        });

        this.eventsList = new ScrollableWindow(5, 21, 5, 21, getWidth() - 10, getHeight() - 29, false);
        this.addElement(this.eventsList);

        generateList();
    }
    @Override
    public void render(
            //? if >=1.20 {
            GuiGraphics ctx
            //? } else {
            /*PoseStack ctx*/
            //? }
            , int mouseX, int mouseY, float delta) {
        int adjX = mouseX - this.getX();
        int adjY = mouseY - this.getYOffset();

        GUIUtils.matricesUtil(ctx, 0, 0, 200, () -> {
            super.render(ctx, mouseX, mouseY, delta);
        });
    }

    private int actionIndex = 0;
    private int actionY = 0;

    public void generateList() {
        actionY = 0;
        this.eventsList.clearChildren();
        this.eventsList.setScrollableArea(0);
        this.eventsList.resetScroll();

        Map<String, List<KeyMapping>> byCategory = new LinkedHashMap<>();

        for (KeyMapping key : Minecraft.getInstance().options.keyMappings) {
            byCategory.computeIfAbsent(getCategoryKey(key), k -> new ArrayList<>()).add(key);
        }

        int h = 14;
        for (Map.Entry<String, List<KeyMapping>> entry : byCategory.entrySet()) {
            actionIndex = 0;
            String category = entry.getKey();
            List<KeyMapping> keys = entry.getValue();

            final int capturedY = actionY;
            final String capturedCategory = category;

            System.out.println(capturedCategory);

            this.eventsList.addDrawElement((ctx, mouseX, mouseY) -> {
                GUIUtils.drawFill(ctx, 2, capturedY + 3, this.eventsList.getWidth() - 4, capturedY + 14, 0xFF212121);
                GUIUtils.drawFill(ctx, 3, capturedY + 2, this.eventsList.getWidth() - 6, capturedY + 15, 0xFF212121);
                GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.truncateString(getCategoryLabel(capturedCategory).getString(), 26)), 0,
                        5, capturedY + 5, "top", "left", 0xFFFFFFFF, false);
            });
            actionY += 17;
            this.eventsList.addScrollableArea(17);

            for (KeyMapping key : keys) {
                KeyEventItem item = new KeyEventItem(this, 0, actionY, getWidth(), h, key.getName(), actionIndex);
                this.eventsList.addElement(item);
                this.eventsList.addScrollableArea(h);
                actionIndex++;
                actionY += h;
            }
        }

        this.eventsList.setScrollableArea(actionY);
    }

    private String getCategoryKey(KeyMapping key) {
        //? if >=1.21.9 {
        /*return key.getCategory().label().getString();*/
        //? } else {
        return key.getCategory();
        //? }
    }

    private Component getCategoryLabel(String category) {
        //? if >=1.21.9 {
        /*return TextUtils.literal(category);*/
        //? } else {
        return TextUtils.translatable(category);
        //? }
    }

    @Override
    public void open(Runnable onFinish) {
        this.eventsList.resetScroll();
        super.open(onFinish);
    }

    public void onSelect(String key) {
        EventBus.emit("selectKeyEvent.result", key);
        this.close(() -> {});
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                && mouseY >= this.getY() - 16 && mouseY < this.getY() + this.getHeight();
    }
}