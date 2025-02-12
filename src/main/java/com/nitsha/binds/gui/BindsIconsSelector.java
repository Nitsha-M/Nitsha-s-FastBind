package com.nitsha.binds.gui;


import com.nitsha.binds.ItemsMapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BindsIconsSelector extends ClickableWidget implements Drawable, Element, Selectable {

    public static final List<BindsEditorGUI.IconSettingButton> buttons = new ArrayList<>();
    public static int scrollOffset;
    protected int x;
    protected int y;
    protected int width;
    protected int height;


    public BindsIconsSelector(int x, int y, int width, int height) {
        super(x, y, width, height, Text.literal(""));
        buttons.clear();
        this.scrollOffset = 0;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = 26;
        for (Map.Entry<String, ItemStack> entry : ItemsMapper.itemStackMap.entrySet()) {
            BindsEditorGUI.IconSettingButton[] buttonHolder = new BindsEditorGUI.IconSettingButton[1];
            buttonHolder[0] = new BindsEditorGUI.IconSettingButton(0, 0, entry.getValue(), () -> {
                for (BindsEditorGUI.IconSettingButton button : buttons) {
                    button.setSelected(false);
                }
                buttonHolder[0].setSelected(true);
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                BindsEditorGUI.editIconBtn.setIcon(entry.getValue());
                BindsEditorGUI.editIconBtnString = entry.getKey();
            }, 2);
            buttons.add(buttonHolder[0]);
        }
    }

    public static void updateButtons(int index) {
        for (BindsEditorGUI.IconSettingButton button : buttons) {
            button.setSelected(false);
        }
        buttons.get(index).setSelected(true);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (BindsEditorGUI.isSelectorOpened) {
            context.enableScissor(x, 0, x + width, y + height);
            int index = 0;
            for (BindsEditorGUI.IconSettingButton button : buttons) {
                button.render(context, mouseX, mouseY, delta);
                int buttonX = x + index * 28 - scrollOffset;
                button.setX(buttonX + 2);
                button.setY(y);
                button.render(context, mouseX, mouseY, delta);
                index++;
            }
            context.disableScissor();
        }
    }

    private int getMaxScroll() {
        return Math.max(0, buttons.size() * 28 - width + 2);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= verticalAmount * 28;
        if (scrollOffset < 0) {
            scrollOffset = 0;
        } else if (scrollOffset > getMaxScroll()) {
            scrollOffset = getMaxScroll();
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (BindsEditorGUI.IconSettingButton btn : buttons) {
            if (isButtonVisible(btn) && btn.isMouseOver(mouseX, mouseY)) {
                btn.onClick(mouseX, mouseY);
                return true;
            }
        }
        return false;
    }

    private boolean isButtonVisible(BindsEditorGUI.IconSettingButton button) {
        int buttonX = button.getX();
        int buttonY = button.getY();
        int buttonWidth = button.getWidth();
        int buttonHeight = button.getHeight();

        // Проверка, находится ли кнопка в пределах видимой области
        return buttonX + buttonWidth > x && buttonX < x + width &&
                buttonY + buttonHeight > y && buttonY < y + height;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}