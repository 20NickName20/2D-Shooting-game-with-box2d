package io.github._20nickaname20.imbored;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import io.github._20nickaname20.imbored.entities.living.human.PlayerEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Entity {
    public final Body b;
    final Material material;
    final World world;

    public Set<Body> contacts = new HashSet<>();
    private float lastContactTime = 0;

    private boolean isRemoved = false;

    public Entity(World world, float x, float y, Shape shape, Material material) {
        this.material = material;
        this.world = world;
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
}
