package io.github._20nickname20.imbored;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import io.github._20nickname20.imbored.game_objects.Chunk;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.JointEntity;
import io.github._20nickname20.imbored.game_objects.entities.ItemEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.handlers.EntityContactFilter;
import io.github._20nickname20.imbored.handlers.EntityContactListener;
import io.github._20nickname20.imbored.screens.GameScreen;
import io.github._20nickname20.imbored.util.Constants;
import io.github._20nickname20.imbored.util.Ray;
import io.github._20nickname20.imbored.util.Util;

import java.util.*;

public class GameWorld {
    public final World world;

    private static final String SAVE_PATH = "korobo4ki/save/";

    private final Json json = new Json();

    private final Map<Integer, Chunk> loadedChunks = new HashMap<>();
    private final Map<Integer, Set<Entity>> entitiesByChunk = new HashMap<>();

    public static final float CHUNK_WIDTH = 300f;
    public static final int SIMULATION_DISTANCE = 3;

    private final List<Ray> rays = new ArrayList<>();
    private boolean isFrozen;

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

    public void addRay(Ray ray) {
        rays.add(ray);
    }

    public void renderRays(ShapeRenderer renderer) {
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

    public void loadChunk(int x) {
        if (loadedChunks.containsKey(x)) return;
        Gdx.app.log("Chunk Loading", "Loading chunk at " + x);

        loadedChunks.put(x, null);

        FileHandle handle = Gdx.files.external(SAVE_PATH + "chunk" + x + ".json");
        if (handle.exists()) {
            Gdx.app.log("Chunk Loading", "Found save file");

            Chunk.ChunkData data = json.fromJson(Chunk.ChunkData.class, handle.readString());
            Chunk chunk = new Chunk(this, x, data);
            Chunk left = getLoadedChunk(x - 1);
            if (left != null) {
                left.right = chunk;
                chunk.left = left;
            }
            Chunk right = getLoadedChunk(x + 1);
            if (right != null) {
                right.left = chunk;
                chunk.right = right;
            }
            loadedChunks.put(x, chunk);
            entitiesByChunk.put(x, new HashSet<>());
            return;
        }

        Gdx.app.log("Chunk Loading", "No save file found, generating...");

        Chunk chunk = new Chunk(this, x);
        Chunk left = getLoadedChunk(x - 1);
        if (left != null) {
            left.right = chunk;
            chunk.left = left;
        }
        Chunk right = getLoadedChunk(x + 1);
        if (right != null) {
            right.left = chunk;
            chunk.right = right;
        }
        chunk.generate(left, right);
        loadedChunks.put(x, chunk);
        entitiesByChunk.put(x, new HashSet<>());
    }

    public void unloadChunk(int x) {
        if (!isChunkLoaded(x)) return;

        Chunk chunk = getLoadedChunk(x);
        Chunk.ChunkData data = chunk.createPersistentData();

        for (JointEntity joint : getJointsInChunk(x)) {
            joint.remove();
        }

        for (Entity entity : getEntitiesInChunk(x)) {
            entity.remove();
        }

        if (data.entityData.length != 0) {
            json.toJson(data, Gdx.files.external(SAVE_PATH + "chunk" + x + ".json"));
        }

        entitiesByChunk.remove(x);
        loadedChunks.remove(x);
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

    private final Set<PlayerEntity> players = new HashSet<>();

    public Body anyStaticBody;

    public void spawn(Entity entity) {
        if (entity == null) {
            throw new RuntimeException("ENTITY IS FUCKING NULL");
        }
        if (entity.isRemoved()) return;
        if (entity instanceof PlayerEntity player) {
            players.add(player);
        }
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
        int chunkPos = getChunkPosition(camera.position.x);

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

                if (entity instanceof PlayerEntity player) {
                    playerCenter.add(entity.b.getPosition());
                    registeredPlayers.add(player);
                    playerCount += 1;
                }

                entity.chunkPos = Math.round(entity.b.getPosition().x / CHUNK_WIDTH);
                Set<Entity> entitiesInChunk = entitiesByChunk.computeIfAbsent(entity.chunkPos, k -> new HashSet<>());
                entitiesInChunk.add(entity);


                entity.update(Constants.TIME_STEP * Constants.UPDATES_LATENCY);
                for (Joint joint : jointsToRemove) {
                    world.destroyJoint(joint);
                }
                jointsToRemove.clear();

                if (entity.isRemoved()) {
                    processRemoval(entity);
                }
            }
            playerCenter.scl(1f / playerCount);
        }

        for (int i = chunkPos - SIMULATION_DISTANCE; i <= chunkPos + SIMULATION_DISTANCE; i++) {
            if (!isChunkLoaded(i)) {
                loadChunk(i);
            }
        }
        for (int i : new HashSet<>(loadedChunks.keySet())) {
            if (Math.abs(i - chunkPos) > SIMULATION_DISTANCE) {
                unloadChunk(i);
            }
        }
    }

    public boolean cameraFollowsPlayers = true;
    public Vector2 cameraOffset = new Vector2();

    private final Set<PlayerEntity> registeredPlayers = new HashSet<>();

    public void update(float dt) {
        registeredPlayers.clear();
        doPhysicsStep(dt * Constants.SIMULATION_SPEED);

        for (Entity entity : entitiesToSpawn) {
            entity.onSpawn(world);
        }
        entitiesToSpawn.clear();

        for (JointEntity joint : jointsToSpawn) {
            joint.onSpawn(world);
        }
        jointsToSpawn.clear();

        if (!cameraFollowsPlayers) return;
        Vector2 target = playerCenter.cpy().add(0, 10).add(cameraOffset);
        if (!Float.isNaN(target.x) && !Float.isNaN(target.y)) {
            Vector2 move = target.sub(camera.position.x, camera.position.y).scl(dt * 1.5f);
            camera.position.add(move.x, move.y, 0);
        }
        float maxOffset = 0;
        for (PlayerEntity player : registeredPlayers) {
            maxOffset = Math.max(maxOffset, player.b.getPosition().dst(playerCenter));
        }
        if (maxOffset < 0.1f) return;
        camera.zoom = Math.max(1 / GameScreen.zoom + 1f + (float) Math.pow(maxOffset, 0.6) / 2, 9f);
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
