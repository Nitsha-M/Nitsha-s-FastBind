package com.nitsha.binds.gui.modals;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.utils.EventBus;
import com.nitsha.binds.utils.SearchUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;*/
//? }

public class SelectKeyEvent extends ModalWindow {
    private static final ResourceLocation TAB2_BG = Main.id("textures/gui/test/scroller.png");

    private final BindsEditor screen;
    private ScrollableWindow eventsList;

    public SelectKeyEvent(BindsEditor screen, int x, int y, int width, int height, ResourceLocation t1,
                          ResourceLocation t2) {
        super(screen, x, y, width, height, t1, t2, "nitsha.binds.advances.modals.selectKeyEvent");
        this.screen = screen;

        this.setNeedSearch(true);

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.drawResizableBox(ctx, TAB2_BG, 4, 20, this.getWidth() - 8, this.getHeight() - 27, 1, 3);
        });

        this.eventsList = new ScrollableWindow(5, 21, 5, 21, getWidth() - 10, getHeight() - 29, false);
        this.addElement(this.eventsList);

        this.getSearchField().setTypingEvent(this::generateList);

        generateList();
    }
    @Override
    public void renderWindow(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        int adjX = mouseX - this.getX();
        int adjY = mouseY - this.getYOffset();

        GUIUtils.matricesUtil(ctx, 0, 0, 200, () -> {
            super.renderWindow(ctx, mouseX, mouseY, delta);
        });
    }

    private int actionIndex = 0;
    private int actionY = 0;

    public void generateList() {
        actionY = 0;
        actionIndex = 0;
        this.eventsList.clearChildren();
        this.eventsList.setScrollableArea(0);
        this.eventsList.resetScroll();

        Map<String, List<KeyMapping>> byCategory = new LinkedHashMap<>();
        String query = this.getSearchField().getText().toLowerCase();

        for (KeyMapping key : Minecraft.getInstance().options.keyMappings) {
            byCategory.computeIfAbsent(getCategoryKey(key), k -> new ArrayList<>()).add(key);
        }

        int h = 14;
        for (Map.Entry<String, List<KeyMapping>> entry : byCategory.entrySet()) {
            String category = entry.getKey();
            List<KeyMapping> keys = entry.getValue();

            boolean categoryMatches = !query.isEmpty() && SearchUtil.matches(getCategoryLabel(category).getString().toLowerCase(), query);

            List<KeyMapping> filtered = query.isEmpty() ? keys : categoryMatches ? keys : keys.stream()
                    .filter(k -> SearchUtil.matches(k.getName().toLowerCase(), query))
                    .toList();

            if (filtered.isEmpty()) continue;

            final int capturedY = actionY;
            final String capturedCategory = category;
            this.eventsList.addDrawElement((ctx, mouseX, mouseY) -> {
                GUIUtils.drawFill(ctx, 2, capturedY + 3, this.eventsList.getWidth() - 4, capturedY + 14, 0xFF212121);
                GUIUtils.drawFill(ctx, 3, capturedY + 2, this.eventsList.getWidth() - 6, capturedY + 15, 0xFF212121);
                GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.truncateString(getCategoryLabel(capturedCategory).getString(), 26)), 0,
                        5, capturedY + 5, "top", "left", 0xFFFFFFFF, false);
            });
            actionY += 17;
            this.eventsList.addScrollableArea(17);

            for (KeyMapping key : filtered) {
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
}