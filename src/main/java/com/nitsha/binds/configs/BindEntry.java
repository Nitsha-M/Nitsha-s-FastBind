package com.nitsha.binds.configs;

import java.util.ArrayList;
import java.util.List;

public class BindEntry {
    public String name;
    public String icon;
    public int keyCode;
    public List<String> actions;

    public BindEntry(String name, String icon, int keyCode, List<String> actions) {
        this.name = name;
        this.icon = icon;
        this.keyCode = keyCode;
        this.actions = new ArrayList<>(actions);
    }
}