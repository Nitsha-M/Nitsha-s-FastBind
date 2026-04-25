package com.nitsha.binds.gui.widget.window;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.TextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class ModalWindow extends AnimatedWindow {

    private static final ResourceLocation CLOSE = Main.idSprite("close_modal");
    private static final ResourceLocation CLOSE_HOVER = Main.idSprite("close_modal_hover");
    private static final ResourceLocation SEARCH = Main.idSprite("search_modal");
    private static final ResourceLocation SEARCH_HOVER = Main.idSprite("search_modal_hover");

    private final BindsEditor screen;
    private AbstractWidget closeBtn;
    private AbstractWidget searchBtn;
    private TextField searchField;

    private float bgAlpha = 0f;
    private float bgAlphaTarget = 0f;
    private static final float BG_MAX_ALPHA = 0.5f;
    private static final float BG_SPEED = 0.2f;

    private String title = "Default";

    private boolean needSearch = false;
    private boolean searchMode = false;

    public ModalWindow(BindsEditor screen, int x, int y, int width, int height, ResourceLocation t1,
                          ResourceLocation t2, String title) {
        super(x, y, width, height, t1, t2, 0);
        this.screen = screen;
        this.bgAlphaTarget = 0f;

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            if (!this.searchMode) {
                GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.truncateString(TextUtils.translatable(title).getString(), 20)), 0,
                        7, 10, "left", "center", 0xFF212121, false);
            }
        }, 1);

        this.closeBtn = GUIUtils.createTexturedBtn(width - 17, 5, 11, 11, new ResourceLocation[]{CLOSE, CLOSE_HOVER}, button -> {
            if (searchMode) {
                this.searchField.setText("");
            } else {
                this.close(()->{});
            }
        });

        this.addElement(closeBtn);

        this.searchField = new TextField(
                Minecraft.getInstance().font,
                4, 3, width - 23, 15,
                Integer.MAX_VALUE, "",
                TextUtils.translatable("nitsha.binds.advances.actions.typeSomeText").getString()
        );
        this.searchField.setAnimatedPlaceholder(false);
        this.searchField.visible = false;
        this.searchField.setEnterEvent(() -> setSearchMode(false));
        this.searchField.setEscapeEvent(() -> setSearchMode(false));
        this.searchField.setClickOutEvent(() -> setSearchMode(false));

        this.searchBtn = GUIUtils.createTexturedBtn(width - 17 - 11 - 2, 5, 11, 11, new ResourceLocation[]{SEARCH, SEARCH_HOVER}, button -> {
            setSearchMode(true);
        });

        this.addElement(searchBtn);
        this.addElement(searchField);

        if (!needSearch) this.searchBtn.visible = false;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public void setSearchMode(boolean mode) {
        if (needSearch) {
            this.searchMode = mode;
            this.searchBtn.visible = !mode;
            this.searchField.visible = mode;
            if (mode) this.searchField.setFocus();
        }
    }

    public boolean isSearchMode() {
        return searchMode;
    }

    public void setNeedSearch(boolean needSearch) {
        this.needSearch = needSearch;
        this.searchBtn.visible = needSearch;
    }

    @Override
    public void renderWindow(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        int adjX = mouseX - this.getX();
        int adjY = mouseY - this.getYOffset();

        bgAlpha += (bgAlphaTarget - bgAlpha) * Math.min(1f, BG_SPEED * delta);

        int alpha = (int)(bgAlpha * 255) << 24;
        int bgColor = alpha | 0x00000000;

        GUIUtils.matricesUtil(ctx, 0, 0, 200, () -> {
            GUIUtils.drawFill(ctx, 0, 0, 10000, 10000, bgColor);
            super.renderWindow(ctx, mouseX, mouseY, delta);
        });
    }

    @Override
    public void open(Runnable onFinish) {
        bgAlphaTarget = BG_MAX_ALPHA;
        super.open(onFinish);
    }

    @Override
    public void close(Runnable onFinish) {
        bgAlphaTarget = 0f;
        super.close(onFinish);
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                && mouseY >= this.getY() - 16 && mouseY < this.getY() + this.getHeight();
    }
}