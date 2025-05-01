package io.github._20nickname20.imbored.game_objects.items.usable.grenade;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.BaseGrenadeItem;

public class GrenadeItem extends BaseGrenadeItem {
    public GrenadeItem(Entity holder) {
        super(holder, 2, 10f, 10, 75, 50);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(0.1f, 0.5f, 0.3f, 1);
        renderer.ellipse(-1, -1, 2, 3f, 30f, 6);
        renderer.ellipse(-1, -1.3f, 2, 3f, 30f, 6);
        renderer.setColor(0.5f, 0.5f, 0f, 1);
        renderer.circle(0, 0, timeLeft / 2);
    }
}
