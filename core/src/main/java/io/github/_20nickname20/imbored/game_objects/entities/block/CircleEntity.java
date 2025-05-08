package io.github._20nickname20.imbored.game_objects.entities.block;

import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.entities.BlockEntity;
import io.github._20nickname20.imbored.util.Shapes;


public abstract class CircleEntity extends BlockEntity {
    public CircleEntity(GameWorld world, float x, float y, float radius) {
        super(world, x, y, Shapes.circleShape(radius));
    }

    public CircleEntity(GameWorld world, EntityData data) {
        super(world, data);
    }
}
