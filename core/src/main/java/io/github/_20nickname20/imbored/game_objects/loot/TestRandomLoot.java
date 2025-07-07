package io.github._20nickname20.imbored.game_objects.loot;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.LootGenerator;
import io.github._20nickname20.imbored.game_objects.items.ammo.AutomaticRifleCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.PistolCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.ShotgunCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.SniperRifleCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.BarbedWireItem;
import io.github._20nickname20.imbored.game_objects.items.usable.GrappleHookItem;
import io.github._20nickname20.imbored.game_objects.items.usable.LandmineItem;
import io.github._20nickname20.imbored.game_objects.items.usable.grenade.GrenadeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.ShotgunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.PistolItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.SniperRifleGunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic.AutomaticRifleItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic.MinigunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.HardDistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.RubberDistanceJointItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.heal.BandageItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.heal.FirstAidKitItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.modirfiers.EnergyDrinkItem;

import java.util.ArrayList;
import java.util.List;

public class TestRandomLoot implements LootGenerator {
    @Override
    public List<Item> generate(float amountMultiplier) {
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < amountMultiplier; i++) {
            Class<? extends Item> type = switch (MathUtils.random(16)) {
                case 0 -> PistolItem.class;
                case 1 -> AutomaticRifleItem.class;
                case 2 -> MinigunItem.class;
                case 3 -> ShotgunItem.class;
                case 4 -> SniperRifleGunItem.class;
                case 5 -> BandageItem.class;
                case 6 -> FirstAidKitItem.class;
                case 7 -> EnergyDrinkItem.class;
                case 8 -> RubberDistanceJointItem.class;
                case 9 -> HardDistanceJointItem.class;
                case 10 -> BarbedWireItem.class;
                case 11 -> LandmineItem.class;
                case 12 -> PistolCartridgeItem.class;
                case 13 -> AutomaticRifleCartridgeItem.class;
                case 14 -> ShotgunCartridgeItem.class;
                case 15 -> SniperRifleCartridgeItem.class;
                default -> GrappleHookItem.class;
            };
            list.add(Item.createFromType(type, null));
        }
        return list;
    }
}
