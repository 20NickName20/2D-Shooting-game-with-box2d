package io.github._20nickname20.imbored.game_objects.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Effect;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;

public class ResistanceEffect extends Effect {
    public ResistanceEffect(float time, float amplifier) {
        super(time, amplifier);
    }

    @Override
    public void onApply(Entity appliedEntity) {
        super.onApply(appliedEntity);
        if (appliedEntity instanceof DamagableEntity damagableEntity) {
            damagableEntity.setDamageScale(0.5f);
        }
    }

    @Override
    public void render(ShapeRenderer renderer, Entity entity) {
        renderer.setColor(Color.TEAL);
        for (float i = 0; i < 1; i += 0.3f) {
            renderer.ellipse(-2.8f + i, -5.6f + i, 5.6f - 2 * i, 11.2f - 2 * i);
        }
        super.render(renderer, entity);
    }

    @Override
    public void remove() {
        super.remove();
        if (appliedEntity instanceof DamagableEntity damagableEntity) {
            damagableEntity.setDamageScale(1.0f);
        }
    }
}
