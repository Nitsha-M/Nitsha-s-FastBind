package com.nitsha.binds.gui.panels;

import com.nitsha.binds.Main;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.configs.dto.preset.BindData;
import com.nitsha.binds.configs.dto.preset.PageData;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.AnimatedSprite;
import com.nitsha.binds.gui.widget.button.TexturedButton;
import com.nitsha.binds.gui.widget.button.ItemButton;
import com.nitsha.binds.gui.widget.button.SmallTextButton;
import com.nitsha.binds.gui.widget.window.AnimatedWindow;
import com.nitsha.binds.multiversions.NitshaCraft;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BindsList extends AnimatedWindow {
    private AnimatedSprite rightBtn;
    private AnimatedSprite leftBtn;
    private AbstractWidget leftA;
    private AbstractWidget rightA;
    private AbstractWidget closeBtn;

    // Textures
    private static final Identifier FRAME = Main.id("textures/gui/frame_bg.png");
    private static final Identifier ARROW_SPRITE = Main.id("textures/gui/test/arrow_animation.png");
    private static final Identifier ARROW_SPRITE_LEFT = Main.id("textures/gui/test/arrow_animation_left.png");
    private static final Identifier ARROW_L = Main.idSprite("arrow_left");
    private static final Identifier ARROW_L_HOVER = Main.idSprite("arrow_left_hover");
    private static final Identifier ARROW_R = Main.idSprite("arrow_right");
    private static final Identifier ARROW_R_HOVER = Main.idSprite("arrow_right_hover");
    private static final Identifier CLOSE = Main.idSprite("close");
    private static final Identifier CLOSE_HOVER = Main.idSprite("close_hover");
    private static final Identifier ITEMS_SELECTOR = Main.id("textures/gui/test/items_2.png");
    private static final Identifier DELETE_SMALL = Main.id("textures/gui/test/delete_small_icon.png");
    private static final Identifier SURE_SMALL = Main.id("textures/gui/test/sure_small.png");
    private static final Identifier ADD_NEW = Main.id("textures/gui/test/add_new_icon.png");

    private static final Identifier SEPARATOR = Main.id("textures/gui/separator.png");

    private final List<ItemButton> buttons = new ArrayList<>();

    private final BindsEditor screen;
    private final float anchorCenterX;
    private final float anchorBottomY;

    public BindsList(BindsEditor screen, float x, int y, float width, int height, Identifier t1, Identifier t2, int delay) {
        super(x, y, width, height, t1, t2, delay);
        this.screen = screen;
        anchorCenterX = x + width / 2.0F;
        anchorBottomY = y + height;
        initUI(screen);
    }

    private void initUI(BindsEditor screen) {
        this.leftA = new TexturedButton(-19, 43, 11, 11, ARROW_L, ARROW_L_HOVER, button -> {
            screen.setNewPage(-1);
        });
        this.rightA = new TexturedButton(141, 43, 11, 11, ARROW_R, ARROW_R_HOVER, button -> {
            screen.setNewPage(1);
        });

        this.closeBtn = new TexturedButton(110, 7, 16, 16, CLOSE, CLOSE_HOVER, button -> {
            screen.closeEditor();
            NitshaCraft.setScreen(null);
        });

        this.rightBtn = new AnimatedSprite(22, 15, ARROW_SPRITE, 0, false, 0, 0, 220, 22, 20, 242, 15);
        this.leftBtn = new AnimatedSprite(22, 15, ARROW_SPRITE_LEFT, 0, false, 0, 0, 220, 22, 20, 242, 15);
        this.leftBtn.setPosition(-20, 42);
        this.rightBtn.setPosition(131, 42);

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            int pageCount = BindsEditor.activePreset != null ? BindsEditor.activePreset.pages.size() : 1;

            MutableComponent page = TextUtils.small(TextUtils.translatable("nitsha.binds.page"));
            MutableComponent currentPage = TextUtils.small(TextUtils.literal(String.valueOf(BindsEditor.getCurrentPage() + 1))).withStyle(ChatFormatting.GRAY);
            MutableComponent totalPage = TextUtils.small(TextUtils.literal(String.valueOf(pageCount))).withStyle(ChatFormatting.GRAY);

            int pageWidth = Minecraft.getInstance().font.width(page);
            int currentPageWidth = Minecraft.getInstance().font.width(currentPage);
            int totalPageWidth = Minecraft.getInstance().font.width(totalPage);

            RenderUtils.drawText(ctx, totalPage, 125, this.getWidth() - 24, 11, "right", "top", 0xFFFFFFFF, false);
            RenderUtils.drawTexture(ctx, SEPARATOR, this.getWidth() - 24 - totalPageWidth - 4, 13, 0, 0, 3, 6, 3, 6);
            RenderUtils.drawText(ctx, currentPage, 125, this.getWidth() - 24 - totalPageWidth - 4, 11, "right", "top", 0xFFFFFFFF, false);

            drawFrame(ctx);
            RenderUtils.drawText(ctx, TextUtils.small(TextUtils.translatable("nitsha.binds.configure")), 141, 8, 11);

            rightBtn.render(ctx);
            leftBtn.render(ctx);
        });

        this.open(() -> {
            rightBtn.startAnimation(true);
            leftBtn.startAnimation(true);
            this.addElement(leftA);
            this.addElement(rightA);
        });

        this.addElement(closeBtn);
        generateButtons(7, 31);
    }

     public void updateSelected(ItemStack icon) {
        int slot = BindsEditor.getActiveBind() - BindsEditor.getFirstBindOfPage(BindsEditor.getCurrentPage());
        if (BindsEditor.isActiveBindOnPage(BindsEditor.getCurrentPage()) && slot >= 0 && slot < buttons.size()) {
            buttons.get(slot).setItemStack(icon);
        }
    }

    public void generateButtons(int startX, int startY) {
        this.buttons.clear();
        this.removeElementsOfType(ItemButton.class);

        int currentPage = BindsEditor.getCurrentPage();
        int activeBind = BindsEditor.getActiveBind();
        if (BindsEditor.activePreset == null) return;

        int columns = BindsEditor.activePreset.columns;
        int rows = BindsEditor.activePreset.rows;
        int targetWidth = columns * 31 + 9;
        int targetHeight = rows * 31 + 38;
        setWidth(targetWidth);
        setHeight(targetHeight);
        setX(anchorCenterX - targetWidth / 2.0F);
        setY(anchorBottomY - targetHeight);

        for (int slot = 0; slot < columns * rows; slot++) {
            int currentX = startX + (slot % columns) * 31;
            int currentY = startY + (slot / columns) * 31;
            int bindIndex = slot + BindsEditor.getFirstBindOfPage(currentPage);
            BindData currentBind = null;
            if (BindsEditor.activePreset != null && currentPage < BindsEditor.activePreset.pages.size()) {
                PageData page = BindsEditor.activePreset.pages.get(currentPage);
                if (page.binds != null) {
                    for (BindData b : page.binds) {
                        if (b.index == bindIndex) { currentBind = b; break; }
                    }
                }
            }
            if (currentBind == null) {
                currentBind = new BindData(); currentBind.index = bindIndex;
            }

            ItemButton[] buttonHolder = new ItemButton[1];

            buttonHolder[0] = new ItemButton(currentX, currentY, ItemsMapper.getItemStack(currentBind.icon), ITEMS_SELECTOR, "", () -> {
                screen.saveBind();

                for (ItemButton btn : buttons) {
                    btn.setSelected(false);
                }

                BindsEditor.setActiveBind(bindIndex);
                buttonHolder[0].setSelected(true);
                screen.selectBind();
            });

            ItemButton button = buttonHolder[0];

            this.addElement(button);
            buttons.add(button);

        }

        int selectedSlot = activeBind - BindsEditor.getFirstBindOfPage(currentPage);
        if (BindsEditor.isActiveBindOnPage(currentPage) && selectedSlot >= 0 && selectedSlot < buttons.size()) {
            buttons.get(selectedSlot).setSelected(true);
        }
    }

    private void drawFrame(GuiGraphicsExtractor ctx) {
        RenderUtils.drawResizableBox(ctx, FRAME, 4, 4, getWidth() - 8, getHeight() - 10, 3, 7);
        RenderUtils.drawResizableBox(ctx, FRAME, 4, 4, getWidth() - 8, 23, 3, 7);
    }

    @Override
    protected void updateLayout() {
        closeBtn.setX(getWidth() - 23);
        int arrowY = (getHeight() - 15) / 2;
        leftA.setY(arrowY + 1);
        rightA.setX(getWidth() + 8);
        rightA.setY(arrowY + 1);
        leftBtn.setPosition(-20, arrowY);
        rightBtn.setPosition(getWidth() - 2, arrowY);
    }

    @Override
    protected void renderChild(GuiGraphicsExtractor ctx, GuiEventListener child, int contentX, int contentY, int mouseX, int mouseY, float delta) {
        if (child instanceof ItemButton) {
            RenderUtils.customScissor(ctx, contentX + 4, contentY + 27, getWidth() - 8, getHeight() - 33, () -> {
                super.renderChild(ctx, child, contentX, contentY, mouseX, mouseY, delta);
            });
            return;
        }

        super.renderChild(ctx, child, contentX, contentY, mouseX, mouseY, delta);
    }

    @Override
    public void renderWindow(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.matricesUtil(ctx, 0, 0, 2, () -> {
            super.renderWindow(ctx, mouseX, mouseY, delta);
        });
    }
}
