package com.mcclasses.rpgclassabilities.client;

import com.google.common.collect.ImmutableMap;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.enums.RpgClass;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ClassScreen extends Screen {
    private final Identifier dashIconTexture = Identifier.of(Rpgclassabilities.MOD_ID, "textures/icons/dash.png");
    private final Identifier chargeIconTexture = Identifier.of(Rpgclassabilities.MOD_ID, "textures/icons/charge.png");
    private final Identifier bindIconTexture = Identifier.of(Rpgclassabilities.MOD_ID, "textures/icons/bind.png");
    private final Identifier transmuteIconTexture = Identifier.of(Rpgclassabilities.MOD_ID, "textures/icons/transmute.png");

    private final double SCALE = 1.333;
    private final int ABILITY_ICON_SCALE = (int) (23 * SCALE);

    private final ImmutableMap<RpgClass, Identifier> classIdentifierMap = ImmutableMap.<RpgClass, Identifier>builder()
            .put(RpgClass.ROGUE, dashIconTexture)
            .put(RpgClass.WARRIOR, chargeIconTexture)
            .put(RpgClass.CLERIC, bindIconTexture)
            .put(RpgClass.WIZARD, transmuteIconTexture)
            .build();

    private final ImmutableMap<RpgClass, String> classAbilityDescriptionMap = ImmutableMap.<RpgClass, String>builder()
            .put(RpgClass.ROGUE, "Dash: " +
                    "Teleport to a destination ahead of you, while dashing you are invisible and invulnerable, dashes also automatically scale heights. ")
            .put(RpgClass.WARRIOR, "Charge: " +
                    "Locks you in a certain direction with an increased speed, " +
                    "while charging you break through most blocks and deal " +
                    "increased damage and inflict status effects on hit.")
            .put(RpgClass.CLERIC, "Bind: " +
                    "Shoot out a projectile that on hit connects you and the hit entity, when connected you steal a percentage of their hp. Disconnect your bind by sending another projectile.")
            .put(RpgClass.WIZARD, "Transmute: " +
                    "Convert your experience into status effects, the more experience you have the better effects you'll get." )
            .build();



    protected ClassScreen(Text title) {super(title);}

    @Override
    protected void init() {

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int iconX = 90;
        int iconY = 20;

        if (mouseX > iconX && mouseX < iconX + ABILITY_ICON_SCALE && mouseY > iconY && mouseY < iconY + ABILITY_ICON_SCALE) {
            context.drawWrappedText(client.textRenderer, StringVisitable.plain(classAbilityDescriptionMap.get(CurrentRpgClass.getCurrentRpgClass())),
                    iconX + 40, iconY, 180, 0xFFFFFFFF, true);
        }

        drawAbilityIcon(CurrentRpgClass.getCurrentRpgClass(), context, iconX, iconY);

        drawClassIcon(CurrentRpgClass.getCurrentRpgClass(), context, (int) (48 * SCALE), (int) (48 * SCALE)).accept(20, 20);
    }

    BiConsumer<Integer, Integer> drawClassIcon(RpgClass rpgClass, DrawContext context, int width, int height) {
        int u = 0;
        int v = 0;

        switch (rpgClass) {
            case RpgClass.ROGUE -> {
                u = 32;
            }
            case RpgClass.CLERIC -> {
                v = 32;
            }
            case RpgClass.WIZARD -> {
                u = 32;
                v = 32;
            }
        }

        int finalU = u;
        int finalV = v;

        return (x, y) -> {
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED, ClassSelectScreen.classIconsTexture,
                    x, y, finalU, finalV,
                    width, height, 32, 32, 64, 64
            );
        };
    }

    void drawAbilityIcon(RpgClass rpgClass, DrawContext context, int x, int y) {
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED, classIdentifierMap.get(rpgClass),
                x, y, 0,0, ABILITY_ICON_SCALE, ABILITY_ICON_SCALE,23, 23, 23, 23
        );

    }
}
