package io.github._20nickname20.imbored.game_objects.loot.supply;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.LootGenerator;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.heal.BandageItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.heal.FirstAidKitItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.modirfiers.EnergyDrinkItem;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.modirfiers.ShieldPotionItem;

import java.util.ArrayList;
import java.util.List;

public class HealSupplyLoot implements LootGenerator {
    @Override
    public List<Item> generate(float amountMultiplier) { // TODO: add melee combat
        List<Item> loot = new ArrayList<>();
        int amount = (int) MathUtils.random(2 * amountMultiplier, 6 * amountMultiplier);
        for (int i = 0; i < amount; i++) {
            Class<? extends Item> type = switch (MathUtils.random(4)) {
                case 0 -> BandageItem.class;
                case 1 -> FirstAidKitItem.class;
                case 2 -> ShieldPotionItem.class;
                default -> EnergyDrinkItem.class;
            };

            loot.add(Item.createFromType(type, null));
        }
        return loot;
    }
}
