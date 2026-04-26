package com.nitsha.binds.gui.panels.advanced;

import com.nitsha.binds.Main;
import com.nitsha.binds.action.ActionRegistry;
import com.nitsha.binds.configs.dto.preset.ActionData;
import com.nitsha.binds.gui.panels.NewAction;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.MainKeybindSelector;
import com.nitsha.binds.gui.widget.TextField;
import com.nitsha.binds.gui.widget.button.BedrockIconButton;
import com.nitsha.binds.gui.widget.button.BedrockIconOptionButton;
import com.nitsha.binds.gui.widget.list.ActionItem;
import com.nitsha.binds.gui.widget.window.ContentWindow;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class VariablesTab extends ContentWindow {
    private static final ResourceLocation TAB2_BG = Main.id("textures/gui/test/scroller.png");

    private ScrollableWindow list;

    private final BindsEditor screen;

    public VariablesTab(BindsEditor screen, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.screen = screen;

        this.list = new ScrollableWindow(5, 25, this.getX(), this.getY(), getWidth() - 10, getHeight() - 54, false);

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.drawResizableBox(ctx, TAB2_BG, 4, 24, this.getWidth() - 8, this.getHeight() - 52, 1, 3);
        });

        this.addElement(list);
    }

    public void updateHeight(int newH) {
        this.setHeight(newH);
        this.list.setHeight(newH - 54);
    }
}
