package com.nitsha.binds.gui.widget.list;

import com.nitsha.binds.Main;
import com.nitsha.binds.action.ActionRegistry;
import com.nitsha.binds.action.ActionType;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.multiversions.TextUtils;
import com.nitsha.binds.gui.widget.button.TexturedButton;
import com.nitsha.binds.gui.panels.advanced.ActionsTab;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.NitshaInput;
import com.nitsha.binds.multiversions.RenderUtils;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import com.nitsha.binds.configs.dto.preset.ActionData;

import java.util.List;

public class ActionItem extends NitshaAbstractWidget {

    private final TexturedButton topBtn;
    private final TexturedButton bottomBtn;
    private final TexturedButton resetBtn;
    private final TexturedButton deleteBtn;

    private static final Identifier TOP            = Main.idSprite("action_top_normal");
    private static final Identifier TOP_HOVER      = Main.idSprite("action_top_hover");
    private static final Identifier BOTTOM         = Main.idSprite("action_bottom_normal");
    private static final Identifier BOTTOM_HOVER   = Main.idSprite("action_bottom_hover");
    private static final Identifier RESET          = Main.idSprite("action_reset_normal");
    private static final Identifier RESET_HOVER    = Main.idSprite("action_reset_hover");
    private static final Identifier DELETE         = Main.idSprite("action_delete_normal");
    private static final Identifier DELETE_HOVER   = Main.idSprite("action_delete_hover");
    private static final Identifier ARROW_DISABLED = Main.id("textures/gui/test/arrow_disabled.png");

    private final int index;
    private final int x, y;

    private final ActionType<ActionData> action;

    @SuppressWarnings("unchecked")
    public ActionItem(ActionsTab parent, String typeId, int x, int y, int width, int height, ActionData value, int index) {
        super(x, y, width, height, TextUtils.empty());
        this.index = index;
        this.x = x;
        this.y = y;

        this.action = (ActionType<ActionData>) ActionRegistry.createById(typeId);
        if (value == null) {
            value = this.action.createDefaultData();
        }
        this.action.init(x, y, width, value);

        this.topBtn = new TexturedButton(x + width - 9, y + 3, 9, 9, TOP, TOP_HOVER,
                button -> { if (index > 0) parent.moveAction(index, -1); });

        this.bottomBtn = new TexturedButton(x + width - 9, y + 13, 9, 9, BOTTOM, BOTTOM_HOVER,
                button -> { if (BindsEditor.getCBind().actions.size() != index + 1) parent.moveAction(index, 1); });

        this.resetBtn = new TexturedButton(x + width - 25, y + 3, 15, 9, RESET, RESET_HOVER,
                button -> action.reset());

        this.deleteBtn = new TexturedButton(x + width - 25, y + 13, 15, 9, DELETE, DELETE_HOVER,
                button -> parent.removeAction(index));
    }

    public int getX() { return this.x; }
    public int getY() { return super.getY(); }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.topBtn.setY(y + 3);
        this.bottomBtn.setY(y + 13);
        this.resetBtn.setY(y + 3);
        this.deleteBtn.setY(y + 13);
        action.setPosition(getX(), y);
    }

    public void updateLayout() {
        this.height = action.getHeight();
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public ActionData getValue() {
        return action.getValue();
    }
    
    public ActionType<?> getActionType() {
        return action;
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        if (index % 2 == 1)
            RenderUtils.drawFill(ctx, getX() - 4, getY(), getX() + getWidth() + 4, getY() + getHeight(), 0x4DFFFFFF);

        this.resetBtn.onRender(ctx, mouseX, mouseY, delta);
        this.deleteBtn.onRender(ctx, mouseX, mouseY, delta);
        RenderUtils.drawTexture(ctx, ARROW_DISABLED, getX() + getWidth() - 9, getY() + 3, 0, 0, 9, 19, 9, 19);

        int aS = BindsEditor.getCBind().actions.size();
        if (aS > 0) {
            if (index > 0)
                this.topBtn.onRender(ctx, mouseX, mouseY, delta);
            if (index != (aS - 1))
                this.bottomBtn.onRender(ctx, mouseX, mouseY, delta);
        }

        action.render(ctx, mouseX, mouseY, delta);

        RenderUtils.drawFill(ctx, getX() - 3, getY() + 8, getX() - 1, getY() + getHeight() - 8, action.getLineColor());
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {

        boolean clicked = false;

        for (GuiEventListener element : List.of(action, deleteBtn, bottomBtn, resetBtn, topBtn)) {
            if (NitshaInput.mouseClicked(List.of(element), mouseX, mouseY, button)) {
                clicked = true;
            }
        }

        return clicked;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        return NitshaInput.mouseReleased(List.of(action, deleteBtn, bottomBtn, resetBtn, topBtn), mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return NitshaInput.mouseDragged(List.of(action, deleteBtn, bottomBtn, resetBtn, topBtn), mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean onKeyPressed(int keyCode, int nativeCode, int modifiers) {
        return NitshaInput.keyPressed(List.of(action), keyCode, nativeCode, modifiers) || super.onKeyPressed(keyCode, nativeCode, modifiers);
    }

    @Override
    public boolean onCharTyped(int codePoint) {
        return NitshaInput.charTyped(List.of(action), codePoint) || super.onCharTyped(codePoint);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (action.mouseScrolled(mouseX, mouseY, verticalAmount)) {
            return true;
        }

        return super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
