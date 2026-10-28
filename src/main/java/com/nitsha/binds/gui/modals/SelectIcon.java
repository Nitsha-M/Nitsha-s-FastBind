package com.nitsha.binds.gui.modals;

import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.window.ModalWindow;
import com.nitsha.binds.guinew.widget.window.IconSelectorWindow;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class SelectIcon extends ModalWindow {

    private final BindsEditor screen;
    private IconSelectorWindow selector;

    public SelectIcon(BindsEditor screen, int x, int y, int width, int height, Identifier t1,
                      Identifier t2) {
        super(screen, x, y, width, height, t1, t2, "nitsha.binds.advances.modals.choose_icon");
        this.screen = screen;

        this.selector = new IconSelectorWindow(4, 20, 162, 144, (stack, key) -> {
            onSelect(key);
        });

        this.addElement(this.selector);

    }
    @Override
    public void renderWindow(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        int adjX = mouseX - this.getX();
        int adjY = mouseY - this.getYOffset();

        GUIUtils.matricesUtil(ctx, 0, 0, 200, () -> {
            super.renderWindow(ctx, mouseX, mouseY, delta);
        });
    }
    @Override
    public void open(Runnable onFinish) {
        super.open(onFinish);
    }

    public void onSelect(String key) {
        EventBus.emit("selectIcon.result", key);
        this.close(() -> {});
    }
}