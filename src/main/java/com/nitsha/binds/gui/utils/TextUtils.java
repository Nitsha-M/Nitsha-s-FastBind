package com.nitsha.binds.gui.utils;

//? if fabric {
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//?} else {
/*import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
*/
//?}

public class TextUtils {
    public static MutableComponent literal(String text) {
        //? if >=1.19 {
        return Component.literal(text);
        //?} else {
        /*return new net.minecraft.network.chat.TextComponent(text);
         *///?}
    }

    public static MutableComponent translatable(String key, Object... args) {
        //? if >=1.19 {
        return Component.translatable(key, args);
        //?} else {
        /*return new net.minecraft.network.chat.TranslatableComponent(key, args);
         *///?}
    }
}