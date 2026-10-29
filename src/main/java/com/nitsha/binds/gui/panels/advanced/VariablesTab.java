package com.nitsha.binds.gui.panels.advanced;

import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.gui.widget.SteppedSlider;
import com.nitsha.binds.multiversions.NitshaContainer;
import com.nitsha.binds.multiversions.RenderUtils;

public class VariablesTab extends NitshaContainer {

    private ScrollableWindow list;

    private final BindsEditor screen;

    public VariablesTab(BindsEditor screen, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.screen = screen;

        this.list = new ScrollableWindow(5, 25, getWidth() - 10, getHeight() - 54, false);

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            RenderUtils.drawResizableBox(ctx, Textures.INNER_BG, 4, 24, this.getWidth() - 8, this.getHeight() - 52, 1, 3);
        });

        this.addElement(list);

        this.addElement(new SteppedSlider(10, 30, this.getWidth() - 20, 12, true, 0, 3, 1, 2));
    }

    public void updateHeight(int newH) {
        this.setHeight(newH);
        this.list.setHeight(newH - 54);
    }
}
