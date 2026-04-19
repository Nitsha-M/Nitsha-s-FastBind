package com.nitsha.binds.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StorageUtils {

    public static Path getGameDir() {
        //? if fabric {
        return net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir();
        //? } elif neoforge {
        // return net.neoforged.fml.loading.FMLPaths.GAMEDIR.get();
        //? } elif forge {
        // return net.minecraftforge.fml.loading.FMLPaths.GAMEDIR.get();
        //? }
    }

    public static Path createFolder(String... parts) {
        Path path = getGameDir();

        for (String part : parts) {
            path = path.resolve(part);
        }

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create folder: " + path, e);
        }

        return path;
    }

}
