package com.nitsha.binds.gui.panels;

import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.gui.panels.advanced.VariablesTab;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.multiversions.TextUtils;
import com.nitsha.binds.gui.widget.button.TexturedButton;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import com.nitsha.binds.gui.panels.advanced.ActionsTab;
import com.nitsha.binds.gui.panels.advanced.IconTab;
import com.nitsha.binds.gui.panels.advanced.OptionsTab;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.gui.widget.button.TabButton;
import com.nitsha.binds.gui.widget.window.AnimatedWindow;
import com.nitsha.binds.multiversions.NitshaInput;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class AdvancedOptions extends AnimatedWindow {

    private final List<TabButton> tabsBtn = new ArrayList<>();

    private final BindsEditor screen;
    private final TexturedButton changeHeight;

    private static int currentTab = 0;

    private ActionsTab actionsTab;
    private IconTab iconTab;
    private VariablesTab variablesTab;
    private OptionsTab optionsTab;

    private ScrollableWindow tabList;

    public AdvancedOptions(BindsEditor screen, int x, int y, int width, int height, Identifier t1, Identifier t2, int delay) {
        super(x, y, width, height, t1, t2, delay);
        tabsBtn.clear();
        this.screen = screen;

        this.tabList = new ScrollableWindow(4, -16, this.getWidth() - 9, 16, true);
        this.tabList.setShowScrollbar(false);

        String[] tabKeys = {"actions", "icon", "variables", "options"};
        TabButton prev = null;
        for (int i = 0; i < tabKeys.length; i++) {
            final int index = i;
            int bX = prev == null ? 0 : prev.getX() + prev.getWidth() + 2;
            TabButton tab = new TabButton(bX, 0, i == 0,
                    TextUtils.translatable("nitsha.binds.advances.tab." + tabKeys[i]),
                    b -> selectTab(index));
            tab.setScrollWindow(this.tabList);
            tabsBtn.add(tab);
            prev = tab;
        }

        int totalW = 0;
        for (TabButton btn : tabsBtn) {
            this.tabList.addElement(btn);
            totalW += btn.getWidth() + 2;
        }
        this.tabList.setScrollableArea(totalW);

        this.changeHeight = new TexturedButton(getWidth() + 4, 0, 9, 9, Textures.RESIZE, Textures.RESIZE_HOVER, button -> {
            Storage.options.fullHeightEditor = !Storage.options.fullHeightEditor;

            int newY = (Storage.options.fullHeightEditor) ? 26 : ((screen.height - 190) / 2);
            int newH = (Storage.options.fullHeightEditor) ? screen.height - 36 : 190;

            this.setHeight(newH);
            this.setY(newY);

            Storage.saveModOptions();
        });

        this.actionsTab = new ActionsTab(screen, 0, 0, this.getWidth(), this.getHeight());
        this.iconTab = new IconTab(screen, 0, 0, this.getWidth(), this.getHeight());
        this.variablesTab = new VariablesTab(screen, 0, 0, this.getWidth(), this.getHeight());
        this.optionsTab = new OptionsTab(screen, 0, 0, this.getWidth(), this.getHeight());

        this.addElement(this.actionsTab);
        this.addElement(this.iconTab);
        this.addElement(this.variablesTab);
        this.addElement(this.optionsTab);

        this.addElement(changeHeight);
        this.addElement(tabList);

        openTab(currentTab);

        this.open(() -> {
        });
    }

    private void openTab(int i) {
        tabsBtn.forEach(tab -> tab.setSelected(false));
        tabsBtn.get(i).setSelected(true);
        currentTab = i;

        if (actionsTab != null) actionsTab.setVisible(i == 0);
        if (iconTab != null) iconTab.setVisible(i == 1);
        if (variablesTab != null) variablesTab.setVisible(i == 2);
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

    public VariablesTab getVariablesTab() {
        return variablesTab;
    }

    @Override
    protected void updateLayout() {
        int currentHeight = getHeight();

        if (actionsTab.getHeight() == currentHeight) return;

        actionsTab.updateHeight(currentHeight);
        iconTab.setHeight(currentHeight);
        variablesTab.updateHeight(currentHeight);
        optionsTab.updateHeight(currentHeight);
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth()
                && mouseY >= this.getYOffset() - 16 && mouseY <= this.getYOffset() + this.getHeight();
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isVisible()) return false;

        double adjX = mouseX - getX();
        double adjY = mouseY - getYOffset();

        if (NitshaInput.mouseScrolled(List.of(tabList), adjX, adjY, horizontalAmount, verticalAmount)) {
            return true;
        }

        return super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
