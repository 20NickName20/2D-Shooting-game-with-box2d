package io.github._20nickname20.imbored.game_objects.entities.block.box;

import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.block.BoxEntity;

public class MetalBoxEntity extends BoxEntity {
    public MetalBoxEntity(GameWorld world, float x, float y, int sizeX, int sizeY) {
        super(world, x, y, sizeX, sizeY);
    }

    public MetalBoxEntity(GameWorld world, EntityData data) {
        super(world, data);
    }

    @Override
    public Material getMaterial() {
        return Material.METAL;
    }
}
