package com.nitsha.binds.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nitsha.binds.FBLogger;
import com.nitsha.binds.configs.dto.preset.ActionData;
import com.nitsha.binds.configs.adapters.ActionAdapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CodecUtil {

    private static final Gson MINIFIED_GSON = new GsonBuilder()
            .registerTypeAdapter(ActionData.class, new ActionAdapter())
            .disableHtmlEscaping()
            .create();

    private static final String PREFIX = "fastbind://";

    public static String exportToText(Object data) {
        try {
            String json = MINIFIED_GSON.toJson(data);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(json.getBytes("UTF-8"));
            }

            byte[] compressedBytes = byteArrayOutputStream.toByteArray();
            return PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(compressedBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T importFromText(String text, Class<T> clazz) {
        if (text == null || text.trim().isEmpty()) return null;

        try {
            String base64Text = text.trim();
            if (!base64Text.startsWith(PREFIX)) return null;
            base64Text = base64Text.substring(PREFIX.length());

            byte[] compressedBytes = Base64.getUrlDecoder().decode(base64Text);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedBytes);
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                byte[] uncompressedBytes = gzipInputStream.readAllBytes();
                String json = new String(uncompressedBytes, "UTF-8");

                return MINIFIED_GSON.fromJson(json, clazz);
            }
        } catch (Exception e) {
            FBLogger.error("Import error: invalid string format");
            e.printStackTrace();
            return null;
        }
    }
}
