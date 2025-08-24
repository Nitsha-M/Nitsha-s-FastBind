package com.nitsha.binds.gui.panels;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.configs.BindEntry;
import com.nitsha.binds.configs.BindsConfig;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.AnimatedSprite;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.AnimatedWindow;
import com.nitsha.binds.gui.widget.ItemButton;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class BindsList extends AnimatedWindow {
    private AnimatedSprite rightBtn;
    private AnimatedSprite leftBtn;
    private PressableWidget leftA;
    private PressableWidget rightA;
    private PressableWidget closeBtn;

    // Textures
    private static final Identifier FRAME = MainClass.id("textures/gui/test/frame.png");
    private static final Identifier ARROW_SPRITE = MainClass.id("textures/gui/test/arrow_animation.png");
    private static final Identifier ARROW_SPRITE_LEFT = MainClass.id("textures/gui/test/arrow_animation_left.png");
    private static final Identifier ARROW_L = MainClass.idSprite("arrow_left");
    private static final Identifier ARROW_L_HOVER = MainClass.idSprite("arrow_left_hover");
    private static final Identifier ARROW_R = MainClass.idSprite("arrow_right");
    private static final Identifier ARROW_R_HOVER = MainClass.idSprite("arrow_right_hover");
    private static final Identifier CLOSE = MainClass.idSprite("close");
    private static final Identifier CLOSE_HOVER = MainClass.idSprite("close_hover");
    private static final Identifier ITEMS_SELECTOR = MainClass.id("textures/gui/test/items_2.png");

    private final List<ItemButton> buttons = new ArrayList<>();

    private final BindsEditor screen;

    public BindsList(BindsEditor screen, float x, int y, float width, int height, Identifier t1, Identifier t2, int delay) {
        super(x, y, width, height, t1, t2, delay);
        this.screen = screen;
        initUI(screen);
    }

    private void initUI(BindsEditor screen) {
        this.leftA = GUIUtils.createTexturedBtn(-19, 43, 11, 11, new Identifier[]{ARROW_L, ARROW_L_HOVER}, button -> screen.setNewPage(-1));
        this.rightA = GUIUtils.createTexturedBtn(141, 43, 11, 11, new Identifier[]{ARROW_R, ARROW_R_HOVER}, button -> screen.setNewPage(1));

        this.closeBtn = GUIUtils.createTexturedBtn(110, 7, 16, 16, new Identifier[]{CLOSE, CLOSE_HOVER}, button -> {
            screen.closeEditor();
            //? if >=1.17.1 {
            MinecraftClient.getInstance().setScreen((Screen)null);
            //? } else {
            /*MinecraftClient.getInstance().openScreen((Screen)null);*/
            //? }
        });

        this.rightBtn = new AnimatedSprite(22, 15, ARROW_SPRITE, 0, false, 0, 0, 220, 22, 20, 242, 15);
        this.leftBtn = new AnimatedSprite(22, 15, ARROW_SPRITE_LEFT, 0, false, 0, 0, 220, 22, 20, 242, 15);
        this.leftBtn.setPosition(-20, 42);
        this.rightBtn.setPosition(131, 42);

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            //? if >=1.20 {
            DrawContext c = (DrawContext) ctx;
            //? } else {
            /*MatrixStack c = (MatrixStack) ctx;
             *///? }
            GUIUtils.adaptiveDrawTexture(ctx, FRAME, 4, 4, 0, 0, 125, 90, 125 , 90);
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.configure"), 141, 8, 11);
            GUIUtils.addText(ctx, TextUtils.literal((BindsEditor.getCurrentPage() + 1) + "/5").styled(style -> style.withColor(Formatting.GRAY)), 133, 108, 11,"right", "top");

            rightBtn.render(c);
            leftBtn.render(c);
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
            BindEntry currentBind = BindsConfig.getBind(BindsEditor.getCurrentPreset(), bindIndex);

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
            DrawContext ctx
            //? } else {
            /*MatrixStack ctx
             *///? }
            , int mouseX, int mouseY, float delta) {
        GUIUtils.matricesUtil(ctx, 0, 0, 2, () -> {
            super.render(ctx, mouseX, mouseY, delta);
        });
    }
}
