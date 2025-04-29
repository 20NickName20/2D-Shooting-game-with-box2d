package io.github._20nickname20.imbored.entities;

import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.Material;
import io.github._20nickname20.imbored.util.Shapes;

public abstract class ItemEntity extends BlockEntity implements Grabbable {

    public ItemEntity(World world, float x, float y, Material material) {
        super(world, x, y, Shapes.boxShape(0.5f, 0.5f), material, 500);
    }

    @Override
    public void spawn(World world) {
        super.spawn(world);
        this.b.setFixedRotation(true);
    }
}
