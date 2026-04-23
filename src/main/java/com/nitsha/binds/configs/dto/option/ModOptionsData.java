package com.nitsha.binds.configs.dto.option;

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
}
