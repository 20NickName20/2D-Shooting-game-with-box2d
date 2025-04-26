package io.github._20nickaname20.imbored.items;

import io.github._20nickaname20.imbored.Item;
import io.github._20nickaname20.imbored.entities.InventoryHolder;
import io.github._20nickaname20.imbored.entities.damagable.living.human.PlayerEntity;

public abstract class UsableItem extends Item {
    public UsableItem(float size) {
        super(size);
    }

    public abstract void onStartUse(PlayerEntity player);
    public abstract void onEndUse(PlayerEntity player);

    @Override
    public void onDeselect(InventoryHolder holder) {
        if (holder instanceof PlayerEntity player) {
            onEndUse(player);
        }
    }
}
