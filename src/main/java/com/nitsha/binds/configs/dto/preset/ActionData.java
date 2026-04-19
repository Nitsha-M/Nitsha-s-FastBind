package com.nitsha.binds.configs.dto.preset;

public abstract class ActionData {

    public transient String type;

    public ActionData(String type) {
        this.type = type;
    }

}