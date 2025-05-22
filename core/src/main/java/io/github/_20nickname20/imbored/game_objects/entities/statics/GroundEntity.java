package io.github._20nickname20.imbored.game_objects.entities.statics;

import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.StaticEntity;

public class GroundEntity extends StaticEntity {
    public GroundEntity(GameWorld world, float x, float y, Shape shape) {
        super(world, x, y, shape);
    }

    public GroundEntity(GameWorld world, EntityData data) {
        super(world, data);
    }

    @Override
    public Material getMaterial() {
        return Material.GROUND;
    }

    @Override
    public float getImpenetrability() {
        return 1000f;
    }
}
