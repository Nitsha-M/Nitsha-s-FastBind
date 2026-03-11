package com.nitsha.binds.action;

import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.TextField;
import net.minecraft.client.Minecraft;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;*/
//?}

import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

public class CommandAction extends ActionType {

    private TextField field;

    // --- Мета ---

    @Override
    public String getId() { return "command"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.command").getString();
    }

    @Override
    public String getDefaultValue() { return ""; }

    @Override
    public int getLineColor() { return 0xFF4e8605; }

    @Override
    public int getHeight() { return 25; }

    // --- Логика исполнения ---

    @Override
    public void buildTasks(Map<String, Object> data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        String cmd = String.valueOf(data.get("value"));
        if (cmd.isEmpty()) return;
        if (client.player == null || client.getConnection() == null) return;

        //? if >=1.19.3 {
        actions.add(() -> client.player.connection.sendCommand(cmd));
        //? } else if >=1.19.1 {
        /*actions.add(() -> client.player.commandUnsigned(cmd));*/
        //? } else if >=1.19 {
        /*actions.add(() -> client.player.command(cmd));*/
        //? } else {
        /*actions.add(() -> client.player.connection.send(new net.minecraft.network.protocol.game.ServerboundChatPacket("/" + cmd)));*/
        //? }
    }

    // --- UI ---

    @Override
    public void init(int x, int y, int width, Object value) {
        this.field = new TextField(
                Minecraft.getInstance().font,
                x, y + 3, width - 26, 19,
                Integer.MAX_VALUE,
                String.valueOf(value),
                TextUtils.translatable("nitsha.binds.advances.actions.commandLine").getString()
        );
    }

    @Override
    public void render(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        field.render((GuiGraphics) ctx, mouseX, mouseY, delta);
        //? } else {
        /*field.render((com.mojang.blaze3d.vertex.PoseStack) ctx, mouseX, mouseY, delta);*/
        //? }
    }

    @Override
    public Map<String, Object> getValue() {
        return Map.of("type", "command", "value", field.getText());
    }

    @Override
    public void reset() {
        field.setText("");
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        return field.mouseClicked(event, bl);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return field.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        return field.charTyped(event);
    }*/
    //? } else {
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        return field.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        return field.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return field.charTyped(c, mods);
    }
    //? }
}