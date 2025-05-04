package io.github._20nickname20.imbored;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.ItemEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.handlers.EntityContactFilter;
import io.github._20nickname20.imbored.handlers.EntityContactListener;
import io.github._20nickname20.imbored.screens.GameScreen;
import io.github._20nickname20.imbored.util.Constants;

import java.util.HashSet;
import java.util.Set;

public class GameWorld {
    public final World world;

    public final OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());;

    private final Array<Body> bodies = new Array<>();
    private final Array<Body> bodies1 = new Array<>();

    private final Set<Entity> entitiesToSpawn = new HashSet<>();
    private final Set<Joint> jointsToRemove = new HashSet<>();

    public GameWorld() {
        world = new World(new Vector2(0, -Constants.ACCELERATION_OF_GRAVITY), true);
        world.setContactListener(new EntityContactListener());
        world.setContactFilter(new EntityContactFilter());
    }


    public void spawn(Entity entity) {
        entitiesToSpawn.add(entity);
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
        float frameTime = Math.min(dt, 0.25f);
        accumulator += frameTime;
        while (accumulator >= Constants.TIME_STEP) {
            world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
            accumulator -= Constants.TIME_STEP;
            step++;
            if (step < Constants.UPDATES_LATENCY) continue;
            step = 0;
            world.getBodies(bodies);
            playerCenter.setZero();
            float playerCount = 0;
            for (Body body : bodies) {
                if (!(body.getUserData() instanceof Entity entity)) continue;

                if (body.getPosition().y > 100) {
                    body.applyLinearImpulse(new Vector2(0, -100), body.getPosition(), true);
                }

                if (entity instanceof PlayerEntity player) {
                    playerCenter.add(entity.b.getPosition());
                    registeredPlayers.add(player);
                    playerCount += 1;
                }

                if (entity.isRemoved()) {
                    processRemoval(entity);
                    continue;
                }
                entity.update(Constants.TIME_STEP * Constants.UPDATES_LATENCY);
                for (Joint joint : jointsToRemove) {
                    world.destroyJoint(joint);
                }
                jointsToRemove.clear();
            }
            playerCenter.scl(1f / playerCount);
        }
    }

    public boolean cameraFollowsPlayers = true;
    public Vector2 cameraOffset = new Vector2();

    private Set<PlayerEntity> registeredPlayers = new HashSet<>();

    public void update(float dt) {
        registeredPlayers.clear();
        doPhysicsStep(dt * Constants.SIMULATION_SPEED);

        for (Entity entity : entitiesToSpawn) {
            entity.onSpawn(world);
        }
        entitiesToSpawn.clear();

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
        camera.zoom = Math.max(1 / GameScreen.zoom + 1f + (float) Math.pow(maxOffset, 0.6) / 2, 5.5f);
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
