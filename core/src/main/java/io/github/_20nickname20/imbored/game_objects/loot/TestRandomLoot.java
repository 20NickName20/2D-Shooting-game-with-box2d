package io.github._20nickname20.imbored.game_objects.loot;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.LootGenerator;
import io.github._20nickname20.imbored.game_objects.items.usable.grenade.GrenadeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.ShotgunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.TestGunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic.AutomaticRifleItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.DistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.HardDistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.RubberDistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.heal.BandageItem;

import java.util.List;

public class TestRandomLoot implements LootGenerator {
    @Override
    public List<Item> generate(float amountMultiplier) {
        Class<? extends Item> type = switch (MathUtils.random(6)) {
            case 0 -> TestGunItem.class;
            case 1 -> AutomaticRifleItem.class;
            case 2 -> ShotgunItem.class;
            case 3 -> BandageItem.class;
            case 4 -> RubberDistanceJointItem.class;
            case 5 -> GrenadeItem.class;
            default -> HardDistanceJointItem.class;
        };
        return List.of(Item.createFromType(type, null));
    }
}
