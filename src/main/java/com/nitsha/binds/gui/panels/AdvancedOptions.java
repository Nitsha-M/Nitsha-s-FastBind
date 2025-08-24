package com.nitsha.binds.gui.panels;

import com.nitsha.binds.MainClass;
import com.nitsha.binds.configs.BindsConfig;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.utils.FastbindParser;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.gui.utils.TextUtils;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
import net.minecraft.client.gui.Element;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class AdvancedOptions extends AnimatedWindow {
    private static final Identifier RANDOM = MainClass.idSprite("random");
    private static final Identifier RANDOM_HOVER = MainClass.idSprite("random_hover");
    private static final Identifier RESET = MainClass.idSprite("keybind_reset_normal");
    private static final Identifier RESET_HOVER = MainClass.idSprite("keybind_reset_hover");
    private static final Identifier TAB2_BG = MainClass.id("textures/gui/test/scroller.png");
    private final List<TabButton> tabsBtn = new ArrayList<>();

    private int currentTab = 0;

    // first tab
    private IconSelector firstTab;

    // second tab
    private ScrollableWindow secondTab;
    public KeybindSelector keybind;

    // third tab
    private ScrollableWindow thirdTab;

    private final BindsEditor screen;

    public AdvancedOptions(BindsEditor screen, int x, int y, int width, int height, Identifier t1, Identifier t2, int delay) {
        super(x, y + 16, width, height - 16, t1, t2, delay);
        tabsBtn.clear();
        this.screen = screen;

        TabButton tab1 = new TabButton(4, -16, true, TextUtils.translatable("nitsha.binds.advances.tab1"), b -> selectTab(0));
        TabButton tab2 = new TabButton(tab1.getX() + tab1.getWidth() + 2, -16, false, TextUtils.translatable("nitsha.binds.advances.tab2"), b -> selectTab(1));
        TabButton tab3 = new TabButton(tab2.getX() + tab2.getWidth() + 2, -16, false, TextUtils.translatable("nitsha.binds.advances.tab3"), b -> selectTab(2));

        tabsBtn.add(tab1);
        tabsBtn.add(tab2);
        tabsBtn.add(tab3);

        this.firstTab = new IconSelector(screen, 4, 20, 162, 126);
        this.secondTab = new ScrollableWindow(5, 21, this.getX(), this.getY(), getWidth() - 10, getHeight() - 50, false);
        this.keybind = new KeybindSelector(this.getWidth() - 84, this.getHeight() - 26, 80, 20);
        this.thirdTab = new ScrollableWindow(2, 4, this.getX(), this.getY(), getWidth() - 4, getHeight() - 11, false);
    }

    public void selectTab(int i) {
        tabsBtn.forEach(tab -> tab.setSelected(false));
        tabsBtn.get(i).setSelected(true);
        currentTab = i;

        this.clearChildren();

        switch (currentTab) {
            case 0 -> fillFirstTab();
            case 1 -> fillSecondTab();
            case 2 -> fillThirdTab();
        }
    }

    private void fillFirstTab() {
        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.choose_icon"), 0, 5, 6, "top", "left", 0xFF212121, false);
        });
        this.addElement(GUIUtils.createTexturedBtn(getWidth() - 53, 6, 49, 9, new Identifier[]{RANDOM, RANDOM_HOVER}, button -> {
            this.firstTab.pickRandom();
        }));
        this.addElement(this.firstTab);
    }

    private void fillSecondTab() {
        secondTab.clearChildren();
        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions"), 0, 5, 6, "top", "left", 0xFF212121, false);
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.keybind"), 0, 5, getHeight() - 20, "top", "left", 0xFF212121, false);
            GUIUtils.drawResizableBox(ctx, TAB2_BG, 4, 20, this.getWidth() - 8, this.getHeight() - 48, 1, 3);
        });
        this.addElement(GUIUtils.createTexturedBtn(this.getWidth() - 106, this.getHeight() - 26, 20, 20, new Identifier[]{RESET, RESET_HOVER}, button -> {
            this.keybind.setKeyCode(0);
            this.keybind.setPressed(false);
        }));
        this.addElement(keybind);
        this.addElement(secondTab);
        generateActionList(BindsEditor.getCBind().actions);
    }

    private void fillThirdTab() {
        thirdTab.clearChildren();
        final boolean[] options = { BindsConfig.getBooleanConfig("holdToOpen", true),
                BindsConfig.getBooleanConfig("openLastPage", true),
                BindsConfig.getBooleanConfig("openLastPreset", true),
                BindsConfig.getBooleanConfig("keepMovement", false),
                BindsConfig.getBooleanConfig("closeOnAction", false),
                BindsConfig.getBooleanConfig("bindMsg", true)};

        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.hold", GUIUtils.truncateString(TextUtils.translatable(KeyBinds.BINDS.getBoundKeyTranslationKey()).getString(), 6)),
                0, 0, getWidth() - 4, 20, false, options[0], () -> {
            BindsConfig.setConfig("holdToOpen", !options[0]);
            options[0] = BindsConfig.getBooleanConfig("holdToOpen", true);
        }));
        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.openLastPage"),
                0, 20, getWidth() - 4, 20, false, options[1], () -> {
            BindsConfig.setConfig("openLastPage", !options[1]);
            options[1] = BindsConfig.getBooleanConfig("openLastPage", true);
        }));
        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.openLastPreset"),
                0, 40, getWidth() - 4, 20, false, options[2], () -> {
            BindsConfig.setConfig("openLastPreset", !options[2]);
            options[2] = BindsConfig.getBooleanConfig("openLastPreset", true);
        }));
        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.keepMovement"),
                0, 60, getWidth() - 4, 20, false, options[3], () -> {
            BindsConfig.setConfig("keepMovement", !options[3]);
            options[3] = BindsConfig.getBooleanConfig("keepMovement", false);
        }));
        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.closeOnAction"),
                0, 80, getWidth() - 4, 20, false, options[4], () -> {
            BindsConfig.setConfig("closeOnAction", !options[4]);
            options[4] = BindsConfig.getBooleanConfig("closeOnAction", false);
        }));
        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.bindMsg"),
                0, 100, getWidth() - 4, 20, false, options[5], () -> {
            BindsConfig.setConfig("bindMsg", !options[5]);
            options[5] = BindsConfig.getBooleanConfig("bindMsg", true);
        }));
        this.thirdTab.setScrollableArea(120);
        addElement(this.thirdTab);
    }

    public IconSelector getFirstTab() {
        return this.firstTab;
    }

    private int actionIndex = 0;
    private int actionY = 0;

    public void generateActionList(List<String> actions) {
        actionIndex = 0;
        actionY = 0;
        this.secondTab.clearChildren();
        for (String action : actions) {
            int h = 26;
            FastbindParser.ParsedEntry parsedAction = FastbindParser.parse(action);
            this.secondTab.addElement(new ActionItem(this, parsedAction.type, 4, actionY, getWidth() - 18, parsedAction.value, actionIndex));
            this.secondTab.addScrollableArea(h);
            actionIndex++;
            actionY += h;
        }
        this.secondTab.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.advances.actions.command").getString(), 50, actionY + 2, 52, 0xFF4e8605, ()-> {
            addAction(1, actionIndex, "");
        }));
        this.secondTab.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.advances.actions.delay").getString(), 4, actionY + 2, 44, 0xFFc99212, ()-> {
            addAction(2, actionIndex, "100");
        }));
        this.secondTab.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.advances.actions.pressKey").getString(), 104, actionY + 2, 62, 0xFF790e06, ()-> {
            addAction(3, actionIndex, "0");
        }));
        this.secondTab.setScrollableArea(actionY + 13);
    }

    private void addAction(int type, int index, String value) {
        if (!BindsEditor.getCBind().actions.isEmpty()) screen.saveBind();
        BindsConfig.addBindAction(BindsEditor.getCurrentPreset(), BindsEditor.getActiveBind(), index, FastbindParser.toAction(type, value));
        generateActionList(BindsEditor.getCBind().actions);
        screen.saveBind();
        this.secondTab.scrollToBottom();
    }

    public void removeAction(int index) {
        if (!BindsEditor.getCBind().actions.isEmpty()) screen.saveBind();
        BindsConfig.removeBindAction(BindsEditor.getCurrentPreset(), BindsEditor.getActiveBind(), index);
        generateActionList(BindsEditor.getCBind().actions);
        screen.saveBind();
    }

    public void moveAction(int index, int dir) {
        if (!BindsEditor.getCBind().actions.isEmpty()) screen.saveBind();
        BindsConfig.moveBindAction(BindsEditor.getCurrentPreset(), BindsEditor.getActiveBind(), index, dir);
        generateActionList(BindsEditor.getCBind().actions);
        screen.saveBind();
    }

    public List<String> getAllActions() {
        List<String> actionsList = new ArrayList<>();
        for (Element child : this.secondTab.children()) {
            if (child instanceof ActionItem actionItem) {
                actionsList.add(actionItem.getValue());
            }
        }
        return actionsList;
    }

    @Override
    public void render(
            //? if >=1.20 {
            DrawContext ctx
            //? } else {
            /*MatrixStack ctx
            *///? }
            , int mouseX, int mouseY, float delta) {
        int adjX = mouseX - this.getX();
        int adjY = mouseY - this.getYOffset();

        GUIUtils.matricesUtil(ctx, this.getX(), this.getYOffset(), 0, () -> {
            if (getDelay() == 0 && isVisible()) {
                for (TabButton btn : tabsBtn) {
                    btn.render(ctx, adjX, adjY, delta);
                }
            }
        });

        GUIUtils.matricesUtil(ctx, 0, 0, 2, () -> {
            super.render(ctx, mouseX, mouseY, delta);
        });
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() && mouseY >= this.getY() - 16 && mouseY < this.getY() + this.getHeight();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isVisible()) return false;
        double adjX = mouseX - this.getX();
        double adjY = mouseY - this.getYOffset();

        for (TabButton btn : tabsBtn) {
            if (btn.mouseClicked(adjX, adjY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
