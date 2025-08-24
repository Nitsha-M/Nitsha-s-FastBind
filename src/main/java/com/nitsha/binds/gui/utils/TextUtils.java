package com.nitsha.binds.gui.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextUtils {
    public static MutableText literal(String text) {
        //? if >=1.19 {
        return Text.literal(text);
        //? } else {
        /*return new net.minecraft.text.LiteralText(text);*/
        //? }
    }

    public static MutableText translatable(String key, Object... args) {
        //? if >=1.19 {
        return Text.translatable(key, args);
        //? } else {
        /*return new net.minecraft.text.TranslatableText(key, args);*/
        //? }
    }
}