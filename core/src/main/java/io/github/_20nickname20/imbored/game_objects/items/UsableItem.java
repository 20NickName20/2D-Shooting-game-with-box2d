package io.github._20nickname20.imbored.game_objects.items;

import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public abstract class UsableItem extends Item {
    public UsableItem(Entity holder, float size) {
        super(holder, size);
    }

    public abstract void onStartUse(PlayerEntity player);
    public abstract void onEndUse(PlayerEntity player);

    @Override
    public void onUnequip(PlayerEntity holder) {
        super.onUnequip(holder);
        onEndUse(holder);
    }
}
