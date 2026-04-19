package com.nitsha.binds.configs.dto.preset;

import com.nitsha.binds.gui.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BindData {

    public String name = TextUtils.translatable("nitsha.binds.default.newBind").getString();
    public String icon = "minecraft:structure_void";
    public int index = 0;
    public int keyCode = 0;
    public String keyMode = "press";
    public int holdMs = 500;
    public List<ActionData> actions = new ArrayList<>();

}
