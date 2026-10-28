package com.nitsha.binds.gui.panels;

import com.nitsha.binds.Main;
import com.nitsha.binds.action.ActionRegistry;
import com.nitsha.binds.action.ActionType;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.list.NewActionItem;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import com.nitsha.binds.guinew.panel.advanced.ActionsTab;
import com.nitsha.binds.guinew.widget.button.SmallTextButton;
import com.nitsha.binds.multiversions.NitshaContainer;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import com.nitsha.binds.utils.Renderable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

import java.util.List;

public class NewAction extends NitshaContainer {
    private static final Identifier TOP = Main.idSprite("top_normal");
    private static final Identifier TOP_HOVER = Main.idSprite("top_hover");
    private static final Identifier BOTTOM = Main.idSprite("bottom_normal");
    private static final Identifier BOTTOM_HOVER = Main.idSprite("bottom_hover");
    private static final Identifier SPRESET = Main.idSprite("select_preset");
    private static final Identifier SPRESET_HOVER = Main.idSprite("select_preset_hover");
    private static final Identifier BACKGROUND = Main.id("textures/gui/btns/menu.png");
    private static final Identifier ADD_NEW = Main.id("textures/gui/test/add_new_icon.png");
    private static final int HEADER_HEIGHT = 17;
    private static final int LIST_HEIGHT = 98;

    private final ActionsTab parent;
    private final List<ActionType<?>> entries;
    private final float speed = Main.GLOBAL_ANIMATION_SPEED - 0.1F;

    private ScrollableWindow actionList;
    private boolean isOpen;
    private float targetWidth;
    private float targetHeight;
    private long lastUpdateTime;
    private int currentAction;

    public NewAction(ActionsTab parent, int x, int y, float width, int height) {
        super(x, y, width, height);
        this.parent = parent;
        this.targetWidth = width;
        this.targetHeight = height;
        this.entries = ActionRegistry.allInstances();
        setVisible(true);
        initUI();
    }

    private ActionType<?> currentEntry() {
        return entries.get(currentAction);
    }

    private void initUI() {
        addDrawElement((ctx, mouseX, mouseY) -> {
            if (entries.isEmpty()) return;

            RenderUtils.drawFill(ctx, 5, 5, 7, 12, currentEntry().getLineColor());
            RenderUtils.drawText(ctx, TextUtils.small(TextUtils.literal(GUIUtils.truncateString(currentEntry().getDisplayName(), 15))), 0, 9, 4, "left", "top", 0xFFFFFFFF, false);
        });

        addElement(GUIUtils.createTexturedBtn(getWidth() - 14, 4, 9, 9, new Identifier[]{SPRESET, SPRESET_HOVER}, button -> openSelector(!isOpen)));
        addElement(GUIUtils.createTexturedBtn(getWidth() - 25, 4, 9, 9, new Identifier[]{BOTTOM, BOTTOM_HOVER}, button -> selectActiveAction(1)));
        addElement(GUIUtils.createTexturedBtn(getWidth() - 36, 4, 9, 9, new Identifier[]{TOP, TOP_HOVER}, button -> selectActiveAction(-1)));

        Runnable addCurrentAction = () -> {
            if (!entries.isEmpty()) {
                addAction(currentEntry());
            }
        };
        addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.advances.actions.addNew"), getWidth() - 38, 4, 0xFF4D9109, "right", ADD_NEW, addCurrentAction));

        actionList = new ScrollableWindow(2, HEADER_HEIGHT - 2, getX(), getY(), getWidth() - 4, LIST_HEIGHT, false);
        actionList.setVisible(false);
        addElement(actionList);

        int itemY = 0;

        for (ActionType<?> entry : entries) {
            NewActionItem item = new NewActionItem(this, entry.getDisplayName(), 0, itemY, getWidth() - 4, 16, entry.getId(), entry.getDefaultValue(), entry.getLineColor());
            actionList.addElement(item);
            itemY += 16;
        }

        actionList.setScrollableArea(itemY);
    }

    private void selectActiveAction(int direction) {
        if (entries.isEmpty()) return;

        currentAction = Math.floorMod(currentAction + direction, entries.size());
    }

    public void addAction(String typeId, String value) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getId().equals(typeId)) {
                currentAction = i;
                break;
            }
        }

        parent.addAction(typeId, value);
    }

    private void addAction(ActionType<?> entry) {
        parent.addAction(entry.getId(), entry.getDefaultValue());
    }

    @Override
    public void setWidth(float width) {
        targetWidth = width;
    }

    @Override
    public void setHeight(float height) {
        targetHeight = height;
    }

    public void openSelector(boolean open) {
        isOpen = open;
        actionList.setVisible(open);
        setHeight(open ? HEADER_HEIGHT + LIST_HEIGHT + 2 : HEADER_HEIGHT);
    }

    public boolean isOpen() {
        return isOpen;
    }

    private void animateValues() {
        long currentTime = System.currentTimeMillis();

        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
            return;
        }

        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        float lerpFactor = 1.0F - (float) Math.pow(1.0F - speed, deltaTime / 16.666F);
        width = Mth.lerp(lerpFactor, width, targetWidth);
        height = Mth.lerp(lerpFactor, height, targetHeight);

        if (Math.abs(width - targetWidth) < 0.1F) {
            width = targetWidth;
        }

        if (Math.abs(height - targetHeight) < 0.1F) {
            height = targetHeight;
        }
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        if (!isVisible()) {
            lastUpdateTime = 0;
            return;
        }

        animateValues();
        super.onRender(ctx, mouseX, mouseY, delta);
    }

    @Override
    protected void renderBackground(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.drawResizableBox(ctx, BACKGROUND, 0, 0, getWidth(), getHeight(), 3, 7);
    }

    @Override
    protected void renderChild(GuiGraphicsExtractor ctx, GuiEventListener child, Renderable renderable, int contentX, int contentY, int mouseX, int mouseY, float delta) {
        if (child == actionList) {
            int viewportHeight = Math.min(actionList.getHeight(), getHeight() - actionList.getY() - 2);

            if (!isOpen || viewportHeight <= 0) return;

            GUIUtils.customScissor(ctx, contentX + actionList.getX(), contentY + actionList.getY(), actionList.getWidth(), viewportHeight, () -> {
                super.renderChild(ctx, child, renderable, contentX, contentY, mouseX, mouseY, delta);
            });

            return;
        }

        super.renderChild(ctx, child, renderable, contentX, contentY, mouseX, mouseY, delta);
    }

    public boolean isMouseInsideArea(double mouseX, double mouseY) {
        return isVisible()
                && mouseX >= getContentX()
                && mouseX < getContentX() + getWidth()
                && mouseY >= getContentY()
                && mouseY < getContentY() + HEADER_HEIGHT;
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseInside(mouseX, mouseY)) return false;

        return super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isVisible() || verticalAmount == 0.0) return false;

        if (isMouseInsideArea(mouseX, mouseY)) {
            if (entries.isEmpty()) return false;

            selectActiveAction(verticalAmount > 0 ? -1 : 1);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }

        return super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
