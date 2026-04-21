package com.nitsha.binds.gui.panels;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.Main;
import com.nitsha.binds.action.ActionRegistry;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.dto.option.HiddenField;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.gui.utils.TextUtils;
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
    private static final ResourceLocation TAB2_BG = Main.id("textures/gui/test/scroller.png");
    private static final ResourceLocation RANDOM_SMALL = Main.id("textures/gui/test/random.png");
    private final List<TabButton> tabsBtn = new ArrayList<>();

    private int currentTab = 0;

    // first tab
    private IconSelector secondTab;

    // second tab
    private ScrollableWindow firstTab;
    public MainKeybindSelector keybind;
    private BedrockIconButton resetKeybind;

    // third tab
    private ScrollableWindow thirdTab;

    private final BindsEditor screen;

    private final NewAction addNewAction;

    private BedrockIconOptionButton triggerModeBtn;
    private TextField holdMsField;

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
        this.secondTab = new IconSelector(4, 20, 162, 144, (stack, key) -> {
            screen.getBasicOptionsWindow().getEditIcon().setIcon(stack);
            this.secondTab.updateButtons(key);
            BindsEditor.editIconBtnString = key;
            if (!BindsEditor.getCBind().actions.isEmpty())  {
                screen.saveBind();
            }
        });
        this.thirdTab = new ScrollableWindow(2, 4, this.getX(), this.getY(), getWidth() - 4, getHeight() - 11, false);

        this.keybind = new MainKeybindSelector(4, this.getHeight() - 26, 80, 20);
        this.resetKeybind = new BedrockIconButton(84, this.getHeight() - 26, 13, 20, "reset_keybind", true, ()-> {
            this.keybind.setKeyCode(0);
            this.keybind.setPressed(false);
        }, 0xFFFFFFFF, 0xFFEF4747, 0xFF262626, 0xFFFFFFFF);
        this.resetKeybind.setButtonDirection("_right");

        this.triggerModeBtn = new BedrockIconOptionButton(getWidth() - 22, getHeight() - 26, 18, 20, this::rebuildTriggerWidgets)
            .addOption("press", "nitsha.binds.advances.actions.option.press", Main.id("textures/gui/sprites/key_press.png"), 0xFF07938d, 0xFF0fb2ab)
            .addOption("hold", "nitsha.binds.advances.actions.option.hold", Main.id("textures/gui/sprites/key_hold.png"), 0xFF9cc708, 0xFFafda19);

        this.holdMsField = new TextField(
                net.minecraft.client.Minecraft.getInstance().font,
                getWidth() - 60, getHeight() - 26, 36, 20,
                6, "500", TextUtils.translatable("nitsha.binds.advances.actions.delayLine").getString(), true
        );
        this.holdMsField.setAnimatedPlaceholder(false);

        this.addNewAction = new NewAction(this, 4, actionY + 4, getWidth() - 8, 17);

        openTab(0);

        this.open(() -> {});
    }

    private void openTab(int i) {
        tabsBtn.forEach(tab -> tab.setSelected(false));
        tabsBtn.get(i).setSelected(true);
        currentTab = i;

        this.clearChildren();

        switch (currentTab) {
            case 0: fillFirstTab();  break;
            case 1: fillSecondTab(); break;
            case 2: fillThirdTab();  break;
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
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.triggerMode"), 0, 5,
                    getHeight() - 44, "top", "left", 0xFF212121, false);
            GUIUtils.drawResizableBox(ctx, TAB2_BG, 4, 24, this.getWidth() - 8, this.getHeight() - 52, 1, 3);

            if (BindsEditor.getCBind().actions == null || BindsEditor.getCBind().actions.isEmpty()) {
                GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.noActions"),
                        this.getWidth() - 8, 4, (this.getHeight() - 52) / 2 + 23, "center", "center", 0xFFAEAEAE, false);
            }
        });

        rebuildTriggerWidgets();
        this.addElement(firstTab);
        generateActionList(BindsEditor.getCBind().actions);
        this.addElement(holdMsField);
        this.addElement(resetKeybind);
        this.addElement(keybind);
        this.addElement(triggerModeBtn);
    }

    private void rebuildTriggerWidgets() {
        boolean isHold = triggerModeBtn.getSelectedIndex() == 1;
        holdMsField.visible = isHold;
    }

    public void loadTriggerMode(String mode, int holdMs) {
        if (triggerModeBtn != null) triggerModeBtn.setSelected(mode != null ? mode : "press");
        if (holdMsField != null) holdMsField.setText(String.valueOf(holdMs > 0 ? holdMs : 500));
        rebuildTriggerWidgets();
    }

    private void fillSecondTab() {
        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.choose_icon"), 0, 5, 10, "left", "center",
                    0xFF212121, false);
        });

        this.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.advances.random_icon"), getWidth() - 4, 6, 7, 0x33000000, 0xFF232425, 0xFF232425, 0xFFe7bc1c, 0, "right", RANDOM_SMALL, ()-> {
            //? if >=26.1 {
            // Map.Entry<String, ItemStackTemplate> randomItem = ItemsMapper.getRandomItem();
            //? } else {
            Map.Entry<String, ItemStack> randomItem = ItemsMapper.getRandomItem();
            //? }

            if (randomItem == null) return;

            screen.getBasicOptionsWindow().getEditIcon().setIcon(randomItem.getValue());
            this.secondTab.updateButtons(randomItem.getKey());
            BindsEditor.editIconBtnString = randomItem.getKey();

            if (!BindsEditor.getCBind().actions.isEmpty()) {
                screen.saveBind();
            }
        }));

        this.addElement(this.secondTab);
    }

    private void fillThirdTab() {
        thirdTab.clearChildren();
        int yOffset = 0;

        Field[] fields = Storage.options.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(HiddenField.class)) {
                continue;
            }
            if (field.getType() == boolean.class) {
                try {
                    field.setAccessible(true);

                    String langKey = "nitsha.binds.advances.options." + field.getName();

                    boolean currentValue = field.getBoolean(Storage.options);

                    Component text;
                    if (field.getName().equals("holdToOpen")) {
                        text = TextUtils.translatable("nitsha.binds.advances.options.holdToOpen",
                                GUIUtils.truncateString(TextUtils.translatable(KeyBinds.BINDS.getTranslatedKeyMessage().getString()).getString(), 6));
                    } else {
                        text = TextUtils.translatable(langKey);
                    }

                    this.thirdTab.addElement(new ToggleButton(
                            text, 0, yOffset, getWidth() - 4, 20, false, currentValue,
                            () -> {
                                try {
                                    boolean val = field.getBoolean(Storage.options);
                                    field.setBoolean(Storage.options, !val);

                                    Storage.saveModOptions();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                    ));
                    yOffset += 20;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        this.thirdTab.setScrollableArea(yOffset + 20);
        addElement(this.thirdTab);
    }

    public IconSelector getSecondTab() {
        return this.secondTab;
    }

    public String getTriggerMode() {
        return triggerModeBtn != null ? triggerModeBtn.getSelected() : "press";
    }

    public int getHoldMs() {
        if (holdMsField == null) return 500;
        try { return Integer.parseInt(holdMsField.getText()); } catch (NumberFormatException e) { return 500; }
    }

    private int actionIndex = 0;
    private int actionY = 0;

    public void generateActionList(List<ActionData> actions) {
        actionIndex = 0;
        actionY = 0;
        this.firstTab.clearChildren();

        if (actions != null) {
            for (ActionData actionData : actions) {
                if (actionData == null) continue;
                String typeId = actionData.type;
                if (typeId == null) continue;

                int h = ActionRegistry.heightById(typeId);

                ActionItem item = new ActionItem(this, typeId, 4, actionY, getWidth() - 18, h, actionData, actionIndex);

                this.firstTab.addElement(item);
                this.firstTab.addScrollableArea(h);
                actionIndex++;
                actionY += h;
            }
        }
        this.firstTab.setScrollableArea(actionY);
        relayoutActions();
    }

    public void relayoutActions() {
        actionY = 0;
        for (GuiEventListener child : this.firstTab.children()) {
            if (child instanceof ActionItem) {
                ActionItem item = (ActionItem) child;
                item.setY(actionY);
                item.updateLayout();
                // Pass layout listener so typing will trigger recalculation without losing focus
                item.getActionType().setHeightChangeListener(this::relayoutActions);
                actionY += item.getHeight();
            }
        }
        this.firstTab.setScrollableArea(actionY);
    }

    public void addAction(String typeId, String value) {
        screen.saveBind();
        ActionData actionData = ActionRegistry.createById(typeId).createDefaultData();
        List<ActionData> currentActions = new ArrayList<>(getAllActions());
        currentActions.add(actionData);
        generateActionList(currentActions);
        screen.saveBind();
        this.firstTab.scrollToBottom();
    }

    public void removeAction(int index) {
        screen.saveBind();
        List<ActionData> currentActions = new ArrayList<>(getAllActions());
        if (index >= 0 && index < currentActions.size()) {
            currentActions.remove(index);
        }
        generateActionList(currentActions);
        screen.saveBind();
    }

    public void moveAction(int index, int dir) {
        screen.saveBind();
        List<ActionData> currentActions = new ArrayList<>(getAllActions());
        int target = index + dir;
        if (target >= 0 && target < currentActions.size() && index >= 0 && index < currentActions.size()) {
            ActionData temp = currentActions.get(target);
            currentActions.set(target, currentActions.get(index));
            currentActions.set(index, temp);
        }
        generateActionList(currentActions);
        screen.saveBind();
    }

    public List<ActionData> getAllActions() {
        List<ActionData> actionsList = new ArrayList<>();
        for (GuiEventListener child : this.firstTab.children()) {
            if (child instanceof ActionItem) {
                ActionItem actionItem = (ActionItem) child;
                actionsList.add(actionItem.getValue());
            }
        }
        return actionsList;
    }

    @Override
    public void renderWindow(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        int adjX = mouseX - this.getX();
        int adjY = mouseY - this.getYOffset();

        GUIUtils.matricesUtil(ctx, this.getX(), this.getYOffset(), 0, () -> {
            if (getDelay() == 0 && isVisible()) {
                for (TabButton btn : tabsBtn) {
                    btn.renderWidget(ctx, adjX, adjY, delta);
                }
            }
        });

        GUIUtils.matricesUtil(ctx, 0, 0, 2, () -> {
            if (addNewAction.isMouseInside(mouseX, mouseY) && addNewAction.isOpen()) {
                super.renderWindow(ctx, -10000, -10000, delta);
            } else {
                super.renderWindow(ctx, mouseX, mouseY, delta);
            }
            GUIUtils.matricesUtil(ctx, getX(), getYOffset(), 2, () -> {
                if (isVisible() && currentTab == 0)
                    //? if >=26.1 {
                    // this.addNewAction.extractRenderState(ctx, mouseX - getX(), mouseY - getYOffset(), delta);
                    //? } else {
                    this.addNewAction.render(ctx, mouseX - getX(), mouseY - getYOffset(), delta);
                    //? }
            });
        });
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                && mouseY >= this.getY() - 16 && mouseY < this.getY() + this.getHeight();
    }

    @Override
            //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (!isVisible()) return false;
        double mouseX = event.x();
        double mouseY = event.y();
        double adjX = mouseX - this.getX();
        double adjY = mouseY - this.getYOffset();

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(adjX, adjY, event.buttonInfo());

        boolean clicked = false;
        boolean wasOpen = addNewAction.isOpen();
        boolean insidePanel = addNewAction.isMouseInside(adjX, adjY);

        if (currentTab == 0) {
            if (insidePanel) {
                if (addNewAction.mouseClicked(adjustedEvent, bl)) clicked = true;
                if (wasOpen) return true;
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
            if (btn.mouseClicked(adjustedEvent, bl)) clicked = true;
        }

        if (!wasOpen || !insidePanel) {
            if (super.mouseClicked(event, bl)) clicked = true;
        }

        return clicked;
    }*/
            //? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isVisible()) return false;
        double adjX = mouseX - this.getX();
        double adjY = mouseY - this.getYOffset();

        boolean clicked = false;
        boolean wasOpen = addNewAction.isOpen();
        boolean insidePanel = addNewAction.isMouseInside(adjX, adjY);

        if (currentTab == 0) {
            if (insidePanel) {
                if (addNewAction.mouseClicked(adjX, adjY, button)) clicked = true;
                if (wasOpen) return true;
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
            if (btn.mouseClicked(adjX, adjY, button)) clicked = true;
        }

        if (!wasOpen || !insidePanel) {
            if (super.mouseClicked(mouseX, mouseY, button)) clicked = true;
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

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(adjustedX, adjustedY, event.buttonInfo());

        if (addNewAction.mouseReleased(adjustedEvent)) released = true;
        if (super.mouseReleased(event)) released = true;

        return released;
    }*/
            //? } else {
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean released = false;
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getYOffset();

        if (addNewAction.mouseReleased(adjustedX, adjustedY, button)) released = true;
        if (super.mouseReleased(mouseX, mouseY, button)) released = true;

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

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(adjustedX, adjustedY, event.buttonInfo());

        if (addNewAction.mouseDragged(adjustedEvent, deltaX, deltaY)) dragged = true;
        if (super.mouseDragged(event, deltaX, deltaY)) dragged = true;

        return dragged;
    }*/
            //? } else {
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean dragged = false;
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getYOffset();

        if (addNewAction.mouseDragged(adjustedX, adjustedY, button, deltaX, deltaY)) dragged = true;
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) dragged = true;

        return dragged;
    }
    //? }

    private boolean scrollLogic(double mouseX, double mouseY, double amount) {
        if (!isVisible()) return false;
        double adjX = mouseX - this.getX();
        double adjY = mouseY - this.getYOffset();

        boolean result = false;

        if (currentTab == 0) {
            if (addNewAction.isMouseInside(adjX, adjY)) {
                //? if >=1.20.2 {
                if (addNewAction.mouseScrolled(adjX, adjY, 0, amount))
                //? } else {
                /*if (addNewAction.mouseScrolled(adjX, adjY, amount))*/
                //? }
                    result = true;
                if (addNewAction.isOpen()) return true;
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
        //? }
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