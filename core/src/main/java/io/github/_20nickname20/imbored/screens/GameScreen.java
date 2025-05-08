package io.github._20nickname20.imbored.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
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
import io.github._20nickname20.imbored.render.GameLineRenderer;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

public class GameScreen extends ScreenAdapter {
    public final Main game;
    private static Viewport viewport;
    public static final float zoom = 0.135f;

    private final GameLineRenderer renderer;
    ShaderProgram shader;

    public GameWorld world;

    private Body ground;

    Controller debugController;

    // TODO: Add worldgen and world saving
    // TODO: Add SpriteBatch rendering

    public GameScreen(Main game) {
        this.game = game;

        world = new GameWorld();

        viewport = new FitViewport(16, 9, world.camera);
        viewport.apply();
        world.camera.update();

        shader = new ShaderProgram(
            Gdx.files.internal("shader/shader.vert").readString(),
            Gdx.files.internal("shader/shader.frag").readString()
        );
        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
        ShaderProgram.pedantic = false;
        renderer = new GameLineRenderer(shader);
        world.camera.zoom /= zoom;

        inputMultiplexer.addProcessor(new MainInputProcessor(this));

        for (Controller controller : Controllers.getControllers()) {
             addControllerPlayer(controller, 0);
        }
    }

    public void addControllerPlayer(Controller controller, float x) {
        if (controller.getName().toLowerCase().contains("joy")) return;
        debugController = controller;
        System.out.println("Controller connected: " + controller.getName() + " ID: " + controller.getUniqueId());

        world.spawn(
            new PlayerEntity(world, x, 100,
                new PlayerGamepadController(controller)
            )
        );
    }

    @Override
    public void render(float dt) {
        world.update(dt);

        shader.bind();
        shader.setUniformf("u_Time", Util.time());
        shader.setUniformf("u_cameraPos", world.camera.position);

        ScreenUtils.clear(0, 0, 0, 1);

        viewport.apply();
        renderer.render(world.world, world.camera.combined, (shape) -> {
            With.translation(shape, world.camera.position.x, world.camera.position.y, () -> {
                shape.setColor(Color.BLACK);
                shape.set(ShapeRenderer.ShapeType.Filled);
                shape.rect(-200, -200, 400, 400);
                shape.set(ShapeRenderer.ShapeType.Line);
            });
            world.renderRays(shape);

            With.translation(shape,world.camera.position.x,world.camera.position.y, () -> AdminTool.renderPart(shape, 12));

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

    public static Viewport getViewport() {
        return viewport;
    }

    public OrthographicCamera getCamera() {
        return world.camera;
    }
}
