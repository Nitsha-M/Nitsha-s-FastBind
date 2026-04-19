package com.nitsha.binds.configs;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nitsha.binds.FBLogger;
import com.nitsha.binds.Main;
import com.nitsha.binds.configs.dto.option.ModOptionsData;
import com.nitsha.binds.configs.dto.preset.BindData;
import com.nitsha.binds.configs.dto.preset.PageData;
import com.nitsha.binds.configs.dto.preset.PresetData;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class LegacyMigrator {

    private static final List<MigrationStep<PresetData>> PRESET_MIGRATIONS = new ArrayList<>();

    static {
        registerPresetMigration("5.0.0", preset -> {
            FBLogger.info("Migrated preset " + preset.name + " to 5.0.0 structure!");
        });
    }

    public static void registerPresetMigration(String targetVersion, Consumer<PresetData> migrationAction) {
        PRESET_MIGRATIONS.add(new MigrationStep<>(targetVersion, migrationAction));
    }

    public static void runSystemMigrations() {
        File oldStorage = Storage.COMMON_DIR.resolve("fastbind_presets.json").toFile();
        if (oldStorage.exists() && oldStorage.length() > 0) {
            FBLogger.info("Found legacy fastbind_presets.json, migrating to individual files...");
            try {
                List<PresetData> oldPresets = Storage.load(new TypeToken<List<PresetData>>(){}.getType(), oldStorage, null);
                if (oldPresets != null) {
                    for (int i = 0; i < oldPresets.size(); i++) {
                        PresetData p = oldPresets.get(i);
                        p.index = i;
                        if (p.id == null || p.id.isEmpty()) {
                            p.id = UUID.randomUUID().toString();
                        }
                        
                        p.modVersion = "5.0.0";
                        
                        if (p.pages != null) {
                            for (PageData page : p.pages) {
                                if (page.binds != null) {
                                    List<BindData> validBinds = new ArrayList<>();
                                    for (int j = 0; j < page.binds.size(); j++) {
                                        BindData b = page.binds.get(j);
                                        if (b != null) {
                                            boolean isEmpty = (b.name == null || b.name.isEmpty()) && 
                                                              (b.actions == null || b.actions.isEmpty()) && 
                                                              b.keyCode == 0;
                                            if (!isEmpty) {
                                                b.index = j;
                                                validBinds.add(b);
                                            }
                                        }
                                    }
                                    page.binds = validBinds;
                                }
                            }
                        }
                        
                        migratePresetData(p);
                        Storage.savePreset(p, p.id);
                    }
                }
                Files.move(oldStorage.toPath(), new File(oldStorage.getAbsolutePath() + ".old").toPath());
            } catch (Exception e) {
                FBLogger.error("Failed to migrate fastbind_presets.json: " + e.getMessage());
            }
        }

        File oldOptions = Storage.COMMON_DIR.resolve("fastbind_configs.json5").toFile();
        if (oldOptions.exists() && oldOptions.length() > 0) {
            FBLogger.info("Found legacy fastbind_configs.json5, migrating options...");
            try {
                JsonObject obj = JsonParser.parseReader(new java.io.FileReader(oldOptions)).getAsJsonObject();
                ModOptionsData newOptions = Storage.options;
                
                if (obj.has("holdToOpen")) newOptions.holdToOpen = obj.get("holdToOpen").getAsBoolean();
                if (obj.has("openLastPage")) newOptions.openLastPage = obj.get("openLastPage").getAsBoolean();
                if (obj.has("openLastPreset")) newOptions.openLastPreset = obj.get("openLastPreset").getAsBoolean();
                if (obj.has("keepMovement")) newOptions.keepMovement = obj.get("keepMovement").getAsBoolean();
                if (obj.has("closeOnAction")) newOptions.closeOnAction = obj.get("closeOnAction").getAsBoolean();
                if (obj.has("bindMsg")) newOptions.showActivationMessage = obj.get("bindMsg").getAsBoolean();
                if (obj.has("easterEgg")) newOptions.easterEgg = obj.get("easterEgg").getAsBoolean();
                if (obj.has("lastPage")) newOptions.lastPageIndex = obj.get("lastPage").getAsInt();
                
                Storage.saveModOptions();
                Files.move(oldOptions.toPath(), new File(oldOptions.getAbsolutePath() + ".old").toPath());
            } catch (Exception e) {
                FBLogger.error("Failed to migrate fastbind_configs.json5: " + e.getMessage());
            }
        }
    }

    public static void migratePresetData(PresetData preset) {
        String currentConfigVersion = preset.modVersion;
        
        if (currentConfigVersion == null) {
            currentConfigVersion = "5.0.0"; 
        }

        boolean updated = false;

        for (MigrationStep<PresetData> step : PRESET_MIGRATIONS) {
            if (compareVersions(currentConfigVersion, step.targetVersion) < 0) {
                step.migrationAction.accept(preset);
                currentConfigVersion = step.targetVersion;
                updated = true;
            }
        }

        preset.modVersion = Main.getModVersion();
    }

    private static int compareVersions(String v1, String v2) {
        if (v1 == null) v1 = "0.0.0";
        if (v2 == null) v2 = "0.0.0";
        
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 < num2) return -1;
            if (num1 > num2) return 1;
        }
        return 0;
    }

    private static class MigrationStep<T> {
        String targetVersion;
        Consumer<T> migrationAction;

        MigrationStep(String targetVersion, Consumer<T> migrationAction) {
            this.targetVersion = targetVersion;
            this.migrationAction = migrationAction;
        }
    }
}
