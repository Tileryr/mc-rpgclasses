package com.mcclasses.rpgclassabilities.client;

import com.mcclasses.rpgclassabilities.enums.RpgClass;
import com.mcclasses.rpgclassabilities.payload.c2s.SelectClassC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ClassScreen extends Screen {
    protected ClassScreen(Text title) {
        super(title);
    }

    RpgClass[] classOrder = {RpgClass.WARRIOR, RpgClass.ROGUE, RpgClass.CLERIC, RpgClass.WIZARD};
    int currentClassIndex = 0;

    final int yOffset = 20;

    @Override
    protected void init() {
        int buttonWidth = 60;
        int baseButtonY = height / 2 + 35 - yOffset;
        int centerX = centered(width, buttonWidth);

        ButtonWidget nextButton = ButtonWidget.builder(Text.of("Next"), (btn) -> {
            currentClassIndex++;
            currentClassIndex = currentClassIndex % 4;
        }).dimensions(centerX, baseButtonY, buttonWidth, 20).build();
        ButtonWidget backButton = ButtonWidget.builder(Text.of("Back"), (btn) -> {
            currentClassIndex--;
            if (currentClassIndex < 0) {
                currentClassIndex = 3;
            }
        }).dimensions(centerX, baseButtonY + 25, buttonWidth, 20).build();

        ButtonWidget selectButton = ButtonWidget.builder(Text.of("Select"), (btn) -> {
            SelectClassC2SPayload payload = new SelectClassC2SPayload(classOrder[currentClassIndex]);

            ClientPlayNetworking.send(payload);
            MinecraftClient.getInstance().setScreen(
                    null
            );
        }).dimensions(centered(width, 100), baseButtonY + 60, 100, 20).build();

        this.addDrawableChild(nextButton);
        this.addDrawableChild(backButton);
        this.addDrawableChild(selectButton);
    }

    private int  centered(int totalLength, int length) {return totalLength / 2 - length / 2;}
    private void drawTextureCentered(DrawContext context, Identifier texture, int u, int v, int width, int height) {
        int x = centered(context.getScaledWindowWidth(), width);
        int y = centered(context.getScaledWindowHeight(), height) - yOffset;
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED, texture,
                x, y, u, v,
                width, height, 32, 32, 64, 64
        );
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow
        Identifier iconsTexture = Identifier.of("rpgclassabilities", "textures/class/class_icons.png");
        int width = 64;
        int height = 64;
        int centeredX = centered(context.getScaledWindowWidth(), width);
        int centeredY = centered(context.getScaledWindowHeight(), height);

        RpgClass currentClass = classOrder[currentClassIndex];

        // Warrior
        String text = "";
        int u = 0;
        int v = 0;

        switch (currentClass) {
            case RpgClass.WARRIOR -> {
                text = "Warrior";
            }
            case RpgClass.ROGUE -> {
                text = "Rogue";
                u = 32;
            }
            case RpgClass.CLERIC -> {
                text = "Cleric";
                v = 32;
            }
            case RpgClass.WIZARD -> {
                text = "Wizard";
                u = 32;
                v = 32;
            }
        }

        context.drawText(client.textRenderer, text, centeredX, centeredY - 10 - yOffset, 0xFFFFFFFF, true);
        drawTextureCentered(context, iconsTexture, u, v, width, height);
        // Rogue
//        int rogueX = baseX + offsetX;
//        context.drawText(client.textRenderer, "Rogue", rogueX, labelY, 0xFFFFFFFF, true);
//        context.drawTexture(
//                RenderPipelines.GUI_TEXTURED, iconsTexture,
//                rogueX, y, 32, 0,
//                width, height, regionWidth, regionHeight, textureWidth, textureHeight
//        );
//        // Cleric
//        int clericX = baseX + offsetX * 2;
//        context.drawText(client.textRenderer, "Cleric", clericX, labelY, 0xFFFFFFFF, true);
//        context.drawTexture(
//                RenderPipelines.GUI_TEXTURED, iconsTexture,
//                clericX, y, 0, 32,
//                width, height, regionWidth, regionHeight, textureWidth, textureHeight
//        );
//        // Wizard
//        int wizardX = baseX + offsetX * 3;
//        context.drawText(client.textRenderer, "Wizard", wizardX, labelY, 0xFFFFFFFF, true);
//        context.drawTexture(
//                RenderPipelines.GUI_TEXTURED, iconsTexture,
//                wizardX, y, 32, 32,
//                width, height, regionWidth, regionHeight, textureWidth, textureHeight
//        );
    }
}
