package io.github._20nickname20.imbored.game_objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Util;

import java.util.*;

public abstract class Entity implements Removable {
    public Body b;
    protected final Shape shape;
    public final GameWorld gameWorld;
    public final World world;
    protected final float area;

    private static final HashMap<UUID, Entity> entitiesByUuid = new HashMap<>();

    protected EntityData persistentData = null;

    public Set<Body> contacts = new HashSet<>();
    private float lastContactTime = 0;
    private Body lastContactedBody = null;

    private boolean isRemoved = false;

    public int chunkPos = 0;

    public final UUID uuid;
    public final float spawnX, spawnY;
    private Runnable onSpawn = null;
    public float spawnTime;
    private final Set<Effect> effects = new HashSet<>();

    public Entity(GameWorld gameWorld, float x, float y, Shape shape) {
        this.spawnX = x;
        this.spawnY = y;
        this.gameWorld = gameWorld;
        this.world = gameWorld.world;
        this.shape = shape;
        this.area = Util.getArea(shape);
        this.uuid = UUID.randomUUID();
        entitiesByUuid.put(this.uuid, this);
    }

    public Entity(GameWorld gameWorld, EntityData data) {
        this.gameWorld = gameWorld;
        this.world = gameWorld.world;
        this.persistentData = data;

        this.uuid = UUID.fromString(data.uuid);
        if (entitiesByUuid.containsKey(this.uuid)) {
            Util.printStackTrace("err");
            new RuntimeException("Tried to create instance of entity with existing UUID").printStackTrace();
        }
        entitiesByUuid.put(this.uuid, this);
        this.spawnX = data.posX;
        this.spawnY = data.posY;

        if (data.bodyType.equals("circle") || data.polygonVertices.length < 3) {
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(data.circleRadius);
            this.shape = circleShape;
        } else {
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(data.polygonVertices);
            this.shape = polygonShape;
        }
        this.area = Util.getArea(this.shape);
    }

    public abstract Material getMaterial();
    public float getImpenetrability() {
        return 1f;
    }

    public final void spawn() {
        gameWorld.spawn(this);
    }

    public void onSpawnAction(Runnable onSpawn) {
        this.onSpawn = onSpawn;
    }

    public void onSpawn(World world) {
        b = Util.createBody(world, spawnX, spawnY, shape, getMaterial().density, getMaterial().friction, getMaterial().restitution);
        b.setUserData(this);
        spawnTime = Util.time();

        if (persistentData != null) {
            switch (persistentData.bodyType) {
                case "static" -> b.setType(BodyDef.BodyType.StaticBody);
                case "kinematic" -> b.setType(BodyDef.BodyType.KinematicBody);
            }
            b.setTransform(persistentData.posX, persistentData.posY, persistentData.angle);
            b.setLinearVelocity(persistentData.velX, persistentData.velY);
            b.setAngularVelocity(persistentData.angularVel);
        }

        if (onSpawn == null) return;
        onSpawn.run();
    }

    private final Set<Effect> toRemove = new HashSet<>();

    public void update(float dt) {
        for (Effect effect : effects) {
            effect.update(dt);
            if (effect.isRemoved()) {
                toRemove.add(effect);
            }
        }

        effects.removeAll(toRemove);
    }

    public void applyEffect(Effect effect) {
        effect.onApply(this);
        effects.add(effect);
    }

    public boolean render(GameRenderer renderer) {
        //for (Effect effect : effects) {
        //    effect.render(renderer, this);
        //}
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
        entitiesByUuid.remove(this.uuid);
    }

    public final boolean isRemoved() {
        return isRemoved;
    }

    public Body getLastContactedBody() {
        return lastContactedBody;
    }

    public boolean onInteract(Interact interact) {
        return false;
    }

    /**
     * DON'T FUCKING FORGET TO TYPE "this.persistentData = customData"
     */
    public EntityData createPersistentData() {
        Shape shape = this.b.getFixtureList().get(0).getShape();

        if (persistentData == null) {
            persistentData = new EntityData();
        }
        persistentData.className = getClass().getName();
        persistentData.uuid = uuid.toString();

        persistentData.bodyType = switch (b.getType()) {
            case DynamicBody -> "dynamic";
            case StaticBody -> "static";
            case KinematicBody -> "kinematic";
        };
        Vector2 pos = b.getPosition();
        persistentData.posX = pos.x;
        persistentData.posY = pos.y;
        persistentData.angle = b.getAngle();
        Vector2 vel = b.getLinearVelocity();
        persistentData.velX = vel.x;
        persistentData.velY = vel.y;
        persistentData.angularVel = b.getAngularVelocity();

        persistentData.circleRadius = shape.getRadius();
        if (shape instanceof PolygonShape polygonShape) {
            persistentData.shapeType = "poly";
            int vertexCount = polygonShape.getVertexCount();
            if (vertexCount > 2) {
                persistentData.polygonVertices = new float[vertexCount * 2];
                Vector2 result = new Vector2();
                for (int i = 0; i < vertexCount; i++) {
                    polygonShape.getVertex(i, result);
                    persistentData.polygonVertices[i * 2] = result.x;
                    persistentData.polygonVertices[i * 2 + 1] = result.y;
                }
            } else {
                persistentData.shapeType = "circle";
                persistentData.polygonVertices = new float[0];
            }
        } else {
            persistentData.shapeType = "circle";
            persistentData.polygonVertices = new float[0];
        }

        return persistentData;
    }

    public static Entity getEntity(Body body) {
        if (body.getUserData() instanceof Entity entity) return entity;
        return null;
    }

    public static Entity getByUuid(UUID uuid) {
        return entitiesByUuid.get(uuid);
    }

    public static Entity createFromData(GameWorld world, EntityData data) {
        try {
            Class<?> clazz = Class.forName(data.className);
            return (Entity) clazz.getConstructor(GameWorld.class, EntityData.class).newInstance(world, data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class EntityData {
        public String className;
        public String uuid;

        public String bodyType;
        public float posX, posY, velX, velY;
        public float angle, angularVel;

        public String shapeType;
        public float[] polygonVertices;
        public float circleRadius;
    }
}
