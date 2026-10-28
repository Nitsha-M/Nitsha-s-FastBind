package com.nitsha.binds.utils;

import com.nitsha.binds.Main;
import com.nitsha.binds.multiversions.NitshaCraft;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

//? if >=1.20.2 {
import net.minecraft.advancements.AdvancementHolder;
//? }

//? if >=26.1 {
import net.minecraft.world.item.ItemStackTemplate;
//? }

public class ToastUtils {
    public static void showFakeAdvancement(Component title, String type, ItemStack icon) {
        AdvancementType toastType = switch (type) {
            case "goal" -> AdvancementType.GOAL;
            case "challenge" -> AdvancementType.CHALLENGE;
            default -> AdvancementType.TASK;
        };

        //? if >=26.1 {
        DisplayInfo displayInfo = new DisplayInfo(ItemStackTemplate.fromNonEmptyStack(icon), title, TextUtils.empty(), Optional.empty(), toastType, true, false, false);
        //? } else if >=1.20.3 {
        /*DisplayInfo displayInfo = new DisplayInfo(icon, title, TextUtils.empty(), Optional.empty(), toastType, true, false, false);*/
        //? } else {
        /*DisplayInfo displayInfo = new DisplayInfo(icon, title, TextUtils.empty(), null, toastType, true, false, false);*/
        //? }

        //? if >=1.20.2 {
        AdvancementHolder advancement = Advancement.Builder.advancement().display(displayInfo).build(Main.id("dummy_advancement"));
        //? } else {
        /*Advancement advancement = Advancement.Builder.advancement().display(displayInfo).build(Main.id("dummy_advancement"));*/
        //? }

        NitshaCraft.addToast(new AdvancementToast(advancement));
    }
}