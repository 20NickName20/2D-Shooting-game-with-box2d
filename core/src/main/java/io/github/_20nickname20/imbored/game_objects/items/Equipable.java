package io.github._20nickname20.imbored.game_objects.items;

import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public abstract class Equipable extends Item {
    public Equipable(Entity holder, float size) {
        super(holder, size);
    }

    public abstract void onEquip(PlayerEntity player);
    public abstract void onUnequip(PlayerEntity player);
}
