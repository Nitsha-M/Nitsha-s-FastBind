package com.nitsha.binds.action;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.FBLogger;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.utils.EventBus;
import com.nitsha.binds.utils.FormattedTextUtils;
import com.nitsha.binds.utils.ToastUtils;
import net.minecraft.client.Minecraft;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

import com.nitsha.binds.configs.dto.actions.AllActionsData.ToastActionData;
import com.nitsha.binds.configs.dto.actions.AllActionsData.ToastInnerData;
import com.nitsha.binds.configs.dto.actions.AllActionsData.TextFormatData;

public class ToastAction extends ActionType<ToastActionData> {

    private static final ResourceLocation ITEMS_SELECTOR_1 = Main.id("textures/gui/test/items_5.png");
    private static final ResourceLocation ITEMS_SELECTOR_2 = Main.id("textures/gui/test/items_6.png");
    private static final ResourceLocation ITEMS_SELECTOR_3 = Main.id("textures/gui/test/items_7.png");
    private static final ResourceLocation TOAST_BG = Main.id("textures/gui/test/toast_bg.png");

    private TextField titleField;
    private ItemButton iconSelector;
    private BedrockIconOptionButton iconStyle;
    private int x, y, width;

    @Override public String getId() { return "toast"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.toast").getString();
    }

    @Override public String getDefaultValue() { return ""; }
    @Override public int getLineColor() { return 0xFF5facfa; }
    @Override public int getHeight() { return 58; }

    @Override
    public ToastActionData createDefaultData() { return new ToastActionData(); }

    @Override
    public void buildTasks(ToastActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        ToastInnerData titleData = data.value;
        if (titleData == null) return;

        MutableComponent titleComponent = FormattedTextUtils.buildComponent(titleData.title);

        if (titleComponent.getString().isEmpty()) return;

        String icon = data.value.icon != null ? data.value.icon : "minecraft:diamond";

        final MutableComponent finalTitle = titleComponent;

        if (client.player == null || client.getConnection() == null) return;

        actions.add(() -> {
            if (client.level != null) {
                ToastUtils.showFakeAdvancement(
                        finalTitle,
                        titleData.toastType.toLowerCase(),
                        ItemsMapper.getItemStack(icon)
                );
            }
        });
    }

    @Override
    public void init(int x, int y, int width, ToastActionData data) {
        this.x = x;
        this.y = y;
        this.width = width;

        this.titleField = new TextField(
                Minecraft.getInstance().font,
                x, y + 3, width - 26 - 19, 19,
                Integer.MAX_VALUE, "",
                TextUtils.translatable("nitsha.binds.advances.actions.toastText").getString()
        );

        this.iconSelector = new ItemButton(x + 5, y + 29, 22, ItemsMapper.getItemStack(data.value.icon), () -> {
            EventBus.off("selectIcon.result");
            EventBus.on("selectIcon.result", (String selectedKey) -> {
                this.iconSelector.setKey(selectedKey);
                this.iconSelector.setIcon(ItemsMapper.getItemStack(selectedKey));
            });
            EventBus.emit("selectIcon.open", null);
        }, getIconSelectorTexture(data.value.toastType), data.value.icon);

        this.iconStyle = new BedrockIconOptionButton(x + width - 26 - 18, y + 3, 18, 19, () -> {
            data.value.toastType = this.iconStyle.getSelected();
            this.iconSelector.setTexture(getIconSelectorTexture(data.value.toastType));
        }).addOption("task", "nitsha.binds.advances.actions.option.task", Main.id("textures/gui/sprites/toast_task.png"), 0xFF07938d, 0xFF0fb2ab)
        .addOption("challenge", "nitsha.binds.advances.actions.option.challenge", Main.id("textures/gui/sprites/toast_challenge.png"), 0xFF07938d, 0xFF0fb2ab)
        .addOption("goal", "nitsha.binds.advances.actions.option.goal", Main.id("textures/gui/sprites/toast_goal.png"), 0xFF9cc708, 0xFFafda19);

        this.iconStyle.setSelected(data.value.toastType.toLowerCase());
        if (data.value != null) {
            if (data.value.title != null) {
                loadFormattedText(this.titleField, data.value.title);
            }
        }
    }

    private void loadFormattedText(TextField field, TextFormatData data) {
        String text = data.text;
        if (text != null) field.setText(text);
        if (data.marks != null) {
            field.setFormatMarksFromMap(data.marks);
        }
    }

    public ResourceLocation getIconSelectorTexture(String type) {
        return switch (type) {
            case "challenge" -> ITEMS_SELECTOR_3;
            case "goal" -> ITEMS_SELECTOR_2;
            default -> ITEMS_SELECTOR_1;
        };
    }

    @Override
    public void setPosition(int x, int y) {
        this.y = y;
        this.x = x;
        if (titleField != null) titleField.setY(y + 3);
        if (iconSelector != null) {
            iconSelector.setY(y + 29);
        }
    }

    private TextFormatData saveFormattedText(TextField field) {
        TextFormatData data = new TextFormatData();
        data.text = field.getText();
        data.marks = field.getFormatMarksAsMap();
        return data;
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        titleField.renderWidget(ctx, mouseX, mouseY, delta);
        iconStyle.renderWidget(ctx, mouseX, mouseY, delta);

        GUIUtils.drawResizableBox(ctx, TOAST_BG, x, y + 24, width, 32, 4, 9);
        iconSelector.render(ctx, mouseX, mouseY, delta);

        String advMade = switch (this.iconStyle.getSelected()) {
            case "goal" -> "advancements.toast.goal";
            case "challenge" -> "advancements.toast.challenge";
            default -> "advancements.toast.task";
        };
        int advColor = (this.iconStyle.getSelected().equals("challenge")) ? 0xFFFC86FC : 0xFFFCFC00;
        GUIUtils.addText(ctx, TextUtils.translatable(advMade), 0, x + 30, y + 31, "left", "top", advColor, false);

        if (this.titleField.getText().isEmpty()) {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.typeSomeText"), 0, x + 30, y + 42, "left", "top", 0xFFAAAAAA, false);
        } else {
            TextFormatData data = saveFormattedText(titleField);
            int maxChars = (width - 44) / 7;
            MutableComponent formattedTitle = FormattedTextUtils.buildComponent(data, maxChars);

            GUIUtils.addText(ctx, formattedTitle, 0, x + 30, y + 42, "left", "top", 0xFFFFFFFF, false);
        }
    }

    @Override
    public ToastActionData getValue() {
        ToastActionData result = new ToastActionData();
        result.value.title = saveFormattedText(titleField);
        result.value.toastType = this.iconStyle.getSelected();
        result.value.icon = this.iconSelector.getKey();
        return result;
    }

    @Override
    public void reset() {
        titleField.setText("");
        iconSelector.setIcon(ItemsMapper.getItemStack("minecraft:diamond"));
        iconSelector.setKey("minecraft:diamond");
        iconStyle.setSelected("task");
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        boolean clicked = titleField.mouseClicked(event, bl) || iconSelector.mouseClicked(event, bl) || iconStyle.mouseClicked(event, bl);
        return clicked;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return titleField.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        return titleField.charTyped(event);
    }*/
    //? } else {
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        boolean clicked = titleField.mouseClicked(mx, my, btn) || iconSelector.mouseClicked(mx, my, btn) || iconStyle.mouseClicked(mx, my, btn);
        return clicked;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        return titleField.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return titleField.charTyped(c, mods);
    }
    //? }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
        boolean r = titleField.mouseReleased(event);
        r |= iconStyle.mouseReleased(event);
        return r;
    }*/
    //? } else {
    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        boolean r = titleField.mouseReleased(mx, my, btn);
        r |= iconStyle.mouseReleased(mx, my, btn);
        return r;
    }
    //? }
}