package io.github._20nickname20.imbored.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.Entity;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.Material;

public class StaticEntity extends Entity {
    public StaticEntity(GameWorld world, float x, float y, Shape shape, Material material) {
        super(world, x, y, shape, material);
    }

    @Override
    public void onSpawn(World world) {
        super.onSpawn(world);
        this.b.setType(BodyDef.BodyType.StaticBody);
    }
}
