package io.github._20nickname20.imbored.game_objects.entities.block.box;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.block.BoxEntity;
import io.github._20nickname20.imbored.util.Util;

public class WoodenBoxEntity extends BoxEntity {
    public WoodenBoxEntity(GameWorld world, float x, float y, int sizeX, int sizeY) {
        super(world, x, y, sizeX, sizeY);
    }

    public WoodenBoxEntity(GameWorld world, EntityData data) {
        super(world, data);
    }

    @Override
    public Material getMaterial() {
        return Material.WOOD;
    }

    @Override
    public float getImpenetrability() {
        return 0.6f;
    }
}
