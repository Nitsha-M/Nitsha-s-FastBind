package com.nitsha.binds.multiversions;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.Minecraft;

//? if fabric {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//? }
//? if >=1.21.9 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.input.CharacterEvent;
//? }

public final class NitshaInput {
    private NitshaInput() {
    }

    public static boolean isKeyDown(KeyMapping mapping) {
        //? if fabric {
        int keyCode = KeyMappingHelper.getBoundKeyOf(mapping).getValue();
        //? } else {
        /*int keyCode = mapping.getKey().getValue();*/
        //? }

        //? if >=26.3 {
        return InputConstants.isKeyDown(keyCode);
        //? } else if >=1.21.9 {
        /*return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), keyCode);*/
        //? } else {
        /*return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keyCode);*/
        //? }
    }

    public static String getKeyName(int keyCode) {
        //$ if >=26.3 'return InputConstants.Type.KEYBOARD.getOrCreate(keyCode).getDisplayName().getString();' else 'return InputConstants.Type.KEYSYM.getOrCreate(keyCode).getDisplayName().getString();'
        return InputConstants.Type.KEYBOARD.getOrCreate(keyCode).getDisplayName().getString();
    }

    public static boolean matchesKey(KeyMapping mapping, int keyCode, int nativeCode) {
        //? if >=1.21.9 {
        return mapping.matches(new KeyEvent(keyCode, nativeCode, 0));
        //? } else {
        /*return mapping.matches(keyCode, nativeCode);*/
        //? }
    }

    public static boolean matchesMouse(KeyMapping mapping, int button) {
        //? if >=1.21.9 {
        MouseButtonEvent event = new MouseButtonEvent(0.0, 0.0, new MouseButtonInfo(button, 0));

        return mapping.matchesMouse(event);
        //? } else {
        /*return mapping.matchesMouse(button);*/
        //? }
    }

    public static boolean mouseClicked(Iterable<? extends GuiEventListener> children, double mouseX, double mouseY, int button) {
        return mouseClicked(children, mouseX, mouseY, button, 0, false);
    }

    public static boolean mouseClicked(Iterable<? extends GuiEventListener> children, double mouseX, double mouseY, int button, int modifiers, boolean doubleClick) {
        //? if >=1.21.9 {
        MouseButtonEvent event = new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, modifiers));
        //? }

        for (GuiEventListener child : children) {
            //? if >=1.21.9 {
            if (child.mouseClicked(event, doubleClick)) {
                return true;
            }
            //? } else {
            /*
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            */
            //? }
        }

        return false;
    }

    public static boolean mouseReleased(Iterable<? extends GuiEventListener> children, double mouseX, double mouseY, int button) {
        return mouseReleased(children, mouseX, mouseY, button, 0);
    }

    public static boolean mouseReleased(Iterable<? extends GuiEventListener> children, double mouseX, double mouseY, int button, int modifiers) {
        //? if >=1.21.9 {
        MouseButtonEvent event = new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, modifiers));
        //? }

        boolean released = false;

        for (GuiEventListener child : children) {
            //? if >=1.21.9 {
            if (child.mouseReleased(event)) {
                released = true;
            }
            //? } else {
            /*
            if (child.mouseReleased(mouseX, mouseY, button)) {
                released = true;
            }
            */
            //? }
        }

        return released;
    }

    public static boolean mouseDragged(Iterable<? extends GuiEventListener> children, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return mouseDragged(children, mouseX, mouseY, button, 0, deltaX, deltaY);
    }

    public static boolean mouseDragged(Iterable<? extends GuiEventListener> children, double mouseX, double mouseY, int button, int modifiers, double deltaX, double deltaY) {
        //? if >=1.21.9 {
        MouseButtonEvent event = new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, modifiers));
        //? }

        boolean dragged = false;

        for (GuiEventListener child : children) {
            //? if >=1.21.9 {
            if (child.mouseDragged(event, deltaX, deltaY)) {
                dragged = true;
            }
            //? } else {
            /*
            if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                dragged = true;
            }
            */
            //? }
        }

        return dragged;
    }

    public static boolean mouseScrolled(Iterable<? extends GuiEventListener> children, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (GuiEventListener child : children) {
            //? if >=1.20.2 {
            if (child.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true;
            }
            //? } else {
            /*
            if (child.mouseScrolled(mouseX, mouseY, verticalAmount)) {
                return true;
            }
            */
            //? }
        }

        return false;
    }

    public static boolean keyPressed(Iterable<? extends GuiEventListener> children, int keyCode, int nativeCode, int modifiers) {
        //? if >=1.21.9 {
        KeyEvent event = new KeyEvent(keyCode, nativeCode, modifiers);
        //? }

        for (GuiEventListener child : children) {
            //? if >=1.21.9 {
            if (child.keyPressed(event)) {
                return true;
            }
            //? } else {
            /*
            if (child.keyPressed(keyCode, nativeCode, modifiers)) {
                return true;
            }
            */
            //? }
        }

        return false;
    }

    public static boolean charTyped(Iterable<? extends GuiEventListener> children, int codePoint) {
        //? if >=26.1 {
        CharacterEvent event = new CharacterEvent(codePoint);
        //? } else if >=1.21.9 {
        /*CharacterEvent event = new CharacterEvent(codePoint, 0);*/
        //? }

        for (GuiEventListener child : children) {
            //? if >=1.21.9 {
            if (child.charTyped(event)) {
                return true;
            }
            //? } else {
        /*
        if (child.charTyped((char) codePoint, 0)) {
            return true;
        }
        */
            //? }
        }

        return false;
    }
}