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
    private static final Identifier FRAME = Main.id("textures/gui/test/frame.png");
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

    private SmallTextButton deleteBtn;
    private long deleteConfirmationTime = 0;
    private boolean deleteConfirmShown = false;

    public BindsList(BindsEditor screen, float x, int y, float width, int height, Identifier t1, Identifier t2, int delay) {
        super(x, y, width, height, t1, t2, delay);
        this.screen = screen;
        initUI(screen);
    }

    private void initUI(BindsEditor screen) {
        this.leftA = new TexturedButton(-19, 43, 11, 11, ARROW_L, ARROW_L_HOVER, button -> {
            screen.setNewPage(-1);
            confirm(false);
        });
        this.rightA = new TexturedButton(141, 43, 11, 11, ARROW_R, ARROW_R_HOVER, button -> {
            screen.setNewPage(1);
            confirm(false);
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
            RenderUtils.drawTexture(ctx, FRAME, 4, 4, 0, 0, 125, 90, 125, 90);
            RenderUtils.drawText(ctx, TextUtils.small(TextUtils.translatable("nitsha.binds.configure")), 141, 8, 11);

            int pageCount = BindsEditor.activePreset != null ? BindsEditor.activePreset.pages.size() : 1;

            MutableComponent page = TextUtils.small(TextUtils.translatable("nitsha.binds.page"));
            MutableComponent currentPage = TextUtils.small(TextUtils.literal(String.valueOf(BindsEditor.getCurrentPage() + 1))).withStyle(ChatFormatting.GRAY);
            MutableComponent totalPage = TextUtils.small(TextUtils.literal(String.valueOf(pageCount))).withStyle(ChatFormatting.GRAY);

            int pageWidth = Minecraft.getInstance().font.width(page);
            int currentPageWidth = Minecraft.getInstance().font.width(currentPage);
            int totalPageWidth = Minecraft.getInstance().font.width(totalPage);

            RenderUtils.drawText(ctx, page, 126, 4, 96, "left", "top", 0xFFFFFFFF, false);
            RenderUtils.drawText(ctx, totalPage, 125, 129, 96, "right", "top", 0xFFFFFFFF, false);
            RenderUtils.drawTexture(ctx, SEPARATOR, 129 - totalPageWidth - 4, 98, 0, 0, 3, 6, 3, 6);
            RenderUtils.drawText(ctx, currentPage, 125, 129 - totalPageWidth - 4, 96, "right", "top", 0xFFFFFFFF, false);

            RenderUtils.drawFill(ctx, pageWidth + 6, 100, getWidth() - 6 - currentPageWidth - totalPageWidth - 4, 101, 0xFF555555);

            rightBtn.render(ctx);
            leftBtn.render(ctx);
        });

        this.deleteBtn = new SmallTextButton(TextUtils.translatable("nitsha.binds.delete"), 4, 107, 0xFF790e06, 61, "left", DELETE_SMALL, ()-> {
            if (deleteConfirmShown) {
                int currentPage = BindsEditor.getCurrentPage();
                int activeBind = screen.getActiveBind();

                boolean activeBindOnDeletedPage = screen.isActiveBindOnPage(currentPage);

                if (BindsEditor.activePreset != null && BindsEditor.activePreset.pages.size() > 1) {
                    BindsEditor.activePreset.pages.remove(currentPage);
                    Storage.savePreset(BindsEditor.activePreset, BindsEditor.activePresetId);
                }

                int newTotalPages = BindsEditor.activePreset != null ? BindsEditor.activePreset.pages.size() : 1;

                if (currentPage >= newTotalPages) {
                    currentPage = newTotalPages - 1;
                }

                if (activeBindOnDeletedPage) {
                    screen.setActiveBind(screen.getFirstBindOfPage(currentPage));
                    screen.selectBind();
                }
                else if (activeBind > screen.getFirstBindOfPage(currentPage)) {
                    screen.setActiveBind(activeBind - 8);
                    screen.selectBind();
                }

                screen.selectPage(currentPage);
                screen.getBindsListWindow().generateButtons(7, 31);
                confirm(false);
            } else {
                deleteConfirmationTime = System.currentTimeMillis();
                confirm(true);
            }
        });

        this.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.addNew"), 67, 107, 0xFF4d9109, 62, "left", ADD_NEW, ()-> {
            if (BindsEditor.activePreset != null) {
                BindsEditor.activePreset.pages.add(new PageData());
                Storage.savePreset(BindsEditor.activePreset, BindsEditor.activePresetId);
            }
            deleteBtn.setEnabled(true);
        }));

        this.open(() -> {
            rightBtn.startAnimation(true);
            leftBtn.startAnimation(true);
            this.addElement(leftA);
            this.addElement(rightA);
        });

        this.addElement(closeBtn);
        this.addElement(deleteBtn);
        generateButtons(7, 31);
    }

    public void confirm(boolean status) {
        deleteConfirmShown = status;
        if (status) {
            deleteBtn.setColors(0xFFfac70c, 0xFFF5CC35, 0xFFFFFFFF, 0xFFFFFFFF);
            deleteBtn.setIcon(SURE_SMALL);
        } else {
            deleteBtn.setColors(0xFF790e06, 0xFF8C150B, 0xFFFFFFFF, 0xFFFFFFFF);
            deleteBtn.setIcon(DELETE_SMALL);
        }
    }

    public boolean isDeleteConfirmation() {
        return System.currentTimeMillis() - deleteConfirmationTime < 5000;
    }

     public void updateSelected(ItemStack icon) {
        int aB = (int) Math.floor((double) BindsEditor.getActiveBind() / 8);
        if (aB == BindsEditor.getCurrentPage()) buttons.get(BindsEditor.getActiveBind() - (8 * aB)).setItemStack(icon);
    }

    public void generateButtons(int startX, int startY) {
        this.buttons.clear();
        this.removeElementsOfType(ItemButton.class);

        int currentX = startX;
        int currentY = startY;

        int currentPage = BindsEditor.getCurrentPage();
        int activeBind = BindsEditor.getActiveBind();

        for (int row = 0; row < 8; row++) {
            int bindIndex = row + (8 * currentPage);
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

            currentX += 31;
            if (row == 3) {
                currentX = startX;
                currentY = startY + 31;
            }
        }

        int aB = (int) Math.floor((double) activeBind / 8);
        if (aB == currentPage) {
            buttons.get(activeBind - (8 * aB)).setSelected(true);
        }
        deleteBtn.setEnabled(BindsEditor.activePreset != null && BindsEditor.activePreset.pages.size() > 1);
    }

    @Override
    public void renderWindow(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.matricesUtil(ctx, 0, 0, 2, () -> {
            super.renderWindow(ctx, mouseX, mouseY, delta);
        });
    }

    @Override
    public void tick() {
        super.tick();
        if (deleteConfirmShown && !isDeleteConfirmation()) {
            confirm(false);
        }
    }
}