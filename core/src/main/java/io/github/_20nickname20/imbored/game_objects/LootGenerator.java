package io.github._20nickname20.imbored.game_objects;

import java.util.List;

public interface LootGenerator {
    List<Item> generate(float amountMultiplier);
}
