package io.github._20nickname20.imbored.game_objects.items.usable.timed;

import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;
import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;
import io.github._20nickname20.imbored.game_objects.items.usable.TimedUsableItem;

public abstract class HealItem extends TimedUsableItem {
    private final float healAmount;
    public HealItem(Entity holder, float size, float useTime, float healAmount) {
        super(holder, size, useTime);
        this.healAmount = healAmount;
    }

    @Override
    protected void onUseFinish(Entity holder) {
        if (holder instanceof DamagableEntity damagable) {
            damagable.heal(healAmount);
        }
        super.onUseFinish(holder);
    }
}
