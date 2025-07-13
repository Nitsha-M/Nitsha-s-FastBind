package com.nitsha.binds.gui.panels;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.GUIUtils;
import com.nitsha.binds.gui.widget.BedrockButton;
import com.nitsha.binds.gui.widget.BedrockIconButton;
import com.nitsha.binds.gui.widget.ItemButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class IconSelector extends AbstractParentElement implements Drawable, Element, Selectable {
    private final List<Element> children = new ArrayList<>();
    private final List<BedrockIconButton> catBtns = new ArrayList<>();
    public static final List<ItemButton> buttons = new ArrayList<>();

    private static final Identifier ITEMS = MainClass.id("textures/gui/test/items_4.png");
    private static final Identifier SCROLLER = MainClass.id("textures/gui/test/scroller.png");
    private static final Identifier SCROLLER_BTN = MainClass.id("textures/gui/btns/button_normal.png");
    private static final Identifier SCROLLER_BTN_HVR = MainClass.id("textures/gui/btns/button_hover.png");

    private int width, height, x, y;
    private boolean isOpen;

    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int scrollBarOffset = 0;
    private int barSize = 20;

    private static final int COLUMNS = 9;
    private static final int VISIBLE_ROWS = 7;

    public static String activeKey = "STRUCTURE_BLOCK";
    private boolean isDraggingScrollbar = false;

    private final float speed = 0.1f;
    private float animHeight = 0, targetH = 0;

    private String currentCategory = "blocks";
    //? if <=1.20.4 {
    /*private ItemStack maceIcon = new ItemStack(Items.DIAMOND_HOE);
     *///? } else {
    private ItemStack maceIcon = new ItemStack(Items.MACE);
    //? }
    private ItemStack[] categories = {new ItemStack(Items.GRASS_BLOCK), maceIcon, new ItemStack(Items.COOKED_BEEF),
                                      ItemsMapper.getPotionItem(Potions.SWIFTNESS, Items.POTION), new ItemStack(Items.ORANGE_SHULKER_BOX), new ItemStack(Items.NETHERITE_INGOT)};
    private final String[] categoriesList = {"blocks", "tools", "foods", "potions", "colored", "gold"};

    public IconSelector(int width, int height, int x, int y) {
        this.x = x;
        this.width = width;
        this.y = y;
        this.height = height;
        this.targetH = this.y + this.height + 22;
        buttons.clear();
        isOpen = false;
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
            catBtns.add(cat);
        }
    }

    private void createButtons() {
        buttons.clear();

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
                MinecraftClient.getInstance().getSoundManager()
                        .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                BindsEditor.editIconBtn.setIcon(stack);
                BindsEditor.editIconBtnString = key;
                updateButtons(key);
            }, ITEMS, key);

            buttons.add(button);
        }

        int rowAmount = (int) Math.ceil(fullList.size() / (float) COLUMNS);
        maxScroll = Math.max(0, rowAmount - VISIBLE_ROWS);
        updateScrollLogic(rowAmount);
        updateButtons(activeKey);
    }

    public static void pickRandom() {
        if (ItemsMapper.itemStackMap.isEmpty()) return;

        List<Map.Entry<String, ItemStack>> entries = new ArrayList<>(ItemsMapper.itemStackMap.entrySet());
        Map.Entry<String, ItemStack> entry = entries.get(new Random().nextInt(entries.size()));

        String key = entry.getKey();
        BindsEditor.editIconBtn.setIcon(entry.getValue());
        BindsEditor.editIconBtnString = key;
        updateButtons(key);
    }

    public static void updateButtons(String key) {
        activeKey = key;
        for (ItemButton btn : buttons) {
            boolean selected = btn.getKey().equals(key);
            btn.setSelected(selected);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void open(boolean o) {
        this.isOpen = o;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        //? if <1.21.6 {
        MatrixStack matrices = ctx.getMatrices();
        matrices.push();
        //? }
        if (isOpen) {
            this.animHeight = MathHelper.lerp(speed, this.animHeight, targetH);
            if (Math.abs(this.animHeight - targetH) < 0.1f) this.animHeight = targetH;
        } else {
            this.animHeight = MathHelper.lerp(speed, this.animHeight, 0);
            if (Math.abs(this.animHeight - 0) < 0.1f) this.animHeight = 0;
        }
        ctx.enableScissor(this.x, this.y, this.x + this.width + 14, Math.round(this.animHeight));

        for (ItemButton button : buttons) {
            button.render(ctx, mouseX, mouseY, delta);
        }
        for (BedrockIconButton button : catBtns) {
            button.render(ctx, mouseX, mouseY, delta);
        }

        int scrollbarX = this.x + this.width + 1;
        int scrollbarY = this.y + 1 + scrollBarOffset;
        boolean isHoveringScrollbar = mouseX >= scrollbarX && mouseX <= scrollbarX + 8 &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + barSize;

        Identifier scrollbarTexture = (isHoveringScrollbar || isDraggingScrollbar) ? SCROLLER_BTN_HVR : SCROLLER_BTN;
        int scrollbarColor = (isHoveringScrollbar || isDraggingScrollbar) ? 0xFFFFFFFF : 0xFF8B8B8B;
        GUIUtils.drawResizableBox(ctx, SCROLLER, this.x + this.width, this.y, 10, this.height, 1, 3);
        GUIUtils.drawResizableBox(ctx, scrollbarTexture, this.x + this.width + 1, this.y + 1 + scrollBarOffset, 8, barSize, 3, 7);
        ctx.drawHorizontalLine(this.x + this.width + 3, this.x + this.width + 6, this.y + (barSize / 2) + scrollBarOffset, scrollbarColor);
        ctx.drawHorizontalLine(this.x + this.width + 3, this.x + this.width + 6, this.y + (barSize / 2) + scrollBarOffset - 2, scrollbarColor);
        ctx.drawHorizontalLine(this.x + this.width + 3, this.x + this.width + 6, this.y + (barSize / 2) + scrollBarOffset + 2, scrollbarColor);

        ctx.disableScissor();
        //? if <1.21.6 {
        matrices.pop();
        //? }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) { }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    public void updateScrollLogic(int rowAmount) {
        this.maxScroll = Math.max(0, rowAmount - 7); // 7 видимых строк
        int trackHeight = height - 2;
        this.barSize = Math.max(20, trackHeight - (maxScroll * 5));
        int scrollArea = trackHeight - barSize;

        scrollOffset = Math.min(scrollOffset, maxScroll);

        float scrollProgress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0;
        scrollBarOffset = (int)(scrollProgress * scrollArea);
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isOpen) return false;

        scrollOffset = MathHelper.clamp(scrollOffset - (int) verticalAmount, 0, maxScroll);

        float scrollProgress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0;
        scrollBarOffset = (int) ((height - 2 - barSize) * scrollProgress);

        createButtons();

        return true;
    }
    //? } else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!isOpen) return false;

        scrollOffset = MathHelper.clamp(scrollOffset - (int) amount, 0, maxScroll);

        float scrollProgress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0;
        scrollBarOffset = (int) ((height - 2 - barSize) * scrollProgress);

        createButtons();

        return true;
    }
    *///? }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (!isOpen) return;
        for (Element element : catBtns) {
            element.mouseMoved(mouseX, mouseY);
        }
        for (Element element : buttons) {
            element.mouseMoved(mouseX, mouseY);
        }
    }

    private int dragStartY = 0;
    private int dragStartScrollOffset = 0;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isOpen) return false;

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

        for (BedrockIconButton btn : catBtns) {
            if (btn.isMouseOver(mouseX, mouseY)) {
                btn.mouseClicked(mouseX, mouseY, button);
                return true;
            }
        }

        for (ItemButton btn : buttons) {
            if (btn.isMouseOver(mouseX, mouseY)) {
                btn.mouseClicked(mouseX, mouseY, button);
                return true;
            }
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isOpen) return false;

        if (button == 0 && isDraggingScrollbar) {
            isDraggingScrollbar = false;
        }

        boolean handled = false;
        for (Element element : catBtns) {
            if (element.mouseReleased(mouseX, mouseY, button)) handled = true;
        }
        for (Element element : buttons) {
            if (element.mouseReleased(mouseX, mouseY, button)) handled = true;
        }
        return handled;
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isOpen || !isDraggingScrollbar) return false;

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
}
