package com.nitsha.binds.gui.panels;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.Main;
import com.nitsha.binds.configs.Bind;
import com.nitsha.binds.configs.BindsStorage;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.AnimatedSprite;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.AnimatedWindow;
import com.nitsha.binds.gui.widget.ItemButton;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.SmallTextButton;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//? }
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BindsList extends AnimatedWindow {
    private AnimatedSprite rightBtn;
    private AnimatedSprite leftBtn;
    private AbstractWidget leftA;
    private AbstractWidget rightA;
    private AbstractWidget closeBtn;

    // Textures
    private static final ResourceLocation FRAME = Main.id("textures/gui/test/frame.png");
    private static final ResourceLocation ARROW_SPRITE = Main.id("textures/gui/test/arrow_animation.png");
    private static final ResourceLocation ARROW_SPRITE_LEFT = Main.id("textures/gui/test/arrow_animation_left.png");
    private static final ResourceLocation ARROW_L = Main.idSprite("arrow_left");
    private static final ResourceLocation ARROW_L_HOVER = Main.idSprite("arrow_left_hover");
    private static final ResourceLocation ARROW_R = Main.idSprite("arrow_right");
    private static final ResourceLocation ARROW_R_HOVER = Main.idSprite("arrow_right_hover");
    private static final ResourceLocation CLOSE = Main.idSprite("close");
    private static final ResourceLocation CLOSE_HOVER = Main.idSprite("close_hover");
    private static final ResourceLocation ITEMS_SELECTOR = Main.id("textures/gui/test/items_2.png");
    private static final ResourceLocation DELETE_SMALL = Main.id("textures/gui/test/delete_small_icon.png");
    private static final ResourceLocation ADD_NEW = Main.id("textures/gui/test/add_new_icon.png");

    private static final ResourceLocation SEPARATOR = Main.id("textures/gui/separator.png");


    private final List<ItemButton> buttons = new ArrayList<>();

    private final BindsEditor screen;

    private static final char[] SUPER = {'⁰','¹','²','³','⁴','⁵','⁶','⁷','⁸','⁹'};

    public BindsList(BindsEditor screen, float x, int y, float width, int height, ResourceLocation t1, ResourceLocation t2, int delay) {
        super(x, y, width, height, t1, t2, delay);
        this.screen = screen;
        initUI(screen);
    }

    private void initUI(BindsEditor screen) {
        this.leftA = GUIUtils.createTexturedBtn(-19, 43, 11, 11, new ResourceLocation[]{ARROW_L, ARROW_L_HOVER}, button -> screen.setNewPage(-1));
        this.rightA = GUIUtils.createTexturedBtn(141, 43, 11, 11, new ResourceLocation[]{ARROW_R, ARROW_R_HOVER}, button -> screen.setNewPage(1));

        this.closeBtn = GUIUtils.createTexturedBtn(110, 7, 16, 16, new ResourceLocation[]{CLOSE, CLOSE_HOVER}, button -> {
            screen.closeEditor();
            //? if >=1.17.1 {
            Minecraft.getInstance().setScreen((Screen)null);
            //?} else {
            /*Minecraft.getInstance().setScreen((Screen)null);*/
            //?}
        });

        this.rightBtn = new AnimatedSprite(22, 15, ARROW_SPRITE, 0, false, 0, 0, 220, 22, 20, 242, 15);
        this.leftBtn = new AnimatedSprite(22, 15, ARROW_SPRITE_LEFT, 0, false, 0, 0, 220, 22, 20, 242, 15);
        this.leftBtn.setPosition(-20, 42);
        this.rightBtn.setPosition(131, 42);

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            //? if >=1.20 {
            GuiGraphics c = (GuiGraphics) ctx;
            //?} else {
            /*PoseStack c = (PoseStack) ctx;
             *///?}
            GUIUtils.adaptiveDrawTexture(ctx, FRAME, 4, 4, 0, 0, 125, 90, 125 , 90);
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.configure"), 141, 8, 11);

            MutableComponent page = TextUtils.translatable("nitsha.binds.page");
            MutableComponent currentPage = TextUtils.literal(toSuper(String.valueOf(BindsEditor.getCurrentPage() + 1)));
            MutableComponent totalPage = TextUtils.literal(toSuper(String.valueOf(
                    BindsEditor.getCurrentPreset() >= 0 && BindsEditor.getCurrentPreset() < BindsStorage.presets.size()
                            ? BindsStorage.presets.get(BindsEditor.getCurrentPreset()).pages.size()
                            : 1
            )));
            int pWidth = Minecraft.getInstance().font.width(page);
            int сPWidth = Minecraft.getInstance().font.width(currentPage);
            int tPWidth = Minecraft.getInstance().font.width(totalPage);

            GUIUtils.addText(ctx, page, 126, 4, 96, "left", "top", 0xFFFFFFFF, false);
            GUIUtils.addText(ctx, totalPage.withStyle(style -> style.withColor(ChatFormatting.GRAY)), 125, 129, 98, "right", "top");
            GUIUtils.adaptiveDrawTexture(ctx, SEPARATOR, 129 - tPWidth - 4, 98, 0, 0, 3, 6, 3, 6);
            GUIUtils.addText(ctx, currentPage.withStyle(style -> style.withColor(ChatFormatting.GRAY)), 125, 129 - tPWidth - 4, 98, "right", "top");

            GUIUtils.drawFill(c, pWidth + 6, 100, getWidth() - 6 - сPWidth - tPWidth - 4, 101, 0xFF555555);

            rightBtn.render(c);
            leftBtn.render(c);
        });

        this.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.delete"), 4, 107, 0xFF790e06, 61,"left", DELETE_SMALL, ()-> {
            int currentPage = BindsEditor.getCurrentPage();
            int currentPreset = BindsEditor.getCurrentPreset();
            int activeBind = screen.getActiveBind();

            boolean activeBindOnDeletedPage = screen.isActiveBindOnPage(currentPage);

            BindsStorage.removePage(currentPreset, currentPage);

            int newTotalPages = BindsStorage.presets.get(currentPreset).pages.size();

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
        }));


        this.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.addNew"), 67, 107, 0xFF4d9109, 62, "left", ADD_NEW, ()-> {
            BindsStorage.addPage(BindsEditor.getCurrentPreset());
        }));

        this.open(() -> {
            rightBtn.startAnimation(true);
            leftBtn.startAnimation(true);
            this.addElement(leftA);
            this.addElement(rightA);
        });

        this.addElement(closeBtn);
        generateButtons(7, 31);
    }

    public static String toSuper(String s) {
        StringBuilder r = new StringBuilder();
        for (char c : s.toCharArray())
            r.append(c >= '0' && c <= '9' ? SUPER[c - '0'] : c);
        return r.toString();
    }

    public void updateSelected(ItemStack icon) {
        int aB = (int) Math.floor((double) BindsEditor.getActiveBind() / 8);
        if (aB == BindsEditor.getCurrentPage()) buttons.get(BindsEditor.getActiveBind() - (8 * aB)).setIcon(icon);
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
            Bind currentBind = BindsStorage.getBind(BindsEditor.getCurrentPreset(), bindIndex);

            ItemButton[] buttonHolder = new ItemButton[1];

            buttonHolder[0] = new ItemButton(currentX, currentY, ItemsMapper.getItemStack(currentBind.icon), () -> {
                screen.saveBind();
                for (ItemButton btn : buttons) {
                    btn.setSelected(false);
                }
                BindsEditor.setActiveBind(bindIndex);
                buttonHolder[0].setSelected(true);
                screen.selectBind();
            }, ITEMS_SELECTOR, "");

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
    }

    @Override
    public void render(
            //? if >=1.20 {
            GuiGraphics ctx
            //?} else {
            /*PoseStack ctx
             *///?}
            , int mouseX, int mouseY, float delta) {
        GUIUtils.matricesUtil(ctx, 0, 0, 2, () -> {
            super.render(ctx, mouseX, mouseY, delta);
        });
    }
}