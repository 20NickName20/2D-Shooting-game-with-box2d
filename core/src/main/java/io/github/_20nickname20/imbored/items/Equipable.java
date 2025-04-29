package io.github._20nickname20.imbored.items;

import io.github._20nickname20.imbored.Entity;
import io.github._20nickname20.imbored.Item;
import io.github._20nickname20.imbored.entities.InventoryHolder;
import io.github._20nickname20.imbored.entities.damagable.living.human.cursor.PlayerEntity;

public abstract class Equipable extends Item {
    public Equipable(Entity holder, float size) {
        super(holder, size);
    }

    public abstract void onEquip(PlayerEntity player);
    public abstract void onUnequip(PlayerEntity player);
}
