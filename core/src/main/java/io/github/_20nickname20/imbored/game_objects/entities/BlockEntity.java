package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public abstract class BlockEntity extends DamagableEntity implements Grabbable {
    private PlayerEntity grabber;
    public float maxHealth = 1;

    public BlockEntity(GameWorld world, float x, float y, Shape shape) {
        super(world, x, y, shape);
    }

    public BlockEntity(GameWorld world, EntityData data) {
        super(world, data);
    }

    @Override
    public void onSpawn(World world) {
        this.maxHealth = this.area * getMaterial().healthPerUnit;
        super.onSpawn(world);
    }

    @Override
    public float getMaxHealth() {
        return maxHealth;
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
