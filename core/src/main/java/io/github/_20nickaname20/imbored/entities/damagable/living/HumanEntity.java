package io.github._20nickaname20.imbored.entities.damagable.living;

import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickaname20.imbored.Util;
import io.github._20nickaname20.imbored.entities.damagable.LivingEntity;

public abstract class HumanEntity extends LivingEntity {
    public HumanEntity(World world, float x, float y, float maxHealth) {
        super(world, x, y, Util.boxShape(1.75f, 3.5f), maxHealth);
    }
}
