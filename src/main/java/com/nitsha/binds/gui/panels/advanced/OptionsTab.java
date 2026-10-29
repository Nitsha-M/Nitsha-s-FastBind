package com.nitsha.binds.gui.panels.advanced;

import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.dto.option.HiddenField;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.AnimatedSprite;
import com.nitsha.binds.gui.utils.DrawElement;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.gui.widget.button.BedrockIconTextButton;
import com.nitsha.binds.gui.widget.button.SmallTextButton;
import com.nitsha.binds.gui.widget.button.ToggleButton;
import com.nitsha.binds.multiversions.NitshaContainer;
import com.nitsha.binds.multiversions.NitshaCraft;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.lang.reflect.Field;

public class OptionsTab extends NitshaContainer {

    private final ScrollableWindow optionsList;
    private final BindsEditor screen;
    private final BedrockIconTextButton boosty;
    private final BedrockIconTextButton wiki;
    private final String boostyUrl = "https://boosty.to/nitsha_m";
    private final String wikiUrl = "https://github.com/Nitsha-M/Nitsha-s-FastBind/wiki";
    private final AnimatedSprite catLook;

    public OptionsTab(BindsEditor screen, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.screen = screen;

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            RenderUtils.drawText(ctx, TextUtils.small(TextUtils.translatable("nitsha.binds.advances.tab.options")), 0, 5, 10, "left", "center", 0xFF212121, false);
            RenderUtils.drawText(ctx, TextUtils.small(TextUtils.translatable("nitsha.binds.advances.options.support")), this.getWidth(), 0, getHeight() - 25, "center", "bottom", 0xFF212121, false);

            RenderUtils.drawFill(ctx, 4, 19, getWidth() - 4, 20, 0xFF8B8B8B);
            RenderUtils.drawFill(ctx, 4, getHeight() - 25 - 10, getWidth() - 4, getHeight() - 25 - 9, 0xFF8B8B8B);
        });

        this.addElement(new SmallTextButton(TextUtils.translatable("nitsha.binds.advances.options.resetToDefault"), getWidth() - 4, 6, 7, 0x33000000, 0xFF232425, 0xFF232425, 0xFFe7bc1c, 0, "right", null, ()-> {
            Storage.options.resetToDefaults();
            Storage.saveModOptions();
            loadOptions();
        }));

        this.optionsList = new ScrollableWindow(2, 20, getWidth() - 4, getHeight() - 27 - 20 - 8, false);

        this.wiki = new BedrockIconTextButton(Textures.ICON_WIKI, TextUtils.small(TextUtils.translatable("nitsha.binds.advances.options.wiki")), 4, getHeight() - 25, 60, 20, true, () -> {
            NitshaCraft.confirmLink(wikiUrl);
        });
        this.wiki.setColors(0xFF88899A, 0xFFA5A6B2, 0xFFFFFFFF, 0xFFFFFFFF);
        this.wiki.setButtonDirection(1);

        this.boosty = new BedrockIconTextButton(Textures.ICON_BOOSTY, TextUtils.small(TextUtils.translatable("nitsha.binds.advances.options.support.boosty")), 4 + 60, getHeight() - 25, getWidth() - 8 - 60, 20, true, () -> {
            NitshaCraft.confirmLink(boostyUrl);
        });
        this.boosty.setColors(0xFFF15F2C, 0xFFFF7D4A, 0xFFFFFFFF, 0xFFFFFFFF);
        addShine(this.boosty);
        this.boosty.setButtonDirection(2);

        this.wiki.setNeighbor(this.boosty);
        this.boosty.setNeighbor(this.wiki);


        loadOptions();
        this.addElement(this.boosty);
        this.addElement(this.wiki);
        this.addElement(this.optionsList);

        catLook = new AnimatedSprite(24, 14, Textures.CAT_LOOK, 0, false, 0, 0, 480, 24, 100, 504, 14);
        catLook.setLoop(true);
        catLook.setLoopPause(2000);
        catLook.startAnimation(true);

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            catLook.setPosition(getWidth() - 24 - 8, getHeight() - 25 - 14 + Math.round(this.boosty.getOffsetY()));
            catLook.render(ctx);
        });
    }

    private void addShine(BedrockIconTextButton btn) {
        int shineX = btn.getX() + 1;
        int shineY = btn.getY() + 1;
        int shineW = btn.getWidth() - 2;
        int shineH = 16;
        int texW = 18;
        int texH = 16;
        btn.setBottomLvl(new DrawElement((ctx, mouseX, mouseY) -> {
            int btnY = shineY + Math.round(btn.getOffsetY());
            drawShineAnimation(ctx, Textures.SHINE_BOTTOM, shineX, btnY + 2, shineW, shineH, texW, texH, 400f, 2000f);
        }));
        btn.setTopLvl(new DrawElement((ctx, mouseX, mouseY) -> {
            int btnY = shineY + Math.round(btn.getOffsetY());
            drawShineAnimation(ctx, Textures.SHINE_TOP, shineX, btnY, shineW, shineH, texW, texH, 400f, 2000f);
        }));
    }

    private void loadOptions() {
        this.optionsList.clearChildren();
        int yOffset = 0;

        Field[] fields = Storage.options.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(HiddenField.class)) continue;

            if (field.getType() == boolean.class) {
                try {
                    field.setAccessible(true);
                    String langKey = "nitsha.binds.advances.options." + field.getName();
                    boolean currentValue = field.getBoolean(Storage.options);
                    MutableComponent text;
                    if (field.getName().equals("holdToOpen")) {
                        text = TextUtils.translatable("nitsha.binds.advances.options.holdToOpen",
                                RenderUtils.truncateString(TextUtils.translatable(KeyBinds.BINDS.getTranslatedKeyMessage().getString()).getString(), 6));
                    } else {
                        text = TextUtils.translatable(langKey);
                    }
                    this.optionsList.addElement(new ToggleButton(text, 0, yOffset, getWidth() - 4, 20, false, currentValue,
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
        this.optionsList.setScrollableArea(yOffset);
    }


    public static void drawShineAnimation(GuiGraphicsExtractor ctx, Identifier texture, int x, int y, int width, int height, int texW, int texH, float durationMs, float pauseMs) {
        long time = Util.getMillis();
        long totalCycle = (long) (durationMs + pauseMs);
        long currentCycleTime = time % totalCycle;
        if (currentCycleTime > durationMs) {
            return;
        }
        float progress = (float) currentCycleTime / durationMs;
        int startX = x - texW;
        int endX = x + width;
        int currentX = (int) (startX + (endX - startX) * progress);
        RenderUtils.customScissor(ctx, x, y, width, height, () -> {
            RenderUtils.drawTexture(ctx, texture, currentX, y, 0, 0, texW, texH, texW, texH);
        });
    }

    public void updateHeight(int newH) {
        this.setHeight(newH);
        this.optionsList.setHeight(newH - 27 - 20 - 8);
        this.boosty.setY(newH - 25);
        this.wiki.setY(newH - 25);
        addShine(this.boosty);
    }
}
