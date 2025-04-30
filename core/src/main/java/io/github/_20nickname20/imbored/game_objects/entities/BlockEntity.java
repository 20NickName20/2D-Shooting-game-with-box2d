package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public class BlockEntity extends DamagableEntity implements Grabbable {
    private PlayerEntity grabber;

    public BlockEntity(GameWorld world, float x, float y, Shape shape, Material material, float maxHealth) {
        super(world, x, y, shape, material, maxHealth);
    }

    @Override
    public PlayerEntity getGrabber() {
        return grabber;
    }

    @Override
    public void onGrabbed(PlayerEntity grabber) {
        this.grabber = grabber;
    }

    @Override
    public void onPutted(PlayerEntity grabber) {
        this.grabber = null;
    }

    @Override
    public void remove() {
        super.remove();
        if (grabber != null) {
            grabber.put();
        }
    }
}
