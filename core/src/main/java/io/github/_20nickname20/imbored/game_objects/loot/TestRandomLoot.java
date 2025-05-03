package io.github._20nickname20.imbored.game_objects.loot;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.LootGenerator;
import io.github._20nickname20.imbored.game_objects.items.usable.GrappleHookItem;
import io.github._20nickname20.imbored.game_objects.items.usable.LandmineItem;
import io.github._20nickname20.imbored.game_objects.items.usable.grenade.GrenadeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.ShotgunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.PistolItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic.AutomaticRifleItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic.MinigunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.HardDistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.RubberDistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.heal.BandageItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.heal.FirstAidKitItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.modirfiers.EnergyDrinkItem;

import java.util.List;

public class TestRandomLoot implements LootGenerator {
    @Override
    public List<Item> generate(float amountMultiplier) {
        Class<? extends Item> type = switch (MathUtils.random(16)) {
            case 0 -> PistolItem.class;
            case 1 -> AutomaticRifleItem.class;
            case 2 -> ShotgunItem.class;
            case 3 -> BandageItem.class;
            case 4 -> RubberDistanceJointItem.class;
            case 5 -> GrenadeItem.class;
            case 6 -> MinigunItem.class;
            case 7 -> FirstAidKitItem.class;
            case 8 -> EnergyDrinkItem.class;
            case 9 -> HardDistanceJointItem.class;
            case 10 -> LandmineItem.class;
            default -> GrappleHookItem.class;
        };
        return List.of(Item.createFromType(type, null));
    }
}
