package com.nitsha.binds.bind;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bind {
    public String name;
    public String icon;
    public int keyCode;
    public String keyMode;
    public int holdMs;
    public List<Map<String, Object>> actions;

    public Bind(String name, String icon, int keyCode, String keyMode, int holdMs, List<Map<String, Object>> actions) {
        this.name = name;
        this.icon = icon;
        this.keyCode = keyCode;
        this.keyMode = keyMode;
        this.holdMs = holdMs;
        this.actions = actions != null ? actions : new ArrayList<>();
    }
}
