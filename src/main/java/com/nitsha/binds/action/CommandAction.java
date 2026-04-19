package com.nitsha.binds.action;

import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.TextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

import com.nitsha.binds.configs.dto.actions.AllActionsData.CommandActionData;

public class CommandAction extends ActionType<CommandActionData> {

    private TextField field;

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

    @Override
    public CommandActionData createDefaultData() { return new CommandActionData(); }

    @Override
    public void buildTasks(CommandActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        String cmd = data.value;
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

    @Override
    public void init(int x, int y, int width, CommandActionData data) {
        this.field = new TextField(
                Minecraft.getInstance().font,
                x, y + 3, width - 26, 19,
                Integer.MAX_VALUE,
                data.value,
                TextUtils.translatable("nitsha.binds.advances.actions.commandLine").getString()
        );
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        field.renderWidget(ctx, mouseX, mouseY, delta);
    }

    @Override
    public CommandActionData getValue() {
        CommandActionData result = new CommandActionData();
        result.value = field.getText();
        return result;
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