package com.nitsha.binds.gui.panels.advanced;

import com.nitsha.binds.Main;
import com.nitsha.binds.action.ActionRegistry;
import com.nitsha.binds.configs.dto.preset.ActionData;
import com.nitsha.binds.gui.panels.NewAction;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.widget.TextField;
import com.nitsha.binds.gui.widget.list.ActionItem;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.gui.widget.button.BedrockIconButton;
import com.nitsha.binds.gui.widget.button.BedrockIconOptionButton;
import com.nitsha.binds.gui.widget.button.MainKeybindSelector;
import com.nitsha.binds.multiversions.NitshaContainer;
import com.nitsha.binds.multiversions.NitshaInput;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActionsTab extends NitshaContainer {

    private ScrollableWindow list;
    public MainKeybindSelector keybind;
    private BedrockIconButton resetKeybind;
    private BedrockIconOptionButton triggerModeBtn;
    private TextField holdMsField;
    private NewAction addNewAction;

    private final BindsEditor screen;

    private int actionIndex = 0;
    private int actionY = 0;

    public ActionsTab(BindsEditor screen, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.screen = screen;

        this.list = new ScrollableWindow(5, 25, getWidth() - 10, getHeight() - 54, false);

        this.keybind = new MainKeybindSelector(4, this.getHeight() - 26, 80, 20);
        this.resetKeybind = new BedrockIconButton(84, this.getHeight() - 26, 13, 20, Textures.ICON_RESET, true, ()-> {
            this.keybind.setKeyCode(0);
            this.keybind.setPressed(false);
        }, 0xFFFFFFFF, 0xFFEF4747, 0xFF262626, 0xFFFFFFFF);
        this.resetKeybind.setButtonDirection(2);

        this.keybind.setNeighbor(this.resetKeybind);
        this.resetKeybind.setNeighbor(this.keybind);

        this.triggerModeBtn = new BedrockIconOptionButton(getWidth() - 22, getHeight() - 26, 18, 20, this::rebuildTriggerWidgets)
                .addOption("press", "nitsha.binds.advances.actions.option.press", Main.id("textures/gui/sprites/key_press.png"), 0xFF07938d, 0xFF0fb2ab, 0xFFFFFFFF, 0xFFFFFFFF)
                .addOption("hold", "nitsha.binds.advances.actions.option.hold", Main.id("textures/gui/sprites/key_hold.png"), 0xFF9cc708, 0xFFafda19, 0xFFFFFFFF, 0xFFFFFFFF);

        this.holdMsField = new TextField(
                Minecraft.getInstance().font, getWidth() - 60, getHeight() - 26, 36, 20,
                6, "500", TextUtils.translatable("nitsha.binds.advances.actions.delayLine").getString(), true
        );
        this.holdMsField.setAnimatedPlaceholder(false);

        this.addNewAction = new NewAction(this, 4, actionY + 4, getWidth() - 8, 17);

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            RenderUtils.drawResizableBox(ctx, Textures.INNER_BG, 4, 24, this.getWidth() - 8, this.getHeight() - 52, 1, 3);

            if (BindsEditor.getCBind().actions == null || BindsEditor.getCBind().actions.isEmpty()) {
                RenderUtils.drawText(ctx, TextUtils.small(TextUtils.translatable("nitsha.binds.advances.actions.noActions")),
                        this.getWidth() - 8, 4, (this.getHeight() - 52) / 2 + 23, "center", "center", 0xFFAEAEAE, false);
            }
        });

        rebuildTriggerWidgets();
        this.addElement(list);
        generateActionList(BindsEditor.getCBind().actions);
        this.addElement(holdMsField);
        this.addElement(resetKeybind);
        this.addElement(keybind);
        this.addElement(triggerModeBtn);
        this.addElement(addNewAction);
    }

    public void updateHeight(int newH) {
        this.setHeight(newH);
        this.list.setHeight(newH - 54);
        this.keybind.setY(newH - 26);
        this.resetKeybind.setY(newH - 26);
        this.holdMsField.setY(newH - 26);
        this.triggerModeBtn.setY(newH - 26);
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

    public String getTriggerMode() {
        return triggerModeBtn != null ? triggerModeBtn.getSelected() : "press";
    }

    public int getHoldMs() {
        if (holdMsField == null) return 500;
        try { return Integer.parseInt(holdMsField.getText()); } catch (NumberFormatException e) { return 500; }
    }

    public void generateActionList(List<ActionData> actions) {
        actionIndex = 0;
        actionY = 0;
        this.list.clearChildren();

        if (actions != null) {
            for (ActionData actionData : actions) {
                if (actionData == null) continue;
                String typeId = actionData.type;
                if (typeId == null) continue;

                int h = ActionRegistry.heightById(typeId);

                ActionItem item = new ActionItem(this, typeId, 4, actionY, getWidth() - 18, h, actionData, actionIndex);

                this.list.addElement(item);
                this.list.addScrollableArea(h);
                actionIndex++;
                actionY += h;
            }
        }
        this.list.setScrollableArea(actionY);
        relayoutActions();
    }

    public void relayoutActions() {
        actionY = 0;
        for (GuiEventListener child : this.list.children()) {
            if (child instanceof ActionItem) {
                ActionItem item = (ActionItem) child;
                item.setY(actionY);
                item.updateLayout();
                item.getActionType().setHeightChangeListener(this::relayoutActions);
                actionY += item.getHeight();
            }
        }
        this.list.setScrollableArea(actionY);
    }

    public void addAction(String typeId, String value) {
        screen.saveBind();
        ActionData actionData = ActionRegistry.createById(typeId).createDefaultData();
        List<ActionData> currentActions = new ArrayList<>(getAllActions());
        currentActions.add(actionData);
        generateActionList(currentActions);
        screen.saveBind();
        this.list.scrollToBottom();
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
        for (GuiEventListener child : this.list.children()) {
            if (child instanceof ActionItem) {
                ActionItem actionItem = (ActionItem) child;
                actionsList.add(actionItem.getValue());
            }
        }
        return actionsList;
    }

    public NewAction getAddNewAction() {
        return this.addNewAction;
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!isVisible()) return false;

        double adjX = mouseX - getContentX();
        double adjY = mouseY - getContentY();

        if (addNewAction != null && addNewAction.isOpen()) {
            if (!addNewAction.isMouseInside(adjX, adjY)) {
                addNewAction.openSelector(false);
            } else {
                NitshaInput.mouseClicked(List.of(addNewAction), adjX, adjY, button);
                return true;
            }
        }

        return super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isVisible()) return false;

        double adjX = mouseX - getContentX();
        double adjY = mouseY - getContentY();

        if (addNewAction != null && addNewAction.isOpen()) {
            if (!addNewAction.isMouseInside(adjX, adjY)) {
                addNewAction.openSelector(false);
            } else {
                NitshaInput.mouseScrolled(List.of(addNewAction), adjX, adjY, horizontalAmount, verticalAmount);
                return true;
            }
        }

        return super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    protected void renderChild(GuiGraphicsExtractor ctx, GuiEventListener child, int contentX, int contentY, int mouseX, int mouseY, float delta) {
        boolean blockHover = child != addNewAction && addNewAction.isOpen() && addNewAction.isMouseInside(mouseX, mouseY);
        int childMouseX = blockHover ? -10000 : mouseX;
        int childMouseY = blockHover ? -10000 : mouseY;

        if (child instanceof ScrollableWindow window) {
            RenderUtils.customScissor(ctx, contentX + window.getX(), contentY + window.getY(), window.getWidth(), window.getHeight(), () -> {
                super.renderChild(ctx, child, contentX, contentY, childMouseX, childMouseY, delta);
            });

            return;
        }

        super.renderChild(ctx, child, contentX, contentY, childMouseX, childMouseY, delta);
    }
}
