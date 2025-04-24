package io.github._20nickaname20.imbored.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickaname20.imbored.Entity;
import io.github._20nickaname20.imbored.Material;

public abstract class LivingEntity extends Entity implements Moving {
    float health;
    protected Vector2 movement = new Vector2();
    private float lastXMovement = 0;
    private float lastYMovement = 0;
    float maxWalkSpeed = 20;

    public LivingEntity(World world, float x, float y, Shape shape) {
        super(world, x, y, shape, Material.FLESH);
        b.setFixedRotation(true);
    }

    @Override
    public void update(float dt) {
        if (this.b.getLinearVelocity().len() > maxWalkSpeed) return;
        this.b.applyLinearImpulse(this.getMovement(), this.b.getPosition(), true);
    }

    @Override
    public Vector2 getMovement() {
        return movement.cpy();
    }

    @Override
    public boolean isMoving() {
        return !movement.isZero();
    }

    @Override
    public void setXMovement(float x) {
        movement.x = x;
        if (x == 0) return;
        lastXMovement = x;
    }

    @Override
    public void setYMovement(float y) {
        movement.y = y;
        if (y == 0) return;
        lastYMovement = y;
    }

    @Override
    public void clearXMovement() {
        movement.x = 0;
    }

    @Override
    public void clearYMovement() {
        movement.y = 0;
    }

    @Override
    public float getLastXMovement() {
        return lastXMovement;
    }

    @Override
    public float getLastYMovement() {
        return lastYMovement;
    }
}
