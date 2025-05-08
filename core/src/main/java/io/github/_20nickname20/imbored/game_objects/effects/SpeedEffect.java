package io.github._20nickname20.imbored.game_objects.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Effect;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.LivingEntity;

public class SpeedEffect extends Effect {
    public SpeedEffect(float time, float amplifier) {
        super(time, amplifier);
    }

    @Override
    public void onApply(Entity appliedEntity) {
        super.onApply(appliedEntity);
        if (appliedEntity instanceof LivingEntity living) {
            living.setSpeedModifier(2f * this.amplifier);
            living.setMaxWalkSpeed(living.getDefaultMaxWalkSpeed() * 2 * this.amplifier);
        }
    }



    @Override
    public void remove() {
        super.remove();
        if (this.appliedEntity instanceof LivingEntity living) {
            living.setSpeedModifier(1);
            living.setMaxWalkSpeed(living.getDefaultMaxWalkSpeed());
        }
    }
}
