package com.nitsha.binds.gui.widget.window;

import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.gui.widget.TextField;
import com.nitsha.binds.gui.widget.button.BedrockButton;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TextFormattingWindow extends AnimatedWindow {

    public static final Object[][] COLORS_DATA = {
            { 0xFF000000, 0,  TextUtils.literal("") },
            { 0xFF0001a9, 1,  TextUtils.literal("") },
            { 0xFF04a702, 2,  TextUtils.literal("") },
            { 0xFF01a7a9, 3,  TextUtils.literal("") },
            { 0xFFaa0101, 4,  TextUtils.literal("") },
            { 0xFFa501a7, 5,  TextUtils.literal("") },
            { 0xFFfca702, 6,  TextUtils.literal("") },
            { 0xFFa8a8a8, 7,  TextUtils.literal("") },
            { 0xFF545454, 8,  TextUtils.literal("") },
            { 0xFF5254fa, 9,  TextUtils.literal("") },
            { 0xFF55fb53, 10, TextUtils.literal("") },
            { 0xFF50fcf7, 11, TextUtils.literal("") },
            { 0xFFfd5357, 12, TextUtils.literal("") },
            { 0xFFf955fb, 13, TextUtils.literal("") },
            { 0xFFfbfd4f, 14, TextUtils.literal("") },
            { 0xFFFFFFFF, 15, TextUtils.literal("") },
            { 0xFF316d20, 20, TextUtils.literal("ʙ").setStyle(Style.EMPTY.withBold(true)) },
            { 0xFF316d20, 21, TextUtils.literal("ɪ").setStyle(Style.EMPTY.withItalic(true)) },
            { 0xFF316d20, 22, TextUtils.literal("ᴜ").setStyle(Style.EMPTY.withUnderlined(true)) },
            //? if >=1.20 {
            { 0xFF316d20, 23, TextUtils.literal("s").setStyle(Style.EMPTY.withStrikethrough(true)) },
            { 0xFF316d20, 24, TextUtils.literal("ᴏ").setStyle(Style.EMPTY.withObfuscated(true)) },
            //?} else {
            /*{ 0xFF316d20, 23, TextUtils.literal("s").withStyle(s -> s.applyFormat(ChatFormatting.STRIKETHROUGH)) },
            { 0xFF316d20, 24, TextUtils.literal("ᴏ").withStyle(s -> s.applyFormat(ChatFormatting.OBFUSCATED)) },*/
            //?}
            { 0xFF316d20, 99, TextUtils.literal("ʀ") },
    };


    private final List<BedrockButton> colorsItem = new ArrayList<>();
    private TextField target;
    private boolean closing;

    public TextFormattingWindow(int x, int y) {
        super(x, y, 138, 34, Textures.BACKGROUND, Textures.BACKGROUND_FLAT, 0);

        int startX = 3;
        int buttonsPerRow = 11;
        int btnW = 12;
        int btnH = 12;

        for (int i = 0; i < COLORS_DATA.length; i++) {
            MutableComponent text = (MutableComponent) COLORS_DATA[i][2];
            int color = (int) COLORS_DATA[i][0];
            int styleCode = (int) COLORS_DATA[i][1];
            int column = i % buttonsPerRow;
            int row = i / buttonsPerRow;

            BedrockButton button = new BedrockButton(text, startX + column * btnW, 3 + row * (btnH + 2), btnW, btnH, true, () -> {
                if (hasTarget()) {
                    TextField.setLastClickedWidget(target);
                    target.applyFormatting(styleCode);
                }
            }, color, RenderUtils.lightenColor(color, 0.2f), 0xFFFFFFFF, 0xFFFFFFFF);

            if (column == 0) {
                button.setButtonDirection(1);
            } else if (column == buttonsPerRow - 1) {
                button.setButtonDirection(2);
            } else {
                button.setButtonDirection(3);
            }

            colorsItem.add(button);
            addElement(button);
        }

        for (int i = 0; i < colorsItem.size(); i++) {
            BedrockButton button = colorsItem.get(i);
            int column = i % buttonsPerRow;

            if (column == 0) {
                button.setNeighbor(colorsItem.get(i + 1));
            } else if (column == buttonsPerRow - 1) {
                button.setNeighbor(colorsItem.get(i - 1));
            } else {
                button.setNeighbor(colorsItem.get(i + 1));
                button.setNeighbor2(colorsItem.get(i - 1));
            }
        }
    }


    public boolean hasTarget() {
        return target != null && target == TextField.getFocusedField() && target.isFocused() && target.isFormattingAllowed();
    }

    public void showFor(TextField field, RenderUtils.WidgetBounds bounds, int screenWidth, int screenHeight) {
        int x = Math.max(4, Math.min(bounds.x(), screenWidth - getWidth() - 4));
        int y = bounds.y() + bounds.height() + 3;
        if (y + getHeight() > screenHeight - 4) {
            y = bounds.y() - getHeight() - 3;
        }
        y = Math.max(4, Math.min(y, screenHeight - getHeight() - 4));
        setPositionImmediately(x, y);

        if (target != field || closing) {
            for (BedrockButton button : colorsItem) {
                button.setPressed(false);
            }
        }

        target = field;
        if (closing) setVisible(false);
        closing = false;
        if (!isVisible()) open(() -> {});
    }

    public void hide() {
        target = null;
        if (!isVisible() || closing) return;

        closing = true;
        for (BedrockButton button : colorsItem) {
            button.setPressed(false);
        }
        close(() -> closing = false);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!hasTarget() || !isMouseInside(mouseX, mouseY)) return false;

        TextField.setLastClickedWidget(target);
        super.onMouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return hasTarget() && isMouseInside(mouseX, mouseY);
    }
}
