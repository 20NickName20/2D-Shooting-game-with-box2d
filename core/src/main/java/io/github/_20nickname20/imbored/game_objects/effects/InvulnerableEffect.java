package io.github._20nickname20.imbored.game_objects.effects;

import io.github._20nickname20.imbored.game_objects.Effect;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;
import io.github._20nickname20.imbored.game_objects.entities.LivingEntity;

public class InvulnerableEffect extends Effect {
    public InvulnerableEffect(float time, float amplifier) {
        super(time, amplifier);
    }

    @Override
    public void onApply(Entity appliedEntity) {
        super.onApply(appliedEntity);
        if (appliedEntity instanceof DamagableEntity damagableEntity) {
            damagableEntity.setDamageScale(0.0f);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (appliedEntity instanceof DamagableEntity damagableEntity) {
            damagableEntity.setDamageScale(1.0f);
        }
    }
}
