package io.github._20nickname20.imbored.game_objects.items;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.HumanEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public abstract class BodyEquipableItem extends Item {
    public BodyEquipableItem() {
        super();
    }

    public BodyEquipableItem(ItemData data) {
        super(data);
    }

    public abstract void onBodyEquip(PlayerEntity player);
    public abstract void onBodyUnequip(PlayerEntity player);

    public abstract void renderOnPlayer(ShapeRenderer renderer, PlayerEntity player);
}
