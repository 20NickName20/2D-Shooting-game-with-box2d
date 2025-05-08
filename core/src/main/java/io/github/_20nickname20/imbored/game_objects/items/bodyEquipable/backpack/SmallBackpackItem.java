package io.github._20nickname20.imbored.game_objects.items.bodyEquipable.backpack;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.bodyEquipable.BaseBackpackItem;

public class SmallBackpackItem extends BaseBackpackItem {
    private static final float SIZE = 3f;
    private static final float ADDITIONAL_SIZE = 4f;

    public SmallBackpackItem() {
        super();
    }

    public SmallBackpackItem(ItemData data) {
        super(data);
    }

    @Override
    public float getAdditionalSize() {
        return ADDITIONAL_SIZE;
    }

    @Override
    public float getSize() {
        return SIZE;
    }

    @Override
    public void renderOnPlayer(ShapeRenderer renderer, PlayerEntity player) {

    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {

    }
}
