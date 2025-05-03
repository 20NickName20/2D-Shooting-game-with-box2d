package io.github._20nickname20.imbored.game_objects.loot.supply;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.LootGenerator;
import io.github._20nickname20.imbored.game_objects.items.usable.GrappleHookItem;
import io.github._20nickname20.imbored.game_objects.items.usable.LandmineItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.HardDistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.RubberDistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.heal.BandageItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.heal.FirstAidKitItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.modirfiers.EnergyDrinkItem;

import java.util.ArrayList;
import java.util.List;

public class StuffSupplyLoot implements LootGenerator {
    @Override
    public List<Item> generate(float amountMultiplier) {
        List<Item> loot = new ArrayList<>();
        int amount = (int) MathUtils.random(4 * amountMultiplier, 6 * amountMultiplier);
        for (int i = 0; i < amount; i++) {
            Class<? extends Item> type = switch (MathUtils.random(3)) {
                case 0 -> LandmineItem.class;
                case 1 -> HardDistanceJointItem.class;
                case 2 -> RubberDistanceJointItem.class;
                default -> GrappleHookItem.class;
            };

            loot.add(Item.createFromType(type, null));
        }
        return loot;
    }
}
