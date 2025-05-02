package io.github._20nickname20.imbored.game_objects.entities.living;

import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.util.Shapes;
import io.github._20nickname20.imbored.game_objects.entities.LivingEntity;

public abstract class HumanEntity extends LivingEntity {
    public HumanEntity(GameWorld world, float x, float y, float maxHealth, float defaultMaxWalkSpeed) {
        super(world, x, y, Shapes.boxShape(1.75f, 3.5f), maxHealth, defaultMaxWalkSpeed);
    }
}
