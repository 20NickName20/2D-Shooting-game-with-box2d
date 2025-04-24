package io.github._20nickaname20.imbored;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github._20nickaname20.imbored.controllers.PlayerJoyconController;
import io.github._20nickaname20.imbored.controllers.PlayerKeyboardController;
import io.github._20nickaname20.imbored.entities.BlockEntity;
import io.github._20nickaname20.imbored.entities.living.human.PlayerEntity;

import java.util.Random;

import static io.github._20nickaname20.imbored.Util.fixBleeding;

public class Main extends ApplicationAdapter {
    public static OrthographicCamera camera;
    public static Viewport viewport;
    public static long startTime;
    public final static int spriteSize = 256;

    public World world;
    GameRenderer renderer;
    ShapeRenderer shapeRenderer;
    public SpriteBatch batch;

    MouseJoint mouseJoint = null;
    float zoom = 18;

    float dt;

    Body ground;

    Random random = new Random();

    public static InputMultiplexer inputMultiplexer = new InputMultiplexer();

    int controllerPlayers = 0;

    Controller debugController;

    void addXInputControllerPlayer(Controller controller) {
        if (!controller.getName().contains("XInput")) return;
        debugController = controller;
        System.out.println("Controller connected: " + controller.getName() + " ID: " + controller.getUniqueId());

        new PlayerEntity(world, 5 * controllerPlayers, -30,
            new PlayerJoyconController(controller)
        );
        controller.setPlayerIndex(controllerPlayers % 4);
        controllerPlayers++;
    }

    void boxEnv() {
        ground = Util.createStaticBox(world, new Vector2(0, -55), 102, 2);
        Util.createStaticBox(world, new Vector2(-100, 0), 2, 57);
        Util.createStaticBox(world, new Vector2(100, 0), 2, 57);
    }

    void entityTest() {
        for (int i = 4; i > 0; i--) {
            new BlockEntity(world, -90, -20 - 5 * i, Util.boxShape(i, i), Material.WOOD);
            new BlockEntity(world, -75, -20 - 5 * i, Util.boxShape(i, i), Material.ROCK);
            new BlockEntity(world, -60, -20 - 5 * i, Util.boxShape(i, i), Material.METAL);
        }
        new BlockEntity(world, 70, -40, Util.circleShape(1.5f), Material.ROCK);
        new PlayerEntity(world, 0, -40,
            new PlayerKeyboardController(Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.Q, Input.Keys.Z, Input.Keys.X, Input.Keys.SHIFT_LEFT)
        );
        for (Controller controller : Controllers.getControllers()) {
            addXInputControllerPlayer(controller);
        }
        // new PlayerEntity(world, 1, 2, new PlayerController());
        // new PlayerEntity(world, 2, 2, new PlayerController());
    }

    @Override
    public void create() {
        startTime = TimeUtils.millis();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(spriteSize * 16, spriteSize * 9, camera);
        viewport.apply();
        camera.update();

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        world = new World(new Vector2(0, -Constants.ACCELERATION_OF_GRAVITY), true);
        world.setContactListener(new EntityContactListener());
        renderer = new GameRenderer();
        shapeRenderer = new ShapeRenderer();

        camera.zoom /= zoom;
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexer.addProcessor(new CursorInputProcessor(this));

        Controllers.addListener(new ControllerAdapter() {
            @Override
            public void connected(Controller controller) {
                System.out.println("Controller connected: " + controller.getName() + " ID: " + controller.getUniqueId());
                addXInputControllerPlayer(controller);
            }

            @Override
            public boolean buttonDown(Controller controller, int buttonIndex) {
                System.out.println("Controller button down: " + controller.getName() + " ID: " + controller.getUniqueId() + " Button: " + buttonIndex);
                debugController = controller;
                return false;
            }

            @Override
            public boolean axisMoved(Controller controller, int axisIndex, float value) {
                //System.out.println("Controller axis moved: " + controller.getName() + " ID: " + controller.getUniqueId() + " Axis: " + axisIndex + " value: " + value);
                return false;
            }
        });

        boxEnv();
        entityTest();
    }

    private float accumulator = 0;

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= Constants.TIME_STEP) {
            world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
            accumulator -= Constants.TIME_STEP;
        }
    }

    @Override
    public void render() {
        dt = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0, 0, 0, 1);
        doPhysicsStep(dt * Constants.SIMULATION_SPEED);
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1,1,1,1);
        for (Body body : bodies) {
            Object userdata = body.getUserData();
            if (!(userdata instanceof Entity entity)) continue;
            entity.update(dt);
            if (entity instanceof PlayerEntity playerEntity) {
                Vector2 pos = playerEntity.getCursorPosition();
                shapeRenderer.circle(pos.x, pos.y,1.2f);
            }
        }
        // System.out.println(debugController.getAxis(0) + "; " + debugController.getAxis(0));
        if (debugController != null) {
            shapeRenderer.circle(-10, 0, 5);
            shapeRenderer.circle(debugController.getAxis(0) * 5 * 2 - 10, -debugController.getAxis(1) * 5 * 2, 1.5f);
            shapeRenderer.line(-16, 0, -4, 0);
            shapeRenderer.line(-10, -6, -10, 6);
            shapeRenderer.circle(10, 0, 5);
            shapeRenderer.circle(debugController.getAxis(2) * 5 * 2 + 10, -debugController.getAxis(3) * 5 * 2, 1.5f);
            shapeRenderer.line(4, 0, 16, 0);
            shapeRenderer.line(10, -6, 10, 6);
        }
        shapeRenderer.end();
        renderer.render(world, camera.combined);

        //batch.begin();
        //batch.draw(sprites[0][0], Gdx.input.getX(), Gdx.input.getY());
        //batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        renderer.dispose();
        shapeRenderer.dispose();
        world.dispose();
    }
}
