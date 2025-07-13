package com.nitsha.binds;

import net.minecraft.block.Blocks;
//? if >=1.20.5 {
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
//?} else {
/*import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
*///?}
import net.minecraft.item.Item;
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
    public static final Map<String, Map<String, ItemStack>> categories = new HashMap<>();
    private static final ItemStack notFound = new ItemStack(Blocks.BARRIER);

    static {
        addCategory("blocks", cat_BLOCKS());
        addCategory("tools", cat_TOOLS());
        addCategory("foods", cat_FOODS());
        addCategory("potions", cat_POTIONS());
        addCategory("colored", cat_COLORED());
        addCategory("gold", cat_GOLD());
    }

    private static Map<String, ItemStack> cat_BLOCKS() {
        Map<String, ItemStack> map = new LinkedHashMap<>();
        map.put("STRUCTURE_VOID", new ItemStack(Items.STRUCTURE_VOID));

        // --- Земляные и сыпучие блоки ---
        map.put("GRASS", new ItemStack(Blocks.GRASS_BLOCK));
        map.put("PODZOL", new ItemStack(Blocks.PODZOL));
        map.put("DIRT", new ItemStack(Blocks.DIRT));
        map.put("COARSE_DIRT", new ItemStack(Blocks.COARSE_DIRT));
        map.put("ROOTED_DIRT", new ItemStack(Blocks.ROOTED_DIRT));
        map.put("DIRT_PATH", new ItemStack(Blocks.DIRT_PATH));
        map.put("FARMLAND", new ItemStack(Blocks.FARMLAND));
        map.put("MUD", new ItemStack(Blocks.MUD));
        map.put("PACKED_MUD", new ItemStack(Blocks.PACKED_MUD));
        map.put("CLAY", new ItemStack(Blocks.CLAY));
        map.put("GRAVEL", new ItemStack(Blocks.GRAVEL));
        map.put("SUSPICIOUS_GRAVEL", new ItemStack(Blocks.SUSPICIOUS_GRAVEL));
        map.put("SAND", new ItemStack(Blocks.SAND));
        map.put("RED_SAND", new ItemStack(Blocks.RED_SAND));
        map.put("SUSPICIOUS_SAND", new ItemStack(Blocks.SUSPICIOUS_SAND));
        map.put("SOUL_SAND", new ItemStack(Blocks.SOUL_SAND));
        map.put("SOUL_SOIL", new ItemStack(Blocks.SOUL_SOIL));

// --- Камень и его производные (Верхний Мир) ---
        map.put("STONE", new ItemStack(Blocks.STONE));
        map.put("SMOOTH_STONE", new ItemStack(Blocks.SMOOTH_STONE));
        map.put("COBBLESTONE", new ItemStack(Blocks.COBBLESTONE));
        map.put("MOSSY_COBBLESTONE", new ItemStack(Blocks.MOSSY_COBBLESTONE));
        map.put("STONE_BRICKS", new ItemStack(Blocks.STONE_BRICKS));
        map.put("MOSSY_STONE_BRICKS", new ItemStack(Blocks.MOSSY_STONE_BRICKS));
        map.put("CRACKED_STONE_BRICKS", new ItemStack(Blocks.CRACKED_STONE_BRICKS));
        map.put("CHISELED_STONE_BRICKS", new ItemStack(Blocks.CHISELED_STONE_BRICKS));

        map.put("GRANITE", new ItemStack(Blocks.GRANITE));
        map.put("POLISHED_GRANITE", new ItemStack(Blocks.POLISHED_GRANITE));
        map.put("DIORITE", new ItemStack(Blocks.DIORITE));
        map.put("POLISHED_DIORITE", new ItemStack(Blocks.POLISHED_DIORITE));
        map.put("ANDESITE", new ItemStack(Blocks.ANDESITE));
        map.put("POLISHED_ANDESITE", new ItemStack(Blocks.POLISHED_ANDESITE));

        map.put("DEEPSLATE", new ItemStack(Blocks.DEEPSLATE));
        map.put("COBBLED_DEEPSLATE", new ItemStack(Blocks.COBBLED_DEEPSLATE));
        map.put("POLISHED_DEEPSLATE", new ItemStack(Blocks.POLISHED_DEEPSLATE));
        map.put("DEEPSLATE_BRICKS", new ItemStack(Blocks.DEEPSLATE_BRICKS));
        map.put("CRACKED_DEEPSLATE_BRICKS", new ItemStack(Blocks.CRACKED_DEEPSLATE_BRICKS));
        map.put("DEEPSLATE_TILES", new ItemStack(Blocks.DEEPSLATE_TILES));
        map.put("CRACKED_DEEPSLATE_TILES", new ItemStack(Blocks.CRACKED_DEEPSLATE_TILES));
        map.put("CHISELED_DEEPSLATE", new ItemStack(Blocks.CHISELED_DEEPSLATE));

        map.put("TUFF", new ItemStack(Blocks.TUFF));
        //? if >=1.20.3 {
        map.put("POLISHED_TUFF", new ItemStack(Blocks.POLISHED_TUFF));
        map.put("TUFF_BRICKS", new ItemStack(Blocks.TUFF_BRICKS));
        map.put("CHISELED_TUFF_BRICKS", new ItemStack(Blocks.CHISELED_TUFF_BRICKS));
        map.put("CHISELED_TUFF", new ItemStack(Blocks.CHISELED_TUFF));
        //? }

// --- Песчаник и его производные ---
        map.put("SANDSTONE", new ItemStack(Blocks.SANDSTONE));
        map.put("CHISELED_SANDSTONE", new ItemStack(Blocks.CHISELED_SANDSTONE));
        map.put("CUT_SANDSTONE", new ItemStack(Blocks.CUT_SANDSTONE));
        map.put("SMOOTH_SANDSTONE", new ItemStack(Blocks.SMOOTH_SANDSTONE));
        map.put("RED_SANDSTONE", new ItemStack(Blocks.RED_SANDSTONE));
        map.put("CHISELED_RED_SANDSTONE", new ItemStack(Blocks.CHISELED_RED_SANDSTONE));
        map.put("CUT_RED_SANDSTONE", new ItemStack(Blocks.CUT_RED_SANDSTONE));
        map.put("SMOOTH_RED_SANDSTONE", new ItemStack(Blocks.SMOOTH_RED_SANDSTONE));

// --- Блоки из Незера ---
        map.put("NETHERRACK", new ItemStack(Blocks.NETHERRACK));
        map.put("NETHER_BRICKS", new ItemStack(Blocks.NETHER_BRICKS));
        map.put("RED_NETHER_BRICKS", new ItemStack(Blocks.RED_NETHER_BRICKS));
        map.put("CHISELED_NETHER_BRICKS", new ItemStack(Blocks.CHISELED_NETHER_BRICKS));
        map.put("CRACKED_NETHER_BRICKS", new ItemStack(Blocks.CRACKED_NETHER_BRICKS));
        map.put("BASALT", new ItemStack(Blocks.BASALT));
        map.put("SMOOTH_BASALT", new ItemStack(Blocks.SMOOTH_BASALT));
        map.put("POLISHED_BASALT", new ItemStack(Blocks.POLISHED_BASALT));
        map.put("BLACKSTONE", new ItemStack(Blocks.BLACKSTONE));
        map.put("POLISHED_BLACKSTONE", new ItemStack(Blocks.POLISHED_BLACKSTONE));
        map.put("POLISHED_BLACKSTONE_BRICKS", new ItemStack(Blocks.POLISHED_BLACKSTONE_BRICKS));
        map.put("CRACKED_POLISHED_BLACKSTONE_BRICKS", new ItemStack(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS));
        map.put("CHISELED_POLISHED_BLACKSTONE", new ItemStack(Blocks.CHISELED_POLISHED_BLACKSTONE));

// --- Блоки из Энда ---
        map.put("END_STONE", new ItemStack(Blocks.END_STONE));
        map.put("END_STONE_BRICKS", new ItemStack(Blocks.END_STONE_BRICKS));
        map.put("PURPUR_BLOCK", new ItemStack(Blocks.PURPUR_BLOCK));
        map.put("PURPUR_PILLAR", new ItemStack(Blocks.PURPUR_PILLAR));

// --- Кирпичи, Призмарин и Декорации ---
        map.put("BRICKS", new ItemStack(Blocks.BRICKS));
        map.put("MUD_BRICKS", new ItemStack(Blocks.MUD_BRICKS));
        map.put("DECORATED_POT", new ItemStack(Blocks.DECORATED_POT));
        map.put("PRISMARINE", new ItemStack(Blocks.PRISMARINE));
        map.put("PRISMARINE_BRICKS", new ItemStack(Blocks.PRISMARINE_BRICKS));
        map.put("DARK_PRISMARINE", new ItemStack(Blocks.DARK_PRISMARINE));
        map.put("SEA_LANTERN", new ItemStack(Blocks.SEA_LANTERN));

// --- Природные минералы и ресурсы ---
        map.put("COAL_BLOCK", new ItemStack(Blocks.COAL_BLOCK));
        map.put("RAW_IRON_BLOCK", new ItemStack(Blocks.RAW_IRON_BLOCK));
        map.put("RAW_GOLD_BLOCK", new ItemStack(Blocks.RAW_GOLD_BLOCK));
        map.put("CALCITE", new ItemStack(Blocks.CALCITE));
        map.put("AMETHYST_BLOCK", new ItemStack(Blocks.AMETHYST_BLOCK));
        map.put("BUDDING_AMETHYST", new ItemStack(Blocks.BUDDING_AMETHYST));
        map.put("LARGE_AMETHYST_BUD", new ItemStack(Blocks.LARGE_AMETHYST_BUD));
        map.put("MEDIUM_AMETHYST_BUD", new ItemStack(Blocks.MEDIUM_AMETHYST_BUD));
        map.put("SMALL_AMETHYST_BUD", new ItemStack(Blocks.AMETHYST_CLUSTER));
        map.put("BONE_BLOCK", new ItemStack(Blocks.BONE_BLOCK));
        map.put("OBSIDIAN", new ItemStack(Blocks.OBSIDIAN));
        map.put("CRYING_OBSIDIAN", new ItemStack(Blocks.CRYING_OBSIDIAN));
        map.put("MAGMA_BLOCK", new ItemStack(Blocks.MAGMA_BLOCK));
        map.put("ICE", new ItemStack(Blocks.ICE));
        map.put("PACKED_ICE", new ItemStack(Blocks.PACKED_ICE));
        map.put("BLUE_ICE", new ItemStack(Blocks.BLUE_ICE));
        map.put("SNOW_BLOCK", new ItemStack(Blocks.SNOW_BLOCK));

// --- Стены ---
        map.put("COBBLESTONE_WALL", new ItemStack(Blocks.COBBLESTONE_WALL));
        map.put("MOSSY_COBBLESTONE_WALL", new ItemStack(Blocks.MOSSY_COBBLESTONE_WALL));
        map.put("BRICK_WALL", new ItemStack(Blocks.BRICK_WALL));
        map.put("MUD_BRICK_WALL", new ItemStack(Blocks.MUD_BRICK_WALL));
        map.put("STONE_BRICK_WALL", new ItemStack(Blocks.STONE_BRICK_WALL));
        map.put("MOSSY_STONE_BRICK_WALL", new ItemStack(Blocks.MOSSY_STONE_BRICK_WALL));
        map.put("GRANITE_WALL", new ItemStack(Blocks.GRANITE_WALL));
        map.put("DIORITE_WALL", new ItemStack(Blocks.DIORITE_WALL));
        map.put("ANDESITE_WALL", new ItemStack(Blocks.ANDESITE_WALL));
        map.put("SANDSTONE_WALL", new ItemStack(Blocks.SANDSTONE_WALL));
        map.put("RED_SANDSTONE_WALL", new ItemStack(Blocks.RED_SANDSTONE_WALL));
        map.put("PRISMARINE_WALL", new ItemStack(Blocks.PRISMARINE_WALL));
        map.put("NETHER_BRICK_WALL", new ItemStack(Blocks.NETHER_BRICK_WALL));
        map.put("RED_NETHER_BRICK_WALL", new ItemStack(Blocks.RED_NETHER_BRICK_WALL));
        map.put("END_STONE_BRICK_WALL", new ItemStack(Blocks.END_STONE_BRICK_WALL));
        map.put("BLACKSTONE_WALL", new ItemStack(Blocks.BLACKSTONE_WALL));
        map.put("POLISHED_BLACKSTONE_WALL", new ItemStack(Blocks.POLISHED_BLACKSTONE_WALL));
        map.put("POLISHED_BLACKSTONE_BRICK_WALL", new ItemStack(Blocks.POLISHED_BLACKSTONE_BRICK_WALL));
        map.put("COBBLED_DEEPSLATE_WALL", new ItemStack(Blocks.COBBLED_DEEPSLATE_WALL));
        map.put("POLISHED_DEEPSLATE_WALL", new ItemStack(Blocks.POLISHED_DEEPSLATE_WALL));
        map.put("DEEPSLATE_BRICK_WALL", new ItemStack(Blocks.DEEPSLATE_BRICK_WALL));
        map.put("DEEPSLATE_TILE_WALL", new ItemStack(Blocks.DEEPSLATE_TILE_WALL));
        //? if >=1.20.3 {
        map.put("TUFF_WALL", new ItemStack(Blocks.TUFF_WALL));
        map.put("POLISHED_TUFF_WALL", new ItemStack(Blocks.POLISHED_TUFF_WALL));
        map.put("TUFF_BRICK_WALL", new ItemStack(Blocks.TUFF_BRICK_WALL));
        //? }

// --- Прочие тематические блоки ---
        map.put("BEDROCK", new ItemStack(Blocks.BEDROCK));
        map.put("TINTED_GLASS", new ItemStack(Blocks.TINTED_GLASS));
        map.put("ANVIL", new ItemStack(Blocks.ANVIL));
        map.put("STONECUTTER", new ItemStack(Blocks.STONECUTTER));

        // Дерево
        map.put("OAK_LOG", new ItemStack(Blocks.OAK_LOG));
        map.put("SPRUCE_LOG", new ItemStack(Blocks.SPRUCE_LOG));
        map.put("BIRCH_LOG", new ItemStack(Blocks.BIRCH_LOG));
        map.put("JUNGLE_LOG", new ItemStack(Blocks.JUNGLE_LOG));
        map.put("ACACIA_LOG", new ItemStack(Blocks.ACACIA_LOG));
        map.put("CHERRY_LOG", new ItemStack(Blocks.CHERRY_LOG));
        map.put("DARK_OAK_LOG", new ItemStack(Blocks.DARK_OAK_LOG));
        //? if >=1.21.2 {
        map.put("PALE_OAK_LOG", new ItemStack(Blocks.PALE_OAK_LOG));
        //?}
        map.put("MANGROVE_LOG", new ItemStack(Blocks.MANGROVE_LOG));
        map.put("MANGROVE_ROOTS", new ItemStack(Blocks.MANGROVE_ROOTS));
        map.put("MUDDY_MANGROVE_ROOTS", new ItemStack(Blocks.MUDDY_MANGROVE_ROOTS));
        map.put("BAMBOO_BLOCK", new ItemStack(Blocks.BAMBOO_BLOCK));
        map.put("STRIPPED_SPRUCE_LOG", new ItemStack(Blocks.STRIPPED_SPRUCE_LOG));
        map.put("STRIPPED_BIRCH_LOG", new ItemStack(Blocks.STRIPPED_BIRCH_LOG));
        map.put("STRIPPED_JUNGLE_LOG", new ItemStack(Blocks.STRIPPED_JUNGLE_LOG));
        map.put("STRIPPED_ACACIA_LOG", new ItemStack(Blocks.STRIPPED_ACACIA_LOG));
        map.put("STRIPPED_CHERRY_LOG", new ItemStack(Blocks.STRIPPED_CHERRY_LOG));
        map.put("STRIPPED_DARK_OAK_LOG", new ItemStack(Blocks.STRIPPED_DARK_OAK_LOG));
        //? if >=1.21.2 {
        map.put("STRIPPED_PALE_OAK_LOG", new ItemStack(Blocks.STRIPPED_PALE_OAK_LOG));
        //?}
        map.put("STRIPPED_OAK_LOG", new ItemStack(Blocks.STRIPPED_OAK_LOG));
        map.put("STRIPPED_MANGROVE_LOG", new ItemStack(Blocks.STRIPPED_MANGROVE_LOG));
        map.put("STRIPPED_BAMBOO_BLOCK", new ItemStack(Blocks.STRIPPED_BAMBOO_BLOCK));
        map.put("OAK_WOOD", new ItemStack(Blocks.OAK_WOOD));
        map.put("SPRUCE_WOOD", new ItemStack(Blocks.SPRUCE_WOOD));
        map.put("BIRCH_WOOD", new ItemStack(Blocks.BIRCH_WOOD));
        map.put("JUNGLE_WOOD", new ItemStack(Blocks.JUNGLE_WOOD));
        map.put("ACACIA_WOOD", new ItemStack(Blocks.ACACIA_WOOD));
        map.put("CHERRY_WOOD", new ItemStack(Blocks.CHERRY_WOOD));
        map.put("DARK_OAK_WOOD", new ItemStack(Blocks.DARK_OAK_WOOD));
        map.put("MANGROVE_WOOD", new ItemStack(Blocks.MANGROVE_WOOD));
        map.put("STRIPPED_OAK_WOOD", new ItemStack(Blocks.STRIPPED_OAK_WOOD));
        map.put("STRIPPED_SPRUCE_WOOD", new ItemStack(Blocks.STRIPPED_SPRUCE_WOOD));
        map.put("STRIPPED_BIRCH_WOOD", new ItemStack(Blocks.STRIPPED_BIRCH_WOOD));
        map.put("STRIPPED_JUNGLE_WOOD", new ItemStack(Blocks.STRIPPED_JUNGLE_WOOD));
        map.put("STRIPPED_ACACIA_WOOD", new ItemStack(Blocks.STRIPPED_ACACIA_WOOD));
        map.put("STRIPPED_CHERRY_WOOD", new ItemStack(Blocks.STRIPPED_CHERRY_WOOD));
        map.put("STRIPPED_DARK_OAK_WOOD", new ItemStack(Blocks.STRIPPED_DARK_OAK_WOOD));
        //? if >=1.21.2 {
        map.put("STRIPPED_PALE_OAK_WOOD", new ItemStack(Blocks.STRIPPED_PALE_OAK_WOOD));
        //?}
        map.put("STRIPPED_MANGROVE_WOOD", new ItemStack(Blocks.STRIPPED_MANGROVE_WOOD));
        map.put("OAK_PLANKS", new ItemStack(Blocks.OAK_PLANKS));
        map.put("SPRUCE_PLANKS", new ItemStack(Blocks.SPRUCE_PLANKS));
        map.put("BIRCH_PLANKS", new ItemStack(Blocks.BIRCH_PLANKS));
        map.put("JUNGLE_PLANKS", new ItemStack(Blocks.JUNGLE_PLANKS));
        map.put("ACACIA_PLANKS", new ItemStack(Blocks.ACACIA_PLANKS));
        map.put("CHERRY_PLANKS", new ItemStack(Blocks.CHERRY_PLANKS));
        map.put("DARK_OAK_PLANKS", new ItemStack(Blocks.DARK_OAK_PLANKS));
        //? if >=1.21.2 {
        map.put("PALE_OAK_WOOD", new ItemStack(Blocks.PALE_OAK_WOOD));
        map.put("PALE_OAK_PLANKS", new ItemStack(Blocks.PALE_OAK_PLANKS));
        //?}
        map.put("MANGROVE_PLANKS", new ItemStack(Blocks.MANGROVE_PLANKS));
        map.put("BAMBOO_PLANKS", new ItemStack(Blocks.BAMBOO_PLANKS));
        map.put("BAMBOO_MOSAIC", new ItemStack(Blocks.BAMBOO_MOSAIC));
        map.put("OAK_SIGN", new ItemStack(Blocks.OAK_SIGN));
        map.put("SPRUCE_SIGN", new ItemStack(Blocks.SPRUCE_SIGN));
        map.put("BIRCH_SIGN", new ItemStack(Blocks.BIRCH_SIGN));
        map.put("ACACIA_SIGN", new ItemStack(Blocks.ACACIA_SIGN));
        map.put("CHERRY_SIGN", new ItemStack(Blocks.CHERRY_SIGN));
        map.put("JUNGLE_SIGN", new ItemStack(Blocks.JUNGLE_SIGN));
        map.put("DARK_OAK_SIGN", new ItemStack(Blocks.DARK_OAK_SIGN));
        //? if >=1.21.2 {
        map.put("PALE_OAK_SIGN", new ItemStack(Blocks.PALE_OAK_SIGN));
        //?}
        map.put("MANGROVE_SIGN", new ItemStack(Blocks.MANGROVE_SIGN));
        map.put("BAMBOO_SIGN", new ItemStack(Blocks.BAMBOO_SIGN));
        map.put("OAK_HANGING_SIGN", new ItemStack(Blocks.OAK_HANGING_SIGN));
        map.put("SPRUCE_HANGING_SIGN", new ItemStack(Blocks.SPRUCE_HANGING_SIGN));
        map.put("BIRCH_HANGING_SIGN", new ItemStack(Blocks.BIRCH_HANGING_SIGN));
        map.put("ACACIA_HANGING_SIGN", new ItemStack(Blocks.ACACIA_HANGING_SIGN));
        map.put("CHERRY_HANGING_SIGN", new ItemStack(Blocks.CHERRY_HANGING_SIGN));
        map.put("JUNGLE_HANGING_SIGN", new ItemStack(Blocks.JUNGLE_HANGING_SIGN));
        map.put("DARK_OAK_HANGING_SIGN", new ItemStack(Blocks.DARK_OAK_HANGING_SIGN));
        //? if >=1.21.2 {
        map.put("PALE_OAK_HANGING_SIGN", new ItemStack(Blocks.PALE_OAK_HANGING_SIGN));
        //?}
        map.put("CRIMSON_HANGING_SIGN", new ItemStack(Blocks.CRIMSON_HANGING_SIGN));
        map.put("WARPED_HANGING_SIGN", new ItemStack(Blocks.WARPED_HANGING_SIGN));
        map.put("MANGROVE_HANGING_SIGN", new ItemStack(Blocks.MANGROVE_HANGING_SIGN));
        map.put("BAMBOO_HANGING_SIGN", new ItemStack(Blocks.BAMBOO_HANGING_SIGN));
        map.put("OAK_PRESSURE_PLATE", new ItemStack(Blocks.OAK_PRESSURE_PLATE));
        map.put("SPRUCE_PRESSURE_PLATE", new ItemStack(Blocks.SPRUCE_PRESSURE_PLATE));
        map.put("BIRCH_PRESSURE_PLATE", new ItemStack(Blocks.BIRCH_PRESSURE_PLATE));
        map.put("JUNGLE_PRESSURE_PLATE", new ItemStack(Blocks.JUNGLE_PRESSURE_PLATE));
        map.put("ACACIA_PRESSURE_PLATE", new ItemStack(Blocks.ACACIA_PRESSURE_PLATE));
        map.put("CHERRY_PRESSURE_PLATE", new ItemStack(Blocks.CHERRY_PRESSURE_PLATE));
        map.put("DARK_OAK_PRESSURE_PLATE", new ItemStack(Blocks.DARK_OAK_PRESSURE_PLATE));
        //? if >=1.21.2 {
        map.put("PALE_OAK_PRESSURE_PLATE", new ItemStack(Blocks.PALE_OAK_PRESSURE_PLATE));
        //?}
        map.put("MANGROVE_PRESSURE_PLATE", new ItemStack(Blocks.MANGROVE_PRESSURE_PLATE));
        map.put("BAMBOO_PRESSURE_PLATE", new ItemStack(Blocks.BAMBOO_PRESSURE_PLATE));
        map.put("OAK_TRAPDOOR", new ItemStack(Blocks.OAK_TRAPDOOR));
        map.put("SPRUCE_TRAPDOOR", new ItemStack(Blocks.SPRUCE_TRAPDOOR));
        map.put("BIRCH_TRAPDOOR", new ItemStack(Blocks.BIRCH_TRAPDOOR));
        map.put("JUNGLE_TRAPDOOR", new ItemStack(Blocks.JUNGLE_TRAPDOOR));
        map.put("ACACIA_TRAPDOOR", new ItemStack(Blocks.ACACIA_TRAPDOOR));
        map.put("CHERRY_TRAPDOOR", new ItemStack(Blocks.CHERRY_TRAPDOOR));
        map.put("DARK_OAK_TRAPDOOR", new ItemStack(Blocks.DARK_OAK_TRAPDOOR));
        //? if >=1.21.2 {
        map.put("PALE_OAK_TRAPDOOR", new ItemStack(Blocks.PALE_OAK_TRAPDOOR));
        //?}
        map.put("MANGROVE_TRAPDOOR", new ItemStack(Blocks.MANGROVE_TRAPDOOR));
        map.put("BAMBOO_TRAPDOOR", new ItemStack(Blocks.BAMBOO_TRAPDOOR));
        map.put("OAK_BUTTON", new ItemStack(Blocks.OAK_BUTTON));
        map.put("SPRUCE_BUTTON", new ItemStack(Blocks.SPRUCE_BUTTON));
        map.put("BIRCH_BUTTON", new ItemStack(Blocks.BIRCH_BUTTON));
        map.put("JUNGLE_BUTTON", new ItemStack(Blocks.JUNGLE_BUTTON));
        map.put("ACACIA_BUTTON", new ItemStack(Blocks.ACACIA_BUTTON));
        map.put("CHERRY_BUTTON", new ItemStack(Blocks.CHERRY_BUTTON));
        map.put("DARK_OAK_BUTTON", new ItemStack(Blocks.DARK_OAK_BUTTON));
        //? if >=1.21.2 {
        map.put("PALE_OAK_BUTTON", new ItemStack(Blocks.PALE_OAK_BUTTON));
        //?}
        map.put("MANGROVE_BUTTON", new ItemStack(Blocks.MANGROVE_BUTTON));
        map.put("BAMBOO_BUTTON", new ItemStack(Blocks.BAMBOO_BUTTON));
        map.put("OAK_DOOR", new ItemStack(Blocks.OAK_DOOR));
        map.put("SPRUCE_DOOR", new ItemStack(Blocks.SPRUCE_DOOR));
        map.put("BIRCH_DOOR", new ItemStack(Blocks.BIRCH_DOOR));
        map.put("JUNGLE_DOOR", new ItemStack(Blocks.JUNGLE_DOOR));
        map.put("ACACIA_DOOR", new ItemStack(Blocks.ACACIA_DOOR));
        map.put("CHERRY_DOOR", new ItemStack(Blocks.CHERRY_DOOR));
        map.put("DARK_OAK_DOOR", new ItemStack(Blocks.DARK_OAK_DOOR));
        //? if >=1.21.2 {
        map.put("PALE_OAK_DOOR", new ItemStack(Blocks.PALE_OAK_DOOR));
        //?}
        map.put("MANGROVE_DOOR", new ItemStack(Blocks.MANGROVE_DOOR));
        map.put("BAMBOO_DOOR", new ItemStack(Blocks.BAMBOO_DOOR));
        map.put("OAK_FENCE", new ItemStack(Blocks.OAK_FENCE));
        map.put("SPRUCE_FENCE", new ItemStack(Blocks.SPRUCE_FENCE));
        map.put("BIRCH_FENCE", new ItemStack(Blocks.BIRCH_FENCE));
        map.put("JUNGLE_FENCE", new ItemStack(Blocks.JUNGLE_FENCE));
        map.put("ACACIA_FENCE", new ItemStack(Blocks.ACACIA_FENCE));
        map.put("CHERRY_FENCE", new ItemStack(Blocks.CHERRY_FENCE));
        map.put("DARK_OAK_FENCE", new ItemStack(Blocks.DARK_OAK_FENCE));
        //? if >=1.21.2 {
        map.put("PALE_OAK_FENCE", new ItemStack(Blocks.PALE_OAK_FENCE));
        //?}
        map.put("MANGROVE_FENCE", new ItemStack(Blocks.MANGROVE_FENCE));
        map.put("BAMBOO_FENCE", new ItemStack(Blocks.BAMBOO_FENCE));
        map.put("CRIMSON_FENCE", new ItemStack(Blocks.CRIMSON_FENCE));
        map.put("WARPED_FENCE", new ItemStack(Blocks.WARPED_FENCE));
        map.put("NETHER_BRICK_FENCE", new ItemStack(Blocks.NETHER_BRICK_FENCE));
        map.put("OAK_FENCE_GATE", new ItemStack(Blocks.OAK_FENCE_GATE));
        map.put("SPRUCE_FENCE_GATE", new ItemStack(Blocks.SPRUCE_FENCE_GATE));
        map.put("BIRCH_FENCE_GATE", new ItemStack(Blocks.BIRCH_FENCE_GATE));
        map.put("JUNGLE_FENCE_GATE", new ItemStack(Blocks.JUNGLE_FENCE_GATE));
        map.put("ACACIA_FENCE_GATE", new ItemStack(Blocks.ACACIA_FENCE_GATE));
        map.put("CHERRY_FENCE_GATE", new ItemStack(Blocks.CHERRY_FENCE_GATE));
        map.put("DARK_OAK_FENCE_GATE", new ItemStack(Blocks.DARK_OAK_FENCE_GATE));
        //? if >=1.21.2 {
        map.put("PALE_OAK_FENCE_GATE", new ItemStack(Blocks.PALE_OAK_FENCE_GATE));
        //?}
        map.put("MANGROVE_FENCE_GATE", new ItemStack(Blocks.MANGROVE_FENCE_GATE));
        map.put("BAMBOO_FENCE_GATE", new ItemStack(Blocks.BAMBOO_FENCE_GATE));
        map.put("CRIMSON_FENCE_GATE", new ItemStack(Blocks.CRIMSON_FENCE_GATE));
        map.put("WARPED_FENCE_GATE", new ItemStack(Blocks.WARPED_FENCE_GATE));

        // Адское дерево
        map.put("BARREL", new ItemStack(Blocks.BARREL));
        map.put("BOOKSHELF", new ItemStack(Blocks.BOOKSHELF));
        map.put("BOWL", new ItemStack(Items.BOWL));
        map.put("CARTOGRAPHY_TABLE", new ItemStack(Blocks.CARTOGRAPHY_TABLE));
        map.put("CHISELED_BOOKSHELF", new ItemStack(Blocks.CHISELED_BOOKSHELF));
        map.put("COMPOSTER", new ItemStack(Blocks.COMPOSTER));
        map.put("CRIMSON_DOOR", new ItemStack(Blocks.CRIMSON_DOOR));
        map.put("CRIMSON_PLANKS", new ItemStack(Blocks.CRIMSON_PLANKS));
        map.put("CRIMSON_SIGN", new ItemStack(Items.CRIMSON_SIGN));
        map.put("CRIMSON_STEM", new ItemStack(Blocks.CRIMSON_STEM));
        map.put("CRIMSON_TRAPDOOR", new ItemStack(Blocks.CRIMSON_TRAPDOOR));
        map.put("FLETCHING_TABLE", new ItemStack(Blocks.FLETCHING_TABLE));
        map.put("GRINDSTONE", new ItemStack(Blocks.GRINDSTONE));
        map.put("LOOM", new ItemStack(Blocks.LOOM));
        map.put("SCAFFOLDING", new ItemStack(Blocks.SCAFFOLDING));
        map.put("SMITHING_TABLE", new ItemStack(Blocks.SMITHING_TABLE));
        map.put("STRIPPED_CRIMSON_HYPHAE", new ItemStack(Blocks.STRIPPED_CRIMSON_HYPHAE));
        map.put("STRIPPED_CRIMSON_STEM", new ItemStack(Blocks.STRIPPED_CRIMSON_STEM));
        map.put("STRIPPED_WARPED_HYPHAE", new ItemStack(Blocks.STRIPPED_WARPED_HYPHAE));
        map.put("STRIPPED_WARPED_STEM", new ItemStack(Blocks.STRIPPED_WARPED_STEM));
        map.put("WARPED_DOOR", new ItemStack(Blocks.WARPED_DOOR));
        map.put("WARPED_HYPHAE", new ItemStack(Blocks.WARPED_HYPHAE));
        map.put("WARPED_PLANKS", new ItemStack(Blocks.WARPED_PLANKS));
        map.put("WARPED_SIGN", new ItemStack(Items.WARPED_SIGN));
        map.put("WARPED_STEM", new ItemStack(Blocks.WARPED_STEM));
        map.put("WARPED_TRAPDOOR", new ItemStack(Blocks.WARPED_TRAPDOOR));


        // Лестница
        map.put("OAK_STAIRS", new ItemStack(Blocks.OAK_STAIRS));
        map.put("SPRUCE_STAIRS", new ItemStack(Blocks.SPRUCE_STAIRS));
        map.put("BIRCH_STAIRS", new ItemStack(Blocks.BIRCH_STAIRS));
        map.put("JUNGLE_STAIRS", new ItemStack(Blocks.JUNGLE_STAIRS));
        map.put("ACACIA_STAIRS", new ItemStack(Blocks.ACACIA_STAIRS));
        map.put("CHERRY_STAIRS", new ItemStack(Blocks.CHERRY_STAIRS));
        map.put("DARK_OAK_STAIRS", new ItemStack(Blocks.DARK_OAK_STAIRS));
        //? if >=1.21.2 {
        map.put("PALE_OAK_STAIRS", new ItemStack(Blocks.PALE_OAK_STAIRS));
        //?}
        map.put("MANGROVE_STAIRS", new ItemStack(Blocks.MANGROVE_STAIRS));
        map.put("BAMBOO_STAIRS", new ItemStack(Blocks.BAMBOO_STAIRS));
        map.put("BAMBOO_MOSAIC_STAIRS", new ItemStack(Blocks.BAMBOO_MOSAIC_STAIRS));
        map.put("CRIMSON_STAIRS", new ItemStack(Blocks.CRIMSON_STAIRS));
        map.put("WARPED_STAIRS", new ItemStack(Blocks.WARPED_STAIRS));
        map.put("COBBLESTONE_STAIRS", new ItemStack(Blocks.COBBLESTONE_STAIRS));
        map.put("MOSSY_COBBLESTONE_STAIRS", new ItemStack(Blocks.MOSSY_COBBLESTONE_STAIRS));
        map.put("STONE_STAIRS", new ItemStack(Blocks.STONE_STAIRS));
        map.put("STONE_BRICK_STAIRS", new ItemStack(Blocks.STONE_BRICK_STAIRS));
        map.put("MOSSY_STONE_BRICK_STAIRS", new ItemStack(Blocks.MOSSY_STONE_BRICK_STAIRS));
        map.put("POLISHED_ANDESITE_STAIRS", new ItemStack(Blocks.POLISHED_ANDESITE_STAIRS));
        map.put("POLISHED_DIORITE_STAIRS", new ItemStack(Blocks.POLISHED_DIORITE_STAIRS));
        map.put("POLISHED_GRANITE_STAIRS", new ItemStack(Blocks.POLISHED_GRANITE_STAIRS));
        map.put("GRANITE_STAIRS", new ItemStack(Blocks.GRANITE_STAIRS));
        map.put("DIORITE_STAIRS", new ItemStack(Blocks.DIORITE_STAIRS));
        map.put("ANDESITE_STAIRS", new ItemStack(Blocks.ANDESITE_STAIRS));
        map.put("BRICK_STAIRS", new ItemStack(Blocks.BRICK_STAIRS));
        map.put("MUD_BRICK_STAIRS", new ItemStack(Blocks.MUD_BRICK_STAIRS));
        map.put("SANDSTONE_STAIRS", new ItemStack(Blocks.SANDSTONE_STAIRS));
        map.put("RED_SANDSTONE_STAIRS", new ItemStack(Blocks.RED_SANDSTONE_STAIRS));
        map.put("SMOOTH_SANDSTONE_STAIRS", new ItemStack(Blocks.SMOOTH_SANDSTONE_STAIRS));
        map.put("SMOOTH_RED_SANDSTONE_STAIRS", new ItemStack(Blocks.SMOOTH_RED_SANDSTONE_STAIRS));
        map.put("QUARTZ_STAIRS", new ItemStack(Blocks.QUARTZ_STAIRS));
        map.put("SMOOTH_QUARTZ_STAIRS", new ItemStack(Blocks.SMOOTH_QUARTZ_STAIRS));
        map.put("NETHER_BRICK_STAIRS", new ItemStack(Blocks.NETHER_BRICK_STAIRS));
        map.put("RED_NETHER_BRICK_STAIRS", new ItemStack(Blocks.RED_NETHER_BRICK_STAIRS));
        map.put("BLACKSTONE_STAIRS", new ItemStack(Blocks.BLACKSTONE_STAIRS));
        map.put("POLISHED_BLACKSTONE_STAIRS", new ItemStack(Blocks.POLISHED_BLACKSTONE_STAIRS));
        map.put("POLISHED_BLACKSTONE_BRICK_STAIRS", new ItemStack(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS));
        map.put("END_STONE_BRICK_STAIRS", new ItemStack(Blocks.END_STONE_BRICK_STAIRS));
        map.put("PURPUR_STAIRS", new ItemStack(Blocks.PURPUR_STAIRS));
        map.put("PRISMARINE_STAIRS", new ItemStack(Blocks.PRISMARINE_STAIRS));
        map.put("PRISMARINE_BRICK_STAIRS", new ItemStack(Blocks.PRISMARINE_BRICK_STAIRS));
        map.put("DARK_PRISMARINE_STAIRS", new ItemStack(Blocks.DARK_PRISMARINE_STAIRS));
        //? if >=1.20.3 {
        map.put("TUFF_STAIRS", new ItemStack(Blocks.TUFF_STAIRS));
        map.put("POLISHED_TUFF_STAIRS", new ItemStack(Blocks.POLISHED_TUFF_STAIRS));
        map.put("TUFF_BRICK_STAIRS", new ItemStack(Blocks.TUFF_BRICK_STAIRS));
        //? }
        map.put("CUT_COPPER_STAIRS", new ItemStack(Blocks.CUT_COPPER_STAIRS));
        map.put("EXPOSED_CUT_COPPER_STAIRS", new ItemStack(Blocks.EXPOSED_CUT_COPPER_STAIRS));
        map.put("WEATHERED_CUT_COPPER_STAIRS", new ItemStack(Blocks.WEATHERED_CUT_COPPER_STAIRS));
        map.put("OXIDIZED_CUT_COPPER_STAIRS", new ItemStack(Blocks.OXIDIZED_CUT_COPPER_STAIRS));
        map.put("COBBLED_DEEPSLATE_STAIRS", new ItemStack(Blocks.COBBLED_DEEPSLATE_STAIRS));
        map.put("POLISHED_DEEPSLATE_STAIRS", new ItemStack(Blocks.POLISHED_DEEPSLATE_STAIRS));
        map.put("DEEPSLATE_BRICK_STAIRS", new ItemStack(Blocks.DEEPSLATE_BRICK_STAIRS));
        map.put("DEEPSLATE_TILE_STAIRS", new ItemStack(Blocks.DEEPSLATE_TILE_STAIRS));

        // Полублоки
        map.put("OAK_SLAB", new ItemStack(Blocks.OAK_SLAB));
        map.put("SPRUCE_SLAB", new ItemStack(Blocks.SPRUCE_SLAB));
        map.put("BIRCH_SLAB", new ItemStack(Blocks.BIRCH_SLAB));
        map.put("JUNGLE_SLAB", new ItemStack(Blocks.JUNGLE_SLAB));
        map.put("ACACIA_SLAB", new ItemStack(Blocks.ACACIA_SLAB));
        map.put("CHERRY_SLAB", new ItemStack(Blocks.CHERRY_SLAB));
        map.put("DARK_OAK_SLAB", new ItemStack(Blocks.DARK_OAK_SLAB));
        //? if >=1.21.2 {
        map.put("PALE_OAK_SLAB", new ItemStack(Blocks.PALE_OAK_SLAB));
        //?}
        map.put("MANGROVE_SLAB", new ItemStack(Blocks.MANGROVE_SLAB));
        map.put("BAMBOO_SLAB", new ItemStack(Blocks.BAMBOO_SLAB));
        map.put("BAMBOO_MOSAIC_SLAB", new ItemStack(Blocks.BAMBOO_MOSAIC_SLAB));
        map.put("CRIMSON_SLAB", new ItemStack(Blocks.CRIMSON_SLAB));
        map.put("WARPED_SLAB", new ItemStack(Blocks.WARPED_SLAB));
        map.put("PETRIFIED_OAK_SLAB", new ItemStack(Blocks.PETRIFIED_OAK_SLAB));
        map.put("STONE_SLAB", new ItemStack(Blocks.STONE_SLAB));
        map.put("SMOOTH_STONE_SLAB", new ItemStack(Blocks.SMOOTH_STONE_SLAB));
        map.put("COBBLESTONE_SLAB", new ItemStack(Blocks.COBBLESTONE_SLAB));
        map.put("MOSSY_COBBLESTONE_SLAB", new ItemStack(Blocks.MOSSY_COBBLESTONE_SLAB));
        map.put("STONE_BRICK_SLAB", new ItemStack(Blocks.STONE_BRICK_SLAB));
        map.put("MOSSY_STONE_BRICK_SLAB", new ItemStack(Blocks.MOSSY_STONE_BRICK_SLAB));
        map.put("POLISHED_ANDESITE_SLAB", new ItemStack(Blocks.POLISHED_ANDESITE_SLAB));
        map.put("POLISHED_DIORITE_SLAB", new ItemStack(Blocks.POLISHED_DIORITE_SLAB));
        map.put("POLISHED_GRANITE_SLAB", new ItemStack(Blocks.POLISHED_GRANITE_SLAB));
        map.put("GRANITE_SLAB", new ItemStack(Blocks.GRANITE_SLAB));
        map.put("DIORITE_SLAB", new ItemStack(Blocks.DIORITE_SLAB));
        map.put("ANDESITE_SLAB", new ItemStack(Blocks.ANDESITE_SLAB));
        map.put("BRICK_SLAB", new ItemStack(Blocks.BRICK_SLAB));
        map.put("MUD_BRICK_SLAB", new ItemStack(Blocks.MUD_BRICK_SLAB));
        map.put("SANDSTONE_SLAB", new ItemStack(Blocks.SANDSTONE_SLAB));
        map.put("CUT_SANDSTONE_SLAB", new ItemStack(Blocks.CUT_SANDSTONE_SLAB));
        map.put("RED_SANDSTONE_SLAB", new ItemStack(Blocks.RED_SANDSTONE_SLAB));
        map.put("CUT_RED_SANDSTONE_SLAB", new ItemStack(Blocks.CUT_RED_SANDSTONE_SLAB));
        map.put("QUARTZ_SLAB", new ItemStack(Blocks.QUARTZ_SLAB));
        map.put("SMOOTH_QUARTZ_SLAB", new ItemStack(Blocks.SMOOTH_QUARTZ_SLAB));
        map.put("SMOOTH_SANDSTONE_SLAB", new ItemStack(Blocks.SMOOTH_SANDSTONE_SLAB));
        map.put("SMOOTH_RED_SANDSTONE_SLAB", new ItemStack(Blocks.SMOOTH_RED_SANDSTONE_SLAB));
        map.put("NETHER_BRICK_SLAB", new ItemStack(Blocks.NETHER_BRICK_SLAB));
        map.put("RED_NETHER_BRICK_SLAB", new ItemStack(Blocks.RED_NETHER_BRICK_SLAB));
        map.put("BLACKSTONE_SLAB", new ItemStack(Blocks.BLACKSTONE_SLAB));
        map.put("POLISHED_BLACKSTONE_SLAB", new ItemStack(Blocks.POLISHED_BLACKSTONE_SLAB));
        map.put("POLISHED_BLACKSTONE_BRICK_SLAB", new ItemStack(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB));
        map.put("END_STONE_BRICK_SLAB", new ItemStack(Blocks.END_STONE_BRICK_SLAB));
        map.put("PURPUR_SLAB", new ItemStack(Blocks.PURPUR_SLAB));
        map.put("PRISMARINE_SLAB", new ItemStack(Blocks.PRISMARINE_SLAB));
        map.put("PRISMARINE_BRICK_SLAB", new ItemStack(Blocks.PRISMARINE_BRICK_SLAB));
        map.put("DARK_PRISMARINE_SLAB", new ItemStack(Blocks.DARK_PRISMARINE_SLAB));
        //? if >=1.20.3 {
        map.put("TUFF_SLAB", new ItemStack(Blocks.TUFF_SLAB));
        map.put("POLISHED_TUFF_SLAB", new ItemStack(Blocks.POLISHED_TUFF_SLAB));
        map.put("TUFF_BRICK_SLAB", new ItemStack(Blocks.TUFF_BRICK_SLAB));
        //? }
        map.put("CUT_COPPER_SLAB", new ItemStack(Blocks.CUT_COPPER_SLAB));
        map.put("EXPOSED_CUT_COPPER_SLAB", new ItemStack(Blocks.EXPOSED_CUT_COPPER_SLAB));
        map.put("WEATHERED_CUT_COPPER_SLAB", new ItemStack(Blocks.WEATHERED_CUT_COPPER_SLAB));
        map.put("OXIDIZED_CUT_COPPER_SLAB", new ItemStack(Blocks.OXIDIZED_CUT_COPPER_SLAB));
        map.put("COBBLED_DEEPSLATE_SLAB", new ItemStack(Blocks.COBBLED_DEEPSLATE_SLAB));
        map.put("POLISHED_DEEPSLATE_SLAB", new ItemStack(Blocks.POLISHED_DEEPSLATE_SLAB));
        map.put("DEEPSLATE_BRICK_SLAB", new ItemStack(Blocks.DEEPSLATE_BRICK_SLAB));
        map.put("DEEPSLATE_TILE_SLAB", new ItemStack(Blocks.DEEPSLATE_TILE_SLAB));

        // Медные блоки
        //? if >=1.20.3 {
        map.put("CHISELED_COPPER", new ItemStack(Blocks.CHISELED_COPPER));
        //? }
        map.put("COPPER_BLOCK", new ItemStack(Blocks.COPPER_BLOCK));
        //? if >=1.20.3 {
        map.put("COPPER_BULB", new ItemStack(Blocks.COPPER_BULB));
        map.put("COPPER_DOOR", new ItemStack(Blocks.COPPER_DOOR));
        map.put("COPPER_GRATE", new ItemStack(Blocks.COPPER_GRATE));
        map.put("COPPER_TRAPDOOR", new ItemStack(Blocks.COPPER_TRAPDOOR));
        //? }
        map.put("CUT_COPPER", new ItemStack(Blocks.CUT_COPPER));
        //? if >=1.20.3 {
        map.put("EXPOSED_CHISELED_COPPER", new ItemStack(Blocks.EXPOSED_CHISELED_COPPER));
        //? }
        map.put("EXPOSED_COPPER", new ItemStack(Blocks.EXPOSED_COPPER));
        //? if >=1.20.3 {
        map.put("EXPOSED_COPPER_BULB", new ItemStack(Blocks.EXPOSED_COPPER_BULB));
        map.put("EXPOSED_COPPER_DOOR", new ItemStack(Blocks.EXPOSED_COPPER_DOOR));
        map.put("EXPOSED_COPPER_GRATE", new ItemStack(Blocks.EXPOSED_COPPER_GRATE));
        //? }
        map.put("EXPOSED_CUT_COPPER", new ItemStack(Blocks.EXPOSED_CUT_COPPER));
        //? if >=1.20.3 {
        map.put("OXIDIZED_CHISELED_COPPER", new ItemStack(Blocks.OXIDIZED_CHISELED_COPPER));
        //? }
        map.put("OXIDIZED_COPPER", new ItemStack(Blocks.OXIDIZED_COPPER));
        //? if >=1.20.3 {
        map.put("OXIDIZED_COPPER_BULB", new ItemStack(Blocks.OXIDIZED_COPPER_BULB));
        map.put("OXIDIZED_COPPER_DOOR", new ItemStack(Blocks.OXIDIZED_COPPER_DOOR));
        map.put("OXIDIZED_COPPER_GRATE", new ItemStack(Blocks.OXIDIZED_COPPER_GRATE));
        //? }
        map.put("OXIDIZED_CUT_COPPER", new ItemStack(Blocks.OXIDIZED_CUT_COPPER));
        map.put("RAW_COPPER_BLOCK", new ItemStack(Blocks.RAW_COPPER_BLOCK));
        //? if >=1.20.3 {
        map.put("WEATHERED_CHISELED_COPPER", new ItemStack(Blocks.WEATHERED_CHISELED_COPPER));
        //? }
        map.put("WEATHERED_COPPER", new ItemStack(Blocks.WEATHERED_COPPER));
        //? if >=1.20.3 {
        map.put("WEATHERED_COPPER_BULB", new ItemStack(Blocks.WEATHERED_COPPER_BULB));
        map.put("WEATHERED_COPPER_DOOR", new ItemStack(Blocks.WEATHERED_COPPER_DOOR));
        map.put("WEATHERED_COPPER_GRATE", new ItemStack(Blocks.WEATHERED_COPPER_GRATE));
        //? }
        map.put("WEATHERED_CUT_COPPER", new ItemStack(Blocks.WEATHERED_CUT_COPPER));

        // Кварцевые блоки
        map.put("CHISELED_QUARTZ_BLOCK", new ItemStack(Blocks.CHISELED_QUARTZ_BLOCK));
        map.put("QUARTZ_BLOCK", new ItemStack(Blocks.QUARTZ_BLOCK));
        map.put("QUARTZ_BRICKS", new ItemStack(Blocks.QUARTZ_BRICKS));
        map.put("QUARTZ_PILLAR", new ItemStack(Blocks.QUARTZ_PILLAR));
        map.put("SMOOTH_QUARTZ", new ItemStack(Blocks.SMOOTH_QUARTZ));

        // Редстоун и механизмы
        map.put("ACTIVATOR_RAIL", new ItemStack(Blocks.ACTIVATOR_RAIL));
        map.put("BELL", new ItemStack(Blocks.BELL));
        map.put("BLAST_FURNACE", new ItemStack(Blocks.BLAST_FURNACE));
        map.put("BREWING_STAND", new ItemStack(Blocks.BREWING_STAND));
        map.put("CALIBRATED_SCULK_SENSOR", new ItemStack(Blocks.CALIBRATED_SCULK_SENSOR));
        map.put("CHEST", new ItemStack(Blocks.CHEST));
        map.put("COMPARATOR", new ItemStack(Blocks.COMPARATOR));
        //? if >=1.20.3 {
        map.put("CRAFTER", new ItemStack(Blocks.CRAFTER));
        //? }
        map.put("CRAFTING_TABLE", new ItemStack(Blocks.CRAFTING_TABLE));
        map.put("CRIMSON_BUTTON", new ItemStack(Blocks.CRIMSON_BUTTON));
        map.put("CRIMSON_PRESSURE_PLATE", new ItemStack(Blocks.CRIMSON_PRESSURE_PLATE));
        map.put("DAYLIGHT_DETECTOR", new ItemStack(Blocks.DAYLIGHT_DETECTOR));
        map.put("DETECTOR_RAIL", new ItemStack(Blocks.DETECTOR_RAIL));
        map.put("DISPENSER", new ItemStack(Blocks.DISPENSER));
        map.put("DROPPER", new ItemStack(Blocks.DROPPER));
        map.put("ENDER_CHEST", new ItemStack(Blocks.ENDER_CHEST));
        map.put("FURNACE", new ItemStack(Blocks.FURNACE));
        map.put("HEAVY_WEIGHTED_PRESSURE_PLATE", new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE));
        map.put("HOPPER", new ItemStack(Blocks.HOPPER));
        map.put("IRON_DOOR", new ItemStack(Blocks.IRON_DOOR));
        map.put("IRON_TRAPDOOR", new ItemStack(Blocks.IRON_TRAPDOOR));
        map.put("JUKEBOX", new ItemStack(Blocks.JUKEBOX));
        map.put("LECTERN", new ItemStack(Blocks.LECTERN));
        map.put("LEVER", new ItemStack(Blocks.LEVER));
        map.put("LIGHT_WEIGHTED_PRESSURE_PLATE", new ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE));
        map.put("NOTE_BLOCK", new ItemStack(Blocks.NOTE_BLOCK));
        map.put("OBSERVER", new ItemStack(Blocks.OBSERVER));
        map.put("PISTON", new ItemStack(Blocks.PISTON));
        map.put("POLISHED_BLACKSTONE_BUTTON", new ItemStack(Blocks.POLISHED_BLACKSTONE_BUTTON));
        map.put("POLISHED_BLACKSTONE_PRESSURE_PLATE", new ItemStack(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE));
        map.put("POWERED_RAIL", new ItemStack(Blocks.POWERED_RAIL));
        map.put("RAIL", new ItemStack(Blocks.RAIL));
        map.put("REDSTONE_BLOCK", new ItemStack(Blocks.REDSTONE_BLOCK));
        map.put("REDSTONE_LAMP", new ItemStack(Blocks.REDSTONE_LAMP));
        map.put("REDSTONE_TORCH", new ItemStack(Blocks.REDSTONE_TORCH));
        map.put("REDSTONE_WIRE", new ItemStack(Blocks.REDSTONE_WIRE));
        map.put("REPEATER", new ItemStack(Blocks.REPEATER));
        map.put("SCULK_SENSOR", new ItemStack(Blocks.SCULK_SENSOR));
        map.put("SCULK_SHRIEKER", new ItemStack(Blocks.SCULK_SHRIEKER));
        map.put("SMOKER", new ItemStack(Blocks.SMOKER));
        map.put("STICKY_PISTON", new ItemStack(Blocks.STICKY_PISTON));
        map.put("STONE_BUTTON", new ItemStack(Blocks.STONE_BUTTON));
        map.put("STONE_PRESSURE_PLATE", new ItemStack(Blocks.STONE_PRESSURE_PLATE));
        map.put("TARGET", new ItemStack(Blocks.TARGET));
        map.put("TNT", new ItemStack(Blocks.TNT));
        map.put("TRIPWIRE", new ItemStack(Blocks.TRIPWIRE));
        map.put("TRIPWIRE_HOOK", new ItemStack(Blocks.TRIPWIRE_HOOK));
        map.put("WARPED_BUTTON", new ItemStack(Blocks.WARPED_BUTTON));
        map.put("WARPED_PRESSURE_PLATE", new ItemStack(Blocks.WARPED_PRESSURE_PLATE));

        // Растения
        map.put("COBWEB", new ItemStack(Blocks.COBWEB));
        map.put("OAK_SAPLING", new ItemStack(Blocks.OAK_SAPLING));
        map.put("SPRUCE_SAPLING", new ItemStack(Blocks.SPRUCE_SAPLING));
        map.put("BIRCH_SAPLING", new ItemStack(Blocks.BIRCH_SAPLING));
        map.put("JUNGLE_SAPLING", new ItemStack(Blocks.JUNGLE_SAPLING));
        map.put("ACACIA_SAPLING", new ItemStack(Blocks.ACACIA_SAPLING));
        map.put("CHERRY_SAPLING", new ItemStack(Blocks.CHERRY_SAPLING));
        map.put("DARK_OAK_SAPLING", new ItemStack(Blocks.DARK_OAK_SAPLING));
        //? if >=1.21.2 {
        map.put("PALE_OAK_SAPLING", new ItemStack(Blocks.PALE_OAK_SAPLING));
        //?}
        map.put("MANGROVE_PROPAGULE", new ItemStack(Blocks.MANGROVE_PROPAGULE));
        //? if >=1.20.3 {
        map.put("SHORT_GRASS", new ItemStack(Blocks.SHORT_GRASS));
        //? }
        map.put("FERN", new ItemStack(Blocks.FERN));
        map.put("DEAD_BUSH", new ItemStack(Blocks.DEAD_BUSH));
        map.put("SEAGRASS", new ItemStack(Blocks.SEAGRASS));
        map.put("DANDELION", new ItemStack(Blocks.DANDELION));
        map.put("TORCHFLOWER", new ItemStack(Blocks.TORCHFLOWER));
        map.put("POPPY", new ItemStack(Blocks.POPPY));
        map.put("BLUE_ORCHID", new ItemStack(Blocks.BLUE_ORCHID));
        map.put("ALLIUM", new ItemStack(Blocks.ALLIUM));
        map.put("AZURE_BLUET", new ItemStack(Blocks.AZURE_BLUET));
        map.put("RED_TULIP", new ItemStack(Blocks.RED_TULIP));
        map.put("ORANGE_TULIP", new ItemStack(Blocks.ORANGE_TULIP));
        map.put("WHITE_TULIP", new ItemStack(Blocks.WHITE_TULIP));
        map.put("PINK_TULIP", new ItemStack(Blocks.PINK_TULIP));
        map.put("OXEYE_DAISY", new ItemStack(Blocks.OXEYE_DAISY));
        map.put("CORNFLOWER", new ItemStack(Blocks.CORNFLOWER));
        map.put("WITHER_ROSE", new ItemStack(Blocks.WITHER_ROSE));
        map.put("LILY_OF_THE_VALLEY", new ItemStack(Blocks.LILY_OF_THE_VALLEY));
        map.put("BROWN_MUSHROOM", new ItemStack(Blocks.BROWN_MUSHROOM));
        map.put("RED_MUSHROOM", new ItemStack(Blocks.RED_MUSHROOM));
        map.put("OAK_LEAVES", new ItemStack(Blocks.OAK_LEAVES));
        map.put("SPRUCE_LEAVES", new ItemStack(Blocks.SPRUCE_LEAVES));
        map.put("BIRCH_LEAVES", new ItemStack(Blocks.BIRCH_LEAVES));
        map.put("JUNGLE_LEAVES", new ItemStack(Blocks.JUNGLE_LEAVES));
        map.put("ACACIA_LEAVES", new ItemStack(Blocks.ACACIA_LEAVES));
        map.put("CHERRY_LEAVES", new ItemStack(Blocks.CHERRY_LEAVES));
        map.put("DARK_OAK_LEAVES", new ItemStack(Blocks.DARK_OAK_LEAVES));
        //? if >=1.21.2 {
        map.put("PALE_OAK_LEAVES", new ItemStack(Blocks.PALE_OAK_LEAVES));
        //?}
        map.put("MANGROVE_LEAVES", new ItemStack(Blocks.MANGROVE_LEAVES));
        map.put("AZALEA_LEAVES", new ItemStack(Blocks.AZALEA_LEAVES));
        map.put("FLOWERING_AZALEA_LEAVES", new ItemStack(Blocks.FLOWERING_AZALEA_LEAVES));
        map.put("BROWN_MUSHROOM_BLOCK", new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK));
        map.put("RED_MUSHROOM_BLOCK", new ItemStack(Blocks.RED_MUSHROOM_BLOCK));
        map.put("MUSHROOM_STEM", new ItemStack(Blocks.MUSHROOM_STEM));
        map.put("SUNFLOWER", new ItemStack(Blocks.SUNFLOWER));
        map.put("LILAC", new ItemStack(Blocks.LILAC));
        map.put("ROSE_BUSH", new ItemStack(Blocks.ROSE_BUSH));
        map.put("PEONY", new ItemStack(Blocks.PEONY));
        map.put("TALL_GRASS", new ItemStack(Blocks.TALL_GRASS));
        map.put("LARGE_FERN", new ItemStack(Blocks.LARGE_FERN));
        map.put("TORCHFLOWER_CROP", new ItemStack(Blocks.TORCHFLOWER_CROP));
        map.put("PITCHER_CROP", new ItemStack(Blocks.PITCHER_CROP));
        map.put("PITCHER_PLANT", new ItemStack(Blocks.PITCHER_PLANT));
        map.put("BEETROOTS", new ItemStack(Blocks.BEETROOTS));
        map.put("WARPED_FUNGUS", new ItemStack(Blocks.WARPED_FUNGUS));
        map.put("WARPED_WART_BLOCK", new ItemStack(Blocks.WARPED_WART_BLOCK));
        map.put("WARPED_ROOTS", new ItemStack(Blocks.WARPED_ROOTS));
        map.put("NETHER_SPROUTS", new ItemStack(Blocks.NETHER_SPROUTS));
        map.put("CRIMSON_FUNGUS", new ItemStack(Blocks.CRIMSON_FUNGUS));
        map.put("SHROOMLIGHT", new ItemStack(Blocks.SHROOMLIGHT));
        map.put("WEEPING_VINES", new ItemStack(Blocks.WEEPING_VINES));
        map.put("TWISTING_VINES", new ItemStack(Blocks.TWISTING_VINES));
        map.put("CRIMSON_ROOTS", new ItemStack(Blocks.CRIMSON_ROOTS));
        map.put("POINTED_DRIPSTONE", new ItemStack(Blocks.POINTED_DRIPSTONE));
        map.put("DRIPSTONE_BLOCK", new ItemStack(Blocks.DRIPSTONE_BLOCK));
        map.put("SPORE_BLOSSOM", new ItemStack(Blocks.SPORE_BLOSSOM));
        map.put("AZALEA", new ItemStack(Blocks.AZALEA));
        map.put("FLOWERING_AZALEA", new ItemStack(Blocks.FLOWERING_AZALEA));
        map.put("MOSS_CARPET", new ItemStack(Blocks.MOSS_CARPET));
        map.put("PINK_PETALS", new ItemStack(Blocks.PINK_PETALS));
        map.put("MOSS_BLOCK", new ItemStack(Blocks.MOSS_BLOCK));
        map.put("BIG_DRIPLEAF", new ItemStack(Blocks.BIG_DRIPLEAF));
        map.put("SMALL_DRIPLEAF", new ItemStack(Blocks.SMALL_DRIPLEAF));
        map.put("HANGING_ROOTS", new ItemStack(Blocks.HANGING_ROOTS));
        //? if >=1.21.2 {
        map.put("PALE_MOSS_BLOCK", new ItemStack(Blocks.PALE_MOSS_BLOCK));
        map.put("PALE_MOSS_CARPET", new ItemStack(Blocks.PALE_MOSS_CARPET));
        map.put("PALE_HANGING_MOSS", new ItemStack(Blocks.PALE_HANGING_MOSS));
        //?}
        map.put("VINE", new ItemStack(Blocks.VINE));
        map.put("GLOW_LICHEN", new ItemStack(Blocks.GLOW_LICHEN));
        map.put("BAMBOO", new ItemStack(Blocks.BAMBOO));
        map.put("CACTUS", new ItemStack(Blocks.CACTUS));
        map.put("COCOA", new ItemStack(Blocks.COCOA));
        map.put("CRIMSON_NYLIUM", new ItemStack(Blocks.CRIMSON_NYLIUM));
        map.put("MYCELIUM", new ItemStack(Blocks.MYCELIUM));
        map.put("NETHER_WART", new ItemStack(Blocks.NETHER_WART));
        map.put("NETHER_WART_BLOCK", new ItemStack(Blocks.NETHER_WART_BLOCK));
        map.put("SUGAR_CANE", new ItemStack(Blocks.SUGAR_CANE));
        map.put("WARPED_NYLIUM", new ItemStack(Blocks.WARPED_NYLIUM));

        // --- Кораллы и морские предметы ---
        map.put("BRAIN_CORAL", new ItemStack(Blocks.BRAIN_CORAL));
        map.put("BRAIN_CORAL_BLOCK", new ItemStack(Blocks.BRAIN_CORAL_BLOCK));
        map.put("BRAIN_CORAL_FAN", new ItemStack(Blocks.BRAIN_CORAL_FAN));
        map.put("BRAIN_CORAL_WALL_FAN", new ItemStack(Blocks.BRAIN_CORAL_WALL_FAN));
        map.put("BUBBLE_CORAL", new ItemStack(Blocks.BUBBLE_CORAL));
        map.put("BUBBLE_CORAL_BLOCK", new ItemStack(Blocks.BUBBLE_CORAL_BLOCK));
        map.put("BUBBLE_CORAL_FAN", new ItemStack(Blocks.BUBBLE_CORAL_FAN));
        map.put("BUBBLE_CORAL_WALL_FAN", new ItemStack(Blocks.BUBBLE_CORAL_WALL_FAN));
        map.put("FIRE_CORAL", new ItemStack(Blocks.FIRE_CORAL));
        map.put("FIRE_CORAL_BLOCK", new ItemStack(Blocks.FIRE_CORAL_BLOCK));
        map.put("FIRE_CORAL_FAN", new ItemStack(Blocks.FIRE_CORAL_FAN));
        map.put("FIRE_CORAL_WALL_FAN", new ItemStack(Blocks.FIRE_CORAL_WALL_FAN));
        map.put("HORN_CORAL", new ItemStack(Blocks.HORN_CORAL));
        map.put("HORN_CORAL_BLOCK", new ItemStack(Blocks.HORN_CORAL_BLOCK));
        map.put("HORN_CORAL_FAN", new ItemStack(Blocks.HORN_CORAL_FAN));
        map.put("HORN_CORAL_WALL_FAN", new ItemStack(Blocks.HORN_CORAL_WALL_FAN));
        map.put("TUBE_CORAL", new ItemStack(Blocks.TUBE_CORAL));
        map.put("TUBE_CORAL_BLOCK", new ItemStack(Blocks.TUBE_CORAL_BLOCK));
        map.put("TUBE_CORAL_FAN", new ItemStack(Blocks.TUBE_CORAL_FAN));
        map.put("TUBE_CORAL_WALL_FAN", new ItemStack(Blocks.TUBE_CORAL_WALL_FAN));
        map.put("SEA_PICKLE", new ItemStack(Blocks.SEA_PICKLE));
        map.put("KELP", new ItemStack(Blocks.KELP));
        map.put("DRIED_KELP_BLOCK", new ItemStack(Blocks.DRIED_KELP_BLOCK));
        map.put("LILY_PAD", new ItemStack(Blocks.LILY_PAD));
        map.put("PRISMARINE_SHARD", new ItemStack(Items.PRISMARINE_SHARD));
        map.put("PRISMARINE_CRYSTALS", new ItemStack(Items.PRISMARINE_CRYSTALS));
        map.put("TURTLE_EGG", new ItemStack(Blocks.TURTLE_EGG));
        //? if >1.20.4 {
        map.put("TURTLE_SCUTE", new ItemStack(Items.TURTLE_SCUTE));
        //?}

        // --- Предметы из Энда ---
        map.put("CHORUS_PLANT", new ItemStack(Blocks.CHORUS_PLANT));
        map.put("CHORUS_FLOWER", new ItemStack(Blocks.CHORUS_FLOWER));
        map.put("ENDER_PEARL", new ItemStack(Items.ENDER_PEARL));
        map.put("ENDER_EYE", new ItemStack(Items.ENDER_EYE));
        map.put("END_ROD", new ItemStack(Blocks.END_ROD));

        // --- Источники света ---
        map.put("TORCH", new ItemStack(Blocks.TORCH));
        map.put("SOUL_TORCH", new ItemStack(Blocks.SOUL_TORCH));
        map.put("LANTERN", new ItemStack(Blocks.LANTERN));
        map.put("SOUL_LANTERN", new ItemStack(Blocks.SOUL_LANTERN));
        map.put("CAMPFIRE", new ItemStack(Blocks.CAMPFIRE));
        map.put("SOUL_CAMPFIRE", new ItemStack(Blocks.SOUL_CAMPFIRE));
        map.put("GLOWSTONE", new ItemStack(Blocks.GLOWSTONE));
        map.put("GLOWSTONE_DUST", new ItemStack(Items.GLOWSTONE_DUST));
        map.put("OCHRE_FROGLIGHT", new ItemStack(Blocks.OCHRE_FROGLIGHT));
        map.put("PEARLESCENT_FROGLIGHT", new ItemStack(Blocks.PEARLESCENT_FROGLIGHT));
        map.put("VERDANT_FROGLIGHT", new ItemStack(Blocks.VERDANT_FROGLIGHT));

        // --- Моб-дропы и связанные предметы ---
        //? if >1.20.4 {
        map.put("ARMADILLO_SCUTE", new ItemStack(Items.ARMADILLO_SCUTE));
        //?}
        map.put("BLAZE_ROD", new ItemStack(Items.BLAZE_ROD));
        map.put("BLAZE_POWDER", new ItemStack(Items.BLAZE_POWDER));
        //? if >1.20.4 {
        map.put("BREEZE_ROD", new ItemStack(Items.BREEZE_ROD));
        //?}
        map.put("FEATHER", new ItemStack(Items.FEATHER));
        map.put("GHAST_TEAR", new ItemStack(Items.GHAST_TEAR));
        map.put("GUNPOWDER", new ItemStack(Items.GUNPOWDER));
        map.put("MAGMA_CREAM", new ItemStack(Items.MAGMA_CREAM));
        map.put("PHANTOM_MEMBRANE", new ItemStack(Items.PHANTOM_MEMBRANE));
        map.put("SLIME_BALL", new ItemStack(Items.SLIME_BALL));
        map.put("STRING", new ItemStack(Items.STRING));

        // --- Книги и карты ---
        map.put("BOOK", new ItemStack(Items.BOOK));
        map.put("WRITABLE_BOOK", new ItemStack(Items.WRITABLE_BOOK));
        map.put("WRITTEN_BOOK", new ItemStack(Items.WRITTEN_BOOK));
        map.put("MAP", new ItemStack(Items.MAP));
        map.put("FILLED_MAP", new ItemStack(Items.FILLED_MAP));

        // --- Блоки и предметы, связанные со Скалком (Sculk) ---
        map.put("SCULK", new ItemStack(Blocks.SCULK));
        map.put("SCULK_VEIN", new ItemStack(Blocks.SCULK_VEIN));
        map.put("SCULK_CATALYST", new ItemStack(Blocks.SCULK_CATALYST));

        // --- Остальные предметы и блоки ---
        map.put("BEEHIVE", new ItemStack(Blocks.BEEHIVE));
        map.put("BEE_NEST", new ItemStack(Blocks.BEE_NEST));
        map.put("BRICK", new ItemStack(Items.BRICK));
        map.put("CAULDRON", new ItemStack(Blocks.CAULDRON));
        map.put("CHAIN", new ItemStack(Blocks.CHAIN));
        map.put("CLAY_BALL", new ItemStack(Items.CLAY_BALL));
        //? if >=1.21.2 {
        map.put("CREAKING_HEART", new ItemStack(Blocks.CREAKING_HEART));
        //?}
        map.put("FIREWORK_ROCKET", new ItemStack(Items.FIREWORK_ROCKET));
        map.put("FIREWORK_STAR", new ItemStack(Items.FIREWORK_STAR));
        map.put("FIRE_CHARGE", new ItemStack(Items.FIRE_CHARGE));
        map.put("FLINT", new ItemStack(Items.FLINT));
        map.put("FLINT_AND_STEEL", new ItemStack(Items.FLINT_AND_STEEL));
        map.put("FLOWER_POT", new ItemStack(Blocks.FLOWER_POT));
        map.put("FROGSPAWN", new ItemStack(Blocks.FROGSPAWN));
        map.put("ITEM_FRAME", new ItemStack(Items.ITEM_FRAME));
        map.put("GLOW_ITEM_FRAME", new ItemStack(Items.GLOW_ITEM_FRAME));
        map.put("GOLD_NUGGET", new ItemStack(Items.GOLD_NUGGET));
        map.put("HAY_BLOCK", new ItemStack(Blocks.HAY_BLOCK));
        map.put("HONEY_BLOCK", new ItemStack(Blocks.HONEY_BLOCK));
        map.put("HONEYCOMB_BLOCK", new ItemStack(Blocks.HONEYCOMB_BLOCK));
        map.put("IRON_BARS", new ItemStack(Blocks.IRON_BARS));
        map.put("IRON_NUGGET", new ItemStack(Items.IRON_NUGGET));
        map.put("LADDER", new ItemStack(Blocks.LADDER));
        map.put("LIGHTNING_ROD", new ItemStack(Blocks.LIGHTNING_ROD));
        map.put("NETHER_BRICK", new ItemStack(Items.NETHER_BRICK));
        map.put("PAINTING", new ItemStack(Items.PAINTING));
        map.put("PAPER", new ItemStack(Items.PAPER));
        map.put("SLIME_BLOCK", new ItemStack(Blocks.SLIME_BLOCK));
        map.put("SNOW", new ItemStack(Blocks.SNOW));
        map.put("SNOWBALL", new ItemStack(Items.SNOWBALL));

        return map;
    }

    private static Map<String, ItemStack> cat_TOOLS() {
        Map<String, ItemStack> map = new LinkedHashMap<>();
        map.put("WOODEN_SWORD", new ItemStack(Items.WOODEN_SWORD));
        map.put("WOODEN_SHOVEL", new ItemStack(Items.WOODEN_SHOVEL));
        map.put("WOODEN_PICKAXE", new ItemStack(Items.WOODEN_PICKAXE));
        map.put("WOODEN_AXE", new ItemStack(Items.WOODEN_AXE));
        map.put("WOODEN_HOE", new ItemStack(Items.WOODEN_HOE));
        map.put("STONE_SWORD", new ItemStack(Items.STONE_SWORD));
        map.put("STONE_SHOVEL", new ItemStack(Items.STONE_SHOVEL));
        map.put("STONE_PICKAXE", new ItemStack(Items.STONE_PICKAXE));
        map.put("STONE_AXE", new ItemStack(Items.STONE_AXE));
        map.put("STONE_HOE", new ItemStack(Items.STONE_HOE));
        map.put("GOLDEN_SWORD", new ItemStack(Items.GOLDEN_SWORD));
        map.put("GOLDEN_SHOVEL", new ItemStack(Items.GOLDEN_SHOVEL));
        map.put("GOLDEN_PICKAXE", new ItemStack(Items.GOLDEN_PICKAXE));
        map.put("GOLDEN_AXE", new ItemStack(Items.GOLDEN_AXE));
        map.put("GOLDEN_HOE", new ItemStack(Items.GOLDEN_HOE));
        map.put("IRON_SWORD", new ItemStack(Items.IRON_SWORD));
        map.put("IRON_SHOVEL", new ItemStack(Items.IRON_SHOVEL));
        map.put("IRON_PICKAXE", new ItemStack(Items.IRON_PICKAXE));
        map.put("IRON_AXE", new ItemStack(Items.IRON_AXE));
        map.put("IRON_HOE", new ItemStack(Items.IRON_HOE));
        map.put("DIAMOND_SWORD", new ItemStack(Items.DIAMOND_SWORD));
        map.put("DIAMOND_SHOVEL", new ItemStack(Items.DIAMOND_SHOVEL));
        map.put("DIAMOND_PICKAXE", new ItemStack(Items.DIAMOND_PICKAXE));
        map.put("DIAMOND_AXE", new ItemStack(Items.DIAMOND_AXE));
        map.put("DIAMOND_HOE", new ItemStack(Items.DIAMOND_HOE));
        map.put("NETHERITE_SWORD", new ItemStack(Items.NETHERITE_SWORD));
        map.put("NETHERITE_SHOVEL", new ItemStack(Items.NETHERITE_SHOVEL));
        map.put("NETHERITE_PICKAXE", new ItemStack(Items.NETHERITE_PICKAXE));
        map.put("NETHERITE_AXE", new ItemStack(Items.NETHERITE_AXE));
        map.put("NETHERITE_HOE", new ItemStack(Items.NETHERITE_HOE));
        //? if >1.20.4 {
        map.put("MACE", new ItemStack(Items.MACE));
        //?}
        map.put("STICK", new ItemStack(Items.STICK));
        map.put("BOW", new ItemStack(Items.BOW));
        map.put("ARROW", new ItemStack(Items.ARROW));
        map.put("SPECTRAL_ARROW", new ItemStack(Items.SPECTRAL_ARROW));
        map.put("TIPPED_ARROW", new ItemStack(Items.TIPPED_ARROW));
        map.put("ARMOR_STAND", new ItemStack(Items.ARMOR_STAND));
        map.put("IRON_HORSE_ARMOR", new ItemStack(Items.IRON_HORSE_ARMOR));
        map.put("GOLDEN_HORSE_ARMOR", new ItemStack(Items.GOLDEN_HORSE_ARMOR));
        map.put("DIAMOND_HORSE_ARMOR", new ItemStack(Items.DIAMOND_HORSE_ARMOR));
        map.put("LEATHER_HORSE_ARMOR", new ItemStack(Items.LEATHER_HORSE_ARMOR));
        //? if >1.20.4 {
        map.put("WOLF_ARMOR", new ItemStack(Items.WOLF_ARMOR));
        //?}
        map.put("TURTLE_HELMET", new ItemStack(Items.TURTLE_HELMET));
        map.put("LEATHER_HELMET", new ItemStack(Items.LEATHER_HELMET));
        map.put("LEATHER_CHESTPLATE", new ItemStack(Items.LEATHER_CHESTPLATE));
        map.put("LEATHER_LEGGINGS", new ItemStack(Items.LEATHER_LEGGINGS));
        map.put("LEATHER_BOOTS", new ItemStack(Items.LEATHER_BOOTS));
        map.put("CHAINMAIL_HELMET", new ItemStack(Items.CHAINMAIL_HELMET));
        map.put("CHAINMAIL_CHESTPLATE", new ItemStack(Items.CHAINMAIL_CHESTPLATE));
        map.put("CHAINMAIL_LEGGINGS", new ItemStack(Items.CHAINMAIL_LEGGINGS));
        map.put("CHAINMAIL_BOOTS", new ItemStack(Items.CHAINMAIL_BOOTS));
        map.put("IRON_HELMET", new ItemStack(Items.IRON_HELMET));
        map.put("IRON_CHESTPLATE", new ItemStack(Items.IRON_CHESTPLATE));
        map.put("IRON_LEGGINGS", new ItemStack(Items.IRON_LEGGINGS));
        map.put("IRON_BOOTS", new ItemStack(Items.IRON_BOOTS));
        map.put("DIAMOND_HELMET", new ItemStack(Items.DIAMOND_HELMET));
        map.put("DIAMOND_CHESTPLATE", new ItemStack(Items.DIAMOND_CHESTPLATE));
        map.put("DIAMOND_LEGGINGS", new ItemStack(Items.DIAMOND_LEGGINGS));
        map.put("DIAMOND_BOOTS", new ItemStack(Items.DIAMOND_BOOTS));
        map.put("GOLDEN_HELMET", new ItemStack(Items.GOLDEN_HELMET));
        map.put("GOLDEN_CHESTPLATE", new ItemStack(Items.GOLDEN_CHESTPLATE));
        map.put("GOLDEN_LEGGINGS", new ItemStack(Items.GOLDEN_LEGGINGS));
        map.put("GOLDEN_BOOTS", new ItemStack(Items.GOLDEN_BOOTS));
        map.put("NETHERITE_HELMET", new ItemStack(Items.NETHERITE_HELMET));
        map.put("NETHERITE_CHESTPLATE", new ItemStack(Items.NETHERITE_CHESTPLATE));
        map.put("NETHERITE_LEGGINGS", new ItemStack(Items.NETHERITE_LEGGINGS));
        map.put("NETHERITE_BOOTS", new ItemStack(Items.NETHERITE_BOOTS));
        map.put("TRIDENT", new ItemStack(Items.TRIDENT));
        map.put("CROSSBOW", new ItemStack(Items.CROSSBOW));
        map.put("OAK_BOAT", new ItemStack(Items.OAK_BOAT));
        map.put("SPRUCE_BOAT", new ItemStack(Items.SPRUCE_BOAT));
        map.put("BIRCH_BOAT", new ItemStack(Items.BIRCH_BOAT));
        map.put("JUNGLE_BOAT", new ItemStack(Items.JUNGLE_BOAT));
        map.put("ACACIA_BOAT", new ItemStack(Items.ACACIA_BOAT));
        map.put("CHERRY_BOAT", new ItemStack(Items.CHERRY_BOAT));
        map.put("DARK_OAK_BOAT", new ItemStack(Items.DARK_OAK_BOAT));
        //? if >=1.21.2 {
        map.put("PALE_OAK_BOAT", new ItemStack(Items.PALE_OAK_BOAT));
        //?}
        map.put("MANGROVE_BOAT", new ItemStack(Items.MANGROVE_BOAT));
        map.put("BAMBOO_RAFT", new ItemStack(Items.BAMBOO_RAFT));
        map.put("OAK_CHEST_BOAT", new ItemStack(Items.OAK_CHEST_BOAT));
        map.put("SPRUCE_CHEST_BOAT", new ItemStack(Items.SPRUCE_CHEST_BOAT));
        map.put("BIRCH_CHEST_BOAT", new ItemStack(Items.BIRCH_CHEST_BOAT));
        map.put("JUNGLE_CHEST_BOAT", new ItemStack(Items.JUNGLE_CHEST_BOAT));
        map.put("ACACIA_CHEST_BOAT", new ItemStack(Items.ACACIA_CHEST_BOAT));
        map.put("CHERRY_CHEST_BOAT", new ItemStack(Items.CHERRY_CHEST_BOAT));
        map.put("DARK_OAK_CHEST_BOAT", new ItemStack(Items.DARK_OAK_CHEST_BOAT));
        //? if >=1.21.2 {
        map.put("PALE_OAK_CHEST_BOAT", new ItemStack(Items.PALE_OAK_CHEST_BOAT));
        //?}
        map.put("MANGROVE_CHEST_BOAT", new ItemStack(Items.MANGROVE_CHEST_BOAT));
        map.put("BAMBOO_CHEST_RAFT", new ItemStack(Items.BAMBOO_CHEST_RAFT));
        map.put("SADDLE", new ItemStack(Items.SADDLE));
        map.put("MINECART", new ItemStack(Items.MINECART));
        map.put("CHEST_MINECART", new ItemStack(Items.CHEST_MINECART));
        map.put("FURNACE_MINECART", new ItemStack(Items.FURNACE_MINECART));
        map.put("TNT_MINECART", new ItemStack(Items.TNT_MINECART));
        map.put("HOPPER_MINECART", new ItemStack(Items.HOPPER_MINECART));
        map.put("COMMAND_BLOCK_MINECART", new ItemStack(Items.COMMAND_BLOCK_MINECART));
        map.put("CARROT_ON_A_STICK", new ItemStack(Items.CARROT_ON_A_STICK));
        map.put("WARPED_FUNGUS_ON_A_STICK", new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
        map.put("ELYTRA", new ItemStack(Items.ELYTRA));
        map.put("COMPASS", new ItemStack(Items.COMPASS));
        map.put("RECOVERY_COMPASS", new ItemStack(Items.RECOVERY_COMPASS));
        map.put("FISHING_ROD", new ItemStack(Items.FISHING_ROD));
        map.put("CLOCK", new ItemStack(Items.CLOCK));
        map.put("SPYGLASS", new ItemStack(Items.SPYGLASS));
        map.put("SHEARS", new ItemStack(Items.SHEARS));
        map.put("LEAD", new ItemStack(Items.LEAD));
        map.put("NAME_TAG", new ItemStack(Items.NAME_TAG));
        map.put("SHIELD", new ItemStack(Items.SHIELD));
        map.put("TOTEM_OF_UNDYING", new ItemStack(Items.TOTEM_OF_UNDYING));
        map.put("GOAT_HORN", new ItemStack(Items.GOAT_HORN));
        map.put("BRUSH", new ItemStack(Items.BRUSH));
        map.put("BUCKET", new ItemStack(Items.BUCKET));
        map.put("WATER_BUCKET", new ItemStack(Items.WATER_BUCKET));
        map.put("LAVA_BUCKET", new ItemStack(Items.LAVA_BUCKET));
        map.put("POWDER_SNOW_BUCKET", new ItemStack(Items.POWDER_SNOW_BUCKET));
        map.put("MILK_BUCKET", new ItemStack(Items.MILK_BUCKET));
        map.put("PUFFERFISH_BUCKET", new ItemStack(Items.PUFFERFISH_BUCKET));
        map.put("SALMON_BUCKET", new ItemStack(Items.SALMON_BUCKET));
        map.put("COD_BUCKET", new ItemStack(Items.COD_BUCKET));
        map.put("TROPICAL_FISH_BUCKET", new ItemStack(Items.TROPICAL_FISH_BUCKET));
        map.put("AXOLOTL_BUCKET", new ItemStack(Items.AXOLOTL_BUCKET));
        map.put("TADPOLE_BUCKET", new ItemStack(Items.TADPOLE_BUCKET));
        //? if >=1.20.3 {
        map.put("TRIAL_KEY", new ItemStack(Items.TRIAL_KEY));
        //? }
        //? if >1.20.4 {
        map.put("OMINOUS_TRIAL_KEY", new ItemStack(Items.OMINOUS_TRIAL_KEY));
        //?}
        return map;
    }

    private static Map<String, ItemStack> cat_FOODS() {
        Map<String, ItemStack> map = new LinkedHashMap<>();
        map.put("APPLE", new ItemStack(Items.APPLE));
        map.put("LEATHER", new ItemStack(Items.LEATHER));
        map.put("MUSHROOM_STEW", new ItemStack(Items.MUSHROOM_STEW));
        map.put("WHEAT_SEEDS", new ItemStack(Items.WHEAT_SEEDS));
        map.put("BREAD", new ItemStack(Items.BREAD));
        map.put("PORKCHOP", new ItemStack(Items.PORKCHOP));
        map.put("COOKED_PORKCHOP", new ItemStack(Items.COOKED_PORKCHOP));
        map.put("GOLDEN_APPLE", new ItemStack(Items.GOLDEN_APPLE));
        map.put("ENCHANTED_GOLDEN_APPLE", new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
        map.put("EGG", new ItemStack(Items.EGG));
        map.put("COD", new ItemStack(Items.COD));
        map.put("SALMON", new ItemStack(Items.SALMON));
        map.put("PUFFERFISH", new ItemStack(Items.PUFFERFISH));
        map.put("COOKED_COD", new ItemStack(Items.COOKED_COD));
        map.put("COOKED_SALMON", new ItemStack(Items.COOKED_SALMON));
        map.put("TROPICAL_FISH", new ItemStack(Items.TROPICAL_FISH));
        map.put("BONE_MEAL", new ItemStack(Items.BONE_MEAL));
        map.put("BONE", new ItemStack(Items.BONE));
        map.put("SUGAR", new ItemStack(Items.SUGAR));
        map.put("COOKIE", new ItemStack(Items.COOKIE));
        map.put("MELON_SLICE", new ItemStack(Items.MELON_SLICE));
        map.put("DRIED_KELP", new ItemStack(Items.DRIED_KELP));
        map.put("PUMPKIN_SEEDS", new ItemStack(Items.PUMPKIN_SEEDS));
        map.put("MELON_SEEDS", new ItemStack(Items.MELON_SEEDS));
        map.put("BEEF", new ItemStack(Items.BEEF));
        map.put("COOKED_BEEF", new ItemStack(Items.COOKED_BEEF));
        map.put("CHICKEN", new ItemStack(Items.CHICKEN));
        map.put("COOKED_CHICKEN", new ItemStack(Items.COOKED_CHICKEN));
        map.put("ROTTEN_FLESH", new ItemStack(Items.ROTTEN_FLESH));
        map.put("GLISTERING_MELON_SLICE", new ItemStack(Items.GLISTERING_MELON_SLICE));
        map.put("CARROT", new ItemStack(Items.CARROT));
        map.put("POTATO", new ItemStack(Items.POTATO));
        map.put("BAKED_POTATO", new ItemStack(Items.BAKED_POTATO));
        map.put("POISONOUS_POTATO", new ItemStack(Items.POISONOUS_POTATO));
        map.put("SPIDER_EYE", new ItemStack(Items.SPIDER_EYE));
        map.put("FERMENTED_SPIDER_EYE", new ItemStack(Items.FERMENTED_SPIDER_EYE));
        map.put("GOLDEN_CARROT", new ItemStack(Items.GOLDEN_CARROT));
        map.put("RABBIT", new ItemStack(Items.RABBIT));
        map.put("COOKED_RABBIT", new ItemStack(Items.COOKED_RABBIT));
        map.put("RABBIT_STEW", new ItemStack(Items.RABBIT_STEW));
        map.put("RABBIT_FOOT", new ItemStack(Items.RABBIT_FOOT));
        map.put("RABBIT_HIDE", new ItemStack(Items.RABBIT_HIDE));
        map.put("MUTTON", new ItemStack(Items.MUTTON));
        map.put("COOKED_MUTTON", new ItemStack(Items.COOKED_MUTTON));
        map.put("CHORUS_FRUIT", new ItemStack(Items.CHORUS_FRUIT));
        map.put("POPPED_CHORUS_FRUIT", new ItemStack(Items.POPPED_CHORUS_FRUIT));
        map.put("TORCHFLOWER_SEEDS", new ItemStack(Items.TORCHFLOWER_SEEDS));
        map.put("PITCHER_POD", new ItemStack(Items.PITCHER_POD));
        map.put("BEETROOT", new ItemStack(Items.BEETROOT));
        map.put("BEETROOT_SEEDS", new ItemStack(Items.BEETROOT_SEEDS));
        map.put("BEETROOT_SOUP", new ItemStack(Items.BEETROOT_SOUP));
        map.put("SUSPICIOUS_STEW", new ItemStack(Items.SUSPICIOUS_STEW));
        map.put("SWEET_BERRIES", new ItemStack(Items.SWEET_BERRIES));
        map.put("GLOW_BERRIES", new ItemStack(Items.GLOW_BERRIES));
        map.put("HONEYCOMB", new ItemStack(Items.HONEYCOMB));
        map.put("HONEY_BOTTLE", new ItemStack(Items.HONEY_BOTTLE));
        map.put("PUMPKIN_PIE", new ItemStack(Items.PUMPKIN_PIE));
        map.put("PUMPKIN", new ItemStack(Blocks.PUMPKIN));
        map.put("MELON", new ItemStack(Blocks.MELON));
        map.put("CARVED_PUMPKIN", new ItemStack(Blocks.CARVED_PUMPKIN));
        map.put("JACK_O_LANTERN", new ItemStack(Blocks.JACK_O_LANTERN));
        map.put("CAKE", new ItemStack(Blocks.CAKE));
        return map;
    }

    private static Map<String, ItemStack> cat_COLORED() {
        Map<String, ItemStack> map = new LinkedHashMap<>();
        // Баннера
        map.put("WHITE_BANNER", new ItemStack(Blocks.WHITE_BANNER));
        map.put("ORANGE_BANNER", new ItemStack(Blocks.ORANGE_BANNER));
        map.put("MAGENTA_BANNER", new ItemStack(Blocks.MAGENTA_BANNER));
        map.put("LIGHT_BLUE_BANNER", new ItemStack(Blocks.LIGHT_BLUE_BANNER));
        map.put("YELLOW_BANNER", new ItemStack(Blocks.YELLOW_BANNER));
        map.put("LIME_BANNER", new ItemStack(Blocks.LIME_BANNER));
        map.put("PINK_BANNER", new ItemStack(Blocks.PINK_BANNER));
        map.put("GRAY_BANNER", new ItemStack(Blocks.GRAY_BANNER));
        map.put("LIGHT_GRAY_BANNER", new ItemStack(Blocks.LIGHT_GRAY_BANNER));
        map.put("CYAN_BANNER", new ItemStack(Blocks.CYAN_BANNER));
        map.put("PURPLE_BANNER", new ItemStack(Blocks.PURPLE_BANNER));
        map.put("BLUE_BANNER", new ItemStack(Blocks.BLUE_BANNER));
        map.put("BROWN_BANNER", new ItemStack(Blocks.BROWN_BANNER));
        map.put("GREEN_BANNER", new ItemStack(Blocks.GREEN_BANNER));
        map.put("RED_BANNER", new ItemStack(Blocks.RED_BANNER));
        map.put("BLACK_BANNER", new ItemStack(Blocks.BLACK_BANNER));

        // Стекло
        map.put("GLASS", new ItemStack(Blocks.GLASS));
        map.put("WHITE_STAINED_GLASS", new ItemStack(Blocks.WHITE_STAINED_GLASS));
        map.put("ORANGE_STAINED_GLASS", new ItemStack(Blocks.ORANGE_STAINED_GLASS));
        map.put("MAGENTA_STAINED_GLASS", new ItemStack(Blocks.MAGENTA_STAINED_GLASS));
        map.put("LIGHT_BLUE_STAINED_GLASS", new ItemStack(Blocks.LIGHT_BLUE_STAINED_GLASS));
        map.put("YELLOW_STAINED_GLASS", new ItemStack(Blocks.YELLOW_STAINED_GLASS));
        map.put("LIME_STAINED_GLASS", new ItemStack(Blocks.LIME_STAINED_GLASS));
        map.put("PINK_STAINED_GLASS", new ItemStack(Blocks.PINK_STAINED_GLASS));
        map.put("GRAY_STAINED_GLASS", new ItemStack(Blocks.GRAY_STAINED_GLASS));
        map.put("LIGHT_GRAY_STAINED_GLASS", new ItemStack(Blocks.LIGHT_GRAY_STAINED_GLASS));
        map.put("CYAN_STAINED_GLASS", new ItemStack(Blocks.CYAN_STAINED_GLASS));
        map.put("PURPLE_STAINED_GLASS", new ItemStack(Blocks.PURPLE_STAINED_GLASS));
        map.put("BLUE_STAINED_GLASS", new ItemStack(Blocks.BLUE_STAINED_GLASS));
        map.put("BROWN_STAINED_GLASS", new ItemStack(Blocks.BROWN_STAINED_GLASS));
        map.put("GREEN_STAINED_GLASS", new ItemStack(Blocks.GREEN_STAINED_GLASS));
        map.put("RED_STAINED_GLASS", new ItemStack(Blocks.RED_STAINED_GLASS));
        map.put("BLACK_STAINED_GLASS", new ItemStack(Blocks.BLACK_STAINED_GLASS));

        // Стеклянные панели
        map.put("GLASS_PANE", new ItemStack(Blocks.GLASS_PANE));
        map.put("WHITE_STAINED_GLASS_PANE", new ItemStack(Blocks.WHITE_STAINED_GLASS_PANE));
        map.put("ORANGE_STAINED_GLASS_PANE", new ItemStack(Blocks.ORANGE_STAINED_GLASS_PANE));
        map.put("MAGENTA_STAINED_GLASS_PANE", new ItemStack(Blocks.MAGENTA_STAINED_GLASS_PANE));
        map.put("LIGHT_BLUE_STAINED_GLASS_PANE", new ItemStack(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE));
        map.put("YELLOW_STAINED_GLASS_PANE", new ItemStack(Blocks.YELLOW_STAINED_GLASS_PANE));
        map.put("LIME_STAINED_GLASS_PANE", new ItemStack(Blocks.LIME_STAINED_GLASS_PANE));
        map.put("PINK_STAINED_GLASS_PANE", new ItemStack(Blocks.PINK_STAINED_GLASS_PANE));
        map.put("GRAY_STAINED_GLASS_PANE", new ItemStack(Blocks.GRAY_STAINED_GLASS_PANE));
        map.put("LIGHT_GRAY_STAINED_GLASS_PANE", new ItemStack(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE));
        map.put("CYAN_STAINED_GLASS_PANE", new ItemStack(Blocks.CYAN_STAINED_GLASS_PANE));
        map.put("PURPLE_STAINED_GLASS_PANE", new ItemStack(Blocks.PURPLE_STAINED_GLASS_PANE));
        map.put("BLUE_STAINED_GLASS_PANE", new ItemStack(Blocks.BLUE_STAINED_GLASS_PANE));
        map.put("BROWN_STAINED_GLASS_PANE", new ItemStack(Blocks.BROWN_STAINED_GLASS_PANE));
        map.put("GREEN_STAINED_GLASS_PANE", new ItemStack(Blocks.GREEN_STAINED_GLASS_PANE));
        map.put("RED_STAINED_GLASS_PANE", new ItemStack(Blocks.RED_STAINED_GLASS_PANE));
        map.put("BLACK_STAINED_GLASS_PANE", new ItemStack(Blocks.BLACK_STAINED_GLASS_PANE));

        // Шалкера
        map.put("SHULKER_BOX", new ItemStack(Blocks.SHULKER_BOX));
        map.put("WHITE_SHULKER_BOX", new ItemStack(Blocks.WHITE_SHULKER_BOX));
        map.put("ORANGE_SHULKER_BOX", new ItemStack(Blocks.ORANGE_SHULKER_BOX));
        map.put("MAGENTA_SHULKER_BOX", new ItemStack(Blocks.MAGENTA_SHULKER_BOX));
        map.put("LIGHT_BLUE_SHULKER_BOX", new ItemStack(Blocks.LIGHT_BLUE_SHULKER_BOX));
        map.put("YELLOW_SHULKER_BOX", new ItemStack(Blocks.YELLOW_SHULKER_BOX));
        map.put("LIME_SHULKER_BOX", new ItemStack(Blocks.LIME_SHULKER_BOX));
        map.put("PINK_SHULKER_BOX", new ItemStack(Blocks.PINK_SHULKER_BOX));
        map.put("GRAY_SHULKER_BOX", new ItemStack(Blocks.GRAY_SHULKER_BOX));
        map.put("LIGHT_GRAY_SHULKER_BOX", new ItemStack(Blocks.LIGHT_GRAY_SHULKER_BOX));
        map.put("CYAN_SHULKER_BOX", new ItemStack(Blocks.CYAN_SHULKER_BOX));
        map.put("PURPLE_SHULKER_BOX", new ItemStack(Blocks.PURPLE_SHULKER_BOX));
        map.put("BLUE_SHULKER_BOX", new ItemStack(Blocks.BLUE_SHULKER_BOX));
        map.put("BROWN_SHULKER_BOX", new ItemStack(Blocks.BROWN_SHULKER_BOX));
        map.put("GREEN_SHULKER_BOX", new ItemStack(Blocks.GREEN_SHULKER_BOX));
        map.put("RED_SHULKER_BOX", new ItemStack(Blocks.RED_SHULKER_BOX));
        map.put("BLACK_SHULKER_BOX", new ItemStack(Blocks.BLACK_SHULKER_BOX));

        // Терракота
        map.put("TERRACOTTA", new ItemStack(Blocks.TERRACOTTA));
        map.put("WHITE_TERRACOTTA", new ItemStack(Blocks.WHITE_TERRACOTTA));
        map.put("ORANGE_TERRACOTTA", new ItemStack(Blocks.ORANGE_TERRACOTTA));
        map.put("MAGENTA_TERRACOTTA", new ItemStack(Blocks.MAGENTA_TERRACOTTA));
        map.put("LIGHT_BLUE_TERRACOTTA", new ItemStack(Blocks.LIGHT_BLUE_TERRACOTTA));
        map.put("YELLOW_TERRACOTTA", new ItemStack(Blocks.YELLOW_TERRACOTTA));
        map.put("LIME_TERRACOTTA", new ItemStack(Blocks.LIME_TERRACOTTA));
        map.put("PINK_TERRACOTTA", new ItemStack(Blocks.PINK_TERRACOTTA));
        map.put("GRAY_TERRACOTTA", new ItemStack(Blocks.GRAY_TERRACOTTA));
        map.put("LIGHT_GRAY_TERRACOTTA", new ItemStack(Blocks.LIGHT_GRAY_TERRACOTTA));
        map.put("CYAN_TERRACOTTA", new ItemStack(Blocks.CYAN_TERRACOTTA));
        map.put("PURPLE_TERRACOTTA", new ItemStack(Blocks.PURPLE_TERRACOTTA));
        map.put("BLUE_TERRACOTTA", new ItemStack(Blocks.BLUE_TERRACOTTA));
        map.put("BROWN_TERRACOTTA", new ItemStack(Blocks.BROWN_TERRACOTTA));
        map.put("GREEN_TERRACOTTA", new ItemStack(Blocks.GREEN_TERRACOTTA));
        map.put("RED_TERRACOTTA", new ItemStack(Blocks.RED_TERRACOTTA));
        map.put("BLACK_TERRACOTTA", new ItemStack(Blocks.BLACK_TERRACOTTA));

        // Глазурованная терракота
        map.put("WHITE_GLAZED_TERRACOTTA", new ItemStack(Blocks.WHITE_GLAZED_TERRACOTTA));
        map.put("ORANGE_GLAZED_TERRACOTTA", new ItemStack(Blocks.ORANGE_GLAZED_TERRACOTTA));
        map.put("MAGENTA_GLAZED_TERRACOTTA", new ItemStack(Blocks.MAGENTA_GLAZED_TERRACOTTA));
        map.put("LIGHT_BLUE_GLAZED_TERRACOTTA", new ItemStack(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA));
        map.put("YELLOW_GLAZED_TERRACOTTA", new ItemStack(Blocks.YELLOW_GLAZED_TERRACOTTA));
        map.put("LIME_GLAZED_TERRACOTTA", new ItemStack(Blocks.LIME_GLAZED_TERRACOTTA));
        map.put("PINK_GLAZED_TERRACOTTA", new ItemStack(Blocks.PINK_GLAZED_TERRACOTTA));
        map.put("GRAY_GLAZED_TERRACOTTA", new ItemStack(Blocks.GRAY_GLAZED_TERRACOTTA));
        map.put("LIGHT_GRAY_GLAZED_TERRACOTTA", new ItemStack(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA));
        map.put("CYAN_GLAZED_TERRACOTTA", new ItemStack(Blocks.CYAN_GLAZED_TERRACOTTA));
        map.put("PURPLE_GLAZED_TERRACOTTA", new ItemStack(Blocks.PURPLE_GLAZED_TERRACOTTA));
        map.put("BLUE_GLAZED_TERRACOTTA", new ItemStack(Blocks.BLUE_GLAZED_TERRACOTTA));
        map.put("BROWN_GLAZED_TERRACOTTA", new ItemStack(Blocks.BROWN_GLAZED_TERRACOTTA));
        map.put("GREEN_GLAZED_TERRACOTTA", new ItemStack(Blocks.GREEN_GLAZED_TERRACOTTA));
        map.put("RED_GLAZED_TERRACOTTA", new ItemStack(Blocks.RED_GLAZED_TERRACOTTA));
        map.put("BLACK_GLAZED_TERRACOTTA", new ItemStack(Blocks.BLACK_GLAZED_TERRACOTTA));

        // Бетон
        map.put("WHITE_CONCRETE", new ItemStack(Blocks.WHITE_CONCRETE));
        map.put("ORANGE_CONCRETE", new ItemStack(Blocks.ORANGE_CONCRETE));
        map.put("MAGENTA_CONCRETE", new ItemStack(Blocks.MAGENTA_CONCRETE));
        map.put("LIGHT_BLUE_CONCRETE", new ItemStack(Blocks.LIGHT_BLUE_CONCRETE));
        map.put("YELLOW_CONCRETE", new ItemStack(Blocks.YELLOW_CONCRETE));
        map.put("LIME_CONCRETE", new ItemStack(Blocks.LIME_CONCRETE));
        map.put("PINK_CONCRETE", new ItemStack(Blocks.PINK_CONCRETE));
        map.put("GRAY_CONCRETE", new ItemStack(Blocks.GRAY_CONCRETE));
        map.put("LIGHT_GRAY_CONCRETE", new ItemStack(Blocks.LIGHT_GRAY_CONCRETE));
        map.put("CYAN_CONCRETE", new ItemStack(Blocks.CYAN_CONCRETE));
        map.put("PURPLE_CONCRETE", new ItemStack(Blocks.PURPLE_CONCRETE));
        map.put("BLUE_CONCRETE", new ItemStack(Blocks.BLUE_CONCRETE));
        map.put("BROWN_CONCRETE", new ItemStack(Blocks.BROWN_CONCRETE));
        map.put("GREEN_CONCRETE", new ItemStack(Blocks.GREEN_CONCRETE));
        map.put("RED_CONCRETE", new ItemStack(Blocks.RED_CONCRETE));
        map.put("BLACK_CONCRETE", new ItemStack(Blocks.BLACK_CONCRETE));

        // Цемент
        map.put("WHITE_CONCRETE_POWDER", new ItemStack(Blocks.WHITE_CONCRETE_POWDER));
        map.put("ORANGE_CONCRETE_POWDER", new ItemStack(Blocks.ORANGE_CONCRETE_POWDER));
        map.put("MAGENTA_CONCRETE_POWDER", new ItemStack(Blocks.MAGENTA_CONCRETE_POWDER));
        map.put("LIGHT_BLUE_CONCRETE_POWDER", new ItemStack(Blocks.LIGHT_BLUE_CONCRETE_POWDER));
        map.put("YELLOW_CONCRETE_POWDER", new ItemStack(Blocks.YELLOW_CONCRETE_POWDER));
        map.put("LIME_CONCRETE_POWDER", new ItemStack(Blocks.LIME_CONCRETE_POWDER));
        map.put("PINK_CONCRETE_POWDER", new ItemStack(Blocks.PINK_CONCRETE_POWDER));
        map.put("GRAY_CONCRETE_POWDER", new ItemStack(Blocks.GRAY_CONCRETE_POWDER));
        map.put("LIGHT_GRAY_CONCRETE_POWDER", new ItemStack(Blocks.LIGHT_GRAY_CONCRETE_POWDER));
        map.put("CYAN_CONCRETE_POWDER", new ItemStack(Blocks.CYAN_CONCRETE_POWDER));
        map.put("PURPLE_CONCRETE_POWDER", new ItemStack(Blocks.PURPLE_CONCRETE_POWDER));
        map.put("BLUE_CONCRETE_POWDER", new ItemStack(Blocks.BLUE_CONCRETE_POWDER));
        map.put("BROWN_CONCRETE_POWDER", new ItemStack(Blocks.BROWN_CONCRETE_POWDER));
        map.put("GREEN_CONCRETE_POWDER", new ItemStack(Blocks.GREEN_CONCRETE_POWDER));
        map.put("RED_CONCRETE_POWDER", new ItemStack(Blocks.RED_CONCRETE_POWDER));
        map.put("BLACK_CONCRETE_POWDER", new ItemStack(Blocks.BLACK_CONCRETE_POWDER));

        // Свечки
        map.put("CANDLE", new ItemStack(Blocks.CANDLE));
        map.put("WHITE_CANDLE", new ItemStack(Blocks.WHITE_CANDLE));
        map.put("ORANGE_CANDLE", new ItemStack(Blocks.ORANGE_CANDLE));
        map.put("MAGENTA_CANDLE", new ItemStack(Blocks.MAGENTA_CANDLE));
        map.put("LIGHT_BLUE_CANDLE", new ItemStack(Blocks.LIGHT_BLUE_CANDLE));
        map.put("YELLOW_CANDLE", new ItemStack(Blocks.YELLOW_CANDLE));
        map.put("LIME_CANDLE", new ItemStack(Blocks.LIME_CANDLE));
        map.put("PINK_CANDLE", new ItemStack(Blocks.PINK_CANDLE));
        map.put("GRAY_CANDLE", new ItemStack(Blocks.GRAY_CANDLE));
        map.put("LIGHT_GRAY_CANDLE", new ItemStack(Blocks.LIGHT_GRAY_CANDLE));
        map.put("CYAN_CANDLE", new ItemStack(Blocks.CYAN_CANDLE));
        map.put("PURPLE_CANDLE", new ItemStack(Blocks.PURPLE_CANDLE));
        map.put("BLUE_CANDLE", new ItemStack(Blocks.BLUE_CANDLE));
        map.put("BROWN_CANDLE", new ItemStack(Blocks.BROWN_CANDLE));
        map.put("GREEN_CANDLE", new ItemStack(Blocks.GREEN_CANDLE));
        map.put("RED_CANDLE", new ItemStack(Blocks.RED_CANDLE));
        map.put("BLACK_CANDLE", new ItemStack(Blocks.BLACK_CANDLE));

        // Шерсть
        map.put("WHITE_WOOL", new ItemStack(Blocks.WHITE_WOOL));
        map.put("ORANGE_WOOL", new ItemStack(Blocks.ORANGE_WOOL));
        map.put("MAGENTA_WOOL", new ItemStack(Blocks.MAGENTA_WOOL));
        map.put("LIGHT_BLUE_WOOL", new ItemStack(Blocks.LIGHT_BLUE_WOOL));
        map.put("YELLOW_WOOL", new ItemStack(Blocks.YELLOW_WOOL));
        map.put("LIME_WOOL", new ItemStack(Blocks.LIME_WOOL));
        map.put("PINK_WOOL", new ItemStack(Blocks.PINK_WOOL));
        map.put("GRAY_WOOL", new ItemStack(Blocks.GRAY_WOOL));
        map.put("LIGHT_GRAY_WOOL", new ItemStack(Blocks.LIGHT_GRAY_WOOL));
        map.put("CYAN_WOOL", new ItemStack(Blocks.CYAN_WOOL));
        map.put("PURPLE_WOOL", new ItemStack(Blocks.PURPLE_WOOL));
        map.put("BLUE_WOOL", new ItemStack(Blocks.BLUE_WOOL));
        map.put("BROWN_WOOL", new ItemStack(Blocks.BROWN_WOOL));
        map.put("GREEN_WOOL", new ItemStack(Blocks.GREEN_WOOL));
        map.put("RED_WOOL", new ItemStack(Blocks.RED_WOOL));
        map.put("BLACK_WOOL", new ItemStack(Blocks.BLACK_WOOL));

        // Коврики
        map.put("WHITE_CARPET", new ItemStack(Blocks.WHITE_CARPET));
        map.put("ORANGE_CARPET", new ItemStack(Blocks.ORANGE_CARPET));
        map.put("MAGENTA_CARPET", new ItemStack(Blocks.MAGENTA_CARPET));
        map.put("LIGHT_BLUE_CARPET", new ItemStack(Blocks.LIGHT_BLUE_CARPET));
        map.put("YELLOW_CARPET", new ItemStack(Blocks.YELLOW_CARPET));
        map.put("LIME_CARPET", new ItemStack(Blocks.LIME_CARPET));
        map.put("PINK_CARPET", new ItemStack(Blocks.PINK_CARPET));
        map.put("GRAY_CARPET", new ItemStack(Blocks.GRAY_CARPET));
        map.put("LIGHT_GRAY_CARPET", new ItemStack(Blocks.LIGHT_GRAY_CARPET));
        map.put("CYAN_CARPET", new ItemStack(Blocks.CYAN_CARPET));
        map.put("PURPLE_CARPET", new ItemStack(Blocks.PURPLE_CARPET));
        map.put("BLUE_CARPET", new ItemStack(Blocks.BLUE_CARPET));
        map.put("BROWN_CARPET", new ItemStack(Blocks.BROWN_CARPET));
        map.put("GREEN_CARPET", new ItemStack(Blocks.GREEN_CARPET));
        map.put("RED_CARPET", new ItemStack(Blocks.RED_CARPET));
        map.put("BLACK_CARPET", new ItemStack(Blocks.BLACK_CARPET));

        // Кровати
        map.put("WHITE_BED", new ItemStack(Blocks.WHITE_BED));
        map.put("ORANGE_BED", new ItemStack(Blocks.ORANGE_BED));
        map.put("MAGENTA_BED", new ItemStack(Blocks.MAGENTA_BED));
        map.put("LIGHT_BLUE_BED", new ItemStack(Blocks.LIGHT_BLUE_BED));
        map.put("YELLOW_BED", new ItemStack(Blocks.YELLOW_BED));
        map.put("LIME_BED", new ItemStack(Blocks.LIME_BED));
        map.put("PINK_BED", new ItemStack(Blocks.PINK_BED));
        map.put("GRAY_BED", new ItemStack(Blocks.GRAY_BED));
        map.put("LIGHT_GRAY_BED", new ItemStack(Blocks.LIGHT_GRAY_BED));
        map.put("CYAN_BED", new ItemStack(Blocks.CYAN_BED));
        map.put("PURPLE_BED", new ItemStack(Blocks.PURPLE_BED));
        map.put("BLUE_BED", new ItemStack(Blocks.BLUE_BED));
        map.put("BROWN_BED", new ItemStack(Blocks.BROWN_BED));
        map.put("GREEN_BED", new ItemStack(Blocks.GREEN_BED));
        map.put("RED_BED", new ItemStack(Blocks.RED_BED));
        map.put("BLACK_BED", new ItemStack(Blocks.BLACK_BED));

        //? if >=1.21.2 {
        // Мешочки
        map.put("BUNDLE", new ItemStack(Items.BUNDLE));
        map.put("WHITE_BUNDLE", new ItemStack(Items.WHITE_BUNDLE));
        map.put("ORANGE_BUNDLE", new ItemStack(Items.ORANGE_BUNDLE));
        map.put("MAGENTA_BUNDLE", new ItemStack(Items.MAGENTA_BUNDLE));
        map.put("LIGHT_BLUE_BUNDLE", new ItemStack(Items.LIGHT_BLUE_BUNDLE));
        map.put("YELLOW_BUNDLE", new ItemStack(Items.YELLOW_BUNDLE));
        map.put("LIME_BUNDLE", new ItemStack(Items.LIME_BUNDLE));
        map.put("PINK_BUNDLE", new ItemStack(Items.PINK_BUNDLE));
        map.put("GRAY_BUNDLE", new ItemStack(Items.GRAY_BUNDLE));
        map.put("LIGHT_GRAY_BUNDLE", new ItemStack(Items.LIGHT_GRAY_BUNDLE));
        map.put("CYAN_BUNDLE", new ItemStack(Items.CYAN_BUNDLE));
        map.put("PURPLE_BUNDLE", new ItemStack(Items.PURPLE_BUNDLE));
        map.put("BLUE_BUNDLE", new ItemStack(Items.BLUE_BUNDLE));
        map.put("BROWN_BUNDLE", new ItemStack(Items.BROWN_BUNDLE));
        map.put("GREEN_BUNDLE", new ItemStack(Items.GREEN_BUNDLE));
        map.put("RED_BUNDLE", new ItemStack(Items.RED_BUNDLE));
        map.put("BLACK_BUNDLE", new ItemStack(Items.BLACK_BUNDLE));
        //?}

        // Красители
        map.put("INK_SAC", new ItemStack(Items.INK_SAC));
        map.put("GLOW_INK_SAC", new ItemStack(Items.GLOW_INK_SAC));
        map.put("COCOA_BEANS", new ItemStack(Items.COCOA_BEANS));
        map.put("WHITE_DYE", new ItemStack(Items.WHITE_DYE));
        map.put("ORANGE_DYE", new ItemStack(Items.ORANGE_DYE));
        map.put("MAGENTA_DYE", new ItemStack(Items.MAGENTA_DYE));
        map.put("LIGHT_BLUE_DYE", new ItemStack(Items.LIGHT_BLUE_DYE));
        map.put("YELLOW_DYE", new ItemStack(Items.YELLOW_DYE));
        map.put("LIME_DYE", new ItemStack(Items.LIME_DYE));
        map.put("PINK_DYE", new ItemStack(Items.PINK_DYE));
        map.put("GRAY_DYE", new ItemStack(Items.GRAY_DYE));
        map.put("LIGHT_GRAY_DYE", new ItemStack(Items.LIGHT_GRAY_DYE));
        map.put("CYAN_DYE", new ItemStack(Items.CYAN_DYE));
        map.put("PURPLE_DYE", new ItemStack(Items.PURPLE_DYE));
        map.put("BLUE_DYE", new ItemStack(Items.BLUE_DYE));
        map.put("BROWN_DYE", new ItemStack(Items.BROWN_DYE));
        map.put("GREEN_DYE", new ItemStack(Items.GREEN_DYE));
        map.put("RED_DYE", new ItemStack(Items.RED_DYE));
        map.put("BLACK_DYE", new ItemStack(Items.BLACK_DYE));

        // Яйца призыва
        //? if >1.20.4 {
        map.put("ARMADILLO_SPAWN_EGG", new ItemStack(Items.ARMADILLO_SPAWN_EGG));
        //?}
        map.put("ALLAY_SPAWN_EGG", new ItemStack(Items.ALLAY_SPAWN_EGG));
        map.put("AXOLOTL_SPAWN_EGG", new ItemStack(Items.AXOLOTL_SPAWN_EGG));
        map.put("BAT_SPAWN_EGG", new ItemStack(Items.BAT_SPAWN_EGG));
        map.put("BEE_SPAWN_EGG", new ItemStack(Items.BEE_SPAWN_EGG));
        map.put("BLAZE_SPAWN_EGG", new ItemStack(Items.BLAZE_SPAWN_EGG));
        //? if >1.20.4 {
        map.put("BOGGED_SPAWN_EGG", new ItemStack(Items.BOGGED_SPAWN_EGG));
        //?}
        //? if >=1.20.3 {
        map.put("BREEZE_SPAWN_EGG", new ItemStack(Items.BREEZE_SPAWN_EGG));
        //? }
        map.put("CAT_SPAWN_EGG", new ItemStack(Items.CAT_SPAWN_EGG));
        map.put("CAMEL_SPAWN_EGG", new ItemStack(Items.CAMEL_SPAWN_EGG));
        map.put("CAVE_SPIDER_SPAWN_EGG", new ItemStack(Items.CAVE_SPIDER_SPAWN_EGG));
        map.put("CHICKEN_SPAWN_EGG", new ItemStack(Items.CHICKEN_SPAWN_EGG));
        map.put("COD_SPAWN_EGG", new ItemStack(Items.COD_SPAWN_EGG));
        map.put("COW_SPAWN_EGG", new ItemStack(Items.COW_SPAWN_EGG));
        map.put("CREEPER_SPAWN_EGG", new ItemStack(Items.CREEPER_SPAWN_EGG));
        map.put("DOLPHIN_SPAWN_EGG", new ItemStack(Items.DOLPHIN_SPAWN_EGG));
        map.put("DONKEY_SPAWN_EGG", new ItemStack(Items.DONKEY_SPAWN_EGG));
        map.put("DROWNED_SPAWN_EGG", new ItemStack(Items.DROWNED_SPAWN_EGG));
        map.put("ELDER_GUARDIAN_SPAWN_EGG", new ItemStack(Items.ELDER_GUARDIAN_SPAWN_EGG));
        map.put("ENDER_DRAGON_SPAWN_EGG", new ItemStack(Items.ENDER_DRAGON_SPAWN_EGG));
        map.put("ENDERMAN_SPAWN_EGG", new ItemStack(Items.ENDERMAN_SPAWN_EGG));
        map.put("ENDERMITE_SPAWN_EGG", new ItemStack(Items.ENDERMITE_SPAWN_EGG));
        map.put("EVOKER_SPAWN_EGG", new ItemStack(Items.EVOKER_SPAWN_EGG));
        map.put("FOX_SPAWN_EGG", new ItemStack(Items.FOX_SPAWN_EGG));
        map.put("FROG_SPAWN_EGG", new ItemStack(Items.FROG_SPAWN_EGG));
        map.put("GHAST_SPAWN_EGG", new ItemStack(Items.GHAST_SPAWN_EGG));
        map.put("GLOW_SQUID_SPAWN_EGG", new ItemStack(Items.GLOW_SQUID_SPAWN_EGG));
        map.put("GOAT_SPAWN_EGG", new ItemStack(Items.GOAT_SPAWN_EGG));
        map.put("GUARDIAN_SPAWN_EGG", new ItemStack(Items.GUARDIAN_SPAWN_EGG));
        map.put("HOGLIN_SPAWN_EGG", new ItemStack(Items.HOGLIN_SPAWN_EGG));
        map.put("HORSE_SPAWN_EGG", new ItemStack(Items.HORSE_SPAWN_EGG));
        map.put("HUSK_SPAWN_EGG", new ItemStack(Items.HUSK_SPAWN_EGG));
        map.put("IRON_GOLEM_SPAWN_EGG", new ItemStack(Items.IRON_GOLEM_SPAWN_EGG));
        map.put("LLAMA_SPAWN_EGG", new ItemStack(Items.LLAMA_SPAWN_EGG));
        map.put("MAGMA_CUBE_SPAWN_EGG", new ItemStack(Items.MAGMA_CUBE_SPAWN_EGG));
        map.put("MOOSHROOM_SPAWN_EGG", new ItemStack(Items.MOOSHROOM_SPAWN_EGG));
        map.put("MULE_SPAWN_EGG", new ItemStack(Items.MULE_SPAWN_EGG));
        map.put("OCELOT_SPAWN_EGG", new ItemStack(Items.OCELOT_SPAWN_EGG));
        map.put("PANDA_SPAWN_EGG", new ItemStack(Items.PANDA_SPAWN_EGG));
        map.put("PARROT_SPAWN_EGG", new ItemStack(Items.PARROT_SPAWN_EGG));
        map.put("PHANTOM_SPAWN_EGG", new ItemStack(Items.PHANTOM_SPAWN_EGG));
        map.put("PIG_SPAWN_EGG", new ItemStack(Items.PIG_SPAWN_EGG));
        map.put("PIGLIN_SPAWN_EGG", new ItemStack(Items.PIGLIN_SPAWN_EGG));
        map.put("PIGLIN_BRUTE_SPAWN_EGG", new ItemStack(Items.PIGLIN_BRUTE_SPAWN_EGG));
        map.put("PILLAGER_SPAWN_EGG", new ItemStack(Items.PILLAGER_SPAWN_EGG));
        map.put("POLAR_BEAR_SPAWN_EGG", new ItemStack(Items.POLAR_BEAR_SPAWN_EGG));
        map.put("PUFFERFISH_SPAWN_EGG", new ItemStack(Items.PUFFERFISH_SPAWN_EGG));
        map.put("RABBIT_SPAWN_EGG", new ItemStack(Items.RABBIT_SPAWN_EGG));
        map.put("RAVAGER_SPAWN_EGG", new ItemStack(Items.RAVAGER_SPAWN_EGG));
        map.put("SALMON_SPAWN_EGG", new ItemStack(Items.SALMON_SPAWN_EGG));
        map.put("SHEEP_SPAWN_EGG", new ItemStack(Items.SHEEP_SPAWN_EGG));
        map.put("SHULKER_SPAWN_EGG", new ItemStack(Items.SHULKER_SPAWN_EGG));
        map.put("SILVERFISH_SPAWN_EGG", new ItemStack(Items.SILVERFISH_SPAWN_EGG));
        map.put("SKELETON_SPAWN_EGG", new ItemStack(Items.SKELETON_SPAWN_EGG));
        map.put("SKELETON_HORSE_SPAWN_EGG", new ItemStack(Items.SKELETON_HORSE_SPAWN_EGG));
        map.put("SLIME_SPAWN_EGG", new ItemStack(Items.SLIME_SPAWN_EGG));
        map.put("SNIFFER_SPAWN_EGG", new ItemStack(Items.SNIFFER_SPAWN_EGG));
        map.put("SNOW_GOLEM_SPAWN_EGG", new ItemStack(Items.SNOW_GOLEM_SPAWN_EGG));
        map.put("SPIDER_SPAWN_EGG", new ItemStack(Items.SPIDER_SPAWN_EGG));
        map.put("SQUID_SPAWN_EGG", new ItemStack(Items.SQUID_SPAWN_EGG));
        map.put("STRAY_SPAWN_EGG", new ItemStack(Items.STRAY_SPAWN_EGG));
        map.put("STRIDER_SPAWN_EGG", new ItemStack(Items.STRIDER_SPAWN_EGG));
        map.put("TADPOLE_SPAWN_EGG", new ItemStack(Items.TADPOLE_SPAWN_EGG));
        map.put("TRADER_LLAMA_SPAWN_EGG", new ItemStack(Items.TRADER_LLAMA_SPAWN_EGG));
        map.put("TROPICAL_FISH_SPAWN_EGG", new ItemStack(Items.TROPICAL_FISH_SPAWN_EGG));
        map.put("TURTLE_SPAWN_EGG", new ItemStack(Items.TURTLE_SPAWN_EGG));
        map.put("VEX_SPAWN_EGG", new ItemStack(Items.VEX_SPAWN_EGG));
        map.put("VILLAGER_SPAWN_EGG", new ItemStack(Items.VILLAGER_SPAWN_EGG));
        map.put("VINDICATOR_SPAWN_EGG", new ItemStack(Items.VINDICATOR_SPAWN_EGG));
        map.put("WANDERING_TRADER_SPAWN_EGG", new ItemStack(Items.WANDERING_TRADER_SPAWN_EGG));
        map.put("WARDEN_SPAWN_EGG", new ItemStack(Items.WARDEN_SPAWN_EGG));
        map.put("WITCH_SPAWN_EGG", new ItemStack(Items.WITCH_SPAWN_EGG));
        map.put("WITHER_SPAWN_EGG", new ItemStack(Items.WITHER_SPAWN_EGG));
        map.put("WITHER_SKELETON_SPAWN_EGG", new ItemStack(Items.WITHER_SKELETON_SPAWN_EGG));
        map.put("WOLF_SPAWN_EGG", new ItemStack(Items.WOLF_SPAWN_EGG));
        map.put("ZOGLIN_SPAWN_EGG", new ItemStack(Items.ZOGLIN_SPAWN_EGG));
        //? if >=1.21.2 {
        map.put("CREAKING_SPAWN_EGG", new ItemStack(Items.CREAKING_SPAWN_EGG));
        //?}
        map.put("ZOMBIE_SPAWN_EGG", new ItemStack(Items.ZOMBIE_SPAWN_EGG));
        map.put("ZOMBIE_HORSE_SPAWN_EGG", new ItemStack(Items.ZOMBIE_HORSE_SPAWN_EGG));
        map.put("ZOMBIE_VILLAGER_SPAWN_EGG", new ItemStack(Items.ZOMBIE_VILLAGER_SPAWN_EGG));
        map.put("ZOMBIFIED_PIGLIN_SPAWN_EGG", new ItemStack(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG));

        // Пластинки
        map.put("MUSIC_DISC_13", new ItemStack(Items.MUSIC_DISC_13));
        map.put("MUSIC_DISC_CAT", new ItemStack(Items.MUSIC_DISC_CAT));
        map.put("MUSIC_DISC_BLOCKS", new ItemStack(Items.MUSIC_DISC_BLOCKS));
        map.put("MUSIC_DISC_CHIRP", new ItemStack(Items.MUSIC_DISC_CHIRP));
        //? if >=1.21 {
        map.put("MUSIC_DISC_CREATOR", new ItemStack(Items.MUSIC_DISC_CREATOR));
        map.put("MUSIC_DISC_CREATOR_MUSIC_BOX", new ItemStack(Items.MUSIC_DISC_CREATOR_MUSIC_BOX));
        //?}
        map.put("MUSIC_DISC_FAR", new ItemStack(Items.MUSIC_DISC_FAR));
        map.put("MUSIC_DISC_MALL", new ItemStack(Items.MUSIC_DISC_MALL));
        map.put("MUSIC_DISC_MELLOHI", new ItemStack(Items.MUSIC_DISC_MELLOHI));
        map.put("MUSIC_DISC_STAL", new ItemStack(Items.MUSIC_DISC_STAL));
        map.put("MUSIC_DISC_STRAD", new ItemStack(Items.MUSIC_DISC_STRAD));
        map.put("MUSIC_DISC_WARD", new ItemStack(Items.MUSIC_DISC_WARD));
        map.put("MUSIC_DISC_11", new ItemStack(Items.MUSIC_DISC_11));
        map.put("MUSIC_DISC_WAIT", new ItemStack(Items.MUSIC_DISC_WAIT));
        map.put("MUSIC_DISC_OTHERSIDE", new ItemStack(Items.MUSIC_DISC_OTHERSIDE));
        map.put("MUSIC_DISC_RELIC", new ItemStack(Items.MUSIC_DISC_RELIC));
        map.put("MUSIC_DISC_5", new ItemStack(Items.MUSIC_DISC_5));
        map.put("MUSIC_DISC_PIGSTEP", new ItemStack(Items.MUSIC_DISC_PIGSTEP));
        //? if >=1.21 {
        map.put("MUSIC_DISC_PRECIPICE", new ItemStack(Items.MUSIC_DISC_PRECIPICE));
        //?}
        map.put("DISC_FRAGMENT_5", new ItemStack(Items.DISC_FRAGMENT_5));

        // Паттерны
        map.put("FLOWER_BANNER_PATTERN", new ItemStack(Items.FLOWER_BANNER_PATTERN));
        map.put("CREEPER_BANNER_PATTERN", new ItemStack(Items.CREEPER_BANNER_PATTERN));
        map.put("SKULL_BANNER_PATTERN", new ItemStack(Items.SKULL_BANNER_PATTERN));
        map.put("MOJANG_BANNER_PATTERN", new ItemStack(Items.MOJANG_BANNER_PATTERN));
        map.put("GLOBE_BANNER_PATTERN", new ItemStack(Items.GLOBE_BANNER_PATTERN));
        map.put("PIGLIN_BANNER_PATTERN", new ItemStack(Items.PIGLIN_BANNER_PATTERN));
        //? if >1.20.4 {
        map.put("FLOW_BANNER_PATTERN", new ItemStack(Items.FLOW_BANNER_PATTERN));
        map.put("GUSTER_BANNER_PATTERN", new ItemStack(Items.GUSTER_BANNER_PATTERN));
        //?}
        //? if >=1.21.2 {
        map.put("FIELD_MASONED_BANNER_PATTERN", new ItemStack(Items.FIELD_MASONED_BANNER_PATTERN));
        map.put("BORDURE_INDENTED_BANNER_PATTERN", new ItemStack(Items.BORDURE_INDENTED_BANNER_PATTERN));
        //?}

        // Шаблоны брони
        map.put("NETHERITE_UPGRADE_SMITHING_TEMPLATE", new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
        map.put("SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("DUNE_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("COAST_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("WILD_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("WARD_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("EYE_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("VEX_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("TIDE_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("RIB_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("RAISER_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("HOST_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE));
        //? if >1.20.4 {
        map.put("FLOW_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE));
        map.put("BOLT_ARMOR_TRIM_SMITHING_TEMPLATE", new ItemStack(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE));
        //?}

        // Горшки
        map.put("ANGLER_POTTERY_SHERD", new ItemStack(Items.ANGLER_POTTERY_SHERD));
        map.put("ARCHER_POTTERY_SHERD", new ItemStack(Items.ARCHER_POTTERY_SHERD));
        map.put("ARMS_UP_POTTERY_SHERD", new ItemStack(Items.ARMS_UP_POTTERY_SHERD));
        map.put("BLADE_POTTERY_SHERD", new ItemStack(Items.BLADE_POTTERY_SHERD));
        map.put("BREWER_POTTERY_SHERD", new ItemStack(Items.BREWER_POTTERY_SHERD));
        map.put("BURN_POTTERY_SHERD", new ItemStack(Items.BURN_POTTERY_SHERD));
        map.put("DANGER_POTTERY_SHERD", new ItemStack(Items.DANGER_POTTERY_SHERD));
        map.put("EXPLORER_POTTERY_SHERD", new ItemStack(Items.EXPLORER_POTTERY_SHERD));
        //? if >1.20.4 {
        map.put("FLOW_POTTERY_SHERD", new ItemStack(Items.FLOW_POTTERY_SHERD));
        //?}
        map.put("FRIEND_POTTERY_SHERD", new ItemStack(Items.FRIEND_POTTERY_SHERD));
        //? if >1.20.4 {
        map.put("GUSTER_POTTERY_SHERD", new ItemStack(Items.GUSTER_POTTERY_SHERD));
        //?}
        map.put("HEART_POTTERY_SHERD", new ItemStack(Items.HEART_POTTERY_SHERD));
        map.put("HEARTBREAK_POTTERY_SHERD", new ItemStack(Items.HEARTBREAK_POTTERY_SHERD));
        map.put("HOWL_POTTERY_SHERD", new ItemStack(Items.HOWL_POTTERY_SHERD));
        map.put("MINER_POTTERY_SHERD", new ItemStack(Items.MINER_POTTERY_SHERD));
        map.put("MOURNER_POTTERY_SHERD", new ItemStack(Items.MOURNER_POTTERY_SHERD));
        map.put("PLENTY_POTTERY_SHERD", new ItemStack(Items.PLENTY_POTTERY_SHERD));
        map.put("PRIZE_POTTERY_SHERD", new ItemStack(Items.PRIZE_POTTERY_SHERD));
        //? if >1.20.4 {
        map.put("SCRAPE_POTTERY_SHERD", new ItemStack(Items.SCRAPE_POTTERY_SHERD));
        //?}
        map.put("SHEAF_POTTERY_SHERD", new ItemStack(Items.SHEAF_POTTERY_SHERD));
        map.put("SHELTER_POTTERY_SHERD", new ItemStack(Items.SHELTER_POTTERY_SHERD));
        map.put("SKULL_POTTERY_SHERD", new ItemStack(Items.SKULL_POTTERY_SHERD));
        map.put("SNORT_POTTERY_SHERD", new ItemStack(Items.SNORT_POTTERY_SHERD));
        return map;
    }

    private static Map<String, ItemStack> cat_POTIONS() {
        Map<String, ItemStack> map = new LinkedHashMap<>();
        map.put("GLASS_BOTTLE", new ItemStack(Items.GLASS_BOTTLE));
        map.put("POTION", new ItemStack(Items.POTION));
        map.put("EXPERIENCE_BOTTLE", new ItemStack(Items.EXPERIENCE_BOTTLE));
        map.put("DRAGON_BREATH", new ItemStack(Items.DRAGON_BREATH));
        map.put("SPLASH_POTION", new ItemStack(Items.SPLASH_POTION));
        //? if >1.20.4 {
        map.put("OMINOUS_BOTTLE", new ItemStack(Items.OMINOUS_BOTTLE));
        //?}
        map.put("LINGERING_POTION", new ItemStack(Items.LINGERING_POTION));
        map.put("NIGHT_VISION", getPotionItem(Potions.NIGHT_VISION, Items.POTION));
        map.put("INVISIBLE", getPotionItem(Potions.INVISIBILITY, Items.POTION));
        map.put("LEAPING", getPotionItem(Potions.LEAPING, Items.POTION));
        map.put("FIRE_RESISTANCE", getPotionItem(Potions.FIRE_RESISTANCE, Items.POTION));
        map.put("SWIFTNESS", getPotionItem(Potions.SWIFTNESS, Items.POTION));
        map.put("SLOWNESS", getPotionItem(Potions.SLOWNESS, Items.POTION));
        map.put("TURTLE_MASTER", getPotionItem(Potions.TURTLE_MASTER, Items.POTION));
        map.put("WATER_BREATHING", getPotionItem(Potions.WATER_BREATHING, Items.POTION));
        map.put("HEALING", getPotionItem(Potions.HEALING, Items.POTION));
        map.put("HARMING", getPotionItem(Potions.HARMING, Items.POTION));
        map.put("POISON", getPotionItem(Potions.POISON, Items.POTION));
        map.put("REGENERATION", getPotionItem(Potions.REGENERATION, Items.POTION));
        map.put("STRENGTH", getPotionItem(Potions.STRENGTH, Items.POTION));
        map.put("WEAKNESS", getPotionItem(Potions.WEAKNESS, Items.POTION));
        map.put("LUCK", getPotionItem(Potions.LUCK, Items.POTION));
        //? if >1.20.4 {
        map.put("WIND_CHARGED", getPotionItem(Potions.WIND_CHARGED, Items.POTION));
        map.put("WEAVING", getPotionItem(Potions.WEAVING, Items.POTION));
        map.put("OOZING", getPotionItem(Potions.OOZING, Items.POTION));
        map.put("INFESTED", getPotionItem(Potions.INFESTED, Items.POTION));
        //?}

        map.put("NIGHT_VISION_SPLASH_POTION", getPotionItem(Potions.NIGHT_VISION, Items.SPLASH_POTION));
        map.put("INVISIBLE_SPLASH_POTION", getPotionItem(Potions.INVISIBILITY, Items.SPLASH_POTION));
        map.put("LEAPING_SPLASH_POTION", getPotionItem(Potions.LEAPING, Items.SPLASH_POTION));
        map.put("FIRE_RESISTANCE_SPLASH_POTION", getPotionItem(Potions.FIRE_RESISTANCE, Items.SPLASH_POTION));
        map.put("SWIFTNESS_SPLASH_POTION", getPotionItem(Potions.SWIFTNESS, Items.SPLASH_POTION));
        map.put("SLOWNESS_SPLASH_POTION", getPotionItem(Potions.SLOWNESS, Items.SPLASH_POTION));
        map.put("TURTLE_MASTER_SPLASH_POTION", getPotionItem(Potions.TURTLE_MASTER, Items.SPLASH_POTION));
        map.put("WATER_BREATHING_SPLASH_POTION", getPotionItem(Potions.WATER_BREATHING, Items.SPLASH_POTION));
        map.put("HEALING_SPLASH_POTION", getPotionItem(Potions.HEALING, Items.SPLASH_POTION));
        map.put("HARMING_SPLASH_POTION", getPotionItem(Potions.HARMING, Items.SPLASH_POTION));
        map.put("POISON_SPLASH_POTION", getPotionItem(Potions.POISON, Items.SPLASH_POTION));
        map.put("REGENERATION_SPLASH_POTION", getPotionItem(Potions.REGENERATION, Items.SPLASH_POTION));
        map.put("STRENGTH_SPLASH_POTION", getPotionItem(Potions.STRENGTH, Items.SPLASH_POTION));
        map.put("WEAKNESS_SPLASH_POTION", getPotionItem(Potions.WEAKNESS, Items.SPLASH_POTION));
        map.put("LUCK_SPLASH_POTION", getPotionItem(Potions.LUCK, Items.SPLASH_POTION));
        //? if >1.20.4 {
        map.put("WIND_CHARGED_SPLASH_POTION", getPotionItem(Potions.WIND_CHARGED, Items.SPLASH_POTION));
        map.put("WEAVING_SPLASH_POTION", getPotionItem(Potions.WEAVING, Items.SPLASH_POTION));
        map.put("OOZING_SPLASH_POTION", getPotionItem(Potions.OOZING, Items.SPLASH_POTION));
        map.put("INFESTED_SPLASH_POTION", getPotionItem(Potions.INFESTED, Items.SPLASH_POTION));
        //?}

        map.put("NIGHT_VISION_LINGERING_POTION", getPotionItem(Potions.NIGHT_VISION, Items.LINGERING_POTION));
        map.put("INVISIBLE_LINGERING_POTION", getPotionItem(Potions.INVISIBILITY, Items.LINGERING_POTION));
        map.put("LEAPING_LINGERING_POTION", getPotionItem(Potions.LEAPING, Items.LINGERING_POTION));
        map.put("FIRE_RESISTANCE_LINGERING_POTION", getPotionItem(Potions.FIRE_RESISTANCE, Items.LINGERING_POTION));
        map.put("SWIFTNESS_LINGERING_POTION", getPotionItem(Potions.SWIFTNESS, Items.LINGERING_POTION));
        map.put("SLOWNESS_LINGERING_POTION", getPotionItem(Potions.SLOWNESS, Items.LINGERING_POTION));
        map.put("TURTLE_MASTER_LINGERING_POTION", getPotionItem(Potions.TURTLE_MASTER, Items.LINGERING_POTION));
        map.put("WATER_BREATHING_LINGERING_POTION", getPotionItem(Potions.WATER_BREATHING, Items.LINGERING_POTION));
        map.put("HEALING_LINGERING_POTION", getPotionItem(Potions.HEALING, Items.LINGERING_POTION));
        map.put("HARMING_LINGERING_POTION", getPotionItem(Potions.HARMING, Items.LINGERING_POTION));
        map.put("POISON_LINGERING_POTION", getPotionItem(Potions.POISON, Items.LINGERING_POTION));
        map.put("REGENERATION_LINGERING_POTION", getPotionItem(Potions.REGENERATION, Items.LINGERING_POTION));
        map.put("STRENGTH_LINGERING_POTION", getPotionItem(Potions.STRENGTH, Items.LINGERING_POTION));
        map.put("WEAKNESS_LINGERING_POTION", getPotionItem(Potions.WEAKNESS, Items.LINGERING_POTION));
        map.put("LUCK_LINGERING_POTION", getPotionItem(Potions.LUCK, Items.LINGERING_POTION));
        //? if >1.20.4 {
        map.put("WIND_CHARGED_LINGERING_POTION", getPotionItem(Potions.WIND_CHARGED, Items.LINGERING_POTION));
        map.put("WEAVING_LINGERING_POTION", getPotionItem(Potions.WEAVING, Items.LINGERING_POTION));
        map.put("OOZING_LINGERING_POTION", getPotionItem(Potions.OOZING, Items.LINGERING_POTION));
        map.put("INFESTED_LINGERING_POTION", getPotionItem(Potions.INFESTED, Items.LINGERING_POTION));
        //?}
        return map;
    }

    private static Map<String, ItemStack> cat_GOLD() {
        Map<String, ItemStack> map = new LinkedHashMap<>();
        map.put("COAL_ORE", new ItemStack(Blocks.COAL_ORE));
        map.put("DEEPSLATE_COAL_ORE", new ItemStack(Blocks.DEEPSLATE_COAL_ORE));
        map.put("COAL", new ItemStack(Items.COAL));
        map.put("CHARCOAL", new ItemStack(Items.CHARCOAL));

        map.put("IRON_ORE", new ItemStack(Blocks.IRON_ORE));
        map.put("DEEPSLATE_IRON_ORE", new ItemStack(Blocks.DEEPSLATE_IRON_ORE));
        map.put("RAW_IRON", new ItemStack(Items.RAW_IRON));
        map.put("IRON_INGOT", new ItemStack(Items.IRON_INGOT));
        map.put("IRON_BLOCK", new ItemStack(Blocks.IRON_BLOCK));

        map.put("COPPER_ORE", new ItemStack(Blocks.COPPER_ORE));
        map.put("DEEPSLATE_COPPER_ORE", new ItemStack(Blocks.DEEPSLATE_COPPER_ORE));
        map.put("RAW_COPPER", new ItemStack(Items.RAW_COPPER));
        map.put("COPPER_INGOT", new ItemStack(Items.COPPER_INGOT));

        map.put("GOLD_ORE", new ItemStack(Blocks.GOLD_ORE));
        map.put("DEEPSLATE_GOLD_ORE", new ItemStack(Blocks.DEEPSLATE_GOLD_ORE));
        map.put("NETHER_GOLD_ORE", new ItemStack(Blocks.NETHER_GOLD_ORE));
        map.put("RAW_GOLD", new ItemStack(Items.RAW_GOLD));
        map.put("GOLD_INGOT", new ItemStack(Items.GOLD_INGOT));
        map.put("GOLD_BLOCK", new ItemStack(Blocks.GOLD_BLOCK));

        map.put("REDSTONE_ORE", new ItemStack(Blocks.REDSTONE_ORE));
        map.put("DEEPSLATE_REDSTONE_ORE", new ItemStack(Blocks.DEEPSLATE_REDSTONE_ORE));

        map.put("LAPIS_ORE", new ItemStack(Blocks.LAPIS_ORE));
        map.put("DEEPSLATE_LAPIS_ORE", new ItemStack(Blocks.DEEPSLATE_LAPIS_ORE));
        map.put("LAPIS_LAZULI", new ItemStack(Items.LAPIS_LAZULI));
        map.put("LAPIS_BLOCK", new ItemStack(Blocks.LAPIS_BLOCK));

        map.put("DIAMOND_ORE", new ItemStack(Blocks.DIAMOND_ORE));
        map.put("DEEPSLATE_DIAMOND_ORE", new ItemStack(Blocks.DEEPSLATE_DIAMOND_ORE));
        map.put("DIAMOND", new ItemStack(Items.DIAMOND));
        map.put("DIAMOND_BLOCK", new ItemStack(Blocks.DIAMOND_BLOCK));

        map.put("EMERALD_ORE", new ItemStack(Blocks.EMERALD_ORE));
        map.put("DEEPSLATE_EMERALD_ORE", new ItemStack(Blocks.DEEPSLATE_EMERALD_ORE));
        map.put("EMERALD", new ItemStack(Items.EMERALD));
        map.put("EMERALD_BLOCK", new ItemStack(Blocks.EMERALD_BLOCK));

        map.put("NETHER_QUARTZ_ORE", new ItemStack(Blocks.NETHER_QUARTZ_ORE));
        map.put("QUARTZ", new ItemStack(Items.QUARTZ));
        map.put("AMETHYST_SHARD", new ItemStack(Items.AMETHYST_SHARD));

        map.put("ANCIENT_DEBRIS", new ItemStack(Blocks.ANCIENT_DEBRIS));
        map.put("NETHERITE_SCRAP", new ItemStack(Items.NETHERITE_SCRAP));
        map.put("NETHERITE_INGOT", new ItemStack(Items.NETHERITE_INGOT));
        map.put("NETHERITE_BLOCK", new ItemStack(Blocks.NETHERITE_BLOCK));

        map.put("SPONGE", new ItemStack(Blocks.SPONGE));
        map.put("WET_SPONGE", new ItemStack(Blocks.WET_SPONGE));

        map.put("SKELETON_SKULL", new ItemStack(Blocks.SKELETON_SKULL));
        map.put("WITHER_SKELETON_SKULL", new ItemStack(Blocks.WITHER_SKELETON_SKULL));
        map.put("ZOMBIE_HEAD", new ItemStack(Blocks.ZOMBIE_HEAD));
        map.put("CREEPER_HEAD", new ItemStack(Blocks.CREEPER_HEAD));
        map.put("PIGLIN_HEAD", new ItemStack(Blocks.PIGLIN_HEAD));
        map.put("PLAYER_HEAD", new ItemStack(Blocks.PLAYER_HEAD));
        map.put("DRAGON_HEAD", new ItemStack(Blocks.DRAGON_HEAD));
        map.put("DRAGON_EGG", new ItemStack(Blocks.DRAGON_EGG));

        map.put("ENCHANTING_TABLE", new ItemStack(Blocks.ENCHANTING_TABLE));
        map.put("BEACON", new ItemStack(Blocks.BEACON));
        map.put("CONDUIT", new ItemStack(Blocks.CONDUIT));
        map.put("LODESTONE", new ItemStack(Blocks.LODESTONE));
        map.put("RESPAWN_ANCHOR", new ItemStack(Blocks.RESPAWN_ANCHOR));
        map.put("END_CRYSTAL", new ItemStack(Items.END_CRYSTAL));
        map.put("END_PORTAL_FRAME", new ItemStack(Blocks.END_PORTAL_FRAME));

        map.put("NETHER_STAR", new ItemStack(Items.NETHER_STAR));
        map.put("HEART_OF_THE_SEA", new ItemStack(Items.HEART_OF_THE_SEA));
        map.put("NAUTILUS_SHELL", new ItemStack(Items.NAUTILUS_SHELL));
        map.put("SHULKER_SHELL", new ItemStack(Items.SHULKER_SHELL));
        map.put("ECHO_SHARD", new ItemStack(Items.ECHO_SHARD));
        map.put("GILDED_BLACKSTONE", new ItemStack(Blocks.GILDED_BLACKSTONE));
        map.put("SNIFFER_EGG", new ItemStack(Blocks.SNIFFER_EGG));
        //? if >1.20.4 {
        map.put("HEAVY_CORE", new ItemStack(Blocks.HEAVY_CORE));
        map.put("WIND_CHARGE", new ItemStack(Items.WIND_CHARGE));
        //?}

        map.put("ENCHANTED_BOOK", new ItemStack(Items.ENCHANTED_BOOK));
        map.put("KNOWLEDGE_BOOK", new ItemStack(Items.KNOWLEDGE_BOOK));

        map.put("SPAWNER", new ItemStack(Blocks.SPAWNER));
        //? if >=1.20.3 {
        map.put("TRIAL_SPAWNER", new ItemStack(Blocks.TRIAL_SPAWNER));
        //? }
        //? if >1.20.4 {
        map.put("VAULT", new ItemStack(Blocks.VAULT));
        //?}

        map.put("COMMAND_BLOCK", new ItemStack(Blocks.COMMAND_BLOCK));
        map.put("REPEATING_COMMAND_BLOCK", new ItemStack(Blocks.REPEATING_COMMAND_BLOCK));
        map.put("CHAIN_COMMAND_BLOCK", new ItemStack(Blocks.CHAIN_COMMAND_BLOCK));
        map.put("STRUCTURE_BLOCK", new ItemStack(Blocks.STRUCTURE_BLOCK));
        map.put("JIGSAW", new ItemStack(Blocks.JIGSAW));
        map.put("DEBUG_STICK", new ItemStack(Items.DEBUG_STICK));
        map.put("REINFORCED_DEEPSLATE", new ItemStack(Blocks.REINFORCED_DEEPSLATE));
        return map;
    }

    private static void addCategory(String category, Map<String, ItemStack> map) {
        itemStackMap.putAll(map);
        categories.put(category, map);
    }

    //? if >=1.20.5 {
    public static ItemStack getPotionItem(RegistryEntry<Potion> name, Item baseItem) {
        ItemStack potion = new ItemStack(baseItem);
        potion.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(name));
        return potion;
    }
    //? } else if <1.20.5 {
    /*public static ItemStack getPotionItem(Potion potion, Item baseItem) {
        ItemStack stack = new ItemStack(baseItem);
        NbtCompound tag = new NbtCompound();
        tag.putString("Potion", Registries.POTION.getId(potion).toString());
        stack.setNbt(tag);
        return stack;
    }
    *///?}

    public static ItemStack getItemStack(String name) { return itemStackMap.getOrDefault(name, notFound); }

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
}