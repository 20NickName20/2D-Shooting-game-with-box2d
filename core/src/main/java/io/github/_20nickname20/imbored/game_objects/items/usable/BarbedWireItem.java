package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.game_objects.entities.BarbedWireEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.With;

public class BarbedWireItem extends UsableItem {

    private static final float SIZE = 0.5f;

    @Override
    public float getSize() {
        return SIZE;
    }

    @Override
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Color.GRAY);
        renderer.withRotation(45f, () -> {
            for (float i = 0; i < 5; i++) {
                renderer.circle(i - 2f, 0f, 1f);
            }
        });
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        Vector2 cursorPosition = player.getCursorPosition();
        BarbedWireEntity entity = new BarbedWireEntity(player.gameWorld, cursorPosition.x, cursorPosition.y);
        entity.onSpawnAction(() -> {
            entity.b.applyLinearImpulse(player.getCursorDirection().scl(5), entity.b.getPosition(), true);
        });
        player.gameWorld.spawn(entity);
        player.removeEquippedItem();

    }

    @Override
    public void onEndUse(PlayerEntity player) {

    }

    @Override
    public float getCursorDistance() {
        return 10f;
    }
}
