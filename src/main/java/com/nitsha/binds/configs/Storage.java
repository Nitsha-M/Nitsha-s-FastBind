package com.nitsha.binds.configs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nitsha.binds.FBLogger;
import com.nitsha.binds.bind.BindHandler;
import com.nitsha.binds.configs.adapters.ActionAdapter;
import com.nitsha.binds.configs.dto.option.ModOptionsData;
import com.nitsha.binds.configs.dto.preset.ActionData;
import com.nitsha.binds.configs.dto.preset.PresetData;
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
            .disableHtmlEscaping()
            .create();

    private static final String MAIN_DIR_NAME = "nitsha";
    private static final String MOD_DIR_NAME = "fastbind";
    public static final String FILE_EXT = ".fastbind";

    public static final Path COMMON_DIR = StorageUtils.createFolder(MAIN_DIR_NAME);
    public static final Path MOD_DIR = StorageUtils.createFolder(MAIN_DIR_NAME, MOD_DIR_NAME);
    public static final Path PRESETS_DIR = StorageUtils.createFolder(MAIN_DIR_NAME, MOD_DIR_NAME + "/presets");

    private static final File CONFIG_FILE = MOD_DIR.resolve("fastbind_options.json").toFile();

    public static final Map<String, PresetData> PRESET_REGISTRY = new HashMap<>();
    public static ModOptionsData options = new ModOptionsData();

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

    public static void loadAllPresets() {
        LegacyMigrator.runSystemMigrations();
        
        File[] files = PRESETS_DIR.toFile().listFiles((dir, name) -> name.endsWith(FILE_EXT));

        if (files != null && files.length > 0) {
            for (File file : files) {
                PresetData loadedPreset = load(PresetData.class, file, new PresetData());
                loadedPreset.id = file.getName().replace(FILE_EXT, "");
                
                LegacyMigrator.migratePresetData(loadedPreset);

                PRESET_REGISTRY.put(file.getName(), loadedPreset);
            }
            FBLogger.info("Presets loaded: {}", PRESET_REGISTRY.size());
        } else {
            createNewPreset();
            FBLogger.info("Created default preset");
        }
        BindHandler.invalidateCache();
    }

    public static List<PresetData> getSortedPresets() {
        List<PresetData> list = new ArrayList<>(PRESET_REGISTRY.values());
        list.sort(Comparator.comparingInt(p -> p.index));
        return list;
    }

    public static void createNewPreset() {
        String newId = UUID.randomUUID().toString();

        PresetData preset = new PresetData();
        preset.id = newId;

        Storage.PRESET_REGISTRY.put(newId, preset);
        Storage.save(preset, PRESETS_DIR.resolve(newId + FILE_EXT).toFile());
        BindHandler.invalidateCache();
    }

    public static void deletePreset(String fileName) {
        PRESET_REGISTRY.remove(fileName);

        File file = PRESETS_DIR.resolve(fileName + FILE_EXT).toFile();
        if (file.exists()) {
            file.delete();
            FBLogger.info("Preset {} deleted", fileName);
        }
        BindHandler.invalidateCache();
    }

    public static void swapPresets(String fileName1, String fileName2) {
        PresetData p1 = PRESET_REGISTRY.get(fileName1);
        PresetData p2 = PRESET_REGISTRY.get(fileName2);

        if (p1 != null && p2 != null) {
            FBLogger.info("Swap presets: {} to {}", p1.index, p2.index);
            int tempPos = p1.index;
            p1.index = p2.index;
            p2.index = tempPos;

            Storage.save(p1, PRESETS_DIR.resolve(fileName1 + FILE_EXT).toFile());
            Storage.save(p2, PRESETS_DIR.resolve(fileName2 + FILE_EXT).toFile());
            BindHandler.invalidateCache();
        }
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
