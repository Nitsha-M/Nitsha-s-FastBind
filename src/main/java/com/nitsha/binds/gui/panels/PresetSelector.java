package com.nitsha.binds.gui.panels;

import com.nitsha.binds.MainClass;
import com.nitsha.binds.configs.BindsConfig;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.AnimatedWindow;
import com.nitsha.binds.gui.widget.PresetListItem;
import net.minecraft.client.MinecraftClient;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PresetSelector extends AnimatedWindow {
    private static final Identifier SPRESET = MainClass.idSprite("select_preset");
    private static final Identifier SPRESET_HOVER = MainClass.idSprite("select_preset_hover");
    private static final Identifier TOP = MainClass.idSprite("top_normal");
    private static final Identifier TOP_HOVER = MainClass.idSprite("top_hover");
    private static final Identifier BOTTOM = MainClass.idSprite("bottom_normal");
    private static final Identifier BOTTOM_HOVER = MainClass.idSprite("bottom_hover");
    private final List<String> presetNames = (List<String>) BindsConfig.configs.get("presets");

    private final List<PresetListItem> items = new ArrayList<>();

    private boolean isOpen = false;
    public BindsEditor screen;

    public PresetSelector(BindsEditor screen, float x, float y, float width, int height, Identifier t1, Identifier t2, int delay) {
        super(x, y, width, height, t1, t2, delay);
        items.clear();
        this.screen = screen;
        initUI(screen);
    }

    private void initUI(BindsEditor screen) {
        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.addText(ctx, Text.of(GUIUtils.truncateString(BindsEditor.getPresetName(), 15)), 0, 5, 5, "top", "left", 0xFFFFFFFF, false);
        });

        this.addElement(GUIUtils.createTexturedBtn(113, 4, 9, 9, new Identifier[]{SPRESET, SPRESET_HOVER}, button -> {
            openSelector(!isOpen);
        }));
        this.addElement(GUIUtils.createTexturedBtn(91, 4, 9, 9, new Identifier[]{TOP, TOP_HOVER}, button -> {
            screen.setNewPreset(-1);
        }));
        this.addElement(GUIUtils.createTexturedBtn(102, 4, 9, 9, new Identifier[]{BOTTOM, BOTTOM_HOVER}, button -> {
            screen.setNewPreset(1);
        }));

        int tY = 15;
        for (int i = 0; i < 9; i++) {
            int h = 20;
            PresetListItem item = new PresetListItem(this, presetNames.get(i), 2, tY, this.getWidth() - 4, h, i);
            this.addElement(item);
            items.add(item);
            tY += h;
        }

        this.open(() -> {});
    }

    public List<PresetListItem> getItems() {
        return items;
    }

    public void openSelector(boolean status) {
        isOpen = status;
        if (!isOpen) saveAll();
        this.setHeight(isOpen ? 202 : 19);
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
            DrawContext ctx
            //? } else {
            /*MatrixStack ctx
             *///? }
            , int mouseX, int mouseY, float delta) {
        GUIUtils.customScissor(ctx, getX(), getYOffset(), getWidth(), getHeight(), () -> {
            GUIUtils.matricesUtil(ctx, 0, 0, 500, () -> {
                super.render(ctx, mouseX, mouseY, delta);
            });
        });
    }
}
