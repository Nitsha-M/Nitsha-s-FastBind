package com.nitsha.binds.configs.dto.actions;

import com.nitsha.binds.configs.dto.preset.ActionData;

import java.util.List;
import java.util.Map;

public class AllActionsData {

    public static class CommandActionData extends ActionData {
        public String value = "";
        public CommandActionData() { super("command"); }
    }

    public static class DelayActionData extends ActionData {
        public int value = 0;
        public DelayActionData() { super("delay"); }
    }

    public static class KeybindActionData extends ActionData {
        public int value = 0;
        public KeybindActionData(String type) { super(type); }
    }

    public static class KeyEventInnerData {
        public String value = "";
        public String mode = "press";
        public String ms = "500";
    }

    public static class KeyEventActionData extends ActionData {
        public KeyEventInnerData value = new KeyEventInnerData();
        public KeyEventActionData() { super("keyEvent"); }
        public KeyEventActionData(String type) { super(type); }
    }

    public static class PlaySoundInnerData {
        public String value = "";
        public boolean isExternal = false;
        public float volume = 1.0f;
        public float pitch = 1.0f;
    }

    public static class PlaySoundActionData extends ActionData {
        public PlaySoundInnerData value = new PlaySoundInnerData();
        public PlaySoundActionData() { super("playSound"); }
        public PlaySoundActionData(String type) { super(type); }
    }

    public static class LoopInnerData {
        public int actions = 1;
        public int count = 1;
    }

    public static class LoopActionData extends ActionData {
        public LoopInnerData value = new LoopInnerData();
        public LoopActionData() { super("loop"); }
    }

    public static class TextFormatData {
        public String text = "";
        public List<Map<String, Integer>> marks;
    }

    public static class ChatMessageActionData extends ActionData {
        public TextFormatData value = new TextFormatData();
        public ChatMessageActionData() { super("chatMessage"); }
    }

    public static class TitleInnerData {
        public TextFormatData title = new TextFormatData();
        public TextFormatData subtitle = new TextFormatData();
    }

    public static class TitleMessageActionData extends ActionData {
        public TitleInnerData value = new TitleInnerData();
        public TitleMessageActionData() { super("titleMessage"); }
    }

    public static class ToastInnerData {
        public TextFormatData title = new TextFormatData();
        public String icon = "minecraft:diamond";
        public String toastType = "task";
    }

    public static class ToastActionData extends ActionData {
        public ToastInnerData value = new ToastInnerData();
        public ToastActionData() { super("toast"); }
    }

}
