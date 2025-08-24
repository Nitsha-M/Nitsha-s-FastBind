package com.nitsha.binds.gui.widget;

import com.nitsha.binds.MainClass;
import com.nitsha.binds.configs.BindsConfig;
import com.nitsha.binds.gui.panels.PresetSelector;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import net.minecraft.client.MinecraftClient;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
import net.minecraft.client.gui.widget.PressableWidget;import net.minecraft.client.util.math.MatrixStack;
//? if >=1.17 {
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
//? }
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class PresetListItem extends ClickableWidget {
    private final PresetSelector parent;
    private final BindsEditor.TextField newNameField;

    private final PressableWidget editBtn;
    private final PressableWidget saveBtn;
    private static final Identifier EDIT = MainClass.idSprite("editpreset_normal");
    private static final Identifier EDIT_HOVER = MainClass.idSprite("editpreset_hover");
    private static final Identifier SAVE = MainClass.idSprite("savepreset_normal");
    private static final Identifier SAVE_HOVER = MainClass.idSprite("savepreset_hover");

    private boolean isEditing = false;
    private String name;
    private int index;

    private int x, y;

    public PresetListItem(PresetSelector parent, String name, int x, int y, int width, int height, int index) {
        super(x, y, width, height, Text.of(""));
        this.parent = parent;
        this.name = name;
        this.index = index;
        this.x = x;
        this.y = y;

        this.newNameField = new BindsEditor.TextField(MinecraftClient.getInstance().textRenderer, x + 3, y + 3, width - 20,height - 5, 40, name,"Preset name");
        this.newNameField.setEnterEvent(this::savePreset);
        this.newNameField.setEscapeEvent(this::stopEditing);
        this.editBtn = GUIUtils.createTexturedBtn(x + 111, y + 6, 9, 9, new Identifier[]{EDIT, EDIT_HOVER}, button -> {
            for (PresetListItem item : parent.getItems()) {
                if (item.isEditing()) {
                    item.stopEditing();
                }
            }
            isEditing = true;
            //? if >=1.19.4 {
            this.newNameField.setFocused(true);
            //? } else {
            /*this.newNameField.changeFocus(true);*/
            //? }
            BindsEditor.TextField.setFocusedField(this.newNameField);
        });

        this.saveBtn = GUIUtils.createTexturedBtn(x + 111, y + 6, 9, 9, new Identifier[]{SAVE, SAVE_HOVER}, button -> {
            savePreset();
        });
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void stopEditing() {
        isEditing = false;
        this.newNameField.setText(this.name);
    }

    public void onClick(double mouseX, double mouseY) {
        if (!isEditing) {
            parent.screen.selectPreset(index);
            parent.openSelector(false);
        }
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void savePreset() {
        isEditing = false;
        String getName = this.newNameField.getText();
        String newName = (getName.isEmpty()) ? "Preset " +  (index + 1) : getName;
        BindsConfig.setNewPresetName(this.index, newName);
        this.newNameField.setText(newName);
        this.name = newName;
    }

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        DrawContext c = (DrawContext) ctx;
        //? } else {
        /*MatrixStack c = (MatrixStack) ctx;
         *///? }
        GUIUtils.drawFill(ctx, getX() + 3, getY(), getX() + getWidth() - 6, getY() + 1, 0xFF555555);
        if (isEditing) {
            this.newNameField.render(c, mouseX, mouseY, delta);
            this.saveBtn.render(c, mouseX, mouseY, delta);
        } else {
            if (hovered && parent.isOpen()) GUIUtils.drawFill(c, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x0DFFFFFF);
            GUIUtils.addText(c, Text.of(GUIUtils.truncateString(this.name, 15)), 0, getX() + 3, getY() + 7, "left", "top",0xFFFFFFFF, false);
            this.editBtn.render(c, mouseX, mouseY, delta);
        }
    }

    //? if >1.20.2 {
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    //? } else if >=1.20 {
    /*@Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///? } else {
    /*@Override
    public void renderButton(MatrixStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///? }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!parent.isOpen()) return false;

        if (isEditing) {
            if (newNameField.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (saveBtn.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        } else {
            if (editBtn.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!parent.isOpen() || !isEditing) return false;
        return newNameField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!parent.isOpen() || !isEditing) return false;
        return newNameField.charTyped(codePoint, modifiers);
    }

    //? if >=1.19.3 {
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
    //? } else if >=1.17 {
    /*@Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }*/
    //? }
}
