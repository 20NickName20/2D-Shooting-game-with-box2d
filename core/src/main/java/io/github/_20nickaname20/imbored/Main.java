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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github._20nickaname20.imbored.controllers.PlayerGamepadController;
import io.github._20nickaname20.imbored.controllers.PlayerKeyboardController;
import io.github._20nickaname20.imbored.entities.BlockEntity;
import io.github._20nickaname20.imbored.entities.block.BoxEntity;
import io.github._20nickaname20.imbored.entities.damagable.living.human.PlayerEntity;
import io.github._20nickaname20.imbored.items.usable.guns.raycast.TestGunItem;
import io.github._20nickaname20.imbored.items.usable.guns.raycast.automatic.AutomaticRifleItem;

import java.util.Random;

import static io.github._20nickaname20.imbored.Util.fixBleeding;

public class Main extends ApplicationAdapter {
    public static OrthographicCamera camera;
    public static Viewport viewport;
    public static long startTime;
    public final static int spriteSize = 256;

    public World world;
    public static GameRenderer renderer;
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

        PlayerEntity player = new PlayerEntity(world, 5 * controllerPlayers, -30,
            new PlayerGamepadController(controller)
        );
        player.inventory.add(new TestGunItem());
        player.inventory.add(new AutomaticRifleItem());
        controller.setPlayerIndex(controllerPlayers % 4);
        controllerPlayers++;
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

        Tests tests = new Tests(this);
        tests.boxEnv();
        tests.towersTest();
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
        for (Body body : bodies) {
            Object userdata = body.getUserData();
            if (!(userdata instanceof Entity entity)) continue;
            entity.update(dt);
        }
        renderer.render(world, camera.combined, (shape) -> {
            if (debugController != null) {
                shape.setColor(0.75f, 0.75f, 0.75f, 1);
                shape.circle(-10 - 50, 25, 5);
                shape.circle(debugController.getAxis(0) * 5 - 10 - 50, -debugController.getAxis(1) * 5 + 25, 1.5f);
                shape.line(-16 - 50, 25, -4 - 50, 25);
                shape.line(-10 - 50, -6 + 25, -10 - 50, 6 + 25);
                shape.circle(10 - 50, 25, 5);
                shape.circle(debugController.getAxis(2) * 5 + 10 - 50, -debugController.getAxis(3) * 5 + 25, 1.5f);
                shape.line(4 - 50, 25, 16 - 50, 25);
                shape.line(10 - 50, -6 + 25, 10 - 50, 6 + 25);
            }
        });

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
        world.dispose();
    }
}
