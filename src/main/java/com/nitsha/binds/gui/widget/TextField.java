package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
 *///?}
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

//? if >=1.17 {
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}

import java.util.*;

public class TextField extends AbstractButton {
    private static TextField focusedField = null;

    private final boolean isNumerical;
    private Runnable escapeEvent;
    private Runnable enterEvent;

    private static final ResourceLocation NORMAL = Main.id("textures/gui/test/text_field_normal.png");
    private static final ResourceLocation FOCUS = Main.id("textures/gui/test/text_field_focus.png");

    private final Font font;
    private String text = "";
    private Component placeholder;
    private int maxLength = 32;

    private int selectionStart = 0;
    private int selectionEnd = 0;
    private int firstCharacterIndex = 0;

    private long lastSwitchFocusTime = Util.getMillis();

    private final List<Object> focusExceptions = new ArrayList<>();

    private float phScale = 1f;
    private float phTextX = 0f;
    private float phTextY = 0f;
    private float phBoxY = 0f;
    private boolean phInit = false;

    private boolean isAnimatedPlaceholder = true;

    // Система форматирования
    public static class FormatMark implements Comparable<FormatMark> {
        public int position;
        public int styleCode;

        public FormatMark(int position, int styleCode) {
            this.position = position;
            this.styleCode = styleCode;
        }

        @Override
        public int compareTo(FormatMark other) {
            int posCompare = Integer.compare(this.position, other.position);
            if (posCompare != 0)
                return posCompare;
            return Integer.compare(getPriority(this.styleCode), getPriority(other.styleCode));
        }

        private int getPriority(int code) {
            if (code == 99)
                return 0;
            if (code >= 0 && code <= 15)
                return 1;
            return 2;
        }
    }

    private final List<FormatMark> formatMarks = new ArrayList<>();

    public TextField(Font font, int x, int y, int width, int height, int length, String text, String placeholder,
            boolean isNumerical) {
        super(x, y, width, height, Component.literal(text));
        this.font = font;
        this.text = String.valueOf(text);
        this.placeholder = Component.literal(placeholder);
        this.isNumerical = isNumerical;
        this.maxLength = length;
    }

    public TextField(Font font, int x, int y, int width, int height, int length, String text, String placeholder) {
        this(font, x, y, width, height, length, text, placeholder, false);
    }

    public void setAnimatedPlaceholder(boolean animated) {
        isAnimatedPlaceholder = animated;
    }

    // ===== ФОРМАТИРОВАНИЕ =====

    /**
     * Устанавливает стиль в указанной позиции
     *
     * @param styleCode код стиля (0-15 цвета, 20-24 стили, 99 сброс)
     * @param position  позиция в тексте
     */
    public void setStyle(int styleCode, int position) {
        if (position < 0 || position > text.length())
            return;

        if (styleCode == 99) {
            formatMarks.removeIf(mark -> mark.position == position);
        } else if (styleCode >= 0 && styleCode <= 15) {
            formatMarks.removeIf(mark -> mark.position == position && mark.styleCode >= 0 && mark.styleCode <= 15);
            formatMarks.removeIf(mark -> mark.position == position && mark.styleCode == 99);
        } else if (styleCode >= 20 && styleCode <= 24) {
            formatMarks.removeIf(mark -> mark.position == position && mark.styleCode == styleCode);
            formatMarks.removeIf(mark -> mark.position == position && mark.styleCode == 99);
        }

        formatMarks.add(new FormatMark(position, styleCode));
        Collections.sort(formatMarks);
    }

    /**
     * Устанавливает стиль на позиции курсора (форматирует текст после курсора)
     *
     * @param styleCode код стиля (0-15 цвета, 20-24 стили, 99 сброс)
     */
    public void setStyle(int styleCode) {
        if (!isFocused())
            return;

        if (selectionStart != selectionEnd) {
            // Если есть выделение, форматируем выделенный текст
            setStyleToSelection(styleCode);
        } else {
            // Если нет выделения, ставим метку на позиции курсора
            // (будет применяться ко всему тексту после курсора)
            setStyle(styleCode, selectionStart);
        }
    }

    /**
     * Устанавливает стиль на выделенный текст
     * Ставит метку в начале выделения и восстанавливает предыдущий стиль в конце
     */
    public void setStyleToSelection(int styleCode) {
        if (selectionStart == selectionEnd) {
            // Если нет выделения, форматируем весь текст после курсора
            setStyle(styleCode, selectionStart);
            return;
        }

        int start = Math.min(selectionStart, selectionEnd);
        int end = Math.max(selectionStart, selectionEnd);

        // Ставим метку стиля в начале выделения
        setStyle(styleCode, start);

        // Находим, какой стиль был до выделения, чтобы восстановить его после
        Style styleBefore = Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF));

        // Проходим по меткам до начала выделения, чтобы узнать текущий стиль
        for (FormatMark mark : formatMarks) {
            if (mark.position < start) {
                styleBefore = applyStyleCode(mark.styleCode, styleBefore);
            } else if (mark.position == start) {
                // Пропускаем метку, которую только что установили
                continue;
            } else {
                break;
            }
        }

        // Если стиль - это цвет (0-15), ставим метку для восстановления в конце
        // выделения
        if (styleCode >= 0 && styleCode <= 15) {
            // Для цветов восстанавливаем белый цвет по умолчанию
            // Находим предыдущий цвет
            Integer previousColor = null;
            for (FormatMark mark : formatMarks) {
                if (mark.position < start && mark.styleCode >= 0 && mark.styleCode <= 15) {
                    previousColor = mark.styleCode;
                }
            }

            // Восстанавливаем предыдущий цвет или белый
            int restoreColor = (previousColor != null) ? previousColor : 15; // 15 = белый

            // Удаляем существующую метку на позиции end
            formatMarks.removeIf(mark -> mark.position == end && mark.styleCode >= 0 && mark.styleCode <= 15);
            formatMarks.add(new FormatMark(end, restoreColor));
        } else if (styleCode >= 20 && styleCode <= 24) {
            // Для стилей (жирный, курсив и т.д.) нужно проверить, был ли этот стиль активен
            boolean wasStyleActive = false;
            Style checkStyle = Style.EMPTY;

            for (FormatMark mark : formatMarks) {
                if (mark.position < start) {
                    checkStyle = applyStyleCode(mark.styleCode, checkStyle);
                }
            }

            // Проверяем, был ли стиль активен
            switch (styleCode) {
                case 20:
                    wasStyleActive = checkStyle.isBold();
                    break;
                case 21:
                    wasStyleActive = checkStyle.isItalic();
                    break;
                case 22:
                    wasStyleActive = checkStyle.isUnderlined();
                    break;
                case 23:
                    wasStyleActive = checkStyle.isStrikethrough();
                    break;
                case 24:
                    wasStyleActive = checkStyle.isObfuscated();
                    break;
            }

            // Если стиль не был активен, то после выделения нужно его сбросить
            if (!wasStyleActive) {
                // Восстанавливаем стиль, который был до выделения
                formatMarks.removeIf(mark -> mark.position == end && mark.styleCode == styleCode);
                // Можно либо ничего не делать (стиль останется), либо явно сбросить
                // Для простоты оставляем как есть - стиль будет применяться до следующей метки
            }
        } else if (styleCode == 99) {
            // Сброс - восстанавливаем стиль, который был до выделения
            formatMarks.removeIf(mark -> mark.position == end && mark.styleCode == 99);
        }

        Collections.sort(formatMarks);
    }

    /**
     * Удаляет стиль на указанной позиции
     */
    public void removeStyle(int position) {
        formatMarks.removeIf(mark -> mark.position == position);
    }

    /**
     * Удаляет стиль на позиции курсора
     */
    public void removeStyle() {
        int position = isFocused() ? selectionStart : text.length();
        removeStyle(position);
    }

    /**
     * Очищает все стили
     */
    public void clearStyles() {
        formatMarks.clear();
    }

    /**
     * Получает все стили (для сохранения/восстановления)
     */
    public List<FormatMark> getFormatMarks() {
        return new ArrayList<>(formatMarks);
    }

    /**
     * Устанавливает стили из сохраненного списка
     */
    public void setFormatMarks(List<FormatMark> marks) {
        formatMarks.clear();
        formatMarks.addAll(marks);
        Collections.sort(formatMarks);
    }

    public List<Map<String, Integer>> getFormatMarksAsMap() {
        List<Map<String, Integer>> result = new ArrayList<>();
        for (FormatMark mark : formatMarks) {
            Map<String, Integer> markData = new HashMap<>();
            markData.put("pos", mark.position);
            markData.put("style", mark.styleCode);
            result.add(markData);
        }
        return result;
    }

    public void setFormatMarksFromMap(List<Map<String, Object>> marks) {
        formatMarks.clear();
        for (Map<String, Object> markData : marks) {
            int pos = ((Number) markData.get("pos")).intValue();
            int style = ((Number) markData.get("style")).intValue();
            formatMarks.add(new FormatMark(pos, style));
        }
        Collections.sort(formatMarks);
    }

    /**
     * Применяет стиль к базовому Style
     */
    private Style applyStyleCode(int code, Style baseStyle) {
        if (code >= 0 && code <= 15) {
            int[] colors = {
                    0x1a1a1a, 0x0000AA, 0x00AA00, 0x00AAAA,
                    0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
                    0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
                    0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
            };
            // При установке цвета сбрасываем все стили форматирования, но сохраняем текущие
            // стили текста
            Style newStyle = baseStyle.withColor(TextColor.fromRgb(colors[code]));
            // Копируем форматирование из baseStyle
            if (baseStyle.isBold())
                newStyle = newStyle.withBold(true);
            if (baseStyle.isItalic())
                newStyle = newStyle.withItalic(true);
            if (baseStyle.isUnderlined())
                newStyle = newStyle.withUnderlined(true);
            if (baseStyle.isStrikethrough())
                newStyle = newStyle.withStrikethrough(true);
            if (baseStyle.isObfuscated())
                newStyle = newStyle.withObfuscated(true);
            return newStyle;
        }

        // Стили (20-24) - добавляются к существующему
        switch (code) {
            case 20:
                return baseStyle.withBold(true);
            case 21:
                return baseStyle.withItalic(true);
            case 22:
                return baseStyle.withUnderlined(true);
            case 23:
                return baseStyle.withStrikethrough(true);
            case 24:
                return baseStyle.withObfuscated(true);
            case 99:
                return Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF));
            default:
                return baseStyle;
        }
    }

    /**
     * Разбивает текст на сегменты с учётом форматирования
     */
    private List<StyledSegment> buildStyledSegments(String text, int startFrom, int endAt) {
        List<StyledSegment> segments = new ArrayList<>();

        if (text.isEmpty())
            return segments;

        Style currentStyle = Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF));
        int lastPos = startFrom;

        for (FormatMark mark : formatMarks) {
            if (mark.position < startFrom) {
                // Применяем стили до начала видимого текста
                currentStyle = applyStyleCode(mark.styleCode, currentStyle);
                continue;
            }

            if (mark.position >= endAt)
                break;

            // Добавляем сегмент до этой метки
            if (mark.position > lastPos) {
                String segText = text.substring(lastPos, mark.position);
                segments.add(new StyledSegment(segText, currentStyle));
            }

            // Применяем новый стиль
            currentStyle = applyStyleCode(mark.styleCode, currentStyle);
            lastPos = mark.position;
        }

        // Добавляем оставшийся текст
        if (lastPos < endAt) {
            String segText = text.substring(lastPos, endAt);
            segments.add(new StyledSegment(segText, currentStyle));
        }

        return segments;
    }

    private static class StyledSegment {
        String text;
        Style style;

        StyledSegment(String text, Style style) {
            this.text = text;
            this.style = style;
        }
    }

    /**
     * Обновляет позиции меток форматирования при изменении текста
     */
    private void updateFormatMarksAfterEdit(int editPos, int lengthChange) {
        if (lengthChange == 0)
            return;

        if (lengthChange < 0) {
            int deleteStart = editPos;
            int deleteEnd = editPos - lengthChange;
            formatMarks.removeIf(mark -> mark.position >= deleteStart && mark.position < deleteEnd);
        }

        for (FormatMark mark : formatMarks) {
            if (lengthChange > 0) {
                if (mark.position > editPos) {
                    mark.position += lengthChange;
                }
            } else {
                if (mark.position >= editPos) {
                    mark.position += lengthChange;
                }
            }
        }

        formatMarks.removeIf(mark -> mark.position < 0 || mark.position > text.length());
        Collections.sort(formatMarks);
    }


    // ===== БАЗОВЫЕ МЕТОДЫ =====

    public void setEscapeEvent(Runnable event) {
        this.escapeEvent = event;
    }

    public void setEnterEvent(Runnable event) {
        this.enterEvent = event;
    }

    public Runnable getEscapeEvent() {
        return this.escapeEvent;
    }

    public Runnable getEnterEvent() {
        return this.enterEvent;
    }

    public void setPlaceholder(Component placeholder) {
        this.placeholder = placeholder;
    }

    public void setText(String text) {
        if (text.length() > this.maxLength) {
            this.text = text.substring(0, this.maxLength);
        } else {
            this.text = text;
        }
        this.setCursorToEnd(false);
        this.formatMarks.clear();
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        return this.text.substring(i, j);
    }

    public void setCursor(int position, boolean shiftKeyPressed) {
        this.selectionStart = Mth.clamp(position, 0, this.text.length());

        if (!shiftKeyPressed) {
            this.selectionEnd = this.selectionStart;
        }

        this.updateFirstCharacterIndex(this.selectionStart);
    }

    public void setCursorToStart(boolean shiftKeyPressed) {
        this.setCursor(0, shiftKeyPressed);
    }

    public void setCursorToEnd(boolean shiftKeyPressed) {
        this.setCursor(this.text.length(), shiftKeyPressed);
    }

    private void moveCursor(int offset, boolean shiftKeyPressed) {
        this.setCursor(Util.offsetByCodepoints(this.text, this.selectionStart, offset), shiftKeyPressed);
    }

    private int getWordSkipPosition(int wordOffset) {
        int pos = this.selectionStart;
        boolean backwards = wordOffset < 0;
        int count = Math.abs(wordOffset);

        for (int i = 0; i < count; i++) {
            if (!backwards) {
                int len = this.text.length();
                pos = this.text.indexOf(32, pos);
                if (pos == -1) {
                    pos = len;
                } else {
                    while (pos < len && this.text.charAt(pos) == ' ') {
                        pos++;
                    }
                }
            } else {
                while (pos > 0 && this.text.charAt(pos - 1) == ' ') {
                    pos--;
                }
                while (pos > 0 && this.text.charAt(pos - 1) != ' ') {
                    pos--;
                }
            }
        }
        return pos;
    }

    public void write(String input) {
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        int availableSpace = this.maxLength - this.text.length() - (i - j);

        if (availableSpace > 0) {
            String filtered = input;

            if (this.isNumerical) {
                filtered = filtered.replaceAll("[^0-9]", "");
            }

            int len = filtered.length();
            if (availableSpace < len) {
                filtered = filtered.substring(0, availableSpace);
                len = availableSpace;
            }

            String newText = new StringBuilder(this.text)
                    .replace(i, j, filtered)
                    .toString();

            int lengthChange = len - (j - i);
            this.text = newText;

            // Обновляем метки форматирования
            updateFormatMarksAfterEdit(i, lengthChange);

            this.setCursor(i + len, false);
        }
    }

    private void erase(int offset) {
        if (Screen.hasControlDown()) {
            this.eraseWords(offset);
        } else {
            this.eraseCharacters(offset);
        }
    }

    private void eraseCharacters(int characterOffset) {
        this.eraseCharactersTo(Util.offsetByCodepoints(this.text, this.selectionStart, characterOffset));
    }

    private void eraseWords(int wordOffset) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.selectionStart) {
                this.write("");
            } else {
                this.eraseCharactersTo(this.getWordSkipPosition(wordOffset));
            }
        }
    }

    private void eraseCharactersTo(int position) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.selectionStart) {
                this.write("");
            } else {
                int i = Math.min(position, this.selectionStart);
                int j = Math.max(position, this.selectionStart);
                if (i != j) {
                    this.text = new StringBuilder(this.text).delete(i, j).toString();

                    // Обновляем метки форматирования
                    updateFormatMarksAfterEdit(i, -(j - i));

                    this.setCursor(i, false);
                }
            }
        }
    }

    private void updateFirstCharacterIndex(int cursor) {
        if (this.font != null) {
            this.firstCharacterIndex = Math.min(this.firstCharacterIndex, this.text.length());
            int innerWidth = this.width - 8;

            String visibleText = this.font.plainSubstrByWidth(this.text.substring(this.firstCharacterIndex),
                    innerWidth);
            int visibleEnd = visibleText.length() + this.firstCharacterIndex;

            if (cursor == this.firstCharacterIndex) {
                String trimmed = this.font.plainSubstrByWidth(this.text, innerWidth, true);
                this.firstCharacterIndex = Math.max(0, this.firstCharacterIndex - trimmed.length());
            }

            if (cursor > visibleEnd) {
                this.firstCharacterIndex += cursor - visibleEnd;
            } else if (cursor <= this.firstCharacterIndex) {
                this.firstCharacterIndex = Math.max(0, this.firstCharacterIndex - (this.firstCharacterIndex - cursor));
            }

            this.firstCharacterIndex = Mth.clamp(this.firstCharacterIndex, 0, this.text.length());
        }
    }

    @Override
    public void onPress() {
    }

    // ? if >1.20.2 {
    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        rndr(ctx, mouseX, mouseY, delta);
    }
    // ?} else if >=1.20 {
    /*
     @Override
     public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float
     delta) {
     rndr(ctx, mouseX, mouseY, delta);
     }
     */// ?} else {
    /*
     @Override
     public void renderWidget(PoseStack ctx, int mouseX, int mouseY, float delta)
     {
     rndr(ctx, mouseX, mouseY, delta);
     }
     */// ?}

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        GUIUtils.drawResizableBox(
                ctx,
                (this.isFocused()) ? FOCUS : NORMAL,
                getX(), getY(), getWidth(), getHeight(),
                3, 7);

        int renderX = getX() + 4;
        int renderY = this.getY() + (this.height - 8) / 2;

        int innerWidth = this.width - 8;
        String fullText = this.text.substring(this.firstCharacterIndex);
        String visibleText = this.font.plainSubstrByWidth(fullText, innerWidth);
        int visibleLength = visibleText.length();
        int visibleEnd = this.firstCharacterIndex + visibleLength;

        if (this.selectionStart != this.selectionEnd) {
            int selStart = Math.min(this.selectionStart, this.selectionEnd);
            int selEnd = Math.max(this.selectionStart, this.selectionEnd);

            int relativeSelStart = Math.max(0, selStart - this.firstCharacterIndex);
            int relativeSelEnd = Math.min(visibleLength, selEnd - this.firstCharacterIndex);

            if (relativeSelStart < relativeSelEnd) {
                String beforeSel = visibleText.substring(0, relativeSelStart);
                String selection = visibleText.substring(relativeSelStart, relativeSelEnd);

                int selX1 = getX() + 4 + font.width(beforeSel);
                int selX2 = selX1 + font.width(selection);
                GUIUtils.drawFill(ctx, selX1, renderY, selX2, renderY + 8, 0x8033AAFF);
            }
        }

        if (!visibleText.isEmpty()) {
            List<StyledSegment> segments = buildStyledSegments(this.text, this.firstCharacterIndex, visibleEnd);

            for (StyledSegment segment : segments) {
                if (segment.text.isEmpty())
                    continue;

                Component styledText = Component.literal(segment.text).setStyle(segment.style);
                // ? if >=1.20 {
                ((GuiGraphics) ctx).drawString(this.font, styledText, renderX, renderY, 0xFFFFFF, true);
                // ?} else {
                /*((PoseStack)ctx).pushPose();
                 this.font.drawShadow((PoseStack)ctx, styledText, renderX, renderY, 0xFFFFFF);
                 ((PoseStack)ctx).popPose();
                 */
                // ?}

                renderX += font.width(segment.text);
            }
        }

        // Рисуем курсор
        int cursorPos = this.selectionStart - this.firstCharacterIndex;
        boolean cursorVisible = cursorPos >= 0 && cursorPos <= visibleLength;
        boolean shouldBlink = (Util.getMillis() - this.lastSwitchFocusTime) / 300L % 2L == 0L;

        if (isFocused() && cursorVisible && shouldBlink) {
            String beforeCursor = visibleText.substring(0, cursorPos);
            int cursorX = getX() + 4 + font.width(beforeCursor);
            GUIUtils.drawFill(ctx, cursorX, renderY, cursorX + 1, renderY + 8, 0xFFFFFFFF);
        }

        // Анимированный placeholder
        if (isAnimatedPlaceholder) {
            animatedPlaceholder(ctx);
        } else {
            if (!isFocused() && getText().isEmpty()) {
                GUIUtils.addText(
                        ctx,
                        Component.literal(this.placeholder.getString()),
                        0,
                        getX() + 4,
                        getY() + (getHeight() - 8) / 2,
                        "top",
                        "left",
                        0xFFAAAAAA,
                        false);
            }
        }
    }

    private void animatedPlaceholder(Object ctx) {
        float scaleFocused = 0.5f;
        float scaleUnfocused = 1f;

        float boxUnfocusedY = getY();
        float boxFocusedY = getY() - 3f;

        int textUnfocusedX = getX() + 4;
        int textFocusedX = getX() + 5;

        int textUnfocusedY = getY() + (getHeight() - 8) / 2;
        int textFocusedY = getY() - 1;

        boolean animatedPlaceholder = isFocused() || !this.getText().isEmpty();

        float wantScale = animatedPlaceholder ? scaleFocused : scaleUnfocused;
        float wantTextX = animatedPlaceholder ? textFocusedX : textUnfocusedX;
        float wantTextY = animatedPlaceholder ? textFocusedY : textUnfocusedY;
        float wantBoxY = animatedPlaceholder ? boxFocusedY : boxUnfocusedY;
        int wantPColor = animatedPlaceholder ? 0xFFFFFFFF : 0xFFAAAAAA;

        float smooth = 0.22f;
        phScale = Mth.lerp(smooth, phScale, wantScale);
        phTextX = Mth.lerp(smooth, phTextX, wantTextX);
        phTextY = Mth.lerp(smooth, phTextY, wantTextY);
        phBoxY = Mth.lerp(smooth, phBoxY, wantBoxY);

        if (!phInit) {
            phInit = true;
            phScale = wantScale;
            phTextX = wantTextX;
            phTextY = wantTextY;
            phBoxY = wantBoxY;
        }

        int rectLeft = getX() + 3;
        int rectTop = Math.round(phBoxY);
        int rectRight = rectLeft + Math.round(font.width(this.placeholder) * scaleFocused) + 4;
        int rectBottom = getY() + 1;
        int outlineColor = isFocused() ? 0xFFFFFFFF : 0xFFA0A0A0;

        GUIUtils.drawFill(ctx, rectLeft + 1, rectTop, rectRight - 1, rectBottom - 1, outlineColor);
        GUIUtils.drawFill(ctx, rectLeft, rectTop + 1, rectRight, rectBottom - 1, outlineColor);
        GUIUtils.drawFill(ctx, rectLeft - 1, (animatedPlaceholder) ? rectTop + 2 : rectTop + 1, rectRight + 1,
                rectBottom - 1, outlineColor);

        GUIUtils.drawFill(ctx, rectLeft + 1, rectTop + 1, rectRight - 1, rectBottom, 0xFF000000);
        GUIUtils.drawFill(ctx, rectLeft, rectTop + 2, rectRight, rectBottom, 0xFF000000);

        GUIUtils.matricesScale(ctx, phScale, () -> {
            GUIUtils.addText(
                    ctx,
                    Component.literal(this.placeholder.getString()),
                    0,
                    Math.round(phTextX / phScale),
                    Math.round(phTextY / phScale),
                    "top",
                    "left",
                    wantPColor,
                    false);
        });
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (this.text.length() > maxLength) {
            this.text = this.text.substring(0, maxLength);
            formatMarks.removeIf(mark -> mark.position > maxLength);
        }
    }

    public int getCursor() {
        return this.selectionStart;
    }

    public static TextField getFocusedField() {
        return focusedField;
    }

    public static void setFocusedField(TextField ff) {
        if (focusedField != null) {
            focusedField.setFocused(false);
        }
        focusedField = ff;
    }

    private static boolean blockFocus = false;
    private static GuiEventListener lastClickedWidget = null;

    public static void setLastClickedWidget(GuiEventListener widget) {
        lastClickedWidget = widget;
    }

    public static void setBlockFocus() {
        blockFocus = true;
    }

    public static void controlFocus() {
        if (focusedField == null) return;

        // Если кликнули на сам TextField - не сбрасываем
        if (lastClickedWidget == focusedField) {
            return;
        }

        if (blockFocus) {
            blockFocus = false;
            return;
        }

        focusedField.setFocused(false);
        focusedField = null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            setLastClickedWidget(this);
            setFocusedField(this);
            this.setFocused(true);

            int relativeX = Mth.floor(mouseX) - this.getX() - 4;
            String visibleText = this.font.plainSubstrByWidth(this.text.substring(this.firstCharacterIndex),
                    this.width - 8);
            int clickedPos = this.font.plainSubstrByWidth(visibleText, relativeX).length() + this.firstCharacterIndex;
            this.setCursor(clickedPos, Screen.hasShiftDown());

            return true;
        }
        return false;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    // ? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    // ?} else if >=1.17 {
    /*
     * @Override
     * public void updateNarration(NarrationElementOutput builder) {
     * }
     */
    // ?}

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused())
            return false;

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT:
                if (Screen.hasControlDown()) {
                    this.setCursor(this.getWordSkipPosition(-1), Screen.hasShiftDown());
                } else {
                    this.moveCursor(-1, Screen.hasShiftDown());
                }
                return true;

            case GLFW.GLFW_KEY_RIGHT:
                if (Screen.hasControlDown()) {
                    this.setCursor(this.getWordSkipPosition(1), Screen.hasShiftDown());
                } else {
                    this.moveCursor(1, Screen.hasShiftDown());
                }
                return true;

            case GLFW.GLFW_KEY_HOME:
                this.setCursorToStart(Screen.hasShiftDown());
                return true;

            case GLFW.GLFW_KEY_END:
                this.setCursorToEnd(Screen.hasShiftDown());
                return true;

            case GLFW.GLFW_KEY_BACKSPACE:
                this.erase(-1);
                return true;

            case GLFW.GLFW_KEY_DELETE:
                this.erase(1);
                return true;

            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                setFocusedField(null);
                this.setFocused(false);
                if (this.enterEvent != null) {
                    this.enterEvent.run();
                }
                return true;

            case GLFW.GLFW_KEY_ESCAPE:
                setFocusedField(null);
                this.setFocused(false);
                if (this.escapeEvent != null) {
                    this.escapeEvent.run();
                }
                return true;

            default:
                if (Screen.isSelectAll(keyCode)) {
                    this.setCursorToEnd(false);
                    this.selectionEnd = 0;
                    return true;
                } else if (Screen.isCopy(keyCode)) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                    return true;
                } else if (Screen.isPaste(keyCode)) {
                    this.write(Minecraft.getInstance().keyboardHandler.getClipboard());
                    return true;
                } else if (Screen.isCut(keyCode)) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                    this.write("");
                    return true;
                }
                return false;
        }
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (!isFocused())
            return false;

        if (Character.isISOControl(c)) {
            return false;
        }

        this.write(Character.toString(c));
        return true;
    }
}