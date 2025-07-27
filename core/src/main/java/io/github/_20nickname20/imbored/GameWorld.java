package io.github._20nickname20.imbored;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

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
import io.github._20nickname20.imbored.util.Ray;
import io.github._20nickname20.imbored.util.Raycast;
import io.github._20nickname20.imbored.util.Util;

public abstract class GameWorld {
    public final World world;

    public static final float TIME_STEP = 1f / 100;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;
    public static final float SIMULATION_SPEED = 1.65f;

    public static final float ACCELERATION_OF_GRAVITY = 15f;

    public static final float CHUNK_WIDTH = 300f;
    public static final int SIMULATION_DISTANCE = 3;

    protected final List<Ray> rays = new ArrayList<>();
    private boolean isFrozen;

    public final HashMap<String, PlayerEntity> players = new HashMap<>();

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public boolean isFrozen() {
        return isFrozen;
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
        world = new World(new Vector2(0, -ACCELERATION_OF_GRAVITY), true);
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

    protected void tick() {
        if (!isFrozen) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
        accumulator -= TIME_STEP;
        world.getBodies(bodies);
        playerCenter.setZero();
        float playerCount = 0;
        for (Body body : bodies) {
            if (!(body.getUserData() instanceof Entity entity)) continue;

            if (body.getType() == BodyDef.BodyType.StaticBody) {
                anyStaticBody = body;
            }

            if (body.getPosition().y > 300) {
                body.applyLinearImpulse(new Vector2(0, -100), body.getPosition(), true);
            }

            if (entity instanceof PlayerEntity) {
                playerCenter.add(entity.b.getPosition());
                playerCount += 1;
            }

            entity.chunkPos = Math.round(entity.b.getPosition().x / CHUNK_WIDTH);


            if (!isFrozen) {
                entity.update(TIME_STEP);
            }
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

    private float accumulator = 0;

    private final void processTicks(float dt) {
        float frameTime = Math.min(dt, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            tick();
        }
    }

    public boolean cameraFollowsPlayers = true;
    public Vector2 cameraOffset = new Vector2();

    private boolean prevCameraState = false;
    public void update(float dt) {
        processTicks(dt * SIMULATION_SPEED);

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
            if (cameraFollowsPlayers != prevCameraState) {
                camera.position.set(target, 0f);
            } else {
                Vector2 move = target.sub(camera.position.x, camera.position.y).scl(dt * 3.2f);
                camera.position.add(move.x, move.y, 0);
            }

            prevCameraState = cameraFollowsPlayers;
        }

        float maxOffset = 0;

        for (PlayerEntity player : players.values()) {
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
        world.dispose();
    }
}
