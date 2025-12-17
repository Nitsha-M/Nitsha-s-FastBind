package com.nitsha.binds.configs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bind {
    public String name;
    public String icon;
    public int keyCode;
    public List<Map<String, Object>> actions;

    public Bind(String name, String icon, int keyCode, List<Map<String, Object>> actions) {
        this.name = name;
        this.icon = icon;
        this.keyCode = keyCode;
        this.actions = actions != null ? actions : new ArrayList<>();
    }
}
