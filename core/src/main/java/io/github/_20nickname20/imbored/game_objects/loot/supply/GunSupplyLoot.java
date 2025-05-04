package io.github._20nickname20.imbored.game_objects.loot.supply;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.LootGenerator;
import io.github._20nickname20.imbored.game_objects.items.usable.grenade.GrenadeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.PistolItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.ShotgunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.SniperGunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic.AutomaticRifleItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic.MinigunItem;

import java.util.ArrayList;
import java.util.List;

public class GunSupplyLoot implements LootGenerator {
    @Override
    public List<Item> generate(float amountMultiplier) { // TODO: add supply drop
        List<Item> loot = new ArrayList<>();
        int amount = (int) MathUtils.random(2 * amountMultiplier, 4 * amountMultiplier);
        for (int i = 0; i < amount; i++) {
            Class<? extends Item> type = switch (MathUtils.random(10)) {
                case 0 -> MinigunItem.class;
                case 1 -> AutomaticRifleItem.class;
                case 2 -> ShotgunItem.class;
                case 3 -> GrenadeItem.class;
                case 4 -> PistolItem.class;
                default -> SniperGunItem.class;
            };

            loot.add(Item.createFromType(type, null));
        }
        return loot;
    }
}
