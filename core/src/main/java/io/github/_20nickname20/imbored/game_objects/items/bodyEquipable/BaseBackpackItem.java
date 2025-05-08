package io.github._20nickname20.imbored.game_objects.items.bodyEquipable;

import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.BodyEquipableItem;

public abstract class BaseBackpackItem extends BodyEquipableItem {
    public BaseBackpackItem() {
        super();
    }

    public BaseBackpackItem(ItemData data) {
        super(data);
    }

    public abstract float getAdditionalSize();

    @Override
    public void onBodyEquip(PlayerEntity player) {
        player.getInventory().setSizeLimit(player.DEFAULT_INVENTORY_SIZE + getAdditionalSize());
    }

    @Override
    public void onBodyUnequip(PlayerEntity player) {
        for (Item item : player.getInventory().setSizeLimit(player.DEFAULT_INVENTORY_SIZE)) {
            player.dropItem(item);
        }
    }
}
