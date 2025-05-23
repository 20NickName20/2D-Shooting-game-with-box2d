package io.github._20nickname20.imbored;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import io.github._20nickname20.imbored.game_objects.Chunk;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.JointEntity;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;
import io.github._20nickname20.imbored.game_objects.entities.ItemEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.handlers.EntityContactFilter;
import io.github._20nickname20.imbored.handlers.EntityContactListener;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.screens.GameScreen;
import io.github._20nickname20.imbored.util.Constants;
import io.github._20nickname20.imbored.util.Ray;
import io.github._20nickname20.imbored.util.Raycast;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.world.ServerWorld;

import java.util.*;

public abstract class GameWorld {
    public final World world;

    protected final Map<Integer, Chunk> loadedChunks = new HashMap<>();
    protected final Map<Integer, Set<Entity>> entitiesByChunk = new HashMap<>();

    public HashMap<String, ServerWorld.PlayerData> playersByUsername = new HashMap<>();

    public static final float CHUNK_WIDTH = 300f;
    public static final int SIMULATION_DISTANCE = 3;

    protected final List<Ray> rays = new ArrayList<>();
    private boolean isFrozen;

    protected Set<UUID> localPlayers = new HashSet<>();

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public Set<Entity> getEntitiesInChunk(int x) {
        return entitiesByChunk.get(x);
    }

    public int getChunkPosition(float x) {
        return Math.round(x / CHUNK_WIDTH);
    }

    public int getChunkPosition(Vector2 pos) {
        return Math.round(pos.x / CHUNK_WIDTH);
    }

    public Set<JointEntity> getJointsInChunk(int x) {
        Array<Joint> joints = new Array<>();
        world.getJoints(joints);
        Set<JointEntity> atChunk = new HashSet<>();
        for (Joint joint : joints) {
            if (!(joint.getUserData() instanceof JointEntity jointEntity)) continue;
            if (x == getChunkPosition(joint.getAnchorA()) || x == getChunkPosition(joint.getAnchorB())) {
                atChunk.add(jointEntity);
            }
        }
        return atChunk;
    }

    protected void addRay(Ray ray) {
        rays.add(ray);
    }

    public void renderRays(GameRenderer renderer) {
        Set<Ray> toRemove = new HashSet<>();
        float time = Util.time();
        for (Ray ray : rays) {
            if (ray.render(renderer, time)) {
                toRemove.add(ray);
            }
        }
        rays.removeAll(toRemove);
    }

    public Chunk getLoadedChunk(int x) {
        return loadedChunks.get(x);
    }

    public boolean isChunkLoaded(int x) {
        return loadedChunks.containsKey(x);
    }

    public abstract Chunk loadChunk(int x);

    public abstract void unloadChunk(int x);

    public void shootRay(Body ignored, Vector2 position, float angleRad, float range, float power, float damage, float rayLength, float raySpeed, Color rayColor, float penetrateAmount) {
        Vector2 impulse = Vector2.X.cpy().rotateRad(angleRad);
        Vector2 endPosition = position.cpy().add(impulse.cpy().scl(range));
        List<Raycast.Result> results = Raycast.castAll(this.world, ignored, position, endPosition);
        if (results.isEmpty()) {
            this.addRay(new Ray(position, endPosition, Util.time(), rayLength, raySpeed, rayColor));
            return;
        }

        impulse.scl(power);
        for (Raycast.Result result : results) {
            if (penetrateAmount <= 0) {
                break;
            }
            endPosition = result.point;
            result.body.applyLinearImpulse(impulse, result.point, true);
            if (result.body.getUserData() instanceof Entity entity) {
                penetrateAmount -= entity.getImpenetrability();
                if (entity instanceof DamagableEntity damagable) {
                    damagable.damage(damage);
                }
                damage /= 2;
            }
        }
        this.addRay(new Ray(position, endPosition, Util.time(), rayLength, raySpeed, rayColor));
    }

    public void explode(Body ignored, Vector2 position, float stepAngle, float range, float power, float damage, float maxOffset, float rayLength, float raySpeed, Color rayColor, float penetrateAmount) {
        for (float angle = 0; angle < Math.PI * 2; angle += stepAngle) {
            shootRay(ignored, position.cpy(), angle + MathUtils.random(-maxOffset, maxOffset), range, power, damage, rayLength, raySpeed, rayColor, penetrateAmount);
        }
    }

    public void explode(Body ignored, Vector2 position, float stepAngle, float range, float power, float damage, float rayLength, float raySpeed, Color rayColor, float penetrateAmount) {
        explode(ignored, position, stepAngle, range, power, damage, 0, rayLength, raySpeed, rayColor, penetrateAmount);
    }

    public final OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());;

    private final Array<Body> bodies = new Array<>();
    private final Array<Body> bodies1 = new Array<>();

    private final Set<Entity> entitiesToSpawn = new HashSet<>();
    private final Set<JointEntity> jointsToSpawn = new HashSet<>();
    private final Set<Joint> jointsToRemove = new HashSet<>();

    public GameWorld() {
        world = new World(new Vector2(0, -Constants.ACCELERATION_OF_GRAVITY), true);
        world.setContactListener(new EntityContactListener());
        world.setContactFilter(new EntityContactFilter());
    }

    public Body anyStaticBody;

    public void spawn(Entity entity) {
        if (entity == null) {
            throw new RuntimeException("ENTITY IS FUCKING NULL");
        }
        if (entity.b != null) return;
        if (entity.isRemoved()) return;
        entitiesToSpawn.add(entity);
    }

    public void spawn(JointEntity joint) {
        if (joint == null) {
            throw new RuntimeException("bro its null...");
        }
        jointsToSpawn.add(joint);
    }

    public void remove(Entity entity) {
        entity.remove();
    }

    public void remove(Joint joint) {
        jointsToRemove.add(joint);
    }

    private float accumulator = 0;
    private int step = 0;

    private void processRemoval(Entity entity) {
        entity.b.setUserData(null);
        for (JointEdge jointEdge : entity.b.getJointList()) {
            world.destroyJoint(jointEdge.joint);
        }
        world.destroyBody(entity.b);
        world.getBodies(bodies1);
        for (Body body1 : bodies1) {
            if (!(body1.getUserData() instanceof Entity entity1)) continue;
            if (entity1.isRemoved()) continue;
            entity1.contacts.remove(entity.b);
        }
    }

    public Vector2 playerCenter = new Vector2();

    private void doPhysicsStep(float dt) {
        int cameraChunkPos = getChunkPosition(camera.position.x);

        float frameTime = Math.min(dt, 0.25f);
        accumulator += frameTime;
        while (accumulator >= Constants.TIME_STEP) {
            if (!isFrozen) {
                world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
            }
            accumulator -= Constants.TIME_STEP;
            step++;
            if (step < Constants.UPDATES_LATENCY) continue;
            step = 0;
            world.getBodies(bodies);
            playerCenter.setZero();
            float playerCount = 0;
            for (Set<Entity> set : entitiesByChunk.values()) {
                set.clear();
            }
            for (Body body : bodies) {
                if (!(body.getUserData() instanceof Entity entity)) continue;

                if (body.getType() == BodyDef.BodyType.StaticBody) {
                    anyStaticBody = body;
                }

                if (body.getPosition().y > 300) {
                    body.applyLinearImpulse(new Vector2(0, -100), body.getPosition(), true);
                }

                if (localPlayers.contains(entity.uuid)) {
                    playerCenter.add(entity.b.getPosition());
                    playerCount += 1;
                }

                entity.chunkPos = Math.round(entity.b.getPosition().x / CHUNK_WIDTH);
                Set<Entity> entitiesInChunk = entitiesByChunk.computeIfAbsent(entity.chunkPos, k -> new HashSet<>());
                entitiesInChunk.add(entity);


                if (!isFrozen) {
                    entity.update(Constants.TIME_STEP * Constants.UPDATES_LATENCY);
                }
                for (Joint joint : jointsToRemove) {
                    world.destroyJoint(joint);
                }
                jointsToRemove.clear();

                if (entity instanceof PlayerEntity player) {
                    ServerWorld.PlayerData playerData = playersByUsername.get(player.username);
                    if (playerData != null) {
                        playerData.chunk = entity.chunkPos;
                    }
                }

                if (entity.isRemoved()) {
                    processRemoval(entity);
                }
            }
            playerCenter.scl(1f / playerCount);
        }

        for (int i = cameraChunkPos - SIMULATION_DISTANCE; i <= cameraChunkPos + SIMULATION_DISTANCE; i++) {
            loadChunk(i);
        }
        for (int i : new HashSet<>(loadedChunks.keySet())) {
            if (Math.abs(i - cameraChunkPos) - 3 > SIMULATION_DISTANCE) {
                unloadChunk(i);
            }
        }
    }

    public boolean cameraFollowsPlayers = true;
    public Vector2 cameraOffset = new Vector2();

    private boolean prevCameraState = false;
    public void update(float dt) {
        doPhysicsStep(dt * Constants.SIMULATION_SPEED);

        for (Entity entity : entitiesToSpawn) {
            entity.onSpawn(world);
        }
        entitiesToSpawn.clear();

        for (JointEntity joint : jointsToSpawn) {
            joint.onSpawn(world);
        }
        jointsToSpawn.clear();

        if (!cameraFollowsPlayers || localPlayers.isEmpty()) return;
        Vector2 target = playerCenter.cpy().add(0, 10).add(cameraOffset);

        if (!Float.isNaN(target.x) && !Float.isNaN(target.y)) {
            if (cameraFollowsPlayers != prevCameraState) {
                camera.position.set(target, 0f);
            } else {
                Vector2 move = target.sub(camera.position.x, camera.position.y).scl(dt * 3.2f);
                camera.position.add(move.x, move.y, 0);
            }

            prevCameraState = cameraFollowsPlayers;
        }

        float maxOffset = 0;
        for (UUID playerUuid : localPlayers) {
            Entity player = Entity.getByUuid(playerUuid);
            if (player == null) continue;
            maxOffset = Math.max(maxOffset, player.b.getPosition().dst(playerCenter));
        }

        float zoom = Math.max(1 / GameScreen.zoom + 1f + (float) Math.pow(maxOffset, 0.6) / 2, 9f);
        if (!Float.isNaN(zoom)) {
            camera.zoom = zoom;
        }
    }

    public ItemEntity dropItem(Vector2 location, Vector2 impulse, Item item) {
        ItemEntity itemEntity = new ItemEntity(this, location.x, location.y, item);
        itemEntity.onSpawnAction(() -> {
            itemEntity.b.applyLinearImpulse(impulse, itemEntity.b.getPosition(), true);
        });
        this.spawn(itemEntity);
        return itemEntity;
    }

    public void dispose() {
        for (int x : new HashSet<>(loadedChunks.keySet())) {
            unloadChunk(x);
        }
        world.dispose();
    }
}
