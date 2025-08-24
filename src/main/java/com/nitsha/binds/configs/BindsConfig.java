package com.nitsha.binds.configs;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.utils.FastbindParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BindsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getGameDir().resolve("nitsha");

    private static final BindEntry DEFAULT_LINE = new BindEntry(
            "",
            "STRUCTURE_VOID",
            0,
            new ArrayList<>()
    );

    public static List<List<BindEntry>> presets = new ArrayList<>();
    public static Map<String, Object> configs = new HashMap<>();

    static {
        createConfigDirectory();
    }

    private static void createConfigDirectory() {
        if (!Files.exists(CONFIG_DIR)) {
            try {
                Files.createDirectories(CONFIG_DIR);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't create FastBind config directory: " + CONFIG_DIR, e);
            }
        }
    }

    // Presets
    public static void loadPresets() {
        for (int i = 0; i < 9; i++) {
            File CONFIG_FILE = CONFIG_DIR.resolve("fastbind_preset_" + i + ".json5").toFile();
            List<BindEntry> presetList = new ArrayList<>();

            if (CONFIG_FILE.exists()) {
                if (CONFIG_FILE.length() == 0) {
                    generateEmptyList(i);
                }
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    Type listType = new TypeToken<List<BindEntry>>() {}.getType();
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
        List<BindEntry> presetList = new ArrayList<>();
        presetList.add(new BindEntry("Thank you!", "TRIAL_KEY", 0, List.of("nitsha::welcome")));
        for (int i = 0; i < ((5 * 8) - 1); i++) {
            presetList.add(DEFAULT_LINE);
        }
        presets.add(presetList);
        savePresetFile(fileIndex);
    }

    public static BindEntry getBind(int fileIndex, int index) {
        return presets.get(fileIndex).get(index);
    }

    public static void setBind(int fileIndex, int index, BindEntry newLine) {
        presets.get(fileIndex).set(index, newLine);
        savePresetFile(fileIndex);
    }

    public static void addBindAction(int fileIndex, int bindIndex, int actionIndex, String action) {
        presets.get(fileIndex).get(bindIndex).actions.add(actionIndex, action);
        savePresetFile(fileIndex);
    }

    public static void removeBindAction(int fileIndex, int bindIndex, int actionIndex) {
        List<String> actions = presets.get(fileIndex).get(bindIndex).actions;

        if (actionIndex < 0 || actionIndex >= actions.size()) {
            System.err.println("❌ Wrong index: " + actionIndex + ", array has " + actions.size() + " elements");
            return;
        }

        actions.remove(actionIndex);
        savePresetFile(fileIndex);
    }

    public static void setBindKeyBind(int fileIndex, int index, int keyCode) {
        presets.get(fileIndex).get(index).keyCode = keyCode;
        BindHandler.getAllKeyBind();
        savePresetFile(fileIndex);
    }

    public static void moveBindAction(int fileIndex, int bindIndex, int actionIndex, int direction) {
        List<String> actions = presets.get(fileIndex).get(bindIndex).actions;
        int newIndex = actionIndex + direction;

        if (actionIndex < 0 || actionIndex >= actions.size() || newIndex < 0 || newIndex >= actions.size()) {
            return;
        }
        String temp = actions.get(newIndex);
        actions.set(newIndex, actions.get(actionIndex));
        actions.set(actionIndex, temp);
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

    public static void setNewPresetName(int presetIndex, String newName) {
        List<String> presetsName = (List<String>) configs.get("presets");
        presetsName.set(presetIndex, newName);
        saveConfigFile();
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
                if (!getBooleanConfig("refactorPresetNames", false)) {
                    refactorPresetNames();
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
        List<String> presetsName = IntStream.rangeClosed(1, 9)
                .mapToObj(i -> "Preset " + i)
                .collect(Collectors.toList());

        configs.put("presets", presetsName);
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

    private static void refactorPresetNames() {
        Object presetsObject = configs.get("presets");

        if (!(presetsObject instanceof List)) {
            System.err.println("Error: 'presets' is not in config list");
            return;
        }
        @SuppressWarnings("unchecked")
        List<String> presetsList = (List<String>) presetsObject;

        for (int i = 0; i < presetsList.size(); i++) {
            String currentName = presetsList.get(i);

            if ("Default".equals(currentName)) {
                presetsList.set(i, "Preset " + (i + 1));
            }
        }
        setConfig("refactorPresetNames", true);
    }

    public static String getStringConfig(String key, String defaultValue) {
        Object value = configs.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }

    public static int getIntConfig(String key, int defaultValue) {
        Object value = configs.get(key);
        return value instanceof Number ? ((Number) value).intValue() : defaultValue;
    }

    public static boolean getBooleanConfig(String key, boolean defaultValue) {
        Object value = configs.get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getStringListConfig(String key, List<String> defaultValue) {
        Object value = configs.get(key);
        if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            if (!list.isEmpty() && list.get(0) instanceof String) {
                return (List<String>) list;
            }
        }
        return defaultValue;
    }

    public static void setConfig(String key, Object value) {
        configs.put(key, value);
        saveConfigFile();
    }

    @SuppressWarnings("unchecked")
    public static void addToListConfig(String key, String value) {
        List<String> list = (List<String>) configs.get(key);
        if (list == null) {
            list = new ArrayList<>();
            configs.put(key, list);
        }
        list.add(value);
        saveConfigFile();
    }

    // Old file migration
    public static void migrateOldPresetFileIfExists() {
        File oldFile = CONFIG_DIR.resolve("fastbind.json5").toFile();
        File newFile = CONFIG_DIR.resolve("fastbind_preset_0.json5").toFile();

        if (!oldFile.exists() || newFile.exists()) return;

        try (FileReader reader = new FileReader(oldFile)) {
            Type listType = new TypeToken<List<String[]>>() {}.getType();
            List<String[]> oldPreset = GSON.fromJson(reader, listType);

            if (oldPreset != null && !oldPreset.isEmpty()) {
                List<BindEntry> newPreset = convertStringArrayListToBindEntryList(oldPreset);
                try (FileWriter writer = new FileWriter(newFile)) {
                    GSON.toJson(newPreset, writer);
                }
                MainClass.LOGGER.info("Old config fastbind.json5 migrated to fastbind_preset_0.json5");
                Files.deleteIfExists(oldFile.toPath());
            } else {
                //? if >=1.17 {
                MainClass.LOGGER.error("Old config is empty or corrupted");
                //? } else {
                //MainClass.LOGGER.severe("Old config is empty or corrupted");
                //? }
            }
        } catch (IOException e) {
            //? if >=1.17 {
            MainClass.LOGGER.error("Old config migration error:");
            //? } else {
            //MainClass.LOGGER.severe("Old config migration error:");
            //? }
            e.printStackTrace();
        }
    }

    // Миграция старых 9 файлов (String[]) в новый формат (BindEntry)
    public static void migrateOldMultiPresetFiles() {
        if (getBooleanConfig("migrateConfigToV3", false)) return;
        for (int i = 0; i < 9; i++) {
            File file = CONFIG_DIR.resolve("fastbind_preset_" + i + ".json5").toFile();
            if (!file.exists()) continue;

            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<String[]>>() {}.getType();
                List<String[]> oldPreset = GSON.fromJson(reader, listType);

                if (oldPreset == null || oldPreset.isEmpty() || !(oldPreset.get(0) instanceof String[])) {
                    continue;
                }

                List<BindEntry> newPreset = convertStringArrayListToBindEntryList(oldPreset);

                try (FileWriter writer = new FileWriter(file)) {
                    GSON.toJson(newPreset, writer);
                }
                MainClass.LOGGER.info("Migrated fastbind_preset_" + i + ".json5 to new format");
            } catch (IOException e) {
                //? if >=1.17 {
                MainClass.LOGGER.error("Migration error for file fastbind_preset_" + i + ".json5:");
                //? } else {
                //MainClass.LOGGER.severe("Migration error for file fastbind_preset_" + i + ".json5:");
                //? }
                e.printStackTrace();
            }
        }
        setConfig("migrateConfigToV3", true);
    }

    private static List<BindEntry> convertStringArrayListToBindEntryList(List<String[]> oldPreset) {
        List<BindEntry> newPreset = new ArrayList<>();
        for (String[] arr : oldPreset) {
            String name = arr.length > 0 ? arr[0] : "";
            String icon = arr.length > 1 ? arr[1] : "STRUCTURE_VOID";
            int keyCode = 0;
            List<String> actions = new ArrayList<>();
            if (!arr[2].isEmpty()) actions.add(FastbindParser.toAction(1, arr[2]));
            newPreset.add(new BindEntry(name, icon, keyCode, actions));
        }
        return newPreset;
    }

}