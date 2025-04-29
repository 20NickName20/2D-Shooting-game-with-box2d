package io.github._20nickname20.imbored.items;

import io.github._20nickname20.imbored.Entity;
import io.github._20nickname20.imbored.Item;
import io.github._20nickname20.imbored.entities.InventoryHolder;
import io.github._20nickname20.imbored.entities.damagable.living.human.cursor.PlayerEntity;

public abstract class UsableItem extends Item {
    public UsableItem(Entity holder, float size) {
        super(holder, size);
    }

    public abstract void onStartUse(PlayerEntity player);
    public abstract void onEndUse(PlayerEntity player);

    @Override
    public void onDeselect(InventoryHolder holder) {
        super.onDeselect(holder);
        if (holder instanceof PlayerEntity player) {
            onEndUse(player);
        }
    }
}
