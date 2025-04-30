package io.github._20nickname20.imbored.game_objects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.util.Util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Entity {
    public Body b;
    protected final Shape shape;
    public final Material material;
    public final GameWorld gameWorld;
    public final World world;

    public Set<Body> contacts = new HashSet<>();
    private float lastContactTime = 0;
    private Body lastContactedBody = null;

    private boolean isRemoved = false;

    public final UUID uuid = UUID.randomUUID();
    private final float spawnX, spawnY;
    private Runnable onSpawn = null;
    public float spawnTime;

    public Entity(GameWorld gameWorld, float x, float y, Shape shape, Material material) {
        this.spawnX = x;
        this.spawnY = y;
        this.material = material;
        this.gameWorld = gameWorld;
        this.world = gameWorld.world;
        this.shape = shape;
    }

    public void onSpawnAction(Runnable onSpawn) {
        this.onSpawn = onSpawn;
    }

    public void onSpawn(World world) {
        b = Util.createBody(world, spawnX, spawnY, shape, material.density, material.friction, material.restitution);
        b.setUserData(this);
        spawnTime = Util.time();

        if (onSpawn == null) return;
        onSpawn.run();
    }

    public void update(float dt) {

    }

    public boolean render(ShapeRenderer renderer) {
        return false;
    }

    public float getLastContactTime() {
        if (!contacts.isEmpty()) return Util.time();
        return lastContactTime;
    }

    public void beginContact(Body other) {
        this.contacts.add(other);
        lastContactedBody = other;
    }

    public void endContact(Body other) {
        this.lastContactTime = Util.time();
        this.contacts.remove(other);
    }

    public boolean shouldCollide(Entity other) {
        return true;
    }

    public float getTimeSinceContact() {
        if (!contacts.isEmpty()) return 0;
        return Util.time() - lastContactTime;
    }

    public void remove() {
        isRemoved = true;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public static Entity getEntity(Body body) {
        if (body.getUserData() instanceof Entity entity) return entity;
        return null;
    }

    public Body getLastContactedBody() {
        return lastContactedBody;
    }
}
