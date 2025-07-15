package com.nitsha.binds.configs;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nitsha.binds.MainClass;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BindsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getGameDir().resolve("nitsha");

    public static List<List<String[]>> presets = new ArrayList<>();
    public static Map<String, Object> configs = new HashMap<>();
    private static final String[] DEFAULT_LINE = {"", "STRUCTURE_VOID", ""};

    static {
        createConfigDirectory();
    }

    private static void createConfigDirectory() {
        if (!Files.exists(CONFIG_DIR)) {
            try {
                Files.createDirectories(CONFIG_DIR);
            } catch (IOException e) {
                throw new RuntimeException("Не удалось создать папку настроек: " + CONFIG_DIR, e);
            }
        }
    }

    // Presets
    public static void loadPresets() {
        for (int i = 0; i < 9; i++) {
            File CONFIG_FILE = CONFIG_DIR.resolve("fastbind_preset_" + i + ".json5").toFile();
            List<String[]> presetList = new ArrayList<>();
            if (CONFIG_FILE.exists()) {
                if (CONFIG_FILE.length() == 0) {
                    generateEmptyList(i);
                }
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    Type listType = new TypeToken<List<String[]>>() {}.getType();
                    presetList = GSON.fromJson(reader, listType);
                    if (presetList == null) {
                        generateEmptyList(i);
                    }
                } catch (IOException e) {
                    generateEmptyList(i);
                    e.printStackTrace();
                }
                presets.add(presetList);
            } else {
                generateEmptyList(i);
            }
        }
    }

    public static void generateEmptyList(int fileIndex) {
        List<String[]> presetList = new ArrayList<>();
        presetList.add(new String[]{"Thank you!", "TRIAL_KEY", "nitsha::welcome"});
        for (int i = 0; i < ((5 * 8) - 1); i++) {
            presetList.add(DEFAULT_LINE);
        }
        presets.add(presetList);
        savePresetFile(fileIndex);
    }

    public static String[] getBind(int fileIndex, int index) {
        return presets.get(fileIndex).get(index);
    }

    public static void setBind(int fileIndex, int index, String[] newLine) {
        presets.get(fileIndex).set(index, newLine);
        savePresetFile(fileIndex);
    }

    public static void savePresetFile(int fileIndex) {
        File CONFIG_FILE = CONFIG_DIR.resolve("fastbind_preset_" + fileIndex + ".json5").toFile();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(presets.get(fileIndex), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Configs
    public static void loadConfigs() {
        File CONFIG_FILE = CONFIG_DIR.resolve("fastbind_configs.json5").toFile();
        if (CONFIG_FILE.exists()) {
            if (CONFIG_FILE.length() == 0) {
                generateNewConfig();
            }
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
                configs = GSON.fromJson(reader, mapType);
                if (configs == null) {
                    generateNewConfig();
                }
            } catch (IOException e) {
                generateNewConfig();
                e.printStackTrace();
            }
        } else {
            generateNewConfig();
        }
    }

    public static void generateNewConfig() {
        List<String> presetsName = new ArrayList<>(Collections.nCopies(9, "Default"));
        configs.put("presets", presetsName);
        saveConfigFile();
    }

    public static void setNewPresetName(int presetIndex, String newName) {
        List<String> presetsName = (List<String>) configs.get("presets");
        presetsName.set(presetIndex, newName);
        saveConfigFile();
    }

    public static void saveConfigFile() {
        File CONFIG_FILE = CONFIG_DIR.resolve("fastbind_configs.json5").toFile();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configs, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Old file migration
    public static void migrateOldPresetFileIfExists() {
        boolean moved = false;
        File oldFile = CONFIG_DIR.resolve("fastbind.json5").toFile();
        File newFile = CONFIG_DIR.resolve("fastbind_preset_0.json5").toFile();

        if (!oldFile.exists() || newFile.exists()) return;

        try (FileReader reader = new FileReader(oldFile)) {
            Type listType = new TypeToken<List<String[]>>() {}.getType();
            List<String[]> oldPreset = GSON.fromJson(reader, listType);
            if (oldPreset != null && !oldPreset.isEmpty()) {
                try (FileWriter writer = new FileWriter(newFile)) {
                    GSON.toJson(oldPreset, writer);
                    moved = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!oldFile.delete()) {
                }
                MainClass.LOGGER.info("Old config fastbind.json5 was successfully migrate to fastbind_preset_0.json5");
            } else {
                MainClass.LOGGER.error("Old config is empty or corrupted");
            }
        } catch (IOException e) {
            MainClass.LOGGER.error("Old config migration error:");
            e.printStackTrace();
        }

        if (moved) {
            try {
                Files.delete(oldFile.toPath());
                MainClass.LOGGER.info("Old config file " + oldFile.getName() + " was successfully deleted.");
            } catch (IOException e) {
                MainClass.LOGGER.error("Couldn't delete old config file " + oldFile.getName() + ":");
                e.printStackTrace();
            }
        }
    }
}