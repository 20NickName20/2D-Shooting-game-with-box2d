package io.github._20nickname20.imbored.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github._20nickname20.imbored.*;
import io.github._20nickname20.imbored.controllers.PlayerGamepadController;
import io.github._20nickname20.imbored.entities.damagable.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.handlers.MainInputProcessor;
import io.github._20nickname20.imbored.handlers.EntityContactFilter;
import io.github._20nickname20.imbored.handlers.EntityContactListener;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Constants;
import io.github._20nickname20.imbored.util.Tests;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

import java.util.HashSet;
import java.util.Set;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

public class GameScreen extends ScreenAdapter {
    public final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    float zoom = 0.075f;

    private final GameRenderer renderer;
    ShaderProgram shader;

    private World world;

    private Body ground;

    int controllerPlayers = 0;

    Controller debugController;

    private static final Set<Entity> entitiesToSpawn = new HashSet<>();
    private static final Set<Joint> jointsToRemove = new HashSet<>();

    public static void spawnEntity(Entity entity) {
        entitiesToSpawn.add(entity);
    }

    public static void removeJoint(Joint joint) {
        jointsToRemove.add(joint);
    }

    public GameScreen(Main game) {
        this.game = game;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(16, 9, camera);
        viewport.apply();
        camera.update();

        world = new World(new Vector2(0, -Constants.ACCELERATION_OF_GRAVITY), true);
        world.setContactListener(new EntityContactListener());
        world.setContactFilter(new EntityContactFilter());
        shader = new ShaderProgram(
            Gdx.files.internal("shader/shader.vert").readString(),
            Gdx.files.internal("shader/shader.frag").readString()
        );
        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
        ShaderProgram.pedantic = false;
        renderer = new GameRenderer(shader);

        camera.zoom /= zoom;

        inputMultiplexer.addProcessor(new MainInputProcessor(this));
//
//        Controllers.addListener(new ControllerAdapter() {
//            @Override
//            public void connected(Controller controller) {
//                System.out.println("Controller connected: " + controller.getName() + " ID: " + controller.getUniqueId());
//                addXInputControllerPlayer(controller);
//            }
//
//            @Override
//            public boolean buttonDown(Controller controller, int buttonIndex) {
//                System.out.println("Controller button down: " + controller.getName() + " ID: " + controller.getUniqueId() + " Button: " + buttonIndex);
//                debugController = controller;
//                return false;
//            }
//
//            @Override
//            public boolean axisMoved(Controller controller, int axisIndex, float value) {
//                //System.out.println("Controller axis moved: " + controller.getName() + " ID: " + controller.getUniqueId() + " Axis: " + axisIndex + " value: " + value);
//                return false;
//            }
//        });

        init();
    }

    private void init() {
        Tests tests = new Tests(this);
        tests.plane();
        tests.towersTest();
    }

    public void addXInputControllerPlayer(Controller controller) {
        if (!controller.getName().contains("XInput")) return;
        debugController = controller;
        System.out.println("Controller connected: " + controller.getName() + " ID: " + controller.getUniqueId());

        GameScreen.spawnEntity(
            new PlayerEntity(world, 5 * controllerPlayers, -30,
                new PlayerGamepadController(controller)
            )
        );

        controller.setPlayerIndex(controllerPlayers % 4);
        controllerPlayers++;
    }

    private float accumulator = 0;
    private int step = 0;

    private void doPhysicsStep(float dt) {
        float frameTime = Math.min(dt, 0.25f);
        accumulator += frameTime;
        while (accumulator >= Constants.TIME_STEP) {
            world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
            accumulator -= Constants.TIME_STEP;
            step++;
            if (step < Constants.UPDATES_LATENCY) continue;
            step = 0;
            Array<Body> bodies = new Array<>();
            world.getBodies(bodies);
            for (Body body : bodies) {
                if (!(body.getUserData() instanceof Entity entity)) continue;
                if (entity.isRemoved()) {
                    entity.b.setUserData(null);
                    for (JointEdge jointEdge : entity.b.getJointList()) {
                        world.destroyJoint(jointEdge.joint);
                    }
                    world.destroyBody(entity.b);
                    Array<Body> bodies1 = new Array<>();
                    world.getBodies(bodies1);
                    for (Body body1 : bodies1) {
                        if (!(body1.getUserData() instanceof Entity entity1)) continue;
                        if (entity1.isRemoved()) continue;
                        entity1.contacts.remove(body);
                    }
                    continue;
                }
                entity.update(Constants.TIME_STEP * Constants.UPDATES_LATENCY);
                for (Joint joint : jointsToRemove) {
                    world.destroyJoint(joint);
                }
                jointsToRemove.clear();
            }
        }
    }

    private void update(float dt) {
        doPhysicsStep(dt * Constants.SIMULATION_SPEED);

        for (Entity entity : entitiesToSpawn) {
            entity.spawn(world);
        }
        entitiesToSpawn.clear();
    }

    @Override
    public void render(float dt) {
        update(dt);

        // TODO: add camera pos as uniform
        shader.bind();
        shader.setUniformf("u_Time", Util.time());

        ScreenUtils.clear(0, 0, 0, 1);

        renderer.render(world, camera.combined, (shape) -> {
            shape.setColor(Color.BLACK);
            shape.set(ShapeRenderer.ShapeType.Filled);
            With.translation(shape, camera.position.x, camera.position.y, () -> {
                shape.rect(-200, -200, 400, 400);
            });
            shape.set(ShapeRenderer.ShapeType.Line);
            if (debugController != null && Gdx.input.isKeyPressed(Input.Keys.F3)) {
                With.translation(shape, -70, 40, () -> {
                    shape.setColor(0.75f, 0.75f, 0.75f, 1);
                    shape.circle(-10, 0, 5);
                    shape.circle(debugController.getAxis(0) * 5 - 10, -debugController.getAxis(1) * 5, 1.5f);
                    shape.line(-16, 0, -4, 0);
                    shape.line(-10, -6, -10, 6);
                    shape.circle(10, 0, 5);
                    shape.circle(debugController.getAxis(2) * 5 + 10, -debugController.getAxis(3) * 5, 1.5f);
                    shape.line(4, 0, 16, 0);
                    shape.line(10, -6, 10, 6);
                });
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        world.dispose();
        shader.dispose();
        inputMultiplexer.clear();
    }

    public Body getGround() {
        return ground;
    }

    public void setGround(Body ground) {
        this.ground = ground;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public World getWorld() {
        return world;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
