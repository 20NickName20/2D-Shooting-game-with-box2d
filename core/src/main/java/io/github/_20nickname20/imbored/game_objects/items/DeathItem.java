package io.github._20nickname20.imbored.game_objects.items;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public class DeathItem extends Item {
    public DeathItem(Entity holder) {
        super(holder, 0);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        for (float i = 1; i < 2; i += 0.1f) {
            renderer.circle(0, 0, i * i);
        }
    }

    @Override
    public void onPickup(PlayerEntity holder) {
        holder.kill();
    }
}
