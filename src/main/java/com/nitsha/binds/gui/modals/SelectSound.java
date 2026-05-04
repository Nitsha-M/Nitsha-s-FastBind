package com.nitsha.binds.gui.modals;

import com.nitsha.binds.Main;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.button.BedrockButton;
import com.nitsha.binds.gui.widget.button.SmallTextButton;
import com.nitsha.binds.gui.widget.button.SmallToggleButton;
import com.nitsha.binds.gui.widget.list.SoundItem;
import com.nitsha.binds.gui.widget.window.ModalWindow;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import com.nitsha.binds.utils.AudioPlayer;
import com.nitsha.binds.utils.EventBus;
import com.nitsha.binds.utils.SearchUtil;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;*/
//? }

public class SelectSound extends ModalWindow {
    private static final ResourceLocation TAB2_BG = Main.id("textures/gui/test/scroller.png");
    private static final ResourceLocation RANDOM_SMALL = Main.id("textures/gui/test/random.png");

    private final BindsEditor screen;
    private ScrollableWindow soundsList;
    private ScrollableWindow filterList;
    private int tab = 0;

    private SmallTextButton reload;
    private SmallTextButton openFolder;

    private BedrockButton tab1;
    private BedrockButton tab2;

    private int contentHeight;

    private boolean noResult = false;
    private SmallTextButton resetFilter;

    public SelectSound(BindsEditor screen, int x, int y, int width, int height, ResourceLocation t1,
                       ResourceLocation t2) {
        super(screen, x, y, width, height, t1, t2, "nitsha.binds.advances.modals.selectSound");
        this.screen = screen;
        this.contentHeight = this.getHeight() - 20 - 38;

        this.setNeedSearch(true);

        this.resetFilter = new SmallTextButton(TextUtils.literal("reset"), this.getWidth() - 5, getHeight() - 36, 0, 0x33000000, 0xFF232425, 0xFF232425, 0xFFe7bc1c, 0, "right", null, ()-> {
            filterBtns.forEach(btn -> btn.setToggled(false));
            activeFilter = "";
            generateList();
        });

        this.addElement(this.resetFilter);

        this.addDrawElement((ctx, mouseX, mouseY) -> {
            GUIUtils.drawResizableBox(ctx, TAB2_BG, 4, 20, this.getWidth() - 8, contentHeight, 1, 3);

            if (noResult) {
                GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.modals.noSound"),
                        this.getWidth() - 8, 4, (contentHeight / 2) + 20, "center", "center", 0xFFAEAEAE, false);
            }
        });

        int btnWidth = (this.getWidth() - 8) / 2;
        this.tab1 = new BedrockButton(TextUtils.translatable("nitsha.binds.advances.modals.buttons.minecraft").getString(), 4, this.getHeight() - 25, btnWidth, 20, true, () -> selectTab(0));
        this.tab1.setButtonDirection(1);
        this.tab2 = new BedrockButton(TextUtils.translatable("nitsha.binds.advances.modals.buttons.external").getString(), this.getWidth() - 4 - btnWidth, this.getHeight() - 25, btnWidth, 20, true, () -> selectTab(1));
        this.tab2.setButtonDirection(2);

        this.tab1.setNeighbor(this.tab2);
        this.tab2.setNeighbor(this.tab1);

        this.reload = new SmallTextButton(TextUtils.translatable("nitsha.binds.advances.modals.buttons.reload"), getWidth() - 4, getHeight() - 36, 7, 0x33000000, 0xFF232425, 0xFF232425, 0xFFe7bc1c, btnWidth - 1, "right", RANDOM_SMALL, () -> {
            AudioPlayer.loadExternalSounds();
            generateList();
        });

        this.openFolder = new SmallTextButton(TextUtils.translatable("nitsha.binds.advances.modals.buttons.openFolder"), 4, getHeight() - 36, 7, 0x33000000, 0xFF232425, 0xFF232425, 0xFFe7bc1c, btnWidth - 1, "left", RANDOM_SMALL, ()-> {
            Util.getPlatform().openFile(Storage.SOUNDS_DIR.toFile());
        });

        this.reload.visible = false;
        this.openFolder.visible = false;

        this.addElement(this.reload);
        this.addElement(this.openFolder);
        this.addElement(this.tab1);
        this.addElement(this.tab2);

        this.soundsList = new ScrollableWindow(5, 21, 5, 21, getWidth() - 10, contentHeight - 2, false);
        this.addElement(this.soundsList);

        this.filterList = new ScrollableWindow(5, getHeight() - 36, 5, getHeight() - 36, getWidth() - 10 - (this.resetFilter.getWidth() + 2), 12, true);
        this.filterList.setShowScrollbar(false);
        this.addElement(this.filterList);

        this.getSearchField().setTypingEvent(this::generateList);

        generateFilterList();
        selectTab(0);
        generateList();
    }

    private int filterX = 0;
    private int filterIndex = 0;
    private List<SmallToggleButton> filterBtns = new ArrayList<>();
    private String activeFilter = "";

    public void generateFilterList() {
        filterX = 0;
        filterIndex = 0;
        this.filterList.clearChildren();
        this.filterList.setScrollableArea(0);
        this.filterList.resetScroll();

        List<String> list = AudioPlayer.getSoundCategories();

        for (String key : list) {
            final int capturedIndex = filterIndex;
            SmallToggleButton item = new SmallToggleButton(TextUtils.literal(key), filterX, 0, 0, 9, false, () -> {
                SmallToggleButton clicked = filterBtns.get(capturedIndex);
                boolean wasToggled = clicked.isToggled();
                filterBtns.forEach(btn -> btn.setToggled(false));
                clicked.setToggled(!wasToggled);
                activeFilter = (!wasToggled ? key : "");
                generateList();
            });
            this.filterList.addElement(item);
            filterBtns.add(item);
            filterX += ((filterIndex == list.size()) ? 0 : 2) + item.getWidth();
            filterIndex++;
        }

        this.filterList.setScrollableArea(filterX);
    }


    private void selectTab(int newTab) {
        this.tab = newTab;

        if (newTab == 1) {
            this.tab1.setPressed(false);
            this.tab2.setPressed(true);
            this.reload.visible = true;
            this.openFolder.visible = true;
            this.contentHeight = this.getHeight() - 20 - 38;
            this.filterList.setVisible(false);
            this.resetFilter.visible = false;
        } else {
            this.tab1.setPressed(true);
            this.tab2.setPressed(false);
            this.reload.visible = false;
            this.openFolder.visible = false;
            this.contentHeight = this.getHeight() - 20 - 38;
            this.filterList.setVisible(true);
            this.resetFilter.visible = true;
        }
        this.soundsList.setHeight(this.contentHeight - 2);
        generateList();
    }

    @Override
    public void renderWindow(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        int adjX = mouseX - this.getX();
        int adjY = mouseY - this.getYOffset();

        GUIUtils.matricesUtil(ctx, 0, 0, 200, () -> {
            super.renderWindow(ctx, mouseX, mouseY, delta);
        });
    }

    private int actionIndex = 0;
    private int actionY = 0;

    public void generateList() {
        actionY = 0;
        actionIndex = 0;
        this.soundsList.clearChildren();
        this.soundsList.setScrollableArea(0);
        this.soundsList.resetScroll();

        int h = 24;

        String query = this.getSearchField().getText().toLowerCase();

        if (tab == 0) {
            for (ResourceLocation key : AudioPlayer.getSortedSounds()) {
                if (!activeFilter.isEmpty()) {
                    String[] parts = key.getPath().split("\\.");
                    String category = key.getPath().contains(".") ? parts[0] : "other";
                    if (!category.equals(activeFilter)) continue;
                }
                String id = key.getNamespace() + ":" + key.getPath();

                createItem(h, query, id);
            }
        } else {
            for (Map.Entry<String, File> entry : AudioPlayer.EXTERNAL_SOUNDS.entrySet()) {
                String id = entry.getKey();

                createItem(h, query, id);
            }
        }

        noResult = actionIndex == 0;

        this.soundsList.setScrollableArea(actionY);
    }

    private void createItem(int h, String query, String id) {
        if (!query.isEmpty() && !SearchUtil.matches(id.toLowerCase(), query)) return;
        SoundItem item = new SoundItem(this, 0, actionY, this.soundsList.getWidth(), h, id, actionIndex, tab != 0);
        this.soundsList.addElement(item);
        this.soundsList.addScrollableArea(h);
        actionIndex++;
        actionY += h;
    }

    @Override
    public void open(Runnable onFinish) {
        this.soundsList.resetScroll();
        this.filterList.resetScroll();
        filterBtns.forEach(btn -> btn.setToggled(false));
        activeFilter = "";
        this.getSearchField().setText("");
        generateList();
        AudioPlayer.loadExternalSounds();
        super.open(onFinish);
    }

    public void onSelect(String key) {
        EventBus.emit("selectSound.result", key, tab != 0);
        this.close(() -> {});
    }

    @Override
    public void close(Runnable onFinish) {
        super.close(onFinish);
        AudioPlayer.stopChannel(9);
    }
}