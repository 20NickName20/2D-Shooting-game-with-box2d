package io.github._20nickname20.imbored.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github._20nickname20.imbored.*;
import io.github._20nickname20.imbored.handlers.MainInputProcessor;
import io.github._20nickname20.imbored.render.GameLineRenderer;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Util;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

public class GameScreen extends ScreenAdapter {
    public final Main game;
    private static Viewport viewport;
    public static final float zoom = 0.135f;

    private final GameRenderer renderer;
    ShaderProgram shader;

    public GameWorld world;

    private Body ground;

    public GameScreen(Main game, GameWorld world) {
        this.game = game;
        this.world = world;

        viewport = new FitViewport(16, 9, world.camera);
        world.camera.update();

        shader = new ShaderProgram(
            Gdx.files.internal("shader/shader.vert").readString(),
            Gdx.files.internal("shader/shader.frag").readString()
        );
        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
        ShaderProgram.pedantic = false;
        renderer = new GameLineRenderer(shader, world);
        world.camera.zoom /= zoom;

        inputMultiplexer.addProcessor(new MainInputProcessor(this));
    }

    @Override
    public void render(float dt) {
        world.update(dt);

        shader.bind();
        shader.setUniformf("u_Time", Util.time());
        shader.setUniformf("u_cameraPos", world.camera.position);

        ScreenUtils.clear(0, 0, 0, 1);

        viewport.apply();
        renderer.render(() -> {
            AdminTool.render(renderer, dt);
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
