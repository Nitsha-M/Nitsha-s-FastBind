package com.nitsha.binds.configs;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BindsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getGameDir().resolve("nitsha");
    private static final File CONFIG_FILE = CONFIG_DIR.resolve("fastbind.json5").toFile();

    public static List<String[]> bindsList = new ArrayList<>();
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

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            if (CONFIG_FILE.length() == 0) {
                generateEmptyList();
            }
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Type listType = new TypeToken<List<String[]>>() {}.getType();
                bindsList = GSON.fromJson(reader, listType);
                if (bindsList == null) {
                    generateEmptyList();
                }
            } catch (IOException e) {
                generateEmptyList();
                e.printStackTrace();
            }
        } else {
            generateEmptyList();
        }
    }


    public static void generateEmptyList() {
        bindsList.clear();
        bindsList.add(new String[]{"Thank you!", "TRIAL_KEY", "nitsha::welcome"});
        for (int i = 0; i < ((5 * 8) - 1); i++) {
            bindsList.add(DEFAULT_LINE);
        }
        saveConfigFile();
    }

    public static String[] getBind(int index) {
        return bindsList.get(index);
    }

    public static void setBind(int index, String[] newLine) {
        bindsList.set(index, newLine);
        saveConfigFile();
    }

    public static void saveConfigFile() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(bindsList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}