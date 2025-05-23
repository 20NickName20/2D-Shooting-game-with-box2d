package io.github._20nickname20.imbored;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.TimeUtils;
import io.github._20nickname20.imbored.screens.MainMenuScreen;

public class Main extends Game {
    public static long startTime;
    public static final String SAVE_ROOT = "korobo4ki/";

    public static InputMultiplexer inputMultiplexer = new InputMultiplexer() {

    };

    @Override
    public void create() {
        startTime = TimeUtils.millis();
        Gdx.input.setInputProcessor(inputMultiplexer);

        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        this.getScreen().dispose();
    }
}
