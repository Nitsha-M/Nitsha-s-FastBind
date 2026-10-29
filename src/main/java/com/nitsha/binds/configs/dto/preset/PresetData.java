package com.nitsha.binds.configs.dto.preset;

import com.nitsha.binds.Main;
import com.nitsha.binds.multiversions.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PresetData {

    public String modVersion = Main.getModVersion();
    public String name = TextUtils.translatable("nitsha.binds.default.newPreset").getString();
    public transient String id = "";
    public List<PageData> pages = new ArrayList<>();
    public int columns = 4;
    public int rows = 2;
}
