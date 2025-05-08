package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.LandmineEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;

public class LandmineItem extends UsableItem {
    private static final float SIZE = 5f;

    public LandmineItem() {
        super();
    }

    public LandmineItem(ItemData data) {
        super(data);
    }

    @Override
    public float getSize() {
        return SIZE;
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        Vector2 cursorPosition = player.getCursorPosition();
        LandmineEntity entity = new LandmineEntity(player.gameWorld, cursorPosition.x, cursorPosition.y);
        entity.onSpawnAction(() -> {
            entity.b.applyLinearImpulse(player.getCursorDirection().scl(30), entity.b.getPosition(), true);
        });
        player.gameWorld.spawn(entity);
        player.removeEquippedItem();
    }

    @Override
    public void onEndUse(PlayerEntity player) {

    }

    @Override
    public float getCursorDistance() {
        return 9;
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(0, 0.5f, 0.1f, 1);
        renderer.rect(-1.5f, -0.5f, 3, 1);
    }
}
