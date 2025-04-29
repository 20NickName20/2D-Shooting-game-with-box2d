package io.github._20nickname20.imbored.entities;

import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.Material;
import io.github._20nickname20.imbored.entities.living.human.cursor.PlayerEntity;

public class BlockEntity extends DamagableEntity implements Grabbable {
    public PlayerEntity grabber;

    public BlockEntity(GameWorld world, float x, float y, Shape shape, Material material, float maxHealth) {
        super(world, x, y, shape, material, maxHealth);
    }

    @Override
    public PlayerEntity getGrabber() {
        return grabber;
    }

    @Override
    public void remove() {
        super.remove();
        if (grabber != null) {
            grabber.put();
        }
    }
}
