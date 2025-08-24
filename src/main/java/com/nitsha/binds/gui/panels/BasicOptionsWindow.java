package com.nitsha.binds.gui.panels;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.configs.BindsConfig;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.screen.BindsGUI;
import com.nitsha.binds.gui.utils.AnimatedSprite;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.utils.EasterEgg;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? }
import net.minecraft.client.gui.Element;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;

public class BasicOptionsWindow extends AnimatedWindow {
    private BindsEditor.TextField bindNameField;
    private ItemButton editIconBtn;
    private BedrockIconButton pasteBtn;
    private BedrockIconTextButton editAction;

    private static final Identifier ITEMS_EDIT = MainClass.id("textures/gui/test/items_1.png");
    private static final Identifier CAT_MENU = MainClass.id("textures/gui/cat_menu.png");
    private static final Identifier CAT_SPRITE = MainClass.id("textures/gui/cat_sprite.png");
    private static final Identifier CAT_EYES = MainClass.id("textures/gui/cat_eyes.png");
    private static final Identifier CAT_MEOW1 = MainClass.id("textures/gui/cat_meow_0.png");
    private static final Identifier CAT_MEOW2 = MainClass.id("textures/gui/cat_meow_1.png");

    public BasicOptionsWindow(BindsEditor screen, float x, int y, float width, int height, Identifier t1, Identifier t2, int delay) {
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

    private void initUI(BindsEditor screen) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        meowStates = CatMeowStates.NOTHING;
        AnimatedSprite catTail = new AnimatedSprite(14, 12, CAT_SPRITE, 0, false, 0, 0, 490, 14, 60, 504, 12);
        AnimatedSprite catMeow1 = new AnimatedSprite(35, 14, CAT_MEOW1, 0, false, 0, 0, 420, 35, 40, 455, 14);
        AnimatedSprite catMeow2 = new AnimatedSprite(35, 14, CAT_MEOW2, 0, false, 0, 0, 420, 35, 40, 455, 14);
        catMeow1.setPosition(120, 158);
        catMeow2.setPosition(120, 158);

        catEasterEgg = new EasterEgg(
                10,
                500,
                () -> {
                    if (!BindsConfig.getBooleanConfig("easterEgg", false)) {
                        BindsConfig.setConfig("easterEgg", true);
                        meowStates = CatMeowStates.HAPPY;
                        catMeow2.stopAnimation();
                        catMeow1.startAnimation(true);
                    } else {
                        BindsConfig.setConfig("easterEgg", false);
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
                //? if >=1.20 {
                DrawContext c = (DrawContext) ctx;
                //? } else {
                /*MatrixStack c = (MatrixStack) ctx;
                 *///? }
                GUIUtils.adaptiveDrawTexture(ctx, CAT_MENU, 4, 165, 0, 0, 126, 26, 126, 26);
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

                catTail.render(c);
                if (meowStates == CatMeowStates.HAPPY) catMeow1.render(c);
                if (meowStates == CatMeowStates.SAD) catMeow2.render(c);
            });
        }, 1);

        this.bindNameField = new BindsEditor.TextField(textRenderer, 4, 104, 105, 20, 20, "", TextUtils.translatable("nitsha.binds.name").getString());
        this.editIconBtn = new ItemButton(111, 101, ItemsMapper.getItemStack(BindsConfig.getBind(BindsGUI.getCurrentPreset(), BindsEditor.getActiveBind()).icon), () -> {
            screen.openAdvancedOptions();
            screen.getAdvancedOptionsWindow().selectTab(0);
        }, ITEMS_EDIT, "");

        this.editAction = new BedrockIconTextButton(4, 129, 133, 20, "edit_action", TextUtils.translatable("nitsha.binds.openEditor").getString(), true, ()-> {
            screen.openAdvancedOptions();
            screen.getAdvancedOptionsWindow().selectTab(1);
        });

        this.pasteBtn = new BedrockIconButton(49, 151, 43, 20, "paste", false, screen::pasteBind, 0xFF0569CE, 0xFF0776E6, 0xFFFFFFFF, 0xFFFFFFFF);
        if (screen.copied.name.isEmpty()) pasteBtn.setEnabled(false);
        this.addElement(new BedrockIconButton(4, 151, 43, 20, "copy", true, screen::copyBind));
        this.addElement(new BedrockIconButton(94, 151, 43, 20, "delete", true, screen::deleteBind, 0xFFEF4747, 0xFFFF7272, 0xFFFFFFFF, 0xFFFFFFFF));

        this.addElement(bindNameField);
        this.addElement(editIconBtn);
        this.addElement(pasteBtn);
        this.addElement(editAction);

        this.open(() -> {});
    }

    public BindsEditor.TextField getBindName() {
        return this.bindNameField;
    }

    public ItemButton getEditIcon() {
        return editIconBtn;
    }

    public BedrockIconButton getPasteIcon() {
        return pasteBtn;
    }

    public boolean isInsideCat(double mouseX, double mouseY, int x, int y, int width, int height) {
        if (!this.isVisible()) return false;
        float catX = this.getX() + x;
        float catY = getYOffset() + y;
        float catWidth = catX + width;
        float catHeight = catY + height;
        return mouseX >= catX && mouseX <= catWidth && mouseY >= catY && mouseY <= catHeight;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isVisible()) return false;

        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getYOffset();

        for (Element child : children()) {
            if (child.mouseClicked(adjustedX, adjustedY, button)) return true;
        }
        if (catEasterEgg.handleClick(isInsideCat(mouseX, mouseY, 94, 171, 45, 16))) {
            return true;
        };
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
