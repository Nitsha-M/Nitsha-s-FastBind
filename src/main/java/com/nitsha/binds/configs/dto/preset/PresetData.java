package com.nitsha.binds.configs.dto.preset;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PresetData {

    public String modVersion = Main.getModVersion();
    public String name = TextUtils.translatable("nitsha.binds.default.newPreset").toString();
    public transient String id = "";
    public int index = 0;
    public List<PageData> pages = new ArrayList<>();

}
