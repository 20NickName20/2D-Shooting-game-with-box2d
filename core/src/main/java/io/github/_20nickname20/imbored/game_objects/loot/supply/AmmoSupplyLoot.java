package io.github._20nickname20.imbored.game_objects.loot.supply;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.LootGenerator;
import io.github._20nickname20.imbored.game_objects.items.ammo.AutomaticRifleCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.PistolCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.ShotgunCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.GrappleHookItem;
import io.github._20nickname20.imbored.game_objects.items.usable.LandmineItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.HardDistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.RubberDistanceJointItem;

import java.util.ArrayList;
import java.util.List;

public class AmmoSupplyLoot implements LootGenerator {
    @Override
    public List<Item> generate(float amountMultiplier) {
        List<Item> loot = new ArrayList<>();
        int amount = (int) MathUtils.random(3 * amountMultiplier, 4 * amountMultiplier);
        for (int i = 0; i < amount; i++) {
            Class<? extends Item> type = switch (MathUtils.random(3)) {
                case 0 -> PistolCartridgeItem.class;
                case 1 -> ShotgunCartridgeItem.class;
                default -> AutomaticRifleCartridgeItem.class;
            };

            loot.add(Item.createFromType(type, null));
        }
        return loot;
    }
}
