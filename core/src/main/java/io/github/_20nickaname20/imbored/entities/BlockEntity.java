package io.github._20nickaname20.imbored.entities;

import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickaname20.imbored.Entity;
import io.github._20nickaname20.imbored.Material;

public class BlockEntity extends Entity {
    public boolean isGrabbed = false;

    public BlockEntity(World world, float x, float y, Shape shape, Material material) {
        super(world, x, y, shape, material);
    }
}
