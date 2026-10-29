package com.nitsha.binds.configs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nitsha.binds.FBLogger;
import com.nitsha.binds.bind.BindHandler;
import com.nitsha.binds.configs.adapters.ActionAdapter;
import com.nitsha.binds.configs.adapters.VariableAdapter;
import com.nitsha.binds.configs.dto.option.ModOptionsData;
import com.nitsha.binds.configs.dto.preset.ActionData;
import com.nitsha.binds.configs.dto.preset.PageData;
import com.nitsha.binds.configs.dto.preset.PresetData;
import com.nitsha.binds.configs.dto.preset.PresetsOrderData;
import com.nitsha.binds.configs.dto.variables.VariableData;
import com.nitsha.binds.utils.StorageUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Storage {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ActionData.class, new ActionAdapter())
            .registerTypeAdapter(VariableData.class, new VariableAdapter())
            .disableHtmlEscaping()
            .create();

    private static final String MAIN_DIR_NAME = "nitsha";
    private static final String MOD_DIR_NAME = "fastbind";
    public static final String FILE_EXT = ".fastbind";

    public static final Path COMMON_DIR = StorageUtils.createFolder(MAIN_DIR_NAME);
    public static final Path MOD_DIR = StorageUtils.createFolder(MAIN_DIR_NAME, MOD_DIR_NAME);
    public static final Path PRESETS_DIR = StorageUtils.createFolder(MAIN_DIR_NAME, MOD_DIR_NAME + "/presets");
    public static final Path SOUNDS_DIR = StorageUtils.createFolder(MAIN_DIR_NAME, MOD_DIR_NAME + "/sounds");

    private static final File CONFIG_FILE = MOD_DIR.resolve("fastbind_options.json").toFile();

    public static final Map<String, PresetData> PRESET_REGISTRY = new HashMap<>();
    public static ModOptionsData options = new ModOptionsData();

    private static final File ORDER_FILE = MOD_DIR.resolve("fastbind_presets_order.json").toFile();
    private static PresetsOrderData presetsOrder = new PresetsOrderData();

    // Basic logic for load&save

    public static <T> void save(T config, File targetFile) {
        targetFile.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(targetFile)) {
            GSON.toJson(config, writer);
            FBLogger.info("Saved config to {}", targetFile.getName());
        } catch (IOException e) {
            FBLogger.error("Error saving config to {}", targetFile.getName());
            e.printStackTrace();
        }
    }

    public static <T> T load(Class<T> clazz, File targetFile, T fallback) {
        return load((java.lang.reflect.Type) clazz, targetFile, fallback);
    }

    public static <T> T load(java.lang.reflect.Type typeOfT, File targetFile, T fallback) {
        if (!targetFile.exists()) {
            if (fallback != null) save(fallback, targetFile);
            return fallback;
        }

        try (FileReader reader = new FileReader(targetFile)) {
            T result = GSON.fromJson(reader, typeOfT);
            return result != null ? result : fallback;
        } catch (Exception e) {
            FBLogger.error("Error loading config from {}", targetFile.getName());
            e.printStackTrace();
            return fallback;
        }
    }

    // Presets order

    private static void loadPresetsOrder() {
        presetsOrder = load(PresetsOrderData.class, ORDER_FILE, new PresetsOrderData());

        Set<String> orderedIds = new LinkedHashSet<>();

        if (presetsOrder.ids != null) {
            for (String id : presetsOrder.ids) {
                if (PRESET_REGISTRY.containsKey(id)) {
                    orderedIds.add(id);
                }
            }
        }

        List<String> discoveredIds = new ArrayList<>(PRESET_REGISTRY.keySet());
        Collections.sort(discoveredIds);
        orderedIds.addAll(discoveredIds);

        presetsOrder.ids = new ArrayList<>(orderedIds);
        savePresetsOrder();
    }

    private static void savePresetsOrder() {
        save(presetsOrder, ORDER_FILE);
        BindHandler.invalidateCache();
    }

    // Load presets

    public static void loadAllPresets() {
        LegacyMigrator.runSystemMigrations();
        PRESET_REGISTRY.clear();

        File[] files = PRESETS_DIR.toFile().listFiles((dir, name) -> name.endsWith(FILE_EXT));

        if (files != null) {
            for (File file : files) {
                PresetData preset = load(PresetData.class, file, new PresetData());
                preset.id = file.getName().substring(0, file.getName().length() - FILE_EXT.length());

                LegacyMigrator.migratePresetData(preset);

                if (preset.pages == null) {
                    preset.pages = new ArrayList<>();
                }

                if (preset.pages.isEmpty()) {
                    preset.pages.add(new PageData());
                }

                PRESET_REGISTRY.put(preset.id, preset);
            }
        }

        loadPresetsOrder();

        if (PRESET_REGISTRY.isEmpty()) {
            createNewPreset();
        }

        BindHandler.invalidateCache();
    }

    public static List<PresetData> getSortedPresets() {
        List<PresetData> result = new ArrayList<>();

        for (String id : presetsOrder.ids) {
            PresetData preset = PRESET_REGISTRY.get(id);

            if (preset != null) {
                result.add(preset);
            }
        }

        return result;
    }

    public static PresetData createNewPreset() {
        PresetData preset = new PresetData();
        preset.id = UUID.randomUUID().toString();
        preset.pages.add(new PageData());

        PRESET_REGISTRY.put(preset.id, preset);
        presetsOrder.ids.add(preset.id);

        savePreset(preset, preset.id);
        savePresetsOrder();

        return preset;
    }

    public static boolean deletePreset(String id) {
        if (PRESET_REGISTRY.size() <= 1 || !PRESET_REGISTRY.containsKey(id)) {
            return false;
        }

        File file = PRESETS_DIR.resolve(id + FILE_EXT).toFile();

        if (file.exists() && !file.delete()) {
            return false;
        }

        PRESET_REGISTRY.remove(id);
        presetsOrder.ids.remove(id);
        savePresetsOrder();

        return true;
    }

    public static void movePreset(String id, int direction) {
        int currentIndex = presetsOrder.ids.indexOf(id);
        int targetIndex = currentIndex + direction;

        if (currentIndex < 0 || targetIndex < 0 || targetIndex >= presetsOrder.ids.size()) {
            return;
        }

        presetsOrder.ids.remove(currentIndex);
        presetsOrder.ids.add(targetIndex, id);
        savePresetsOrder();
    }

    public static void savePreset(PresetData preset, String id) {
        save(preset, PRESETS_DIR.resolve(id + FILE_EXT).toFile());
        BindHandler.invalidateCache();
    }

    // Mod options
    public static void loadModOptions() {
        options = load(ModOptionsData.class, CONFIG_FILE, new ModOptionsData());
    }

    public static void saveModOptions() {
        save(options, CONFIG_FILE);
    }
}
