package com.nitsha.binds.configs;

import java.util.ArrayList;
import java.util.List;

public class Preset {
    public String name;
    public List<Page> pages;

    public Preset(String name, List<Page> pages) {
        this.name = name;
        this.pages = pages != null ? pages : new ArrayList<>();
    }
}
