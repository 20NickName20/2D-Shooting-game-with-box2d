package io.github._20nickname20.imbored.game_objects.items.usable.timed;

import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;
import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;
import io.github._20nickname20.imbored.game_objects.items.usable.TimedUsableItem;

public abstract class HealItem extends TimedUsableItem {
    public HealItem() {
        super();
    }

    public HealItem(ItemData data) {
        super(data);
    }

    public abstract float getHealAmount();

    @Override
    protected void onUseFinish(Entity holder) {
        if (holder instanceof DamagableEntity damagable) {
            damagable.heal(getHealAmount());
        }
        super.onUseFinish(holder);
    }
}
