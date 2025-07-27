package io.github._20nickname20.imbored.screens;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

import java.util.List;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github._20nickname20.imbored.ControlsProfile;
import io.github._20nickname20.imbored.Main;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.world.ServerWorld;

public class MapCreationScreen extends MenuScreen {
    private String inputText;
    private int newLineCount = 0;

    public MapCreationScreen(Main game, String initialInput, List<ControlsProfile> profiles) {
        super(game);
        this.inputText = initialInput;

        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (character == '\n') {
                    newLineCount++;
                    if (newLineCount <= 1) {
                        return true;
                    }
                    inputText = inputText.trim();
                    inputText = inputText.replaceAll("[^a-zA-Z0-9_\\-]", "_"); // sanitize input

                    if (inputText.isEmpty()) {
                        inputText = "new_map";
                    }
                    if (inputText.length() > 20) {
                        inputText = inputText.substring(0, 20);
                    }

                    MenuScreen.clearControllers();
                    game.setScreen(new GameScreen(
                        game, new ServerWorld(profiles, inputText)
                    ));
                    return true;
                } else if (character == '\b') {
                    if (!inputText.isEmpty()) {
                        inputText = inputText.substring(0, inputText.length() - 1);
                    }
                } else {
                    inputText += character;
                }
                return true;
            }
        });
    }

    @Override
    public void render(float dt) {
        super.render(dt);
        
        ScreenUtils.clear(new Color(0.02f, 0.05f, 0.1f, 1));

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.setColor(0.9f, 0.9f, 0.7f, 1f);
        font.draw(batch, "Введите название карты:", 1f, 8f);

        font.setColor(0.4f, 1f, 0.3f, 1f);
        font.draw(batch, inputText + (Math.round(Util.time()) % 2 == 0 ? '_' : ' '), 1.5f, 7f);

        batch.end();
    }
}
