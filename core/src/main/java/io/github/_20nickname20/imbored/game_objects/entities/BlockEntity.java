package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.util.Util;

public abstract class BlockEntity extends DamagableEntity implements Grabbable {
    private PlayerEntity grabber;
    public final float maxHealth;

    public BlockEntity(GameWorld world, float x, float y, Shape shape) {
        super(world, x, y, shape);
        this.maxHealth = this.area * getMaterial().healthPerUnit;
    }

    public BlockEntity(GameWorld world, EntityData data) {
        super(world, data);
        this.maxHealth = this.area * getMaterial().healthPerUnit;
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
