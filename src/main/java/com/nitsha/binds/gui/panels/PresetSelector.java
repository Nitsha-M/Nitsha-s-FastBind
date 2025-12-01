package com.nitsha.binds.gui.panels;

import com.nitsha.binds.Main;
import com.nitsha.binds.configs.BindsStorage;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.screen.BindsGUI;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.AnimatedWindow;
import com.nitsha.binds.gui.widget.PresetListItem;
import com.nitsha.binds.gui.widget.ScrollableWindow;
import com.nitsha.binds.gui.widget.SmallTextButton;
import com.mojang.blaze3d.vertex.PoseStack;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PresetSelector extends AnimatedWindow {
    private static final ResourceLocation SPRESET = Main.idSprite("select_preset");
    private static final ResourceLocation SPRESET_HOVER = Main.idSprite("select_preset_hover");
    private static final ResourceLocation TOP = Main.idSprite("top_normal");
    private static final ResourceLocation TOP_HOVER = Main.idSprite("top_hover");
    private static final ResourceLocation BOTTOM = Main.idSprite("bottom_normal");
    private static final ResourceLocation BOTTOM_HOVER = Main.idSprite("bottom_hover");

    private static final ResourceLocation ADD_NEW = Main.id("textures/gui/test/add_new_icon.png");

    private final List<PresetListItem> items = new ArrayList<>();

    private boolean isOpen = false;
    public BindsEditor screen;

    private ScrollableWindow presetsList;

    public PresetSelector(BindsEditor screen, float x, float y, float width, int height, ResourceLocation t1, ResourceLocation t2, int delay) {
        super(x, y, width, height, t1, t2, delay);
        items.clear();
        this.screen = screen;
        initUI(screen);
    }

    private void initUI(BindsEditor screen) {
        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.addText(ctx, Component.literal(GUIUtils.truncateString(BindsEditor.getPresetName(), 15)), 0, 5, 5, "top", "left", 0xFFFFFFFF, false);
            if (this.isOpen()) GUIUtils.drawFill(ctx, 5, 15, getWidth() - 5, 16, 0xFF555555);
        });

        this.addElement(GUIUtils.createTexturedBtn(113, 4, 9, 9, new ResourceLocation[]{SPRESET, SPRESET_HOVER}, button -> {
            openSelector(!isOpen);
        }));
        this.addElement(GUIUtils.createTexturedBtn(91, 4, 9, 9, new ResourceLocation[]{TOP, TOP_HOVER}, button -> {
            screen.setNewPreset(-1);
        }));
        this.addElement(GUIUtils.createTexturedBtn(102, 4, 9, 9, new ResourceLocation[]{BOTTOM, BOTTOM_HOVER}, button -> {
            screen.setNewPreset(1);
        }));

        this.presetsList = new ScrollableWindow(2, this.getHeight(), 2, 17, getWidth() - 4, 130, false);
        this.addElement(this.presetsList);

        generatePresetsList();

        this.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.addNew"), 4, 151, 0xFF4d9109, getWidth() - 8, "left", ADD_NEW, ()-> {
            BindsStorage.addPreset("Preset " + (items.size() + 1));
            generatePresetsList();
        }));

        this.open(() -> {});
    }

    public void generatePresetsList() {
        this.presetsList.clearChildren();
        this.presetsList.setScrollableArea(0);
        this.presetsList.resetScroll();
        items.clear();
        int tY = 0;
        for (int i = 0; i < BindsStorage.presets.size(); i++) {
            int h = 20;
            PresetListItem item = new PresetListItem(this, BindsStorage.presets.get(i).name, 0, tY, this.getWidth() - 4, h, i);
            this.presetsList.addElement(item);
            this.presetsList.addScrollableArea(h);
            items.add(item);
            tY += h;
        }
    }

    public List<PresetListItem> getItems() {
        return items;
    }

    public void openSelector(boolean status) {
        isOpen = status;
        if (!isOpen) saveAll();
        this.setHeight(isOpen ? 166 : 19);
    }

    public void saveAll() {
        for (PresetListItem item : items) {
            item.savePreset();
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void render(
            //? if >=1.20 {
            GuiGraphics ctx
            //?} else {
            /*PoseStack ctx
             *///?}
            , int mouseX, int mouseY, float delta) {
        GUIUtils.customScissor(ctx, getX(), getYOffset(), getWidth(), getHeight(), () -> {
            GUIUtils.matricesUtil(ctx, 0, 0, 500, () -> {
                super.render(ctx, mouseX, mouseY, delta);
            });
        });
    }
}
