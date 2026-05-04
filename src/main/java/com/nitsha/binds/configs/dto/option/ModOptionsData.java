package com.nitsha.binds.configs.dto.option;

import java.lang.reflect.Field;

public class ModOptionsData {

    public boolean holdToOpen = true;
    public boolean openLastPage = true;
    public boolean openLastPreset = true;
    public boolean keepMovement = true;
    public boolean closeOnAction = false;
    public boolean showActivationMessage = true;

    @HiddenField
    public boolean easterEgg = false;
    @HiddenField
    public String lastPresetId = "";
    @HiddenField
    public int lastPageIndex = 0;
    @HiddenField
    public boolean fullHeightEditor = false;

    public void resetToDefaults() {
        ModOptionsData defaults = new ModOptionsData();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(HiddenField.class)) {
                try {
                    field.set(this, field.get(defaults));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
