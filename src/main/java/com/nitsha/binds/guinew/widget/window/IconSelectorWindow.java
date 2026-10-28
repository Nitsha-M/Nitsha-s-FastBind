package com.nitsha.binds.guinew.widget.window;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.guinew.utils.Textures;
import com.nitsha.binds.guinew.widget.button.BedrockIconButton;
import com.nitsha.binds.guinew.widget.button.ItemButton;
import com.nitsha.binds.multiversions.NitshaContainer;
import com.nitsha.binds.multiversions.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class IconSelectorWindow extends NitshaContainer {

    private static final Identifier SCROLLER = Main.id("textures/gui/test/scroller.png");
    private static final Identifier SCROLLER_BTN = Main.id("textures/gui/btns/scroll_bar_normal.png");
    private static final Identifier SCROLLER_BTN_HVR = Main.id("textures/gui/btns/scroll_bar_hover.png");
    private static final int COLUMNS = 9;
    private static final int VISIBLE_ROWS = 8;

    private final List<BedrockIconButton> catBtns = new ArrayList<>();
    private final String[] categoriesList = {"blocks", "tools", "colored", "foods", "gold", "mods"};
    private final BiConsumer<ItemStack, String> onClick;
    private int scrollOffset;
    private int maxScroll;
    private int scrollBarOffset;
    private int barSize = 20;
    private int dragStartY;
    private int dragStartScrollOffset;
    private boolean isDraggingScrollbar;
    private boolean buttonsDirty;
    private String currentCategory = "blocks";

    public static String activeKey = "minecraft:structure_block";

    public IconSelectorWindow(int x, int y, int width, int height, BiConsumer<ItemStack, String> onClick) {
        super(x, y, width, height);
        setVisible(true);
        this.onClick = onClick;
        Item[] categoryIcons = {Items.GRASS_BLOCK, Items.MACE, Items.BED.red(), Items.POTION, Items.GOLD_INGOT, Items.COMMAND_BLOCK};
        int xPos = 0;

        for (int i = 0; i < categoriesList.length; i++) {
            String category = categoriesList[i];
            int catWidth = i == 0 || i == categoriesList.length - 1 ? 30 : 28;
            Runnable selectCategory = () -> {
                currentCategory = category;
                scrollOffset = 0;
                isDraggingScrollbar = false;
                updateCategoryButtons();
                buttonsDirty = true;
            };
            BedrockIconButton button = new BedrockIconButton(xPos, height + 1, catWidth, 20, true, selectCategory, ItemsMapper.createIcon(categoryIcons[i]));

            if (i == 0) {
                button.setButtonDirection(1);
            } else if (i == categoriesList.length - 1) {
                button.setButtonDirection(2);
            } else {
                button.setButtonDirection(3);
            }

            catBtns.add(button);
            addElement(button);
            xPos += catWidth;
        }

        for (int i = 0; i < catBtns.size(); i++) {
            if (i < catBtns.size() - 1) {
                catBtns.get(i).setNeighbor(catBtns.get(i + 1));
            } else {
                catBtns.get(i).setNeighbor(catBtns.get(i - 1));
            }

            if (i > 0 && i < catBtns.size() - 1) {
                catBtns.get(i).setNeighbor2(catBtns.get(i - 1));
            }
        }

        updateCategoryButtons();
        createButtons();
        addDrawElement(this::renderScrollbar, 1);
    }

    private void updateCategoryButtons() {
        for (int i = 0; i < catBtns.size(); i++) {
            catBtns.get(i).setPressed(categoriesList[i].equals(currentCategory));
        }
    }

    private void createButtons() {
        removeElementsOfType(ItemButton.class);
        buttonsDirty = false;

        List<ItemsMapper.IconEntry> filtered = ItemsMapper.getCategories().getOrDefault(currentCategory, List.of());
        int rowAmount = (filtered.size() + COLUMNS - 1) / COLUMNS;
        updateScrollLogic(rowAmount);

        int offset = scrollOffset * COLUMNS;
        int maxVisible = COLUMNS * VISIBLE_ROWS;

        for (int i = 0; i < maxVisible && i + offset < filtered.size(); i++) {
            ItemsMapper.IconEntry entry = filtered.get(i + offset);
            ItemStack icon = ItemsMapper.createIcon(entry.icon());
            String key = entry.key();
            int buttonX = i % COLUMNS * 18;
            int buttonY = i / COLUMNS * 18;

            Runnable selectIcon = () -> {
                updateButtons(key);
                onClick.accept(icon, key);
            };
            ItemButton button = new ItemButton(buttonX, buttonY, 18, icon, Textures.ICON_SELECTOR_ITEM, key, selectIcon);

            addElement(button);
        }

        updateButtons(activeKey);
    }

    public void updateButtons(String key) {
        activeKey = key;

        for (GuiEventListener element : children()) {
            if (element instanceof ItemButton button) {
                button.setSelected(button.getKey().equals(key));
            }
        }
    }

    public void resetScroll() {
        scrollOffset = 0;
        currentCategory = "blocks";
        isDraggingScrollbar = false;
        updateCategoryButtons();
        createButtons();
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        if (buttonsDirty) {
            createButtons();
        }

        super.onRender(ctx, mouseX, mouseY, delta);
    }

    private void renderScrollbar(GuiGraphicsExtractor ctx, float mouseX, float mouseY) {
        int scrollbarX = getWidth() + 1;
        int scrollbarY = 1 + scrollBarOffset;
        boolean hovered = mouseX >= scrollbarX && mouseX <= scrollbarX + 8 && mouseY >= scrollbarY && mouseY <= scrollbarY + barSize;
        Identifier texture = hovered || isDraggingScrollbar ? SCROLLER_BTN_HVR : SCROLLER_BTN;
        int color = hovered || isDraggingScrollbar ? 0xFFFFFFFF : 0xFF8B8B8B;

        RenderUtils.drawResizableBox(ctx, SCROLLER, getWidth(), 0, 10, getHeight(), 1, 3);
        RenderUtils.drawResizableBox(ctx, texture, scrollbarX, scrollbarY, 8, barSize, 3, 7);

        for (int offset = -2; offset <= 2; offset += 2) {
            int lineY = barSize / 2 + scrollBarOffset + offset;
            RenderUtils.drawFill(ctx, getWidth() + 3, lineY, getWidth() + 7, lineY + 1, color);
        }
    }

    public void updateScrollLogic(int rowAmount) {
        maxScroll = Math.max(0, rowAmount - VISIBLE_ROWS);
        int trackHeight = Math.max(0, getHeight() - 2);
        barSize = Math.min(trackHeight, Math.max(20, trackHeight - maxScroll * 5));
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);

        float progress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0;
        scrollBarOffset = Math.round(progress * (trackHeight - barSize));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!isVisible()) return false;

        double localX = mouseX - getContentX();
        double localY = mouseY - getContentY();

        if (localX >= getWidth() && localX < getWidth() + 10 && localY >= 0 && localY < getHeight()) {
            return true;
        }

        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isMouseInside(mouseX, mouseY) || verticalAmount == 0.0 || maxScroll == 0) {
            return false;
        }

        int direction = verticalAmount > 0 ? -1 : 1;
        scrollOffset = Mth.clamp(scrollOffset + direction, 0, maxScroll);
        createButtons();
        return true;
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!canClick()) return false;

        if (button == InputConstants.MOUSE_BUTTON_LEFT && maxScroll > 0) {
            int barX = getContentX() + getWidth() + 1;
            int barY = getContentY() + 1 + scrollBarOffset;

            if (mouseX >= barX && mouseX <= barX + 8 && mouseY >= barY && mouseY <= barY + barSize) {
                isDraggingScrollbar = true;
                dragStartY = (int) (mouseY - getContentY());
                dragStartScrollOffset = scrollOffset;
                return true;
            }
        }

        return super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT && isDraggingScrollbar) {
            isDraggingScrollbar = false;
            return true;
        }

        boolean released = super.onMouseReleased(mouseX, mouseY, button);

        if (buttonsDirty) {
            createButtons();
        }

        return released;
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isVisible()) return false;

        if (isDraggingScrollbar && button == InputConstants.MOUSE_BUTTON_LEFT) {
            int scrollArea = getHeight() - 2 - barSize;

            if (scrollArea <= 0) {
                return false;
            }

            float progress = (float) (mouseY - getContentY() - dragStartY) / scrollArea;
            scrollOffset = Mth.clamp(dragStartScrollOffset + Math.round(progress * maxScroll), 0, maxScroll);
            createButtons();
            return true;
        }

        return super.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
