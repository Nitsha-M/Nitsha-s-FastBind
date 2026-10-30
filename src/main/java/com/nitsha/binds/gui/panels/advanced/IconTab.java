package com.nitsha.binds.gui.panels.advanced;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.gui.widget.button.SmallTextButton;
import com.nitsha.binds.gui.widget.window.IconSelectorWindow;
import com.nitsha.binds.multiversions.NitshaContainer;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;

public class IconTab extends NitshaContainer {

    private IconSelectorWindow iconSelectorWindow;

    private final BindsEditor screen;

    public IconTab(BindsEditor screen, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.screen = screen;

        this.iconSelectorWindow = new IconSelectorWindow(4, 20, 162, Math.max(18, height - 46), (stack, key) -> {
            screen.getBasicOptionsWindow().getEditIcon().setItemStack(stack);
            this.iconSelectorWindow.updateButtons(key);
            BindsEditor.editIconBtnString = key;
            if (!BindsEditor.getCBind().actions.isEmpty())  {
                screen.saveBind();
            }
        });

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            RenderUtils.drawText(ctx, TextUtils.small(TextUtils.translatable("nitsha.binds.advances.choose_icon")), 0, 5, 10, "left", "center", 0xFF212121, false);
        });

        Runnable selectRandomIcon = () -> {
            ItemsMapper.IconEntry randomItem = ItemsMapper.getRandomItem();

            if (randomItem == null) return;

            screen.getBasicOptionsWindow().getEditIcon().setItemStack(ItemsMapper.createIcon(randomItem.icon()));
            iconSelectorWindow.updateButtons(randomItem.key());
            BindsEditor.editIconBtnString = randomItem.key();

            if (!BindsEditor.getCBind().actions.isEmpty()) {
                screen.saveBind();
            }
        };

        this.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.advances.random_icon"), getWidth() - 4, 6, 7, 0x33000000, 0xFF232425, 0xFF232425, 0xFFe7bc1c, 0, "right", Textures.ICON_RANDOM, selectRandomIcon));

        this.addElement(this.iconSelectorWindow);
    }

    public IconSelectorWindow getIconSelectorWindow() {
        return iconSelectorWindow;
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        iconSelectorWindow.setHeight(Math.max(18, height - 46));
    }
}
