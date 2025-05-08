package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;

public abstract class StaticEntity extends Entity {
    public StaticEntity(GameWorld world, float x, float y, Shape shape) {
        super(world, x, y, shape);
    }

    public StaticEntity(GameWorld world, EntityData data) {
        super(world, data);
    }

    @Override
    public void onSpawn(World world) {
        super.onSpawn(world);
        this.b.setType(BodyDef.BodyType.StaticBody);
    }
}
