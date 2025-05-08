package io.github._20nickname20.imbored;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Inventory;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;
import io.github._20nickname20.imbored.game_objects.items.ammo.AutomaticRifleCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.PistolCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.ShotgunCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.SniperRifleCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.scrap.MetalScrapItem;
import io.github._20nickname20.imbored.game_objects.items.scrap.PlankItem;
import io.github._20nickname20.imbored.game_objects.items.scrap.StoneItem;
import io.github._20nickname20.imbored.game_objects.items.usable.grenade.GrenadeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.PistolItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.ShotgunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.SniperRifleGunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic.AutomaticRifleItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic.MinigunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.HardDistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.RubberDistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.heal.BandageItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.heal.FirstAidKitItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.modirfiers.EnergyDrinkItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.modirfiers.ShieldPotionItem;

import java.util.ArrayList;
import java.util.List;

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

    static List<Item> items = List.of(
        Item.createFromType(PistolItem.class, null),
        Item.createFromType(AutomaticRifleItem.class, null),
        Item.createFromType(ShotgunItem.class, null),
        Item.createFromType(SniperRifleGunItem.class, null),
        Item.createFromType(MinigunItem.class, null),
        Item.createFromType(GrenadeItem.class, null),

        Item.createFromType(PistolCartridgeItem .class, null),
        Item.createFromType(AutomaticRifleCartridgeItem.class, null),
        Item.createFromType(ShotgunCartridgeItem.class, null),
        Item.createFromType(SniperRifleCartridgeItem.class, null),

        Item.createFromType(PlankItem.class, null),
        Item.createFromType(StoneItem.class, null),
        Item.createFromType(MetalScrapItem.class, null),

        Item.createFromType(HardDistanceJointItem.class, null),
        Item.createFromType(RubberDistanceJointItem.class, null),

        Item.createFromType(BandageItem.class, null),
        Item.createFromType(FirstAidKitItem.class, null),

        Item.createFromType(EnergyDrinkItem.class, null),
        Item.createFromType(ShieldPotionItem.class, null)
    );




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
    public static void renderFull(ShapeRenderer renderer, boolean isEnabled) {
        if (!isEnabled) return; // Checks if isEnabled == true
        for (int i = 0; i <= items.size(); i++) {
            renderer.setColor(1f, 1f, 1f, 1f);
            renderSlot(renderer, i == 0, i * slotSize, 0);
        }
    }

    public static void renderFull(ShapeRenderer renderer) {
        renderFull(renderer, isEnabled);
    }
}
