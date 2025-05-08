package io.github._20nickname20.imbored.game_objects.entities.block.circle;

import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.block.CircleEntity;

public class RockCircleEntity extends CircleEntity {
    public RockCircleEntity(GameWorld world, float x, float y, float radius) {
        super(world, x, y, radius);
    }

    public RockCircleEntity(GameWorld world, EntityData data) {
        super(world, data);
    }

    @Override
    public Material getMaterial() {
        return Material.ROCK;
    }
}
