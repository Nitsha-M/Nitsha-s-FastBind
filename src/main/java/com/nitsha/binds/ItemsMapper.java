package com.nitsha.binds;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
//? if >=1.20.5 {
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.Holder;
//? }
//? if >=1.19.3 {
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.core.registries.BuiltInRegistries;
//? } else {
// import net.minecraft.world.item.alchemy.PotionUtils;
//?}
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import java.util.*;

//? if <1.19.3 {
// import net.minecraft.core.Registry;
//?}

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

        //? if >=1.19.4 {
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
        //?}

        //? if >=1.20 {
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
            //? if >=1.21.2 {
            Optional<Holder.Reference<CreativeModeTab>> holder = BuiltInRegistries.CREATIVE_MODE_TAB.get(tabKey);
            if (holder.isEmpty()) continue;
            CreativeModeTab tab = holder.get().value();
            //?} else {
            /*Holder<CreativeModeTab> holder = BuiltInRegistries.CREATIVE_MODE_TAB.getHolderOrThrow(tabKey);
            CreativeModeTab tab = holder.value();*/
            //?}

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

            for (ItemStack stack : tab.getDisplayItems()) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (id != null) {
                    String key = id.toString();
                    String path = id.getPath();
                    String namespace = id.getNamespace();
                    if (!namespace.equals("minecraft") || path.equals("air") || path.equals("structure_void") || path.equals("structure_block") || path.equals("barrier") || path.equals("light"))
                        continue;
                    categoryMap.put(key, stack.copy());
                }
            }

            String ourCategory = mapCreativeTabToOurCategory(tabKey);

            if (!categoryMap.isEmpty()) {
                addCategory(ourCategory, categoryMap);
            }
        }
        //?} else if >=1.19.3 {
        /*List<CreativeModeTab> orderedTabs = List.of(
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

        for (CreativeModeTab tab : orderedTabs) {
            if (tab == null) continue;
            if (tab.getType() != CreativeModeTab.Type.CATEGORY) continue;
            Collection<ItemStack> items = tab.getDisplayItems();
            if (items.isEmpty()) {
                try {
                    tab.buildContents(FeatureFlags.VANILLA_SET, true);
                    items = tab.getDisplayItems();
                } catch (Exception e) {
                    continue;
                }
            }
            if (items.isEmpty()) continue;
            Map<String, ItemStack> categoryMap = new LinkedHashMap<>();
            for (ItemStack stack : items) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (id != null) {
                    String key = id.toString();
                    String path = id.getPath();
                    String namespace = id.getNamespace();
                    if (!namespace.equals("minecraft") || path.equals("air") || path.equals("structure_void") ||
                            path.equals("structure_block") || path.equals("barrier"))
                        continue;
                    categoryMap.put(key, stack.copy());
                }
            }
            String ourCategory = mapCreativeTabToOurCategory_1_19_3(tab);
            if (!categoryMap.isEmpty()) {
                addCategory(ourCategory, categoryMap);
            }
        }*/
        //? } else {
        /*List<CreativeModeTab> orderedTabs = List.of(
                CreativeModeTab.TAB_BUILDING_BLOCKS,
                CreativeModeTab.TAB_DECORATIONS,
                CreativeModeTab.TAB_REDSTONE,
                CreativeModeTab.TAB_TRANSPORTATION,
                CreativeModeTab.TAB_MISC,
                CreativeModeTab.TAB_TOOLS,
                CreativeModeTab.TAB_COMBAT,
                CreativeModeTab.TAB_BREWING,
                CreativeModeTab.TAB_FOOD,
                CreativeModeTab.TAB_MATERIALS
        );
        for (CreativeModeTab tab : orderedTabs) {
            if (tab == null) continue;

            NonNullList<ItemStack> items = NonNullList.create();
            tab.fillItemList(items);

            if (items.isEmpty()) continue;

            Map<String, ItemStack> categoryMap = new LinkedHashMap<>();

            for (ItemStack stack : items) {
                ResourceLocation id = Registry.ITEM.getKey(stack.getItem());
                if (id != null) {
                    String key = id.toString();
                    String path = id.getPath();
                    String namespace = id.getNamespace();
                    if (!namespace.equals("minecraft") || path.equals("air") || path.equals("structure_void") ||
                            path.equals("structure_block") || path.equals("barrier"))
                        continue;
                    categoryMap.put(key, stack.copy());
                }
            }

            String ourCategory = mapCreativeTabToOurCategory_1_19_2(tab);

            if (!categoryMap.isEmpty()) {
                addCategory(ourCategory, categoryMap);
            }
        }*/
        //? }

        Map<String, ItemStack> moddedItems = new LinkedHashMap<>();
        //? if >=1.18 {
        for (int level = 0; level <= 15; level++) {
            moddedItems.put("minecraft:light_" + level, getLightBlock(level));
        }
        //? }

        //? if >=1.17 {
        /*moddedItems.put("minecraft:light_0", getLightBlock(0));*/
        //? }
        //? if >=1.19.3 {
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        //?} else {
        /*for (Item item : Registry.ITEM) {
            ResourceLocation id = Registry.ITEM.getKey(item);*/
        //? }
            if (id != null && !id.getNamespace().equals("minecraft")) {
                moddedItems.put(id.toString(), new ItemStack(item));
            }
            //? if <1.19.3 {
            /*if (id != null && id.getNamespace().equals("minecraft")) {
                String path = id.getPath();
                if (path.equals("command_block") || path.equals("chain_command_block") ||
                    path.equals("repeating_command_block") || path.equals("jigsaw") ||
                    path.equals("structure_block") ||
                    path.equals("debug_stick") || path.equals("knowledge_book")) {
                    moddedItems.put(id.toString(), new ItemStack(item));
                }
            }*/
            //?}
        }

        if (!moddedItems.isEmpty()) {
            addCategory("mods", moddedItems);
        }
    }

    //? if >=1.19.3 {
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

    //? }

    //? if >=1.19.3 && <1.20 {
    /*private static String mapCreativeTabToOurCategory_1_19_3(CreativeModeTab tabKey) {
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
    *///?}

    //? if <1.19.3 {
    /*private static String mapCreativeTabToOurCategory_1_19_2(CreativeModeTab tab) {
        if (tab.equals(CreativeModeTab.TAB_BUILDING_BLOCKS) ||
                tab.equals(CreativeModeTab.TAB_REDSTONE)) {
            return "blocks";
        }
        if (tab.equals(CreativeModeTab.TAB_DECORATIONS)) {
            return "colored";
        }
        if (tab.equals(CreativeModeTab.TAB_TOOLS) ||
                tab.equals(CreativeModeTab.TAB_COMBAT) ||
                tab.equals(CreativeModeTab.TAB_TRANSPORTATION)) {
            return "tools";
        }
        if (tab.equals(CreativeModeTab.TAB_FOOD) ||
                tab.equals(CreativeModeTab.TAB_BREWING)) {
            return "foods";
        }
        if (tab.equals(CreativeModeTab.TAB_MISC)) {
            return "gold";
        }
        return "mods";
    }
    *///?}

    //? if >=1.20.5 {
    public static ItemStack getLightBlock(int level) {
        ItemStack stack = new ItemStack(Items.LIGHT);
        Map<String, String> properties = new HashMap<>();
        properties.put("level", String.valueOf(level));
        stack.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(properties));
        return stack;
    }
    //?} else if >=1.17 {
    /*public static ItemStack getLightBlock(int level) {
        ItemStack stack = new ItemStack(Items.LIGHT);
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag blockStateTag = new CompoundTag();
        blockStateTag.putString("level", String.valueOf(level));
        tag.put("BlockStateTag", blockStateTag);
        return stack;
    }*/
    //?}

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