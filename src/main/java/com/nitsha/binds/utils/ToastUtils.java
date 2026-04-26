package com.nitsha.binds.utils;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
//? if >1.20.1 {
import net.minecraft.advancements.AdvancementHolder;
//? }

public class ToastUtils {
    public static void showFakeAdvancement(Component title, String type, ItemStack icon) {

        AdvancementType toastType = switch (type) {
            case "goal" -> AdvancementType.GOAL;
            case "challenge" -> AdvancementType.CHALLENGE;
            default -> AdvancementType.TASK;
        };

        DisplayInfo displayInfo = new DisplayInfo(
                icon,
                title,
                TextUtils.empty(),
                null,
                toastType,
                true,
                false,
                false
        );

        //? if >1.20.1 {
        AdvancementHolder advancement = Advancement.Builder.advancement()
                .display(displayInfo)
                .build(Main.id("dummy_advancement"));
        Minecraft.getInstance().getToastManager().addToast(new AdvancementToast(advancement));
        //? } else {
        /*Advancement advancement = Advancement.Builder.advancement()
                .display(displayInfo)
                .build(Main.id("dummy_advancement"));
        Minecraft.getInstance().getToasts().addToast(new AdvancementToast(advancement));*/
        //? }
    }
}
