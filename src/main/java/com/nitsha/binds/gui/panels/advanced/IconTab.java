package com.nitsha.binds.gui.panels.advanced;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.button.SmallTextButton;
import com.nitsha.binds.gui.widget.window.ContentWindow;
import com.nitsha.binds.gui.widget.window.IconSelectorWindow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class IconTab extends ContentWindow {

    private static final ResourceLocation RANDOM_SMALL = Main.id("textures/gui/test/random.png");

    private IconSelectorWindow iconSelectorWindow;

    private final BindsEditor screen;

    public IconTab(BindsEditor screen, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.screen = screen;

        this.iconSelectorWindow = new IconSelectorWindow(4, 20, 162, 144, (stack, key) -> {
            screen.getBasicOptionsWindow().getEditIcon().setIcon(stack);
            this.iconSelectorWindow.updateButtons(key);
            BindsEditor.editIconBtnString = key;
            if (!BindsEditor.getCBind().actions.isEmpty())  {
                screen.saveBind();
            }
        });

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.choose_icon"), 0, 5, 10, "left", "center",
                    0xFF212121, false);
        });

        this.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.advances.random_icon"), getWidth() - 4, 6, 7, 0x33000000, 0xFF232425, 0xFF232425, 0xFFe7bc1c, 0, "right", RANDOM_SMALL, ()-> {
            //? if >=26.1 {
            // Map.Entry<String, ItemStackTemplate> randomItem = ItemsMapper.getRandomItem();
            //? } else {
            Map.Entry<String, ItemStack> randomItem = ItemsMapper.getRandomItem();
            //? }

            if (randomItem == null) return;

            screen.getBasicOptionsWindow().getEditIcon().setIcon(randomItem.getValue());
            this.iconSelectorWindow.updateButtons(randomItem.getKey());
            BindsEditor.editIconBtnString = randomItem.getKey();

            if (!BindsEditor.getCBind().actions.isEmpty()) {
                screen.saveBind();
            }
        }));

        this.addElement(this.iconSelectorWindow);
    }

    public IconSelectorWindow getIconSelectorWindow() {
        return iconSelectorWindow;
    }
}
