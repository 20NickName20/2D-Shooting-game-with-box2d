package io.github._20nickname20.imbored.game_objects.items.usable.grenade;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.BaseGrenadeItem;
import io.github._20nickname20.imbored.util.With;

public class GrenadeItem extends BaseGrenadeItem {
    private static final float POWER = 70f;
    private static final float DAMAGE = 20f;
    private static final float RANGE = 60f;
    private static final float INIT_TIME = 8f;

    public GrenadeItem() {
        super();
    }

    public GrenadeItem(ItemData data) {
        super(data);
    }

    @Override
    public float getPower() {
        return POWER;
    }

    @Override
    public float getDamage() {
        return DAMAGE;
    }

    @Override
    public float getRange() {
        return RANGE;
    }

    @Override
    public float getInitTime() {
        return INIT_TIME;
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(0.1f, 0.5f, 0.3f, 1);
        renderer.ellipse(-1, -1, 2, 3f, 30f, 6);
        renderer.ellipse(-1, -1.3f, 2, 3f, 30f, 6);
        renderer.setColor(0.5f, 0.5f, 0f, 1);
        renderer.circle(0, 0, timeLeft / 2);
    }
}
