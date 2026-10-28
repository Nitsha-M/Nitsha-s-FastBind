package com.nitsha.binds.multiversions;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

public class TextUtils {

    private static final FontDescription SMALL_FONT = new FontDescription.Resource(Identifier.parse("nitsha_fastbind:small"));

    public static MutableComponent small(Component text) {
        return text.copy().withStyle(style -> style.withFont(SMALL_FONT));
    }

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

    public static MutableComponent empty() {
        //? if >=1.19 {
        return Component.empty();
        //?} else {
        /*return new net.minecraft.network.chat.TextComponent("");*/
        //?}
    }
}