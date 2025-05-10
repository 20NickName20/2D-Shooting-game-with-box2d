package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.game_objects.entities.BarbWireEntity;
import io.github._20nickname20.imbored.game_objects.entities.LandmineEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.util.With;

public class BarbWireItem extends UsableItem {

    private static final float SIZE = 0.5f;
    private static final float LENGTH = 5f;
    private static final float MAX_DISTANCE = 10f;
    private static final Color COLOR = Color.MAGENTA;
    private static final float FREQUENCY = 100f;
    private static final float DAMPING = 0.01f;

    @Override
    public float getSize() {
        return SIZE;
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Color.GRAY);
        for (float i = 0; i < LENGTH; i++) {
            renderer.circle(i + 1f, 1f, 1f);
        }

    }

    @Override
    public void onStartUse(PlayerEntity player) {
        Vector2 cursorPosition = player.getCursorPosition();
        BarbWireEntity entity = new BarbWireEntity(player.gameWorld, cursorPosition.x, cursorPosition.y);
        entity.onSpawnAction(() -> {
            entity.b.applyLinearImpulse(player.getCursorDirection().scl(30), entity.b.getPosition(), true);
        });
        player.gameWorld.spawn(entity);
        player.removeEquippedItem();

    }

    @Override
    public void onEndUse(PlayerEntity player) {

    }
}
