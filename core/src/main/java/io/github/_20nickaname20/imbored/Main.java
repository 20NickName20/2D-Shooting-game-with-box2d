package io.github._20nickaname20.imbored;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github._20nickaname20.imbored.controllers.PlayerGamepadController;
import io.github._20nickaname20.imbored.entities.damagable.living.human.PlayerEntity;
import io.github._20nickaname20.imbored.render.GameRenderer;

import java.util.Random;

import static io.github._20nickaname20.imbored.Util.fixBleeding;

public class Main extends ApplicationAdapter {
    public static OrthographicCamera camera;
    public static Viewport viewport;
    public static long startTime;

    public World world;
    public static GameRenderer renderer;
    public SpriteBatch batch;
    ShaderProgram shader;

    MouseJoint mouseJoint = null;
    float zoom = 0.075f;

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

        controller.setPlayerIndex(controllerPlayers % 4);
        controllerPlayers++;
    }

    @Override
    public void create() {
        startTime = TimeUtils.millis();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(16, 9, camera);
        viewport.apply();
        camera.update();

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        world = new World(new Vector2(0, -Constants.ACCELERATION_OF_GRAVITY), true);
        world.setContactListener(new EntityContactListener());
        shader = new ShaderProgram(Gdx.files.internal("shader/shader.vert").readString(), Gdx.files.internal("shader/shader.frag").readString());
        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
        ShaderProgram.pedantic = false;
        renderer = new GameRenderer(shader);

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
        shader.bind();
        shader.setUniformf("u_Time", Util.time());

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
                shape.translate(-70, 40, 0);
                shape.setColor(0.75f, 0.75f, 0.75f, 1);
                shape.circle(-10, 0, 5);
                shape.circle(debugController.getAxis(0) * 5 - 10, -debugController.getAxis(1) * 5, 1.5f);
                shape.line(-16, 0, -4, 0);
                shape.line(-10, -6, -10, 6);
                shape.circle(10, 0, 5);
                shape.circle(debugController.getAxis(2) * 5 + 10, -debugController.getAxis(3) * 5, 1.5f);
                shape.line(4, 0, 16, 0);
                shape.line(10, -6, 10, 6);
                shape.translate(70, -40, 0);
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
        shader.dispose();
    }
}
