package io.github._20nickaname20.imbored;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickaname20.imbored.entities.damagable.living.human.cursor.PlayerEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Entity {
    public final Body b;
    public final Material material;
    public final World world;

    public Set<Body> contacts = new HashSet<>();
    private float lastContactTime = 0;

    private boolean isRemoved = false;
    protected Shape shape;

    public final UUID uuid = UUID.randomUUID();

    public Entity(World world, float x, float y, Shape shape, Material material) {
        this.material = material;
        this.world = world;
        this.shape = shape;
        b = Util.createBody(world, x, y, shape, material.density, material.friction, material.restitution);
        b.setUserData(this);
    }

    public void update(float dt) {

    }

    public Material getMaterial() {
        return material;
    }

    public boolean render(ShapeRenderer renderer) {
        return false;
    }

    public float getLastContactTime() {
        if (!contacts.isEmpty()) return Util.time();
        return lastContactTime;
    }

    public void beginContact(Body other) {
        if (other.getUserData() instanceof PlayerEntity otherEntity) {
            return;
        }
        this.contacts.add(other);
    }

    public void endContact(Body other) {
        this.lastContactTime = Util.time();
        this.contacts.remove(other);
    }

    public float getTimeSinceContact() {
        if (!contacts.isEmpty()) return 0;
        return Util.time() - lastContactTime;
    }

    public void remove() {
        for (JointEdge jointEdge : b.getJointList()) {
            world.destroyJoint(jointEdge.joint);
        }
        world.destroyBody(b);
        isRemoved = true;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public static Entity getEntity(Body body) {
        if (body.getUserData() instanceof Entity entity) return entity;
        return null;
    }

    public static float getVolume(Body body) {
        return body.getMass() / body.getFixtureList().get(0).getDensity();
    }
}
