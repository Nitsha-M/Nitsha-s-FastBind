package com.nitsha.binds.gui.node;

import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.BindsEditorGUI;
import com.nitsha.binds.gui.GUIUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class NodeEditorGUI extends Screen {
    private static final Identifier BACKGROUND = MainClass.id("textures/gui/sprites/nodeeditor_bg.png");
    private static final Identifier VIGNETTE_TEXTURE  = MainClass.id("textures/gui/vignette.png");

    int gridColor = 0x1AFFFFFF ;
    private int offsetX, offsetY; // Смещение "камеры"
    private int dragStartX, dragStartY; // Начало перемещения
    private boolean dragging = false; // Флаг перетаскивания

    private final int canvasWidth = 2000;
    private final int canvasHeight = 2000;
    private final int gridSize = 10;
    private float scale = 1.0f;

    private long window;
    private boolean spacePressed = false;

    private final List<Node> nodes = new ArrayList<>(); // Список нод

    int createdNode = 0;

    private MenuWidget leftSide;
    private MenuWidget centerMenu;

    public NodeEditorGUI() {
        super(NarratorManager.EMPTY);
    }

    protected void init() {
        offsetX = (canvasWidth / 2) - (this.width / 2);
        offsetY = (canvasHeight / 2) - (this.height / 2);
        this.window = this.client.getWindow().getHandle();
        createdNode = 0;

        int cX = this.width / 2 - 50;
        int cY = 10;
        leftSide = new MenuWidget(10, 10, 120, 31);
        centerMenu = new MenuWidget(cX, cY, 100, 46);

        this.addDrawableChild(leftSide);
        this.addDrawableChild(centerMenu);


        leftSide.addWidget(new BindsEditorGUI.TextField(
                this.textRenderer, cX + 4, cY + 4, 92, 17, 20, false, "",
                Text.translatable("nitsha.binds.name").getString()
        ));
        leftSide.addWidget(new GUIUtils.IconButton(cX + 4, cY + 23, 20, 20, 20, 5, 3,  "delete", this::saveNode));
        leftSide.addWidget(new GUIUtils.IconButton(cX + 28, cY + 23, 20, 20, 20, 3, 4, "reset",  this::saveNode));
        leftSide.addWidget(new GUIUtils.IconButton(cX + 52, cY + 23, 20, 20, 20, 4, 4, "duplicate",  this::saveNode));
        leftSide.addWidget(new GUIUtils.IconButton(cX + 76, cY + 23, 20, 20, 20, 5, 4, "save",  this::saveNode));
    }

    private void saveNode() {
        MainClass.LOGGER.warn("Saving node");
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        // Background, vignette
        renderBackgroundTexture(ctx, BACKGROUND, 0, 0, 0.0F, 0.0F, this.width, this.height);
        ctx.drawTexture(RenderLayer::getGuiTextured, VIGNETTE_TEXTURE, 0, 0, 0, 0, width, height, width, height);
        ctx.fillGradient(0, 0, width, height, 0xAA000000, 0x00000000);

        ctx.getMatrices().push(); // Сохраняем текущее состояние матрицы

        ctx.getMatrices().scale(scale, scale, scale); // Изменяем масштаб

        int scaledX = (int) (10 / scale);
        int scaledY = (int) (100 / scale);

        GUIUtils.addText(ctx, Text.of("Масштаб " + (int) (scale * 100) + "%"), 0, scaledX, scaledY, "left", "top");
        ctx.getMatrices().pop();

        // Ограничение offset по границам "холста"
        offsetX = Math.max(0, Math.min(offsetX, canvasWidth - width));
        offsetY = Math.max(0, Math.min(offsetY, canvasHeight - height));

        int startX = -offsetX + width / 2; // Центрируем координаты
        int startY = -offsetY + height / 2;

        // Cells
        MatrixStack matrices = ctx.getMatrices();
        matrices.push();
        matrices.translate(-0.5, -0.5, 0);
        for (int x = -offsetX; x < width; x += gridSize) {
            for (int y = -offsetY; y < height; y += gridSize) {
                ctx.fill(x, y, x + gridSize, y + 1, gridColor); // Горизонтальная линия
                ctx.fill(x, y, x + 1, y + gridSize, gridColor); // Вертикальная линия
            }
        }
        matrices.pop();

        for (Node node : nodes) {
            node.render(ctx, offsetX, offsetY);
        }

        // dd
        leftSide.addDrawElement(drawCtx -> GUIUtils.addText(drawCtx, Text.of("Nitsha's FastBind"), 0, 10 + 6, 10 + 6));
        leftSide.addDrawElement(drawCtx ->
                GUIUtils.addText(drawCtx, Text.literal("Node Editor")
                                .styled(s -> s.withColor(Formatting.WHITE))
                                .append(Text.literal(" | v2.0 WIP")
                                        .styled(s -> s.withColor(TextColor.fromRgb(0xC2F95C)))), // Кастомный цвет
                        0, 10 + 6, 10 + 17)
        );

        // TEMP: display coords
        int relativeX = mouseX + offsetX;
        int relativeY = mouseY + offsetY;
        String text = "Cursor: X=" + relativeX + ", Y=" + relativeY + ", OffsetX=" + offsetX + ", OffsetY=" + offsetY;
        ctx.drawText(client.textRenderer, text, (width - client.textRenderer.getWidth(text)) / 2, height - 20, 0xFFFFFF, true);

        boolean isSpaceDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS;
        if (isSpaceDown && !spacePressed) {
            GLFW.glfwSetCursor(window, GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR));
            spacePressed = true;
        } else if (!isSpaceDown && spacePressed) {
            dragging = false;
            GLFW.glfwSetCursor(window, GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
            spacePressed = false;
        }
    }

    public static void renderBackgroundTexture(DrawContext context, Identifier texture, int x, int y, float u, float v, int width, int height) {
        context.drawTexture(RenderLayer::getGuiTextured, texture, x, y, u, v, width, height, 64, 64);
    }

    @Override
    public void close() {
        GLFW.glfwSetCursor(window, GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
        super.close();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_F) {
            int scaledX = (int) (client.mouse.getX() / client.getWindow().getScaleFactor());
            int scaledY = (int) (client.mouse.getY() / client.getWindow().getScaleFactor());

            int snappedX = Math.round(scaledX / (float) gridSize) * gridSize;
            int snappedY = Math.round(scaledY / (float) gridSize) * gridSize;

            String[] s = {"yellow", "blue", "green"};
            String[] t = {"input", "output"};
            int randomNumber = ThreadLocalRandom.current().nextInt(1, 6); // [1, 6), то есть 1-5
            Random random = new Random();
            String randomColor = s[random.nextInt(s.length)];

            List<String[]> itemsList = new ArrayList<>();

            for (int i = 0; i < randomNumber; i++) {
                Random random2 = new Random();
                String randomType = t[random2.nextInt(t.length)];
                itemsList.add(new String[]{randomType, "Value " + i});
            }

            nodes.add(new Node(createdNode, snappedX, snappedY, offsetX, offsetY, randomColor, randomColor, itemsList));
            createdNode++;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_H) {
            Node activeNode = getActiveNode();
            activeNode.setWidth(activeNode.getWidth() + 10);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_EQUAL) {
            scale = (float) Math.max(0.4, Math.min(2.0, scale + 0.1));
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_MINUS) {
            scale = (float) Math.max(0.4, Math.min(2.0, scale - 0.1));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public Node getActiveNode() {
        for (Node node : nodes) {
            if (node.isActive()) {
                return node;
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (spacePressed) { // Логика перетаскивания
                dragging = true;
                dragStartX = (int) mouseX + offsetX;
                dragStartY = (int) mouseY + offsetY;
                return true;
            }

            for (Node node : nodes) {
                if (node.isClicked((int) mouseX, (int) mouseY)) {
                    for (Node other : nodes) {
                        other.setActive(false);
                    }
                    node.setActive(true);
                    return true; // Если кликнули по Node, прерываем обработку
                }
            }

            for (Node node : nodes) {
                if (node.isClicked((int) mouseX, (int) mouseY)) {
                    node.dragging = true;
                    node.dragOffsetX = (int) mouseX - node.x;
                    node.dragOffsetY = (int) mouseY - node.y;
                    return true; // Перетаскивание
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button); // Передаем дальше
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = false;
            for (Node node : nodes) {
                node.dragging = false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            offsetX -= (int) deltaX;
            offsetY -= (int) deltaY;
            return true;
        }
        for (Node node : nodes) {
            if (node.dragging) {
                node.x = (int) mouseX - node.dragOffsetX;
                node.y = (int) mouseY - node.dragOffsetY;
            }
        }
        return false;
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) { }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean shouldPause() {
        return true;
    }
}