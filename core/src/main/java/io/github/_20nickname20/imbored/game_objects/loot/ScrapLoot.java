package io.github._20nickname20.imbored.game_objects.loot;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.LootGenerator;
import io.github._20nickname20.imbored.game_objects.items.ScrapItem;

import java.util.ArrayList;
import java.util.List;

public class ScrapLoot implements LootGenerator {
    private final Class<? extends ScrapItem> type;
    private final float multiplier;

    public ScrapLoot(Class<? extends ScrapItem> type, float multiplier) {
        this.type = type;
        this.multiplier = multiplier;
    }

    @Override
    public List<Item> generate(float amountMultiplier) {
        amountMultiplier *= this.multiplier;
        List<Item> loot = new ArrayList<>();
        System.out.println(amountMultiplier);
        float amount = (float) Math.floor(amountMultiplier);
        float rest = amountMultiplier - amount;
        if (MathUtils.random() < rest) {
            amount++;
        }
        for (int i = 0; i < amount; i++) {
            loot.add(Item.createFromType(type, null));
        }
        return loot;
    }
}
