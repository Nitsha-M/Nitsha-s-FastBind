package com.nitsha.binds.gui.widget.window;

import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.widget.TextField;
import com.nitsha.binds.gui.widget.button.TexturedButton;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.Identifier;

public class ModalWindow extends AnimatedWindow {

    private final BindsEditor screen;
    private AbstractWidget closeBtn;
    private AbstractWidget searchBtn;
    private TextField searchField;

    private float bgAlpha = 0f;
    private float bgAlphaTarget = 0f;
    private static final float BG_MAX_ALPHA = 0.5f;
    private static final float BG_SPEED = 0.2f;

    private boolean needSearch = false;
    private boolean searchMode = false;

    public ModalWindow(BindsEditor screen, int x, int y, int width, int height, Identifier t1,
                          Identifier t2, String title) {
        super(x, y, width, height, t1, t2, 0);
        this.screen = screen;
        this.bgAlphaTarget = 0f;

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            if (!this.searchMode) {
                RenderUtils.drawText(ctx, TextUtils.small(TextUtils.literal(RenderUtils.truncateString(TextUtils.translatable(title).getString(), 20))), 0,
                        7, 10, "left", "center", 0xFF212121, false);
            }
        }, 1);

        this.closeBtn = new TexturedButton(width - 17, 5, 11, 11, Textures.ICON_CLOSE_MODAL, Textures.ICON_CLOSE_MODAL_HOVER, button -> {
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

        this.searchBtn = new TexturedButton(width - 17 - 11 - 2, 5, 11, 11, Textures.ICON_SEARCH_MODAL, Textures.ICON_SEARCH_MODAL_HOVER, button -> {
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
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        bgAlpha += (bgAlphaTarget - bgAlpha) * Math.min(1.0F, BG_SPEED * delta);

        int bgColor = (int) (bgAlpha * 255) << 24;

        RenderUtils.matricesUtil(ctx, 0, 0, 200, () -> {
            RenderUtils.drawFill(ctx, 0, 0, 10000, 10000, bgColor);
            super.onRender(ctx, mouseX, mouseY, delta);
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