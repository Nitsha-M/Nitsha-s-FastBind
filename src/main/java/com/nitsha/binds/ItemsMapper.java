package com.nitsha.binds;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Holder;
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.Holder;
//?} else if >=1.19.3 {
/*import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
*///?} else {
/*import net.minecraft.world.item.alchemy.PotionUtils;
 *///?}
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import net.minecraft.core.registries.BuiltInRegistries;

import java.util.*;

public class ItemsMapper {
    public static final Map<String, ItemStack> itemStackMap = new LinkedHashMap<>();
    public static final Map<String, Map<String, ItemStack>> categories = new HashMap<>();
    private static final ItemStack standart = new ItemStack(Blocks.STRUCTURE_VOID);
    private static final ItemStack notFound = new ItemStack(Blocks.BARRIER);

    private static List<String> cachedKeys = null;
    private static final Random random = new Random();

    static {
        addCategory("foods", cat_POTIONS());

        getAllItems();
    }

    public static void getAllItems() {
        Minecraft mc = Minecraft.getInstance();
        HolderLookup.Provider registryAccess;

        if (mc.level != null) {
            registryAccess = mc.level.registryAccess();
        } else {
            registryAccess = mc.getConnection() != null
                    ? mc.getConnection().registryAccess()
                    : RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        }

        CreativeModeTab.ItemDisplayParameters displayParams = new CreativeModeTab.ItemDisplayParameters(
                FeatureFlags.VANILLA_SET,
                true,
                registryAccess
        );

        // --- Ванильные вкладки ---
        List<ResourceKey<CreativeModeTab>> orderedTabs = List.of(
                CreativeModeTabs.NATURAL_BLOCKS,
                CreativeModeTabs.BUILDING_BLOCKS,
                CreativeModeTabs.FUNCTIONAL_BLOCKS,
                CreativeModeTabs.REDSTONE_BLOCKS,
                CreativeModeTabs.COLORED_BLOCKS,
                CreativeModeTabs.SPAWN_EGGS,
                CreativeModeTabs.TOOLS_AND_UTILITIES,
                CreativeModeTabs.COMBAT,
                CreativeModeTabs.FOOD_AND_DRINKS,
                CreativeModeTabs.INGREDIENTS,
                CreativeModeTabs.OP_BLOCKS
        );

        for (ResourceKey<CreativeModeTab> tabKey : orderedTabs) {
            //? if >=1.21 {
            Optional<Holder.Reference<CreativeModeTab>> holder = BuiltInRegistries.CREATIVE_MODE_TAB.get(tabKey);
            if (holder.isEmpty()) continue;

            CreativeModeTab tab = holder.get().value();
            //? } else {
            /*Holder<CreativeModeTab> holder = BuiltInRegistries.CREATIVE_MODE_TAB.getHolderOrThrow(tabKey);
            CreativeModeTab tab = holder.value();*/
            //? }

            if (tab == null) continue;

            if (tabKey == null || tab.getType() != CreativeModeTab.Type.CATEGORY) continue;

            try {
                tab.buildContents(displayParams);
            } catch (Exception e) {
                System.out.println("ERROR building contents: " + e.getMessage());
                e.printStackTrace();
                continue;
            }

            if (tab.getDisplayItems().isEmpty()) continue;
            Map<String, ItemStack> categoryMap = new LinkedHashMap<>();

            int itemCount = 0;
            for (ItemStack stack : tab.getDisplayItems()) {
                itemCount++;
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (id != null) {
                    String key = id.toString();
                    String path = id.getPath();
                    String namespace = id.getNamespace();
                    if (!namespace.equals("minecraft") || path.equals("air") || path.equals("structure_void") || path.equals("structure_block") || path.equals("barrier"))
                        continue;
                    categoryMap.put(key, stack.copy());
                }
            }

            String ourCategory = mapCreativeTabToOurCategory(tabKey);

            if (!categoryMap.isEmpty()) {
                addCategory(ourCategory, categoryMap);
            }
        }

        Map<String, ItemStack> moddedItems = new LinkedHashMap<>();

        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id != null && !id.getNamespace().equals("minecraft")) {
                moddedItems.put(id.toString(), new ItemStack(item));
            }
        }

        if (!moddedItems.isEmpty()) {
            addCategory("mods", moddedItems);
        }
    }

    private static String mapCreativeTabToOurCategory(ResourceKey<CreativeModeTab> tabKey) {
        if (tabKey.equals(CreativeModeTabs.NATURAL_BLOCKS) ||
                tabKey.equals(CreativeModeTabs.BUILDING_BLOCKS) ||
                tabKey.equals(CreativeModeTabs.FUNCTIONAL_BLOCKS) ||
                tabKey.equals(CreativeModeTabs.REDSTONE_BLOCKS)) {
            return "blocks";
        }

        if (tabKey.equals(CreativeModeTabs.COLORED_BLOCKS) ||
            tabKey.equals(CreativeModeTabs.SPAWN_EGGS)) {
            return "colored";
        }

        if (tabKey.equals(CreativeModeTabs.TOOLS_AND_UTILITIES) ||
                tabKey.equals(CreativeModeTabs.COMBAT)) {
            return "tools";
        }

        if (tabKey.equals(CreativeModeTabs.FOOD_AND_DRINKS)) {
            return "foods";
        }

        if (tabKey.equals(CreativeModeTabs.INGREDIENTS)) {
            return "gold";
        }

        if (tabKey.equals(CreativeModeTabs.OP_BLOCKS)) {
            return "mods";
        }

        return "mods";
    }

    private static Map<String, ItemStack> cat_POTIONS() {
        Map<String, ItemStack> map = new LinkedHashMap<>();
        map.put("minecraft:night_vision", getPotionItem(Potions.NIGHT_VISION, Items.POTION));
        map.put("minecraft:invisible", getPotionItem(Potions.INVISIBILITY, Items.POTION));
        map.put("minecraft:leaping", getPotionItem(Potions.LEAPING, Items.POTION));
        map.put("minecraft:fire_resistance", getPotionItem(Potions.FIRE_RESISTANCE, Items.POTION));
        map.put("minecraft:swiftness", getPotionItem(Potions.SWIFTNESS, Items.POTION));
        map.put("minecraft:slowness", getPotionItem(Potions.SLOWNESS, Items.POTION));
        map.put("minecraft:turtle_master", getPotionItem(Potions.TURTLE_MASTER, Items.POTION));
        map.put("minecraft:water_breathing", getPotionItem(Potions.WATER_BREATHING, Items.POTION));
        map.put("minecraft:healing", getPotionItem(Potions.HEALING, Items.POTION));
        map.put("minecraft:harming", getPotionItem(Potions.HARMING, Items.POTION));
        map.put("minecraft:poison", getPotionItem(Potions.POISON, Items.POTION));
        map.put("minecraft:regeneration", getPotionItem(Potions.REGENERATION, Items.POTION));
        map.put("minecraft:strength", getPotionItem(Potions.STRENGTH, Items.POTION));
        map.put("minecraft:weakness", getPotionItem(Potions.WEAKNESS, Items.POTION));
        map.put("minecraft:luck", getPotionItem(Potions.LUCK, Items.POTION));
        //? if >1.20.4 {
        map.put("minecraft:wind_charged", getPotionItem(Potions.WIND_CHARGED, Items.POTION));
        map.put("minecraft:weaving", getPotionItem(Potions.WEAVING, Items.POTION));
        map.put("minecraft:oozing", getPotionItem(Potions.OOZING, Items.POTION));
        map.put("minecraft:infested", getPotionItem(Potions.INFESTED, Items.POTION));
        //?}
        map.put("minecraft:night_vision_splash_potion", getPotionItem(Potions.NIGHT_VISION, Items.SPLASH_POTION));
        map.put("minecraft:invisible_splash_potion", getPotionItem(Potions.INVISIBILITY, Items.SPLASH_POTION));
        map.put("minecraft:leaping_splash_potion", getPotionItem(Potions.LEAPING, Items.SPLASH_POTION));
        map.put("minecraft:fire_resistance_splash_potion", getPotionItem(Potions.FIRE_RESISTANCE, Items.SPLASH_POTION));
        map.put("minecraft:swiftness_splash_potion", getPotionItem(Potions.SWIFTNESS, Items.SPLASH_POTION));
        map.put("minecraft:slowness_splash_potion", getPotionItem(Potions.SLOWNESS, Items.SPLASH_POTION));
        map.put("minecraft:turtle_master_splash_potion", getPotionItem(Potions.TURTLE_MASTER, Items.SPLASH_POTION));
        map.put("minecraft:water_breathing_splash_potion", getPotionItem(Potions.WATER_BREATHING, Items.SPLASH_POTION));
        map.put("minecraft:healing_splash_potion", getPotionItem(Potions.HEALING, Items.SPLASH_POTION));
        map.put("minecraft:harming_splash_potion", getPotionItem(Potions.HARMING, Items.SPLASH_POTION));
        map.put("minecraft:poison_splash_potion", getPotionItem(Potions.POISON, Items.SPLASH_POTION));
        map.put("minecraft:regeneration_splash_potion", getPotionItem(Potions.REGENERATION, Items.SPLASH_POTION));
        map.put("minecraft:strength_splash_potion", getPotionItem(Potions.STRENGTH, Items.SPLASH_POTION));
        map.put("minecraft:weakness_splash_potion", getPotionItem(Potions.WEAKNESS, Items.SPLASH_POTION));
        map.put("minecraft:luck_splash_potion", getPotionItem(Potions.LUCK, Items.SPLASH_POTION));
        //? if >1.20.4 {
        map.put("minecraft:wind_charged_splash_potion", getPotionItem(Potions.WIND_CHARGED, Items.SPLASH_POTION));
        map.put("minecraft:weaving_splash_potion", getPotionItem(Potions.WEAVING, Items.SPLASH_POTION));
        map.put("minecraft:oozing_splash_potion", getPotionItem(Potions.OOZING, Items.SPLASH_POTION));
        map.put("minecraft:infested_splash_potion", getPotionItem(Potions.INFESTED, Items.SPLASH_POTION));
        //?}

        map.put("minecraft:night_vision_lingering_potion", getPotionItem(Potions.NIGHT_VISION, Items.LINGERING_POTION));
        map.put("minecraft:invisible_lingering_potion", getPotionItem(Potions.INVISIBILITY, Items.LINGERING_POTION));
        map.put("minecraft:leaping_lingering_potion", getPotionItem(Potions.LEAPING, Items.LINGERING_POTION));
        map.put("minecraft:fire_resistance_lingering_potion", getPotionItem(Potions.FIRE_RESISTANCE, Items.LINGERING_POTION));
        map.put("minecraft:swiftness_lingering_potion", getPotionItem(Potions.SWIFTNESS, Items.LINGERING_POTION));
        map.put("minecraft:slowness_lingering_potion", getPotionItem(Potions.SLOWNESS, Items.LINGERING_POTION));
        map.put("minecraft:turtle_master_lingering_potion", getPotionItem(Potions.TURTLE_MASTER, Items.LINGERING_POTION));
        map.put("minecraft:water_breathing_lingering_potion", getPotionItem(Potions.WATER_BREATHING, Items.LINGERING_POTION));
        map.put("minecraft:healing_lingering_potion", getPotionItem(Potions.HEALING, Items.LINGERING_POTION));
        map.put("minecraft:harming_lingering_potion", getPotionItem(Potions.HARMING, Items.LINGERING_POTION));
        map.put("minecraft:poison_lingering_potion", getPotionItem(Potions.POISON, Items.LINGERING_POTION));
        map.put("minecraft:regeneration_lingering_potion", getPotionItem(Potions.REGENERATION, Items.LINGERING_POTION));
        map.put("minecraft:strength_lingering_potion", getPotionItem(Potions.STRENGTH, Items.LINGERING_POTION));
        map.put("minecraft:weakness_lingering_potion", getPotionItem(Potions.WEAKNESS, Items.LINGERING_POTION));
        map.put("minecraft:luck_lingering_potion", getPotionItem(Potions.LUCK, Items.LINGERING_POTION));
        //? if >1.20.4 {
        map.put("minecraft:wind_charged_lingering_potion", getPotionItem(Potions.WIND_CHARGED, Items.LINGERING_POTION));
        map.put("minecraft:weaving_lingering_potion", getPotionItem(Potions.WEAVING, Items.LINGERING_POTION));
        map.put("minecraft:oozing_lingering_potion", getPotionItem(Potions.OOZING, Items.LINGERING_POTION));
        map.put("minecraft:infested_lingering_potion", getPotionItem(Potions.INFESTED, Items.LINGERING_POTION));
        //?}
        return map;
    }

    private static void addCategory(String category, Map<String, ItemStack> map) {
        itemStackMap.putAll(map);

        categories.computeIfAbsent(category, k -> new LinkedHashMap<>()).putAll(map);
    }

    //? if >=1.20.5 {
    public static ItemStack getPotionItem(Holder<Potion> potion, Item baseItem) {
        ItemStack stack = new ItemStack(baseItem);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }
    //?} else if >=1.19.3 {
    /*public static ItemStack getPotionItem(Potion potion, Item baseItem) {
        ItemStack stack = new ItemStack(baseItem);
        CompoundTag tag = new CompoundTag();
        tag.putString("Potion", BuiltInRegistries.POTION.getKey(potion).toString());
        stack.setTag(tag);
        return stack;
    }
    *///?} else {
    /*public static ItemStack getPotionItem(Potion potion, Item baseItem) {
        ItemStack stack = new ItemStack(baseItem);
        PotionUtils.setPotion(stack, potion);
        return stack;
    }
    *///?}

    public static ItemStack getItemStack(String name) {
        if (!name.contains(":")) {
            name = "minecraft:" + name.toLowerCase();
        }
        return itemStackMap.getOrDefault(name, (name.equals("minecraft:structure_void")) ? standart : notFound);
    }

    public static int getItemStackIndex(String name) {
        int index = 0;
        for (String key : itemStackMap.keySet()) {
            if (key.equals(name)) return index;
            index++;
        }
        index = 0;
        for (String key : itemStackMap.keySet()) {
            if (key.equals("NOT_FOUND")) return index;
            index++;
        }
        return -1;
    }

    public static Map.Entry<String, ItemStack> getRandomItem() {
        if (itemStackMap.isEmpty()) {
            return null;
        }

        if (cachedKeys == null || cachedKeys.size() != itemStackMap.size()) {
            cachedKeys = new ArrayList<>(itemStackMap.keySet());
        }

        String randomKey = cachedKeys.get(random.nextInt(cachedKeys.size()));
        return new AbstractMap.SimpleEntry<>(randomKey, itemStackMap.get(randomKey).copy());
    }

}