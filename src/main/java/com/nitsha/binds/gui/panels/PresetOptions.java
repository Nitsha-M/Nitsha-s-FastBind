package com.nitsha.binds.gui.panels;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.Main;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.dto.preset.BindData;
import com.nitsha.binds.configs.dto.preset.PageData;
import com.nitsha.binds.configs.dto.preset.PresetData;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.gui.widget.SteppedSlider;
import com.nitsha.binds.gui.widget.button.BedrockIconButton;
import com.nitsha.binds.gui.widget.button.SmallTextButton;
import com.nitsha.binds.gui.widget.button.TexturedButton;
import com.nitsha.binds.gui.widget.window.AnimatedWindow;
import com.nitsha.binds.multiversions.NitshaContainer;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import com.nitsha.binds.utils.CodecUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class PresetOptions extends AnimatedWindow {

    private static final int HEADER_HEIGHT = 19;
    private final int expandedHeight;
    private final NitshaContainer content;
    private TexturedButton expandButton;
    private TexturedButton collapseButton;
    private boolean isOpen;

    private BedrockIconButton importBtn;
    private BedrockIconButton exportBtn;

    // Textures
    private static final Identifier DELETE_SMALL = Main.id("textures/gui/test/delete_small_icon.png");
    private static final Identifier SURE_SMALL = Main.id("textures/gui/test/sure_small.png");
    private static final Identifier ADD_NEW = Main.id("textures/gui/test/add_new_icon.png");

    private static final Identifier SEPARATOR = Main.id("textures/gui/separator.png");

    private SmallTextButton deleteBtn;
    private long deleteConfirmationTime = 0;
    private boolean deleteConfirmShown = false;

    private SteppedSlider rows;
    private SteppedSlider columns;
    private boolean syncingSliders;

    public PresetOptions(BindsEditor screen, float x, int y, float width, int height, Identifier t1, Identifier t2, int delay) {
        super(x, y, width, HEADER_HEIGHT, t1, t2, delay);
        this.expandedHeight = HEADER_HEIGHT + 120;
        this.content = new NitshaContainer(0, HEADER_HEIGHT - 4, width, expandedHeight - HEADER_HEIGHT - 4);
        initUI(screen);
    }

    private void initUI(BindsEditor screen) {
        addDrawElement((ctx, mouseX, mouseY) -> {
            RenderUtils.drawText(ctx, TextUtils.small(TextUtils.literal("preset options")), 0, 5, 4, "left", "top", 0xFFFFFFFF, false);
            if (isOpen) RenderUtils.drawFill(ctx, 4, 15, getWidth() - 4, 16, 0xFF555555);
        });
        expandButton = new TexturedButton(getWidth() - 14, 4, 9, 9, Textures.ICON_SMALL_BOTTOM, Textures.ICON_SMALL_BOTTOM_HOVER, button -> openSelector(true));
        collapseButton = new TexturedButton(getWidth() - 14, 4, 9, 9, Textures.ICON_SMALL_TOP, Textures.ICON_SMALL_TOP_HOVER, button -> openSelector(false));
        collapseButton.visible = false;
        addElement(expandButton);
        addElement(collapseButton);
        content.setVisible(false);
        addElement(content);

        content.addDrawElement((ctx, mouseX, mouseY) -> {
            int pageCount = BindsEditor.activePreset != null ? BindsEditor.activePreset.pages.size() : 1;
            MutableComponent pages = TextUtils.small(TextUtils.translatable("nitsha.binds.totalPages", pageCount));
            MutableComponent columns = TextUtils.small(TextUtils.translatable("nitsha.binds.columns", Math.round(this.columns.getValue())));
            MutableComponent rows = TextUtils.small(TextUtils.translatable("nitsha.binds.rows", Math.round(this.rows.getValue())));

            RenderUtils.drawText(ctx, pages, 126, 4, 3, "left", "top", 0xFFFFFFFF, false);
            RenderUtils.drawText(ctx, rows, 0, 4, 14, "left", "top", 0xFFFFFFFF, false);
            RenderUtils.drawText(ctx, columns, 0, 4, 40, "left", "top", 0xFFFFFFFF, false);

//            RenderUtils.drawFill(ctx, 0,  0, content.getWidth(), content.getHeight(), 0xFFFF0000);
        });

        this.deleteBtn = new SmallTextButton(TextUtils.empty(), getWidth() - 9 - 9 - 4 - 2, 3, 0xFF790e06, 9, "left", DELETE_SMALL, ()-> {
            if (deleteConfirmShown) {
                int currentPage = BindsEditor.getCurrentPage();
                PresetData preset = BindsEditor.activePreset;
                if (preset == null || preset.pages.size() <= 1) return;

                preset.pages.remove(currentPage);
                for (int pageIndex = currentPage; pageIndex < preset.pages.size(); pageIndex++) {
                    PageData page = preset.pages.get(pageIndex);
                    if (page.binds == null) continue;

                    int offset = BindsEditor.getFirstBindOfPage(pageIndex + 1) - BindsEditor.getFirstBindOfPage(pageIndex);
                    for (BindData bind : page.binds) {
                        bind.index -= offset;
                    }
                }

                currentPage = Math.min(currentPage, preset.pages.size() - 1);
                screen.selectPage(currentPage);
                BindsEditor.setActiveBind(BindsEditor.getFirstBindOfPage(currentPage));
                Storage.savePreset(preset, preset.id);
                screen.getBindsListWindow().generateButtons(7, 31);
                screen.selectBind();
                confirm(false);
            } else {
                deleteConfirmationTime = System.currentTimeMillis();
                confirm(true);
            }
        });

        content.addElement(new SmallTextButton(TextUtils.empty(), getWidth() - 9 - 4, 3, 0xFF4d9109, 9, "left", ADD_NEW, ()-> {
            if (BindsEditor.activePreset != null) {
                BindsEditor.activePreset.pages.add(new PageData());
                Storage.savePreset(BindsEditor.activePreset, BindsEditor.activePresetId);
            }
            deleteBtn.setEnabled(true);
        }));

        content.addElement(deleteBtn);

        this.rows = new SteppedSlider(4, 26, this.getWidth() - 8, 12, true, PresetData.MIN_ROWS, PresetData.MAX_ROWS, 1, BindsEditor.activePreset.rows);
        this.rows.setBgColor(0xFF555555);
        this.rows.setChangeListener(value -> {
            if (!syncingSliders && BindsEditor.activePreset != null) {
                screen.setPresetGridSize(BindsEditor.activePreset.columns, Math.round(value));
            }
        });

        this.columns = new SteppedSlider(4, 52, this.getWidth() - 8, 12, true, PresetData.MIN_COLUMNS, PresetData.MAX_COLUMNS, 1, BindsEditor.activePreset.columns);
        this.columns.setBgColor(0xFF555555);
        this.columns.setChangeListener(value -> {
            if (!syncingSliders && BindsEditor.activePreset != null) {
                screen.setPresetGridSize(Math.round(value), BindsEditor.activePreset.rows);
            }
        });

        content.addElement(rows);
        content.addElement(columns);

        int transferWidth = (content.getWidth() - 8) / 2;
        int importX = 4 + transferWidth;
        int importWidth = content.getWidth() - importX - 4;

        BedrockIconButton exportPageBtn = new BedrockIconButton(4, 66, transferWidth, 20, Textures.ICON_EXPORT, true, () -> {
            if (BindsEditor.activePreset == null) return;

            screen.saveBind();
            int pageIndex = BindsEditor.getCurrentPage();
            PageData page = BindsEditor.activePreset.pages.get(pageIndex);
            String encodedPage = CodecUtil.exportPageToText(page, BindsEditor.getFirstBindOfPage(pageIndex));

            if (encodedPage != null) {
                Minecraft.getInstance().keyboardHandler.setClipboard(encodedPage);
            }
        });
        exportPageBtn.setButtonDirection(1);
        exportPageBtn.setHoverTooltip(TextUtils.literal("Export current page"));

        BedrockIconButton importPageBtn = new BedrockIconButton(importX, 66, importWidth, 20, Textures.ICON_IMPORT, true, () -> {
            PresetData preset = BindsEditor.activePreset;
            if (preset == null) return;

            int pageIndex = preset.pages.size();
            String clipboardText = Minecraft.getInstance().keyboardHandler.getClipboard();
            PageData importedPage = CodecUtil.importPageFromText(clipboardText, BindsEditor.getFirstBindOfPage(pageIndex));
            if (importedPage == null) return;

            screen.saveBind();
            preset.pages.add(importedPage);
            Storage.savePreset(preset, preset.id);
            screen.selectPage(pageIndex);
            BindsEditor.setActiveBind(BindsEditor.getFirstBindOfPage(pageIndex));
            screen.getBindsListWindow().generateButtons(7, 31);
            screen.selectBind();
            confirm(false);
        }, 0xFF0569CE, 0xFF0776E6, 0xFFFFFFFF, 0xFFFFFFFF);
        importPageBtn.setButtonDirection(2);
        importPageBtn.setHoverTooltip(TextUtils.literal("Import page as a new page"));
        importPageBtn.setNeighbor(exportPageBtn);
        exportPageBtn.setNeighbor(importPageBtn);
        content.addElement(exportPageBtn);
        content.addElement(importPageBtn);

        this.exportBtn = new BedrockIconButton(4, 88, transferWidth, 20, Textures.ICON_EXPORT, true, () -> {
            screen.saveBind();

            PresetData currentPreset = BindsEditor.activePreset;

            String encodedBind = CodecUtil.exportToText(currentPreset);

            if (encodedBind != null) {
                Minecraft.getInstance().keyboardHandler.setClipboard(encodedBind);
            }
        });
        this.exportBtn.setButtonDirection(1);
        this.exportBtn.setHoverTooltip(TextUtils.literal("Export preset"));

        this.importBtn = new BedrockIconButton(importX, 88, importWidth, 20, Textures.ICON_IMPORT, true, () -> {
            String clipboardText = Minecraft.getInstance().keyboardHandler.getClipboard();
            PresetData importedPreset = CodecUtil.importFromText(clipboardText, PresetData.class);

            if (importedPreset == null) return;

            screen.saveBind();
            screen.getPresetSelectorWindow().saveAll();

            Storage.addPreset(importedPreset);
            screen.getPresetSelectorWindow().generatePresetsList();
            screen.selectPreset(importedPreset.id);
        }, 0xFF0569CE, 0xFF0776E6, 0xFFFFFFFF, 0xFFFFFFFF);
        this.importBtn.setButtonDirection(2);
        this.importBtn.setHoverTooltip(TextUtils.literal("Import preset"));

        this.importBtn.setNeighbor(this.exportBtn);
        this.exportBtn.setNeighbor(this.importBtn);

        content.addElement(this.importBtn);
        content.addElement(this.exportBtn);

        this.open(() -> {});
    }

    public void syncPreset() {
        if (BindsEditor.activePreset == null) return;

        syncingSliders = true;
        try {
            rows.setValue(BindsEditor.activePreset.rows);
            columns.setValue(BindsEditor.activePreset.columns);
        } finally {
            syncingSliders = false;
        }
    }

    public void confirm(boolean status) {
        deleteConfirmShown = status;
        if (status) {
            deleteBtn.setColors(0xFFfac70c, 0xFFF5CC35, 0xFFFFFFFF, 0xFFFFFFFF);
            deleteBtn.setIcon(SURE_SMALL);
        } else {
            deleteBtn.setColors(0xFF790e06, 0xFF8C150B, 0xFFFFFFFF, 0xFFFFFFFF);
            deleteBtn.setIcon(DELETE_SMALL);
        }
    }

    public boolean isDeleteConfirmation() {
        return System.currentTimeMillis() - deleteConfirmationTime < 5000;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void openSelector(boolean open) {
        if (isOpen == open) return;

        isOpen = open;
        setHeight(open ? expandedHeight : HEADER_HEIGHT);
        content.setVisible(open);
        expandButton.visible = !open;
        collapseButton.visible = open;
        if (!open) confirm(false);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isMouseInside(mouseX, mouseY);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseInside(mouseX, mouseY)) return false;

        super.onMouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isMouseInside(mouseX, mouseY)) return false;

        super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        return true;
    }

    @Override
    public void renderWindow(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.customScissor(ctx, getContentX(), getContentY(), getWidth(), getHeight(), () -> {
            super.renderWindow(ctx, mouseX, mouseY, delta);
        });
    }

    @Override
    public void tick() {
        super.tick();
        deleteBtn.setEnabled(BindsEditor.activePreset != null && BindsEditor.activePreset.pages.size() > 1);
        if (deleteConfirmShown && !isDeleteConfirmation()) {
            confirm(false);
        }
    }
}
