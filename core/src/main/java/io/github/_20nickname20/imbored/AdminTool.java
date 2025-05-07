package io.github._20nickname20.imbored;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Item;

public class AdminTool {

    /*
PistolItem.class;
AutomaticRifleItem.class;
ShotgunItem.class;
BandageItem.class;
RubberDistanceJointItem.clas
GrenadeItem.class;
MinigunItem.class;
FirstAidKitItem.class;
EnergyDrinkItem.class;
HardDistanceJointItem.class;
LandmineItem.class;
GrappleHookItem.class;
     */

    public static boolean isEnabled = false;
    // Renders a slot
    static void renderSlot(ShapeRenderer renderer, boolean active, float x, float y) {
        renderer.rect(x - 2, y - 2, 4, 4);
        if (active) {
            renderer.rect(x - 2.2f, y - 2.2f, 4.4f, 4.4f);
        }
    }

    public static final float slotSize = 4.6f;

    // Calls a renderSlot() in int slotAmount
    public static void renderPart(ShapeRenderer renderer, int slotAmount, Item special, boolean isEnabled) {
        int range = slotAmount / 2;
        if (!isEnabled) return; // Checks if isEnabled == true
        for (int i = -range; i <= range; i++) {
            if (special != null) {
                renderer.setColor(0.9f, 0.4f, 0.4f, 1f);
            } else {
                renderer.setColor(0.5f, 0.5f, 0.5f, 1f);
            }
            renderSlot(renderer, i == 0, i * slotSize, 0);
        }
    }

    public static void renderPart(ShapeRenderer renderer, int slotAmount) {
        renderPart(renderer, slotAmount, null, isEnabled);
    }
}
