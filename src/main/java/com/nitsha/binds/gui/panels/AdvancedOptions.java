package com.nitsha.binds.gui.panels;

import com.nitsha.binds.Main;
import com.nitsha.binds.configs.BindsStorage;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.gui.utils.TextUtils;
import com.mojang.blaze3d.vertex.PoseStack;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;*/
//? }

public class AdvancedOptions extends AnimatedWindow {
    private static final ResourceLocation RANDOM = Main.idSprite("random");
    private static final ResourceLocation RANDOM_HOVER = Main.idSprite("random_hover");
    private static final ResourceLocation RESET = Main.idSprite("keybind_reset_normal");
    private static final ResourceLocation RESET_HOVER = Main.idSprite("keybind_reset_hover");
    private static final ResourceLocation TAB2_BG = Main.id("textures/gui/test/scroller.png");
    private final List<TabButton> tabsBtn = new ArrayList<>();

    private int currentTab = 0;

    // first tab
    private IconSelector secondTab;

    // second tab
    private ScrollableWindow firstTab;
    public KeybindSelector keybind;

    // third tab
    private ScrollableWindow thirdTab;

    private final BindsEditor screen;

    private final NewAction addNewAction;

    public AdvancedOptions(BindsEditor screen, int x, int y, int width, int height, ResourceLocation t1,
            ResourceLocation t2, int delay) {
        super(x, y + 16, width, height - 16, t1, t2, delay);
        tabsBtn.clear();
        this.screen = screen;

        TabButton tab1 = new TabButton(4, -16, true, TextUtils.translatable("nitsha.binds.advances.tab1"),
                b -> selectTab(0));
        TabButton tab2 = new TabButton(tab1.getX() + tab1.getWidth() + 2, -16, false,
                TextUtils.translatable("nitsha.binds.advances.tab2"), b -> selectTab(1));
        TabButton tab3 = new TabButton(tab2.getX() + tab2.getWidth() + 2, -16, false,
                TextUtils.translatable("nitsha.binds.advances.tab3"), b -> selectTab(2));

        tabsBtn.add(tab1);
        tabsBtn.add(tab2);
        tabsBtn.add(tab3);

        this.firstTab = new ScrollableWindow(5, 25, this.getX(), this.getY(), getWidth() - 10, getHeight() - 54, false);
        this.secondTab = new IconSelector(screen, 4, 20, 162, 126);
        this.keybind = new KeybindSelector(this.getWidth() - 84, this.getHeight() - 26, 80, 20);
        this.thirdTab = new ScrollableWindow(2, 4, this.getX(), this.getY(), getWidth() - 4, getHeight() - 11, false);

        this.addNewAction = new NewAction(this, 4, actionY + 4, getWidth() - 8, 17);

        openTab(0);

        this.open(() -> {
        });
    }

    private void openTab(int i) {
        tabsBtn.forEach(tab -> tab.setSelected(false));
        tabsBtn.get(i).setSelected(true);
        currentTab = i;

        this.clearChildren();

        switch (currentTab) {
            case 0:
                fillFirstTab();
                break;
            case 1:
                fillSecondTab();
                break;
            case 2:
                fillThirdTab();
                break;
        }
    }

    public void selectTab(int i) {
        if (!BindsEditor.getCBind().actions.isEmpty())
            screen.saveBind();
        openTab(i);
    }

    private void fillFirstTab() {
        firstTab.clearChildren();
        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.keybind"), 0, 5,
                    getHeight() - 20, "top", "left", 0xFF212121, false);
            GUIUtils.drawResizableBox(ctx, TAB2_BG, 4, 24, this.getWidth() - 8, this.getHeight() - 52, 1, 3);

            if (BindsEditor.getCBind().actions.isEmpty()) {
                GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.noActions"), this.getWidth() - 8, 4,
                        (this.getHeight() - 52) / 2 + 23, "center", "center", 0xFFAEAEAE, false);
            }
        });
        this.addElement(GUIUtils.createTexturedBtn(this.getWidth() - 106, this.getHeight() - 26, 20, 20,
                new ResourceLocation[] { RESET, RESET_HOVER }, button -> {
                    this.keybind.setKeyCode(0);
                    this.keybind.setPressed(false);
                }));
        this.addElement(keybind);
        this.addElement(firstTab);

        generateActionList(BindsEditor.getCBind().actions);
    }

    private void fillSecondTab() {
        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.choose_icon"), 0, 5, 6, "top", "left",
                    0xFF212121, false);
        });
        this.addElement(GUIUtils.createTexturedBtn(getWidth() - 53, 6, 49, 9,
                new ResourceLocation[] { RANDOM, RANDOM_HOVER }, button -> {
                    this.secondTab.pickRandom();
                }));
        this.addElement(this.secondTab);
    }

    private void fillThirdTab() {
        thirdTab.clearChildren();
        final boolean[] options = { BindsStorage.getBooleanConfig("holdToOpen", true),
                BindsStorage.getBooleanConfig("openLastPage", true),
                BindsStorage.getBooleanConfig("openLastPreset", true),
                BindsStorage.getBooleanConfig("keepMovement", false),
                BindsStorage.getBooleanConfig("closeOnAction", false),
                BindsStorage.getBooleanConfig("bindMsg", true) };

        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.hold",
                        GUIUtils.truncateString(TextUtils
                                .translatable(KeyBinds.BINDS.getTranslatedKeyMessage().getString()).getString(), 6)),
                0, 0, getWidth() - 4, 20, false, options[0], () -> {
                BindsStorage.setConfig("holdToOpen", !options[0]);
                    options[0] = BindsStorage.getBooleanConfig("holdToOpen", true);
                }));
        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.openLastPage"),
                0, 20, getWidth() - 4, 20, false, options[1], () -> {
                BindsStorage.setConfig("openLastPage", !options[1]);
                    options[1] = BindsStorage.getBooleanConfig("openLastPage", true);
                }));
        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.openLastPreset"),
                0, 40, getWidth() - 4, 20, false, options[2], () -> {
                BindsStorage.setConfig("openLastPreset", !options[2]);
                    options[2] = BindsStorage.getBooleanConfig("openLastPreset", true);
                }));
        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.keepMovement"),
                0, 60, getWidth() - 4, 20, false, options[3], () -> {
                BindsStorage.setConfig("keepMovement", !options[3]);
                    options[3] = BindsStorage.getBooleanConfig("keepMovement", false);
                }));
        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.closeOnAction"),
                0, 80, getWidth() - 4, 20, false, options[4], () -> {
                BindsStorage.setConfig("closeOnAction", !options[4]);
                    options[4] = BindsStorage.getBooleanConfig("closeOnAction", false);
                }));
        this.thirdTab.addElement(new ToggleButton(
                TextUtils.translatable("nitsha.binds.advances.options.bindMsg"),
                0, 100, getWidth() - 4, 20, false, options[5], () -> {
                BindsStorage.setConfig("bindMsg", !options[5]);
                    options[5] = BindsStorage.getBooleanConfig("bindMsg", true);
                }));
        this.thirdTab.setScrollableArea(120);
        addElement(this.thirdTab);
    }

    public IconSelector getSecondTab() {
        return this.secondTab;
    }

    private int actionIndex = 0;
    private int actionY = 0;

    public void generateActionList(List<Map<String, Object>> actions) {
        actionIndex = 0;
        actionY = 0;
        this.firstTab.clearChildren();
        for (Map<String, Object> action : actions) {
            String typeStr = (String) action.get("type");
            Object value = action.get("value");
            int type = getActionType(typeStr);
            int h = (type == 4) ? 36 : (type == 5) ? 57 : 26;

            ActionItem item = new ActionItem(this, type, 4, actionY, getWidth() - 18, h, value, actionIndex);

            this.firstTab.addElement(item);
            this.firstTab.addScrollableArea(h);
            actionIndex++;
            actionY += h;
        }
        this.firstTab.setScrollableArea(actionY);
    }

    private int getActionType(String typeStr) {
        switch (typeStr) {
            case "command":
                return 1;
            case "delay":
                return 2;
            case "keybind":
                return 3;
            case "chatMessage":
                return 4;
            case "titleMessage":
                return 5;
            default:
                return 1;
        }
    }

    private String getTypeString(int type) {
        switch (type) {
            case 1:
                return "command";
            case 2:
                return "delay";
            case 3:
                return "keybind";
            case 4:
                return "chatMessage";
            case 5:
                return "titleMessage";
            default:
                return "command";
        }
    }

    public void addAction(int type, String value) {
        if (!BindsEditor.getCBind().actions.isEmpty())
            screen.saveBind();
        Map<String, Object> action = new HashMap<>();
        action.put("type", getTypeString(type));
        action.put("value", type == 2 ? Integer.parseInt(value) : value);
        BindsStorage.addBindAction(BindsEditor.getCurrentPreset(), BindsEditor.getActiveBind(), actionIndex, action);
        generateActionList(BindsEditor.getCBind().actions);
        screen.saveBind();
        this.firstTab.scrollToBottom();
    }

    public void removeAction(int index) {
        if (!BindsEditor.getCBind().actions.isEmpty())
            screen.saveBind();
        BindsStorage.removeBindAction(BindsEditor.getCurrentPreset(), BindsEditor.getActiveBind(), index);
        generateActionList(BindsEditor.getCBind().actions);
        screen.saveBind();
    }

    public void moveAction(int index, int dir) {
        if (!BindsEditor.getCBind().actions.isEmpty())
            screen.saveBind();
        BindsStorage.moveBindAction(BindsEditor.getCurrentPreset(), BindsEditor.getActiveBind(), index, dir);
        generateActionList(BindsEditor.getCBind().actions);
        screen.saveBind();
    }

    public List<Map<String, Object>> getAllActions() {
        List<Map<String, Object>> actionsList = new ArrayList<>();
        for (GuiEventListener child : this.firstTab.children()) {
            if (child instanceof ActionItem) {
                ActionItem actionItem = (ActionItem) child;
                actionsList.add(actionItem.getValue());
            }
        }
        return actionsList;
    }

    @Override
    public void render(
            //? if >=1.20 {
            GuiGraphics ctx
            //? } else {
            /*PoseStack ctx*/
            //? }
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
            if (addNewAction.isMouseInside(mouseX, mouseY) && addNewAction.isOpen()) {
                super.render(ctx, -10000, -10000, delta);
            } else {
                super.render(ctx, mouseX, mouseY, delta);
            }
            GUIUtils.matricesUtil(ctx, getX(), getYOffset(), 2, () -> {
                if (isVisible() && currentTab == 0)
                    this.addNewAction.render(ctx, mouseX - getX(), mouseY - getYOffset(), delta);
            });
        });
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() && mouseY >= this.getY() - 16
                && mouseY < this.getY() + this.getHeight();
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (!isVisible())
            return false;
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.buttonInfo().button();
        double adjX = mouseX - this.getX();
        double adjY = mouseY - this.getYOffset();

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(
            adjX,
            adjY,
            event.buttonInfo()
        );

        boolean clicked = false;

        boolean wasOpen = addNewAction.isOpen();
        boolean insidePanel = addNewAction.isMouseInside(mouseX, mouseY);

        if (currentTab == 0) {
            if (insidePanel) {
                if (addNewAction.mouseClicked(adjustedEvent, bl))
                    clicked = true;
                if (wasOpen)
                    return true;
            } else {
                int scrollOffset = this.firstTab.getScrollOffset();
                int aX = this.firstTab.getX();
                int aY = this.firstTab.getY() - scrollOffset;

                for (GuiEventListener child : this.firstTab.children()) {
                    if (child instanceof ActionItem) {
                        ActionItem actionItem = (ActionItem) child;
                        double itemMouseX = adjX - aX;
                        double itemMouseY = adjY - aY;

                        if (actionItem.isMouseOverColorButtons(itemMouseX, itemMouseY)) {
                            TextField.setBlockFocus();
                        }
                    }
                }
                addNewAction.openSelector(false);
            }
        }

        for (TabButton btn : tabsBtn) {
            if (btn.mouseClicked(adjustedEvent, bl)) {
                clicked = true;
            }
        }

        if (!wasOpen || !insidePanel) {
            if (super.mouseClicked(event, bl)) {
                clicked = true;
            }
        }

        return clicked;
    }*/
    //? } else {
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isVisible())
                return false;
            double adjX = mouseX - this.getX();
            double adjY = mouseY - this.getYOffset();

            boolean clicked = false;

            boolean wasOpen = addNewAction.isOpen();
            boolean insidePanel = addNewAction.isMouseInside(mouseX, mouseY);

            if (currentTab == 0) {
                if (insidePanel) {
                    if (addNewAction.mouseClicked(adjX, adjY, button))
                        clicked = true;
                    if (wasOpen)
                        return true;
                } else {
                    int scrollOffset = this.firstTab.getScrollOffset();
                    int aX = this.firstTab.getX();
                    int aY = this.firstTab.getY() - scrollOffset;

                    for (GuiEventListener child : this.firstTab.children()) {
                        if (child instanceof ActionItem) {
                            ActionItem actionItem = (ActionItem) child;
                            double itemMouseX = adjX - aX;
                            double itemMouseY = adjY - aY;

                            if (actionItem.isMouseOverColorButtons(itemMouseX, itemMouseY)) {
                                TextField.setBlockFocus();
                            }
                        }
                    }
                    addNewAction.openSelector(false);
                }
            }

            for (TabButton btn : tabsBtn) {
                if (btn.mouseClicked(adjX, adjY, button)) {
                    clicked = true;
                }
            }

            if (!wasOpen || !insidePanel) {
                if (super.mouseClicked(mouseX, mouseY, button)) {
                    clicked = true;
                }
            }

            return clicked;
        }
    //? }

        @Override
    //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
        boolean released = false;
        double mouseX = event.x();
        double mouseY = event.y();
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getYOffset();

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(
            adjustedX,
            adjustedY,
            event.buttonInfo()
        );

        if (addNewAction.mouseReleased(adjustedEvent))
            released = true;

        if (super.mouseReleased(event))
            released = true;

        return released;
    }*/
    //? } else {
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            boolean released = false;
            double adjustedX = mouseX - getX();
            double adjustedY = mouseY - getYOffset();

            if (addNewAction.mouseReleased(adjustedX, adjustedY, button))
                released = true;

            if (super.mouseReleased(mouseX, mouseY, button))
                released = true;

            return released;
        }
    //? }

        @Override
    //? if >=1.21.9 {
    /*public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        boolean dragged = false;
        double mouseX = event.x();
        double mouseY = event.y();
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getYOffset();

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(
            adjustedX,
            adjustedY,
            event.buttonInfo()
        );

        if (addNewAction.mouseDragged(adjustedEvent, deltaX, deltaY))
            dragged = true;

        if (super.mouseDragged(event, deltaX, deltaY))
            dragged = true;

        return dragged;
    }*/
    //? } else {
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            boolean dragged = false;
            double adjustedX = mouseX - getX();
            double adjustedY = mouseY - getYOffset();

            if (addNewAction.mouseDragged(adjustedX, adjustedY, button, deltaX, deltaY))
                dragged = true;

            if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
                dragged = true;

            return dragged;
        }
    //? }

    private boolean scrollLogic(double mouseX, double mouseY, double amount) {
        if (!isVisible())
            return false;
        double adjX = mouseX - this.getX();
        double adjY = mouseY - this.getYOffset();

        boolean result = false;

        if (currentTab == 0) {
            if (addNewAction.isMouseInside(mouseX, mouseY)) {
                //? if >=1.20.2 {
                if (addNewAction.mouseScrolled(adjX, adjY, 0, amount))
                //? } else {
                /*if (addNewAction.mouseScrolled(adjX, adjY, amount))*/
                //? }
                    result = true;
                if (addNewAction.isOpen())
                    return true;
            } else {
                addNewAction.openSelector(false);
            }
            int scrollOffset = this.firstTab.getScrollOffset();
            int aX = this.firstTab.getX();
            int aY = this.firstTab.getY() - scrollOffset;

            for (GuiEventListener child : this.firstTab.children()) {
                if (child instanceof ActionItem) {
                    ActionItem actionItem = (ActionItem) child;
                    double itemMouseX = adjX - aX;
                    double itemMouseY = adjY - aY;

                    //? if >=1.20.2 {
                    if (actionItem.mouseScrolled(itemMouseX, itemMouseY, 0, amount)) {
                    //? } else {
                    /*if (actionItem.mouseScrolled(itemMouseX, itemMouseY, amount)) {*/
                    //? }
                        return true;
                    }
                }
            }
        }

        //? if >=1.20.2 {
        if (super.mouseScrolled(mouseX, mouseY, 0, amount)) {
        //? } else {
        /*if (super.mouseScrolled(mouseX, mouseY, amount)) {*/
        //?}
            result = true;
        }

        return result;
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return scrollLogic(mouseX, mouseY, verticalAmount);
    }
    //? } else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    return scrollLogic(mouseX, mouseY, amount);
    }*/
    //? }
}
