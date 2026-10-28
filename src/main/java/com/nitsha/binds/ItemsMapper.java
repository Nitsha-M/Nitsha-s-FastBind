package com.nitsha.binds;

import com.nitsha.binds.mixin.CreativeModeTabPreviewAccessor;
import com.nitsha.binds.preview.PreviewComponentContext;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import net.fabricmc.fabric.impl.resource.pack.ModResourcePackCreator;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public class ItemsMapper {

    private static final Set<Item> SINGLE_ICON_ITEMS = Set.of(Items.PAINTING, Items.GOAT_HORN);
    private static Map<Item, DataComponentMap> itemComponents = Map.of();
    private static Map<String, List<IconEntry>> categories = Map.of();
    private static Map<String, ItemStackTemplate> iconsByKey = Map.of();
    private static List<IconEntry> randomItems = List.of();

    public record IconEntry(String key, ItemStackTemplate icon) {
    }

    public static void initialize() {
        Executor executor = Runnable::run;

        DirectoryValidator validator = new DirectoryValidator(path -> false);
        PackRepository packs = new PackRepository(new ServerPacksSource(validator), new ModResourcePackCreator(PackType.SERVER_DATA));
        WorldLoader.PackConfig config = new WorldLoader.PackConfig(packs, WorldDataConfiguration.DEFAULT, false, true);

        try (var resources = config.createResourceManager().getSecond()) {
            LayeredRegistryAccess<RegistryLayer> layers = RegistryLayer.createRegistryAccess();

            RegistryAccess.Frozen staticRegistries = layers.getLayer(RegistryLayer.STATIC);
            List<Registry.PendingTags<?>> tags = TagLoader.loadTagsForExistingRegistries(resources, staticRegistries);
            List<HolderLookup.RegistryLookup<?>> staticLookups = TagLoader.buildUpdatedLookups(staticRegistries, tags);

            RegistryAccess.Frozen worldRegistries = RegistryDataLoader.load(resources, staticLookups, DynamicRegistries.getWorldRegistries(), executor).join();
            List<HolderLookup.RegistryLookup<?>> worldLookups = Stream.concat(staticLookups.stream(), worldRegistries.listRegistries()).toList();
            RegistryAccess.Frozen dimensions = RegistryDataLoader.load(resources, worldLookups, RegistryDataLoader.DIMENSION_REGISTRIES, executor).join();
            layers = layers.replaceFrom(RegistryLayer.WORLD, worldRegistries, dimensions);

            ReloadableServerRegistries.LoadResult loaded = ReloadableServerRegistries.reload(layers, tags, resources, executor).join();
            HolderLookup.Provider registries = loaded.lookupWithUpdatedTags();

            Map<Item, DataComponentMap> prepared = new HashMap<>();

            var pendingComponents = BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registries);

            for (var pending : pendingComponents) {
                if (!pending.key().equals(Registries.ITEM)) {
                    continue;
                }

                pending.forEach((holder, components) -> {
                    prepared.put((Item) holder.value(), components);
                });
            }

            itemComponents = Map.copyOf(prepared);
            categories = buildCategories(registries, pendingComponents);

            Map<String, ItemStackTemplate> preparedIcons = new HashMap<>();
            List<IconEntry> preparedRandomItems = new ArrayList<>();

            for (List<IconEntry> entries : categories.values()) {
                for (IconEntry entry : entries) {
                    preparedIcons.put(entry.key(), entry.icon());
                    preparedRandomItems.add(entry);
                }
            }

            for (Item item : BuiltInRegistries.ITEM) {
                if (item == Items.AIR) continue;

                String key = BuiltInRegistries.ITEM.getKey(item).toString();

                if (!preparedIcons.containsKey(key)) {
                    preparedIcons.put(key, ItemStackTemplate.fromNonEmptyStack(createIcon(item)));
                }
            }

            iconsByKey = Map.copyOf(preparedIcons);
            randomItems = List.copyOf(preparedRandomItems);
        }
    }

    private static Map<String, List<IconEntry>> buildCategories(HolderLookup.Provider registries, List<DataComponentInitializers.PendingComponents<?>> pendingComponents) {
        Map<String, List<IconEntry>> prepared = new LinkedHashMap<>();
        Map<Item, List<IconEntry>> variants = new HashMap<>();
        Map<Holder<?>, DataComponentMap> previewComponents = new HashMap<>();

        for (String category : List.of("blocks", "tools", "colored", "foods", "gold", "mods")) {
            prepared.put(category, new ArrayList<>());
        }

        for (var pending : pendingComponents) {
            pending.forEach((holder, components) -> {
                previewComponents.put(holder, components);
            });
        }

        PreviewComponentContext.run(previewComponents, () -> {
            CreativeModeTab.ItemDisplayParameters parameters = new CreativeModeTab.ItemDisplayParameters(FeatureFlags.VANILLA_SET, true, registries);
            List<ResourceKey<CreativeModeTab>> orderedTabs = List.of(CreativeModeTabs.NATURAL_BLOCKS, CreativeModeTabs.BUILDING_BLOCKS, CreativeModeTabs.FUNCTIONAL_BLOCKS, CreativeModeTabs.REDSTONE_BLOCKS, CreativeModeTabs.COLORED_BLOCKS, CreativeModeTabs.SPAWN_EGGS, CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.COMBAT, CreativeModeTabs.FOOD_AND_DRINKS, CreativeModeTabs.INGREDIENTS, CreativeModeTabs.OP_BLOCKS);

            for (ResourceKey<CreativeModeTab> tabKey : orderedTabs) {
                CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(tabKey).orElseThrow().value();
                String category = categoryForTab(tabKey);
                CreativeModeTab.Output output = (stack, visibility) -> {
                    if (stack.isEmpty() || visibility == CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY || !stack.getItem().isEnabled(parameters.enabledFeatures())) {
                        return;
                    }

                    Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());

                    if (!id.getNamespace().equals("minecraft") || stack.is(Items.STRUCTURE_VOID) || stack.is(Items.STRUCTURE_BLOCK) || stack.is(Items.BARRIER) || stack.is(Items.LIGHT)) {
                        return;
                    }

                    addIcon(prepared.get(category), variants, stack);
                };

                CreativeModeTab.DisplayItemsGenerator generator = ((CreativeModeTabPreviewAccessor) tab).nitsha$getDisplayItemsGenerator();
                generator.accept(parameters, output);
            }

            for (int level = 0; level <= 15; level++) {
                ItemStack light = createIcon(Items.LIGHT);
                light.set(DataComponents.BLOCK_STATE, new net.minecraft.world.item.component.BlockItemStateProperties(Map.of("level", Integer.toString(level))));
                ItemStackTemplate template = ItemStackTemplate.fromNonEmptyStack(light);
                prepared.get("mods").add(new IconEntry("minecraft:light_" + level, template));
            }

            for (Item item : BuiltInRegistries.ITEM) {
                if (!BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("minecraft")) {
                    addIcon(prepared.get("mods"), variants, createIcon(item));
                }
            }
        });

        return removeCategoryDuplicates(prepared);
    }

    private static Map<String, List<IconEntry>> removeCategoryDuplicates(Map<String, List<IconEntry>> prepared) {
        List<String> priority = List.of("colored", "foods", "tools", "gold", "mods", "blocks");
        Map<Item, String> owners = new HashMap<>();
        Map<String, Map<String, IconEntry>> unique = new LinkedHashMap<>();

        for (String category : prepared.keySet()) {
            unique.put(category, new LinkedHashMap<>());
        }

        for (String category : priority) {
            for (IconEntry entry : prepared.get(category)) {
                owners.putIfAbsent(entry.icon().item().value(), category);
            }
        }

        for (String category : priority) {
            for (IconEntry entry : prepared.get(category)) {
                String owner = owners.get(entry.icon().item().value());
                unique.get(owner).putIfAbsent(entry.key(), entry);
            }
        }

        Map<String, List<IconEntry>> result = new LinkedHashMap<>();
        unique.forEach((category, entries) -> {
            result.put(category, List.copyOf(entries.values()));
        });
        return Collections.unmodifiableMap(result);
    }

    private static String categoryForTab(ResourceKey<CreativeModeTab> tab) {
        if (tab.equals(CreativeModeTabs.NATURAL_BLOCKS) || tab.equals(CreativeModeTabs.BUILDING_BLOCKS) || tab.equals(CreativeModeTabs.FUNCTIONAL_BLOCKS) || tab.equals(CreativeModeTabs.REDSTONE_BLOCKS)) {
            return "blocks";
        }

        if (tab.equals(CreativeModeTabs.COLORED_BLOCKS) || tab.equals(CreativeModeTabs.SPAWN_EGGS)) {
            return "colored";
        }

        if (tab.equals(CreativeModeTabs.TOOLS_AND_UTILITIES) || tab.equals(CreativeModeTabs.COMBAT)) {
            return "tools";
        }

        if (tab.equals(CreativeModeTabs.FOOD_AND_DRINKS)) {
            return "foods";
        }

        if (tab.equals(CreativeModeTabs.INGREDIENTS)) {
            return "gold";
        }

        return "mods";
    }

    private static void addIcon(List<IconEntry> category, Map<Item, List<IconEntry>> variants, ItemStack source) {
        ItemStack icon = SINGLE_ICON_ITEMS.contains(source.getItem()) ? createIcon(source.getItem()) : createIcon(source);
        ItemStackTemplate template = ItemStackTemplate.fromNonEmptyStack(icon);
        List<IconEntry> itemVariants = variants.computeIfAbsent(source.getItem(), item -> new ArrayList<>());
        IconEntry entry = null;

        for (IconEntry variant : itemVariants) {
            if (variant.icon().equals(template)) {
                entry = variant;
                break;
            }
        }

        if (entry == null) {
            String id = BuiltInRegistries.ITEM.getKey(source.getItem()).toString();
            String key = itemVariants.isEmpty() ? id : id + "#" + itemVariants.size();
            entry = new IconEntry(key, template);
            itemVariants.add(entry);
        }

        if (!category.contains(entry)) {
            category.add(entry);
        }
    }

    public static Map<String, List<IconEntry>> getCategories() {
        return categories;
    }

    public static Map<Item, DataComponentMap> getItemComponents() {
        return itemComponents;
    }

    public static ItemStack createIcon(Item item) {
        DataComponentMap components = itemComponents.get(item);

        if (components == null) {
            throw new IllegalStateException("Компоненты не подготовлены: " + BuiltInRegistries.ITEM.getKey(item));
        }

        ItemStack icon = new ItemStack(Holder.direct(item, components), 1);
        prepareNestedIcons(icon);
        return icon;
    }

    public static ItemStack createIcon(ItemStack source) {
        ItemStack icon = createIcon(source.getItem());
        icon.applyComponents(source.getComponentsPatch());
        prepareNestedIcons(icon);
        return icon;
    }

    public static ItemStack createIcon(ItemStackTemplate template) {
        ItemStack icon = createIcon(template.item().value());
        icon.applyComponents(template.components());
        prepareNestedIcons(icon);
        return icon;
    }

    private static void prepareNestedIcons(ItemStack icon) {
        PotDecorations decorations = icon.get(DataComponents.POT_DECORATIONS);

        if (decorations != null) {
            PotDecorations previewDecorations = new PotDecorations(decorations.back().map(ItemsMapper::createPreviewTemplate), decorations.left().map(ItemsMapper::createPreviewTemplate), decorations.right().map(ItemsMapper::createPreviewTemplate), decorations.front().map(ItemsMapper::createPreviewTemplate));
            icon.set(DataComponents.POT_DECORATIONS, previewDecorations);
        }
    }

    private static ItemStackTemplate createPreviewTemplate(ItemStackTemplate template) {
        Item item = template.item().value();
        DataComponentMap components = itemComponents.get(item);

        if (components == null) {
            throw new IllegalStateException("Компоненты вложенного предмета не подготовлены: " + BuiltInRegistries.ITEM.getKey(item));
        }

        return new ItemStackTemplate(Holder.direct(item, components), template.count(), template.components());
    }

    public static ItemStack getItemStack(String key) {
        ItemStackTemplate icon = key == null ? null : iconsByKey.get(key);

        if (icon != null) {
            return createIcon(icon);
        }

        Identifier id = key == null ? null : Identifier.tryParse(key);
        icon = id == null ? null : iconsByKey.get(id.toString());

        if (icon != null) {
            return createIcon(icon);
        }

        return id != null && id.equals(BuiltInRegistries.ITEM.getKey(Items.AIR)) ? ItemStack.EMPTY : createIcon(Items.BARRIER);
    }

    public static IconEntry getRandomItem() {
        return randomItems.isEmpty() ? null : randomItems.get(ThreadLocalRandom.current().nextInt(randomItems.size()));
    }
}
