package com.nitsha.binds.gui.modals;

import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.window.IconSelectorWindow;
import com.nitsha.binds.gui.widget.window.ModalWindow;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;*/
//? }

public class SelectIcon extends ModalWindow {
//    private static final ResourceLocation TAB2_BG = Main.id("textures/gui/test/scroller.png");

    private final BindsEditor screen;
    private IconSelectorWindow selector;

    public SelectIcon(BindsEditor screen, int x, int y, int width, int height, ResourceLocation t1,
                      ResourceLocation t2) {
        super(screen, x, y, width, height, t1, t2, "nitsha.binds.advances.modals.choose_icon");
        this.screen = screen;

        this.selector = new IconSelectorWindow(4, 20, 162, 144, (stack, key) -> {
            onSelect(key);
        });

//        this.addDrawElement((ctx, mouseX, mouseY) -> {
//            GUIUtils.drawResizableBox(ctx, TAB2_BG, 4, 20, this.getWidth() - 8, this.getHeight() - 27, 1, 3);
//        });

        this.addElement(this.selector);

    }
    @Override
    public void renderWindow(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
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