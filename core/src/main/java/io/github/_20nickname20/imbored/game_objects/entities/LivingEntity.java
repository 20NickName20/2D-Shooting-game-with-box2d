package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;

public abstract class LivingEntity extends DamagableEntity implements Moving {
    protected Vector2 movement = new Vector2();
    private float lastXMovement = 0;
    private float lastYMovement = 0;
    public final float defaultMaxWalkSpeed;
    private float maxWalkSpeed;
    protected float speedModifier = 1;

    public LivingEntity(GameWorld world, float x, float y, Shape shape, float maxHealth, float defaultMaxWalkSpeed) {
        super(world, x, y, shape, Material.FLESH, maxHealth);
        this.defaultMaxWalkSpeed = defaultMaxWalkSpeed;
        this.maxWalkSpeed = defaultMaxWalkSpeed;
    }

    public float getDefaultMaxWalkSpeed() {
        return defaultMaxWalkSpeed;
    }

    public float getMaxWalkSpeed() {
        return maxWalkSpeed;
    }

    public void setMaxWalkSpeed(float maxWalkSpeed) {
        this.maxWalkSpeed = maxWalkSpeed;
    }

    public float getSpeedModifier() {
        return speedModifier;
    }

    public void setSpeedModifier(float speedModifier) {
        this.speedModifier = speedModifier;
    }

    @Override
    public void onSpawn(World world) {
        super.onSpawn(world);
        this.b.setFixedRotation(true);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (this.b.getLinearVelocity().len() > maxWalkSpeed && this.b.getLinearVelocity().dot(this.getMovement()) > 0) return;
        this.b.applyLinearImpulse(this.getMovement().cpy().scl(dt * speedModifier * 100), this.b.getPosition(), true);
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
