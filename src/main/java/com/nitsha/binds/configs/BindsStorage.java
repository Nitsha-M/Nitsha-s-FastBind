package com.nitsha.binds.configs;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BindsStorage {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(), new JsonDeserializer<Map<String, Object>>() {
                @Override
                public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return (Map<String, Object>) parseJsonElement(json);
                }

                private Object parseJsonElement(JsonElement element) {
                    if (element.isJsonObject()) {
                        Map<String, Object> map = new HashMap<>();
                        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                            map.put(entry.getKey(), parseJsonElement(entry.getValue()));
                        }
                        return map;
                    } else if (element.isJsonArray()) {
                        List<Object> list = new ArrayList<>();
                        for (JsonElement item : element.getAsJsonArray()) {
                            list.add(parseJsonElement(item));
                        }
                        return list;
                    } else if (element.isJsonPrimitive()) {
                        JsonPrimitive primitive = element.getAsJsonPrimitive();
                        if (primitive.isBoolean()) {
                            return primitive.getAsBoolean();
                        } else if (primitive.isNumber()) {
                            Number num = primitive.getAsNumber();
                            if (num.doubleValue() == num.longValue()) {
                                return num.intValue();
                            }
                            return num.doubleValue();
                        } else {
                            return primitive.getAsString();
                        }
                    } else if (element.isJsonNull()) {
                        return null;
                    }
                    return null;
                }
            })
            .registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                @Override
                public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                    if (src == src.longValue()) {
                        return new JsonPrimitive(src.longValue());
                    }
                    return new JsonPrimitive(src);
                }
            })
            .create();
    //? if fabric {
    private static final Path CONFIG_DIR = net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir().resolve("nitsha");
    //? } elif neoforge {
    // private static final Path CONFIG_DIR = net.neoforged.fml.loading.FMLPaths.GAMEDIR.get().resolve("nitsha");
    //? } elif forge {
    // private static final Path CONFIG_DIR = net.minecraftforge.fml.loading.FMLPaths.GAMEDIR.get().resolve("nitsha");
    //? }

    private static final File STORAGE_FILE = CONFIG_DIR.resolve("fastbind_presets.json").toFile();

    public static List<Preset> presets = new ArrayList<>();
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

    public static void load() {
        File migrationTrigger = CONFIG_DIR.resolve("fastbind_preset_8.json5").toFile();
        if (migrationTrigger.exists()) {
            migrate();
            return;
        }

        if (STORAGE_FILE.exists()) {
            try (FileReader reader = new FileReader(STORAGE_FILE)) {
                Type listType = new TypeToken<List<Preset>>() {
                }.getType();
                presets = GSON.fromJson(reader, listType);
                if (presets == null || presets.isEmpty()) {
                    createDefault();
                }
            } catch (IOException e) {
                e.printStackTrace();
                createDefault();
            }
        } else {
            createDefault();
        }
    }

    private static void migrate() {
        System.out.println("Migrating old FastBind configs...");
        presets = new ArrayList<>();

        List<String> presetNames = new ArrayList<>();
        File configFile = CONFIG_DIR.resolve("fastbind_configs.json5").toFile();
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                Type mapType = new TypeToken<Map<String, Object>>() {
                }.getType();
                Map<String, Object> configMap = GSON.fromJson(reader, mapType);
                if (configMap != null && configMap.containsKey("presets")) {
                    Object presetsObj = configMap.get("presets");
                    if (presetsObj instanceof List) {
                        presetNames = (List<String>) presetsObj;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 9; i++) {
            String presetName = (i < presetNames.size()) ? presetNames.get(i) : "Preset " + (i + 1);
            File oldFile = CONFIG_DIR.resolve("fastbind_preset_" + i + ".json5").toFile();
            if (!oldFile.exists()) {
                addPreset(presetName);
                continue;
            }

            try (FileReader reader = new FileReader(oldFile)) {
                Type listType = new TypeToken<List<OldBind>>() {
                }.getType();
                List<OldBind> oldBinds = GSON.fromJson(reader, listType);

                if (oldBinds == null) {
                    addPreset(presetName);
                    continue;
                }

                List<Page> pages = new ArrayList<>();
                List<Bind> currentBinds = new ArrayList<>();

                for (OldBind ob : oldBinds) {
                    List<Map<String, Object>> newActions = new ArrayList<>();
                    if (ob.actions != null) {
                        for (String action : ob.actions) {
                            String[] parts = action.split("::", 3);
                            if (parts.length < 3)
                                continue;

                            Map<String, Object> newAction = new HashMap<>();
                            String type = parts[1];
                            String value = parts[2];

                            switch (type) {
                                case "fastbind-keybind":
                                    newAction.put("type", "keybind");
                                    try {
                                        newAction.put("value", Integer.parseInt(value));
                                    } catch (NumberFormatException e) {
                                        newAction.put("value", 0);
                                    }
                                    break;
                                case "fastbind-delay":
                                    newAction.put("type", "delay");
                                    try {
                                        newAction.put("value", Integer.parseInt(value));
                                    } catch (NumberFormatException e) {
                                        newAction.put("value", 0);
                                    }
                                    break;
                                case "fastbind-command":
                                    newAction.put("type", "command");
                                    newAction.put("value", value);
                                    break;
                            }
                            if (!newAction.isEmpty()) {
                                newActions.add(newAction);
                            }
                        }
                    }

                    currentBinds.add(new Bind(ob.name, ob.icon, ob.keyCode, newActions));

                    if (currentBinds.size() == 8) {
                        pages.add(new Page(new ArrayList<>(currentBinds)));
                        currentBinds.clear();
                    }
                }

                if (!currentBinds.isEmpty()) {
                    pages.add(new Page(new ArrayList<>(currentBinds)));
                }

                if (pages.isEmpty()) {
                    pages.add(createDefaultPage());
                }

                presets.add(new Preset(presetName, pages));

            } catch (IOException e) {
                e.printStackTrace();
                addPreset(presetName);
            }
        }

        save();

        // Rename old files
        for (int i = 0; i < 9; i++) {
            File oldFile = CONFIG_DIR.resolve("fastbind_preset_" + i + ".json5").toFile();
            if (oldFile.exists()) {
                oldFile.renameTo(CONFIG_DIR.resolve("fastbind_preset_" + i + ".json5.old").toFile());
            }
        }
    }

    private static class OldBind {
        String name;
        String icon;
        int keyCode;
        List<String> actions;
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(STORAGE_FILE)) {
            GSON.toJson(presets, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDefault() {
        presets = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            addPreset("Preset " + i);
        }
    }

    public static void addPreset(String name) {
        List<Page> pages = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            pages.add(createDefaultPage());
        }
        presets.add(new Preset(name, pages));
        save();
    }

    private static Page createDefaultPage() {
        List<Bind> binds = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            binds.add(new Bind("", "minecraft:structure_void", 0, new ArrayList<>()));
        }
        return new Page(binds);
    }

    public static void removePreset(int index) {
        if (presets.size() <= 1) {
            System.err.println("Cannot remove the last preset.");
            return;
        }
        if (index >= 0 && index < presets.size()) {
            presets.remove(index);
            save();
        }
    }

    public static void swapPresets(int index1, int index2) {
        if (index1 >= 0 && index1 < presets.size() && index2 >= 0 && index2 < presets.size()) {
            Collections.swap(presets, index1, index2);
            save();
        }
    }

    public static void addPage(int presetIndex) {
        if (presetIndex >= 0 && presetIndex < presets.size()) {
            presets.get(presetIndex).pages.add(createDefaultPage());
            save();
        }
    }

    public static void removePage(int presetIndex, int pageIndex) {
        if (presetIndex >= 0 && presetIndex < presets.size()) {
            List<Page> pages = presets.get(presetIndex).pages;
            if (pages.size() <= 1) {
                System.err.println("Cannot remove the last page of a preset.");
                return;
            }
            if (pageIndex >= 0 && pageIndex < pages.size()) {
                pages.remove(pageIndex);
                save();
            }
        }
    }

    public static void renamePreset(int presetIndex, String newName) {
        if (presetIndex >= 0 && presetIndex < presets.size()) {
            presets.get(presetIndex).name = newName;
            save();
        }
    }

    // Helper methods for compatibility with old BindsConfig API
    public static Bind getBind(int presetIndex, int bindIndex) {
        if (presetIndex < 0 || presetIndex >= presets.size()) {
            return new Bind("", "minecraft:structure_void", 0, new ArrayList<>());
        }

        Preset preset = presets.get(presetIndex);
        int pageIndex = bindIndex / 8;
        int bindInPage = bindIndex % 8;

        if (pageIndex < 0 || pageIndex >= preset.pages.size()) {
            return new Bind("", "minecraft:structure_void", 0, new ArrayList<>());
        }

        Page page = preset.pages.get(pageIndex);
        if (bindInPage < 0 || bindInPage >= page.binds.size()) {
            return new Bind("", "minecraft:structure_void", 0, new ArrayList<>());
        }

        return page.binds.get(bindInPage);
    }

    public static void setBind(int presetIndex, int bindIndex, Bind newBind) {
        if (presetIndex < 0 || presetIndex >= presets.size()) {
            return;
        }

        Preset preset = presets.get(presetIndex);
        int pageIndex = bindIndex / 8;
        int bindInPage = bindIndex % 8;

        if (pageIndex < 0 || pageIndex >= preset.pages.size()) {
            return;
        }

        Page page = preset.pages.get(pageIndex);
        if (bindInPage < 0 || bindInPage >= page.binds.size()) {
            return;
        }

        page.binds.set(bindInPage, newBind);
        save();
    }

    public static void addBindAction(int presetIndex, int bindIndex, int actionIndex, Map<String, Object> action) {
        Bind bind = getBind(presetIndex, bindIndex);
        if (bind != null && bind.actions != null) {
            if (actionIndex < 0 || actionIndex > bind.actions.size()) {
                bind.actions.add(action);
            } else {
                bind.actions.add(actionIndex, action);
            }
            setBind(presetIndex, bindIndex, bind);
        }
    }

    public static void removeBindAction(int presetIndex, int bindIndex, int actionIndex) {
        Bind bind = getBind(presetIndex, bindIndex);
        if (bind != null && bind.actions != null) {
            if (actionIndex < 0 || actionIndex >= bind.actions.size()) {
                System.err
                        .println("❌ Wrong index: " + actionIndex + ", array has " + bind.actions.size() + " elements");
                return;
            }
            bind.actions.remove(actionIndex);
            setBind(presetIndex, bindIndex, bind);
        }
    }

    public static void setBindKeyBind(int presetIndex, int bindIndex, int keyCode) {
        Bind bind = getBind(presetIndex, bindIndex);
        if (bind != null) {
            bind.keyCode = keyCode;
            setBind(presetIndex, bindIndex, bind);
            BindHandler.getAllKeyBind();
        }
    }

    public static void moveBindAction(int presetIndex, int bindIndex, int actionIndex, int direction) {
        Bind bind = getBind(presetIndex, bindIndex);
        if (bind != null && bind.actions != null) {
            int newIndex = actionIndex + direction;

            if (actionIndex < 0 || actionIndex >= bind.actions.size() ||
                    newIndex < 0 || newIndex >= bind.actions.size()) {
                return;
            }

            Map<String, Object> temp = bind.actions.get(newIndex);
            bind.actions.set(newIndex, bind.actions.get(actionIndex));
            bind.actions.set(actionIndex, temp);
            setBind(presetIndex, bindIndex, bind);
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
}
