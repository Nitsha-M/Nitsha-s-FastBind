package com.nitsha.binds.gui.panels;

import com.nitsha.binds.FBLogger;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.Main;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.dto.preset.BindData;
import com.nitsha.binds.configs.dto.preset.PageData;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.AnimatedSprite;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.utils.CodecUtil;
import com.nitsha.binds.utils.EasterEgg;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
//? if >=1.21.9 {
// import net.minecraft.client.input.MouseButtonEvent;
//? }

public class BasicOptionsWindow extends AnimatedWindow {
    private TextField bindNameField;
    private ItemButton editIconBtn;
    private BedrockIconButton copyBtn;
    private BedrockIconButton pasteBtn;
    private BedrockIconButton importBtn;
    private BedrockIconButton exportBtn;

    private static final ResourceLocation ITEMS_EDIT = Main.id("textures/gui/test/items_1.png");
    private static final ResourceLocation CAT_MENU = Main.id("textures/gui/cat_menu.png");
    private static final ResourceLocation CAT_SPRITE = Main.id("textures/gui/cat_sprite.png");
    private static final ResourceLocation CAT_EYES = Main.id("textures/gui/cat_eyes.png");
    private static final ResourceLocation CAT_MEOW1 = Main.id("textures/gui/cat_meow_0.png");
    private static final ResourceLocation CAT_MEOW2 = Main.id("textures/gui/cat_meow_1.png");

    public BasicOptionsWindow(BindsEditor screen, float x, int y, float width, int height, ResourceLocation t1, ResourceLocation t2, int delay) {
        super(x, y, width, height, t1, t2, delay);
        initUI(screen);
    }

    private float eyeDx = 0;
    private float eyeDy = 0;

    private EasterEgg catEasterEgg;
    private CatMeowStates meowStates;
    private enum CatMeowStates {
        NOTHING,
        HAPPY,
        SAD
    }

    private BedrockIconButton deleteBtn;
    private long deleteConfirmationTime = 0;
    private boolean deleteConfirmShown = false;

    private ScreenTooltip tooltip;
    private Map<AbstractButton, String> tooltips = new LinkedHashMap<>();

    private void initUI(BindsEditor screen) {
        Font textRenderer = Minecraft.getInstance().font;
        meowStates = CatMeowStates.NOTHING;
        AnimatedSprite catTail = new AnimatedSprite(14, 12, CAT_SPRITE, 0, false, 0, 0, 490, 14, 60, 504, 12);
        AnimatedSprite catMeow1 = new AnimatedSprite(35, 14, CAT_MEOW1, 0, false, 0, 0, 420, 35, 40, 455, 14);
        AnimatedSprite catMeow2 = new AnimatedSprite(35, 14, CAT_MEOW2, 0, false, 0, 0, 420, 35, 40, 455, 14);
        catMeow1.setPosition(120, 158);
        catMeow2.setPosition(120, 158);

        tooltip = new ScreenTooltip(0, this.getHeight() + 2, this.getWidth());
        this.addElement(tooltip);

        catEasterEgg = new EasterEgg(
                4,
                500,
                () -> {
                    if (!Storage.options.easterEgg) {
                        Storage.options.easterEgg = true;
                        Storage.saveModOptions();
                        meowStates = CatMeowStates.HAPPY;
                        catMeow2.stopAnimation();
                        catMeow1.startAnimation(true);
                    } else {
                        Storage.options.easterEgg = false;
                        Storage.saveModOptions();
                        meowStates = CatMeowStates.SAD;
                        catMeow1.stopAnimation();
                        catMeow2.startAnimation(true);
                    }
                }
        );
        catTail.setLoop(true);
        catTail.setPosition(122, 174);
        catTail.startAnimation(true);
        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.matricesUtil(ctx, 0, 0, 50, () -> {
                GUIUtils.adaptiveDrawTexture(ctx, CAT_MENU, 4, 165, 0, 0, 126, 26, 126, 26);
                GUIUtils.addText(
                        ctx, TextUtils.translatable("nitsha.binds.autosave"), 0,
                        4,
                        178,
                        "left", "center", 0xFF7f7f7f, false);
                int eyeCenterX = 105;
                int eyeCenterY = 175;
                float maxOffsetX = 1.4f;
                float maxOffsetY = 1.1f;

                float targetDx = (mouseX - eyeCenterX) / 50f;
                float targetDy = (mouseY - eyeCenterY) / 50f;

                targetDx = Math.max(-maxOffsetX, Math.min(maxOffsetX, targetDx));
                targetDy = Math.max(-maxOffsetY, Math.min(maxOffsetY, targetDy));

                eyeDx += (targetDx - eyeDx) * 0.2f;
                eyeDy += (targetDy - eyeDy) * 0.2f;

                GUIUtils.matricesUtil(ctx, eyeDx, eyeDy, 0, () -> {
                    GUIUtils.adaptiveDrawTexture(ctx, CAT_EYES,
                            eyeCenterX, eyeCenterY,
                            0, 0, 8, 6, 8, 6
                    );
                });

                catTail.render(ctx);
                if (meowStates == CatMeowStates.HAPPY) catMeow1.render(ctx);
                if (meowStates == CatMeowStates.SAD) catMeow2.render(ctx);
            });
        }, 1);

        this.bindNameField = new TextField(textRenderer, 4, 126, 105, 20, 20, "", TextUtils.translatable("nitsha.binds.name").getString());
        this.editIconBtn = new ItemButton(111, 123, ItemsMapper.getItemStack(BindsEditor.getCBind().icon), () -> {
            screen.getAdvancedOptionsWindow().selectTab(1);
        }, ITEMS_EDIT, "");

        this.copyBtn = new BedrockIconButton(4, 151, 26, 20, "copy", true, screen::copyBind);
        this.copyBtn.setButtonDirection("_left");
        this.pasteBtn = new BedrockIconButton(30, 151, 26, 20, "paste", false, screen::pasteBind, 0xFF0569CE, 0xFF0776E6, 0xFFFFFFFF, 0xFFFFFFFF);
        this.pasteBtn.setButtonDirection("_right");
        if (screen.copied.name.isEmpty()) pasteBtn.setEnabled(false);


        this.deleteBtn = new BedrockIconButton(58, 151, 25, 20, "delete", false, ()-> {
            if (deleteConfirmShown) {
                screen.deleteBind();
                confirm(false);
            } else {
                deleteConfirmationTime = System.currentTimeMillis();
                confirm(true);
            }
        }, 0xFFEF4747, 0xFFFF7272, 0xFFFFFFFF, 0xFFFFFFFF);

        this.exportBtn = new BedrockIconButton(85, 151, 26, 20, "copy", true, () -> {
            screen.saveBind();

            BindData currentBind = BindsEditor.getCBind();

            String encodedBind = CodecUtil.exportToText(currentBind);

            if (encodedBind != null) {
                Minecraft.getInstance().keyboardHandler.setClipboard(encodedBind);
            }
        }, 0xFF0569CE, 0xFF0776E6, 0xFFFFFFFF, 0xFFFFFFFF);
        this.exportBtn.setButtonDirection("_left");

        this.importBtn = new BedrockIconButton(111, 151, 26, 20, "paste", true, () -> {
            String clipboardText = Minecraft.getInstance().keyboardHandler.getClipboard();

            BindData importedBind = CodecUtil.importFromText(clipboardText, BindData.class);

            if (importedBind != null) {
                importedBind.index = BindsEditor.activeBind;

                PageData page = BindsEditor.activePreset.pages.get(BindsEditor.currentPage);
                boolean found = false;

                if (page.binds != null) {
                    for (int i = 0; i < page.binds.size(); i++) {
                        if (page.binds.get(i).index == BindsEditor.activeBind) {
                            page.binds.set(i, importedBind);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        page.binds.add(importedBind);
                    }
                }

                Storage.savePreset(BindsEditor.activePreset, BindsEditor.activePresetId);
                screen.getBindsListWindow().updateSelected(ItemsMapper.getItemStack(importedBind.icon));
                screen.selectBind();
            }
        }, 0xFF0569CE, 0xFF0776E6, 0xFFFFFFFF, 0xFFFFFFFF);
        this.importBtn.setButtonDirection("_right");

        this.addElement(bindNameField);
        this.addElement(editIconBtn);

        this.addElement(copyBtn);
        this.addElement(pasteBtn);
        this.addElement(deleteBtn);
        this.addElement(importBtn);
        this.addElement(exportBtn);

        tooltips.put(copyBtn,   TextUtils.translatable("nitsha.binds.control.copy").getString());
        tooltips.put(pasteBtn,  TextUtils.translatable("nitsha.binds.control.paste").getString());
        tooltips.put(deleteBtn, TextUtils.translatable("nitsha.binds.control.delete").getString());
        tooltips.put(importBtn, TextUtils.translatable("nitsha.binds.control.import").getString());
        tooltips.put(exportBtn, TextUtils.translatable("nitsha.binds.control.export").getString());

        this.open(() -> {});
    }

    public TextField getBindName() {
        return this.bindNameField;
    }

    public ItemButton getEditIcon() {
        return editIconBtn;
    }

    public BedrockIconButton getPasteBtn() {
        return pasteBtn;
    }

    public BedrockIconButton getDeleteBtn() {
        return deleteBtn;
    }

    public boolean isInsideCat(double mouseX, double mouseY, int x, int y, int width, int height) {
        if (!this.isVisible()) return false;
        float catX = this.getX() + x;
        float catY = getYOffset() + y;
        float catWidth = catX + width;
        float catHeight = catY + height;
        return mouseX >= catX && mouseX <= catWidth && mouseY >= catY && mouseY <= catHeight;
    }

    public void confirm(boolean status) {
        deleteConfirmShown = status;
        if (status) {
            deleteBtn.setColors(0xFFfac70c, 0xFFfcd02f, 0xFFFFFFFF, 0xFFFFFFFF);
            deleteBtn.setIcon("confirm");
        } else {
            deleteBtn.setColors(0xFFEF4747, 0xFFFF7272, 0xFFFFFFFF, 0xFFFFFFFF);
            deleteBtn.setIcon("delete");
        }
    }

    public boolean isDeleteConfirmation() {
        return System.currentTimeMillis() - deleteConfirmationTime < 5000;
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (!this.isVisible()) return false;

        double mouseX = event.x();
        double mouseY = event.y();
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getYOffset();

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(
                adjustedX,
                adjustedY,
                event.buttonInfo()
        );

        for (GuiEventListener child : children()) {
            if (child.mouseClicked(adjustedEvent, bl)) return true;
        }

        if (catEasterEgg.handleClick(isInsideCat(mouseX, mouseY, 94, 171, 45, 16))) {
            return true;
        }

        return super.mouseClicked(event, bl);
    }*/
    //? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isVisible()) return false;

        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getYOffset();

        for (GuiEventListener child : children()) {
            if (child.mouseClicked(adjustedX, adjustedY, button)) return true;
        }

        if (catEasterEgg.handleClick(isInsideCat(mouseX, mouseY, 94, 171, 45, 16))) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
    //? }

    @Override
    public void tick() {
        super.tick();
        if (deleteConfirmShown && !isDeleteConfirmation()) {
            confirm(false);
        }
        tooltips.forEach((btn, text) -> {
            if (btn instanceof BedrockIconButton) {
                BedrockIconButton b = (BedrockIconButton) btn;
                if (b.isHovered()) {
                    tooltip.visible = true;
                    tooltip.setText(text);
                    tooltip.setColor(b.getBtnColor());
                }
            }
        });
        if (tooltips.keySet().stream().noneMatch(AbstractButton::isHovered)) {
            tooltip.visible = false;
            tooltip.setText("");
        }
    }
}
