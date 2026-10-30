package com.nitsha.binds.configs.dto.preset;

import com.nitsha.binds.Main;
import com.nitsha.binds.multiversions.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PresetData {
    public static final int MIN_COLUMNS = 4;
    public static final int MAX_COLUMNS = 7;
    public static final int MIN_ROWS = 2;
    public static final int MAX_ROWS = 4;

    public String modVersion = Main.getModVersion();
    public String name = TextUtils.translatable("nitsha.binds.default.newPreset").getString();
    public transient String id = "";
    public List<PageData> pages = new ArrayList<>();
    public int columns = MIN_COLUMNS;
    public int rows = MIN_ROWS;

    public void clampGridSize() {
        columns = Math.max(MIN_COLUMNS, Math.min(MAX_COLUMNS, columns));
        rows = Math.max(MIN_ROWS, Math.min(MAX_ROWS, rows));
    }
}
