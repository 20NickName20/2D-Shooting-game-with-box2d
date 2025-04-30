package io.github._20nickname20.imbored.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github._20nickname20.imbored.*;
import io.github._20nickname20.imbored.controllers.PlayerGamepadController;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.handlers.MainInputProcessor;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Tests;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

public class GameScreen extends ScreenAdapter {
    public final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    public static final float zoom = 0.075f;

    private final GameRenderer renderer;
    ShaderProgram shader;

    public GameWorld world;

    private Body ground;

    Controller debugController;

    // TODO: Add worldgen and world saving
    // TODO: Add SpriteBatch rendering

    public GameScreen(Main game) {
        this.game = game;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(16, 9, camera);
        viewport.apply();
        camera.update();

        world = new GameWorld();
        shader = new ShaderProgram(
            Gdx.files.internal("shader/shader.vert").readString(),
            Gdx.files.internal("shader/shader.frag").readString()
        );
        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
        ShaderProgram.pedantic = false;
        renderer = new GameRenderer(shader);
        camera.zoom /= zoom;

        inputMultiplexer.addProcessor(new MainInputProcessor(this));

        init();
    }

    private void init() {
        Tests tests = new Tests(this);
        tests.plane();
        tests.crates();
    }

    public void addXInputControllerPlayer(Controller controller, float x) {
        if (!controller.getName().contains("XInput")) return;
        debugController = controller;
        System.out.println("Controller connected: " + controller.getName() + " ID: " + controller.getUniqueId());

        world.spawn(
            new PlayerEntity(world, x, -30,
                new PlayerGamepadController(controller)
            )
        );
    }

    @Override
    public void render(float dt) {
        world.update(dt);

        shader.bind();
        shader.setUniformf("u_Time", Util.time());
        shader.setUniformf("u_cameraPos", camera.position);

        ScreenUtils.clear(0, 0, 0, 1);

        renderer.render(world.world, camera.combined, (pen) -> {
            if (debugController != null && Gdx.input.isKeyPressed(Input.Keys.F3)) {
                With.translation(pen, -70, 40, () -> {
                    pen.setColor(0.75f, 0.75f, 0.75f, 1);
                    //pen.circle(-10, 0, 5);
                    //pen.circle(debugController.getAxis(0) * 5 - 10, -debugController.getAxis(1) * 5, 1.5f);
                    pen.line(-16, 0, -4, 0);
                    pen.line(-10, -6, -10, 6);
                    //pen.circle(10, 0, 5);
                    //pen.circle(debugController.getAxis(2) * 5 + 10, -debugController.getAxis(3) * 5, 1.5f);
                    pen.line(4, 0, 16, 0);
                    pen.line(10, -6, 10, 6);
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

    public OrthographicCamera getCamera() {
        return camera;
    }
}
