package com.nitsha.binds;

import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemsMapper {
    public static final Map<String, ItemStack> itemStackMap = new LinkedHashMap<>();

    static {
        itemStackMap.put("STRUCTURE_VOID", new ItemStack(Items.STRUCTURE_VOID));
        itemStackMap.put("GRASS", new ItemStack(Blocks.GRASS_BLOCK));
        itemStackMap.put("STONE", new ItemStack(Blocks.STONE));
        itemStackMap.put("SCULK_SHRIEKER", new ItemStack(Blocks.SCULK_SHRIEKER));
        itemStackMap.put("SEA_LANTERN", new ItemStack(Blocks.SEA_LANTERN));
        itemStackMap.put("BEACON", new ItemStack(Blocks.BEACON));
        itemStackMap.put("RED_BED", new ItemStack(Blocks.RED_BED));
        itemStackMap.put("DRAGON_EGG", new ItemStack(Blocks.DRAGON_EGG));
        itemStackMap.put("STICK", new ItemStack(Items.STICK));
        itemStackMap.put("SOUL_LANTERN", new ItemStack(Items.SOUL_LANTERN));
        itemStackMap.put("SOUL_CAMPFIRE", new ItemStack(Items.SOUL_CAMPFIRE));
        itemStackMap.put("BELL", new ItemStack(Items.BELL));
        itemStackMap.put("ENDER_EYE", new ItemStack(Items.ENDER_EYE));
        itemStackMap.put("DIAMOND_ORE", new ItemStack(Blocks.DIAMOND_ORE));
        itemStackMap.put("INVISIBLE", getPotionItem(Potions.INVISIBILITY));
        itemStackMap.put("STRENGTH", getPotionItem(Potions.STRENGTH));

        itemStackMap.put("TRIAL_SPAWNER", new ItemStack(Blocks.TRIAL_SPAWNER));
        itemStackMap.put("TRIAL_KEY", new ItemStack(Items.TRIAL_KEY));
        itemStackMap.put("BREEZE_ROD", new ItemStack(Items.BREEZE_ROD));
        itemStackMap.put("HEAVY_CORE", new ItemStack(Items.HEAVY_CORE));
        itemStackMap.put("MACE", new ItemStack(Items.MACE));
    }

    private static ItemStack getPotionItem(RegistryEntry<Potion> name) {
        ItemStack potion = new ItemStack(Items.POTION);
        potion.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(name));
        return potion;
    }

    public static ItemStack getItemStack(String name) {
        return itemStackMap.get(name);
    }
    public static int getItemStackIndex(String name) {
        int index = 0;
        for (String key : itemStackMap.keySet()) {
            if (key.equals(name)) return index;
            index++;
        }
        return -1;
    }
}