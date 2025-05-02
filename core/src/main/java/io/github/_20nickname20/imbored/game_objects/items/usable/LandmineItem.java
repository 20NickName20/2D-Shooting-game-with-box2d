package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;
import io.github._20nickname20.imbored.game_objects.entities.LandmineEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;

public class LandmineItem extends UsableItem {
    public LandmineItem(Entity holder) {
        super(holder, 5);
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        Vector2 cursorPosition = player.getCursorPosition();
        LandmineEntity entity = new LandmineEntity(player.gameWorld, cursorPosition.x, cursorPosition.y);
        entity.onSpawnAction(() -> {
            entity.b.applyLinearImpulse(player.cursorDirection.cpy().scl(30), entity.b.getPosition(), true);
        });
        player.gameWorld.spawn(entity);
        player.removeSelectedItem();
    }

    @Override
    public void onEndUse(PlayerEntity player) {

    }

    @Override
    public void onSelect(InventoryHolder holder) {
        super.onSelect(holder);
        if (holder instanceof CursorEntity cursorEntity) {
            cursorEntity.setCursorDistance(8);
        }
    }

    @Override
    public void onDeselect(InventoryHolder holder) {
        super.onDeselect(holder);
        if (holder instanceof CursorEntity cursorEntity) {
            cursorEntity.setCursorDistance(cursorEntity.getDefaultCursorDistance());
        }
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(0, 0.5f, 0.1f, 1);
        renderer.rect(-1.5f, 0.5f, 3, 1);
    }
}
