package com.nitsha.binds.gui.panels.advanced;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.dto.option.HiddenField;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.button.SmallTextButton;
import com.nitsha.binds.gui.widget.button.ToggleButton;
import com.nitsha.binds.gui.widget.window.ContentWindow;
import com.nitsha.binds.gui.widget.window.IconSelectorWindow;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.Map;

public class OptionsTab extends ContentWindow {

    private ScrollableWindow optionsList;

    private final BindsEditor screen;

    public OptionsTab(BindsEditor screen, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.screen = screen;

        this.optionsList = new ScrollableWindow(2, 4, this.getX(), this.getY(), getWidth() - 4, getHeight() - 11, false);

        int yOffset = 0;
        Field[] fields = Storage.options.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(HiddenField.class)) {
                continue;
            }
            if (field.getType() == boolean.class) {
                try {
                    field.setAccessible(true);

                    String langKey = "nitsha.binds.advances.options." + field.getName();

                    boolean currentValue = field.getBoolean(Storage.options);

                    Component text;
                    if (field.getName().equals("holdToOpen")) {
                        text = TextUtils.translatable("nitsha.binds.advances.options.holdToOpen",
                                GUIUtils.truncateString(TextUtils.translatable(KeyBinds.BINDS.getTranslatedKeyMessage().getString()).getString(), 6));
                    } else {
                        text = TextUtils.translatable(langKey);
                    }

                    this.optionsList.addElement(new ToggleButton(
                            text, 0, yOffset, getWidth() - 4, 20, false, currentValue,
                            () -> {
                                try {
                                    boolean val = field.getBoolean(Storage.options);
                                    field.setBoolean(Storage.options, !val);

                                    Storage.saveModOptions();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                    ));
                    yOffset += 20;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        this.optionsList.setScrollableArea(yOffset + 20);
        addElement(this.optionsList);
    }

    public void updateHeight(int newH) {
        this.setHeight(newH);
        this.optionsList.setHeight(newH - 11);
    }
}
