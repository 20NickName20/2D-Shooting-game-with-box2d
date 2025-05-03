package io.github._20nickname20.imbored.game_objects.items;

import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public abstract class BodyEquipable extends Item {
    public BodyEquipable(Entity holder, float size) {
        super(holder, size);
    }

    public abstract void onBodyEquip(PlayerEntity player);
    public abstract void onBodyUnequip(PlayerEntity player);
}
