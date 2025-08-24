package com.nitsha.binds.gui.panels;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.widget.BedrockIconButton;
import com.nitsha.binds.gui.widget.ItemButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
//? if >=1.17 {
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
//? }
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class IconSelector extends AbstractParentElement implements Drawable, Element /*? if >=1.17 {*/ , Selectable /*? }*/ {
    private final List<Element> children = new ArrayList<>();
    private final List<BedrockIconButton> catBtns = new ArrayList<>();

    private static final Identifier ITEMS = MainClass.id("textures/gui/test/items_4.png");
    private static final Identifier SCROLLER = MainClass.id("textures/gui/test/scroller.png");
    private static final Identifier SCROLLER_BTN = MainClass.id("textures/gui/btns/button_normal.png");
    private static final Identifier SCROLLER_BTN_HVR = MainClass.id("textures/gui/btns/button_hover.png");

    private int width, height, x, y;

    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int scrollBarOffset = 0;
    private int barSize = 20;

    private static final int COLUMNS = 9;
    private static final int VISIBLE_ROWS = 7;

    public static String activeKey = "STRUCTURE_BLOCK";
    private boolean isDraggingScrollbar = false;

    private String currentCategory = "blocks";
    //? if <=1.20.4 {
    /*private ItemStack maceIcon = new ItemStack(Items.DIAMOND_HOE);
     *///? } else {
    private ItemStack maceIcon = new ItemStack(Items.MACE);
    //? }
    private ItemStack[] categories = {new ItemStack(Items.GRASS_BLOCK), maceIcon, new ItemStack(Items.COOKED_BEEF),
                                      ItemsMapper.getPotionItem(Potions.SWIFTNESS, Items.POTION), new ItemStack(Items.ORANGE_SHULKER_BOX), new ItemStack(Items.NETHERITE_INGOT)};
    private final String[] categoriesList = {"blocks", "tools", "foods", "potions", "colored", "gold"};

    private final BindsEditor screen;

    public IconSelector(BindsEditor screen, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.screen = screen;
        createButtons();

        int catNum = 6;
        int btnW = (((width + 10)) - ((catNum - 1) * 2)) / catNum;

        for (int i = 0; i < catNum; i++) {
            int finalI = i;
            BedrockIconButton cat = new BedrockIconButton(this.x + ((btnW + 2) * i), this.y + this.height + 2, btnW, 20, true, () -> {
                this.currentCategory = categoriesList[finalI];
                this.scrollOffset = 0;
                for (BedrockIconButton btn : catBtns) { btn.setPressed(false); }
                catBtns.get(finalI).setPressed(true);
                createButtons();
            }, categories[i], 0xFFFFFFFF, 0xFF83CA6f, 0xFF212121, 0xFFFFFFFF);
            if (i == 0) cat.setPressed(true);
            this.catBtns.add(cat);
            this.children.add(cat);
        }
    }

    private void createButtons() {
        this.children.removeIf(element -> element instanceof ItemButton);
        Map<String, ItemStack> sourceMap = ItemsMapper.categories.getOrDefault(currentCategory, ItemsMapper.itemStackMap);
        List<Map.Entry<String, ItemStack>> fullList = new ArrayList<>(sourceMap.entrySet());

        int offset = scrollOffset * COLUMNS;
        int total = fullList.size();
        int maxVisible = COLUMNS * VISIBLE_ROWS;

        int startX = x;
        int startY = y;

        for (int i = 0; i < maxVisible && (i + offset) < total; i++) {
            Map.Entry<String, ItemStack> entry = fullList.get(i + offset);
            int row = i / COLUMNS;
            int col = i % COLUMNS;

            int bx = startX + col * 18;
            int by = startY + row * 18;

            ItemStack stack = entry.getValue();
            String key = entry.getKey();

            ItemButton button = new ItemButton(bx, by, 18, stack, () -> {
                screen.getBasicOptionsWindow().getEditIcon().setIcon(stack);
                updateButtons(key);
                BindsEditor.editIconBtnString = key;
                if (!BindsEditor.getCBind().actions.isEmpty())  {
                    screen.saveBind();
                }
            }, ITEMS, key);
            this.children.add(button);
        }

        int rowAmount = (int) Math.ceil(fullList.size() / (float) COLUMNS);
        maxScroll = Math.max(0, rowAmount - VISIBLE_ROWS);
        updateScrollLogic(rowAmount);
        updateButtons(activeKey);
    }

    public void pickRandom() {
        if (ItemsMapper.itemStackMap.isEmpty()) return;

        List<Map.Entry<String, ItemStack>> entries = new ArrayList<>(ItemsMapper.itemStackMap.entrySet());
        Map.Entry<String, ItemStack> entry = entries.get(new Random().nextInt(entries.size()));

        String key = entry.getKey();
        screen.getBasicOptionsWindow().getEditIcon().setIcon(entry.getValue());
        updateButtons(key);
        BindsEditor.editIconBtnString = key;
        if (!BindsEditor.getCBind().actions.isEmpty())  {
            screen.saveBind();
        }
    }

    public void updateButtons(String key) {
        activeKey = key;
        for (Element element : children) {
            if (element instanceof ItemButton btn) {
                boolean selected = btn.getKey().equals(key);
                btn.setSelected(selected);
            }
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public void resetScroll() {
        scrollOffset = 0;
        currentCategory = "blocks";
        for (BedrockIconButton btn : catBtns) { btn.setPressed(false); }
        catBtns.get(0).setPressed(true);
        createButtons();
    }

    @Override
    public void render(
            //? if >=1.20 {
            DrawContext ctx
            //? } else {
            /*MatrixStack ctx
             *///? }
            , int mouseX, int mouseY, float delta) {
        for (Element element : this.children) {
            if (element instanceof Drawable drawable) {
                drawable.render(ctx, mouseX, mouseY, delta);
            }
        }

        int scrollbarX = this.x + this.width + 1;
        int scrollbarY = this.y + 1 + scrollBarOffset;
        boolean isHoveringScrollbar = mouseX >= scrollbarX && mouseX <= scrollbarX + 8 &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + barSize;

        Identifier scrollbarTexture = (isHoveringScrollbar || isDraggingScrollbar) ? SCROLLER_BTN_HVR : SCROLLER_BTN;
        int scrollbarColor = (isHoveringScrollbar || isDraggingScrollbar) ? 0xFFFFFFFF : 0xFF8B8B8B;
        GUIUtils.drawResizableBox(ctx, SCROLLER, this.x + this.width, this.y, 10, this.height, 1, 3);
        GUIUtils.drawResizableBox(ctx, scrollbarTexture, this.x + this.width + 1, this.y + 1 + scrollBarOffset, 8, barSize, 3, 7);
        GUIUtils.drawFill(ctx,
                this.x + this.width + 3,
                this.y + (barSize / 2) + scrollBarOffset,
                this.x + this.width + 7,
                this.y + (barSize / 2) + scrollBarOffset + 1,
                scrollbarColor);

        GUIUtils.drawFill(ctx,
                this.x + this.width + 3,
                this.y + (barSize / 2) + scrollBarOffset - 2,
                this.x + this.width + 7,
                this.y + (barSize / 2) + scrollBarOffset - 1,
                scrollbarColor);

        GUIUtils.drawFill(ctx,
                this.x + this.width + 3,
                this.y + (barSize / 2) + scrollBarOffset + 2,
                this.x + this.width + 7,
                this.y + (barSize / 2) + scrollBarOffset + 3,
                scrollbarColor);
    }

    //? if >=1.17 {
    @Override
    public void appendNarrations(NarrationMessageBuilder builder) { }
    //? }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    //? if >=1.17 {
    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
    //? }

    public void updateScrollLogic(int rowAmount) {
        this.maxScroll = Math.max(0, rowAmount - 7); // 7 видимых строк
        int trackHeight = height - 2;
        this.barSize = Math.max(20, trackHeight - (maxScroll * 5));
        int scrollArea = trackHeight - barSize;

        scrollOffset = Math.min(scrollOffset, maxScroll);

        float scrollProgress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0;
        scrollBarOffset = (int)(scrollProgress * scrollArea);
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        float windowX = this.x;
        float windowY = this.y;
        float windowWidth = this.width;
        float windowHeight = this.height;

        return mouseX >= windowX &&
                mouseX <= windowX + windowWidth &&
                mouseY >= windowY &&
                mouseY <= windowY + windowHeight;
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isMouseInside(mouseX, mouseY)) return false;
        scrollOffset = MathHelper.clamp(scrollOffset - (int) verticalAmount, 0, maxScroll);

        float scrollProgress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0;
        scrollBarOffset = (int) ((height - 2 - barSize) * scrollProgress);

        createButtons();

        return true;
    }
    //? } else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        scrollOffset = MathHelper.clamp(scrollOffset - (int) amount, 0, maxScroll);

        float scrollProgress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0;
        scrollBarOffset = (int) ((height - 2 - barSize) * scrollProgress);

        createButtons();

        return true;
    }
    *///? }

    private int dragStartY = 0;
    private int dragStartScrollOffset = 0;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int barX = this.x + this.width + 1;
            int barY = this.y + 1 + scrollBarOffset;

            if (mouseX >= barX && mouseX <= barX + 8 && mouseY >= barY && mouseY <= barY + barSize) {
                isDraggingScrollbar = true;
                dragStartY = (int) mouseY;
                dragStartScrollOffset = scrollOffset;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && isDraggingScrollbar) {
            isDraggingScrollbar = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDraggingScrollbar && button == 0) {
            int trackHeight = height - 2;
            int scrollArea = trackHeight - barSize;

            int dy = (int) mouseY - dragStartY;

            float scrollProgress = (float) dy / scrollArea;
            scrollOffset = MathHelper.clamp(dragStartScrollOffset + Math.round(scrollProgress * maxScroll), 0, maxScroll);

            float newProgress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0;
            scrollBarOffset = (int)(newProgress * scrollArea);

            createButtons();
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
