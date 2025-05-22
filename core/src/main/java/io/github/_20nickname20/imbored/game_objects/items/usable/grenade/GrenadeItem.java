package io.github._20nickname20.imbored.game_objects.items.usable.grenade;

import com.badlogic.gdx.graphics.Color;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.BaseGrenadeItem;
import io.github._20nickname20.imbored.render.GameRenderer;

public class GrenadeItem extends BaseGrenadeItem {
    private static final float POWER = 70f;
    private static final float DAMAGE = 20f;
    private static final float RANGE = 60f;
    private static final float INIT_TIME = 11.5f;

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
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        if (!isPinOut) {
            renderer.setColor(Color.LIGHT_GRAY);
            renderer.circle(0.1f, 0.86f, 0.375f, 5);
        }
        if (Math.sin((INIT_TIME - timeLeft) * (INIT_TIME - timeLeft)) < 0) {
            renderer.setColor(0.75f, 0.6f, 0f, 1);
        } else {
            renderer.setColor(0.1f, 0.5f, 0.3f, 1);
        }
        renderer.withRotation(30f, () -> {
            renderer.polygon(new float[]{
                -0.2f, -1f,
                0.2f, -1f,
                0.7f, -0.4f,
                0.7f, 0.4f,
                0.2f, 1f,
                -0.2f, 1f,
                -0.7f, 0.4f,
                -0.7f, -0.4f
            });
        });
    }
}
