package com.nitsha.binds.gui.panels;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.Main;
import com.nitsha.binds.action.ActionRegistry;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.dto.option.HiddenField;
import com.nitsha.binds.gui.panels.advanced.ActionsTab;
import com.nitsha.binds.gui.panels.advanced.IconTab;
import com.nitsha.binds.gui.panels.advanced.OptionsTab;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.button.*;
import com.nitsha.binds.gui.widget.list.ActionItem;
import com.nitsha.binds.gui.widget.window.AnimatedWindow;
import com.nitsha.binds.gui.widget.window.IconSelectorWindow;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.nitsha.binds.configs.dto.preset.ActionData;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;*/
//? }

//? if >=26.1 {
// import net.minecraft.world.item.ItemStackTemplate;
//? }

public class AdvancedOptions extends AnimatedWindow {
    private final List<TabButton> tabsBtn = new ArrayList<>();

    private static final ResourceLocation RESIZE = Main.idSprite("resize");
    private static final ResourceLocation RESIZE_HOVER = Main.idSprite("resize_hover");

    private final BindsEditor screen;
    private final TexturedButton changeHeight;

    private int currentTab = 0;

    private ActionsTab actionsTab;
    private IconTab iconTab;
    private OptionsTab optionsTab;

    private ScrollableWindow tabList;

    public AdvancedOptions(BindsEditor screen, int x, int y, int width, int height, ResourceLocation t1,
                           ResourceLocation t2, int delay) {
        super(x, y, width, height, t1, t2, delay);
        tabsBtn.clear();
        this.screen = screen;

        this.tabList = new ScrollableWindow(4, -16, 4, -16, this.getWidth() - 8, 16, true);
        this.tabList.setShowScrollbar(false);

        TabButton tab1 = new TabButton(0, 0, true, TextUtils.translatable("nitsha.binds.advances.tab.actions"),
                b -> selectTab(0));
        TabButton tab2 = new TabButton(tab1.getX() + tab1.getWidth() + 2, 0, false,
                TextUtils.translatable("nitsha.binds.advances.tab.icon"), b -> selectTab(1));
        TabButton tab3 = new TabButton(tab2.getX() + tab2.getWidth() + 2, 0, false,
                TextUtils.translatable("nitsha.binds.advances.tab.variables"), b -> selectTab(2));
        TabButton tab4 = new TabButton(tab3.getX() + tab3.getWidth() + 2, 0, false,
                TextUtils.translatable("nitsha.binds.advances.tab.options"), b -> selectTab(3));

        tabsBtn.add(tab1);
        tabsBtn.add(tab2);
        tabsBtn.add(tab3);
        tabsBtn.add(tab4);

        int totalW = 0;
        for (TabButton btn : tabsBtn) {
            this.tabList.addElement(btn);
            totalW += btn.getWidth() + 2;
        }
        this.tabList.setScrollableArea(totalW);

        this.changeHeight = GUIUtils.createTexturedBtn(getWidth() + 4, 0, 9, 9, new ResourceLocation[]{RESIZE, RESIZE_HOVER}, button -> {
            Storage.options.fullHeightEditor = !Storage.options.fullHeightEditor;

            int newY = (Storage.options.fullHeightEditor) ? 16 : ((screen.height - 190) / 2);
            int newH = (Storage.options.fullHeightEditor) ? screen.height - 16 : 190;

            this.setHeight(newH);
            this.setY(newY);

            this.actionsTab.updateHeight(newH);
            this.optionsTab.updateHeight(newH);

            Storage.saveModOptions();
        });

        this.actionsTab = new ActionsTab(screen, 0, 0, this.getWidth(), this.getHeight());
        this.iconTab = new IconTab(screen, 0, 0, this.getWidth(), this.getHeight());
        this.optionsTab = new OptionsTab(screen, 0, 0, this.getWidth(), this.getHeight());

        this.addElement(this.actionsTab);
        this.addElement(this.iconTab);
        this.addElement(this.optionsTab);

        this.addElement(changeHeight);
        this.addElement(tabList);

        openTab(0);

        this.open(() -> {
        });
    }

    private void openTab(int i) {
        tabsBtn.forEach(tab -> tab.setSelected(false));
        tabsBtn.get(i).setSelected(true);
        currentTab = i;

        if (actionsTab != null) actionsTab.setVisible(i == 0);
        if (iconTab != null) iconTab.setVisible(i == 1);
        if (optionsTab != null) optionsTab.setVisible(i == 2);
        if (optionsTab != null) optionsTab.setVisible(i == 3);
    }

    public void selectTab(int i) {
        if (BindsEditor.getCBind().actions != null && !BindsEditor.getCBind().actions.isEmpty())
            screen.saveBind();
        openTab(i);
    }

    public ActionsTab getActionsTab() {
        return this.actionsTab;
    }

    public IconTab getIconTab() {
        return iconTab;
    }

    public OptionsTab getOptionsTab() {
        return optionsTab;
    }

    @Override
    public void renderWindow(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        int adjX = mouseX - this.getX();
        int adjY = mouseY - this.getYOffset();

        if (this.actionsTab.getAddNewAction() != null && this.actionsTab.getAddNewAction().isMouseInside(adjX, adjY) && this.actionsTab.getAddNewAction().isOpen() && currentTab == 0) {
            super.renderWindow(ctx, -10000, -10000, delta);
        } else {
            super.renderWindow(ctx, mouseX, mouseY, delta);
        }

        GUIUtils.matricesUtil(ctx, getX(), getYOffset(), 2, () -> {
            if (isVisible() && currentTab == 0 && this.actionsTab.getAddNewAction() != null)
                //? if >=26.1 {
                // this.actionsTab.getAddNewAction().extractRenderState(ctx, adjX, adjY, delta);
                //? } else {
                this.actionsTab.getAddNewAction().render(ctx, adjX, adjY, delta);
            //? }
        });
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth()
                && mouseY >= this.getYOffset() - 16 && mouseY <= this.getYOffset() + this.getHeight();
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isVisible()) return false;
        double adjX = mouseX - this.getX();
        double adjY = mouseY - this.getYOffset();
        boolean scrolled = false;
        if (this.tabList.mouseScrolled(adjX, adjY, horizontalAmount, verticalAmount)) return true;
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) scrolled = true;
        return scrolled;
    }
    //? } else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!isVisible()) return false;
        double adjX = mouseX - this.getX();
        double adjY = mouseY - this.getYOffset();
        boolean scrolled = false;
        if (this.tabList.mouseScrolled(adjX, adjY, amount)) return true;
        if (super.mouseScrolled(mouseX, mouseY, amount)) scrolled = true;
        return scrolled;
    }*/
    //? }

}