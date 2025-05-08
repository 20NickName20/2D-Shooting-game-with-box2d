package io.github._20nickname20.imbored.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github._20nickname20.imbored.Main;
import io.github._20nickname20.imbored.render.PenRenderer;

public class MainMenuScreen extends ScreenAdapter {
    private final PenRenderer batch;
    private final BitmapFont font;
    private final FitViewport viewport;
    OrthographicCamera camera;

    private final Main game;

    private boolean setGame = false;

    public MainMenuScreen(Main game) {
        this.game = game;

        batch = new PenRenderer();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters += "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        parameter.size = 32;
        font = generator.generateFont(parameter);
        generator.dispose();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(16, 9, camera);
        viewport.apply();
        camera.update();

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        ControllerListener listener = new ControllerAdapter() {
            @Override
            public boolean buttonUp(Controller controller, int buttonIndex) {
                setGame = true;
                return false;
            }
        };
        Controllers.addListener(listener);
    }

    @Override
    public void render(float dt) {
        ScreenUtils.clear(new Color(0.99f, 0.99f, 1f, 1));

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.setColor(Color.BLACK);
        font.draw(batch, "Добро пожаловать в Коробки резня летс гоо!", 1, 1.5f);
        font.draw(batch, "Tap anywhere to begin!", 1, 1);
        batch.setColor(Color.BLUE);
        batch.end();

        if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE) || setGame) {
            setScreenGame();
        }
    }

    public void setScreenGame() {
        Controllers.clearListeners();
        game.setScreen(new GameScreen(game));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
