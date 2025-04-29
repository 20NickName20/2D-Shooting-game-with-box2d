package io.github._20nickname20.imbored.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.Material;

public abstract class LivingEntity extends DamagableEntity implements Moving {
    float health;
    protected Vector2 movement = new Vector2();
    private float lastXMovement = 0;
    private float lastYMovement = 0;
    float maxWalkSpeed = 20;

    public LivingEntity(GameWorld world, float x, float y, Shape shape, float maxHealth) {
        super(world, x, y, shape, Material.FLESH, maxHealth);
    }

    @Override
    public void onSpawn(World world) {
        super.onSpawn(world);
        this.b.setFixedRotation(true);
    }

    @Override
    public void update(float dt) {
        if (this.b.getLinearVelocity().len() > maxWalkSpeed && this.b.getLinearVelocity().dot(this.getMovement()) > 0) return;
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
