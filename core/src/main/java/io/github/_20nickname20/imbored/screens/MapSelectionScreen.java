package io.github._20nickname20.imbored.screens;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github._20nickname20.imbored.ControlsProfile;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.Main;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.GameMap;
import io.github._20nickname20.imbored.world.ServerWorld;

public class MapSelectionScreen extends MenuScreen {
    protected final Json json = new Json();

    private List<ControlsProfile> profiles;
    private List<MapButton> maps = new ArrayList<>();

    Button createButton = new Button("Создать карту", new Color(0.4f, 1f, 0.3f, 1f)) {
        @Override
        public void onUse() {
            MenuScreen.clearControllers();
            game.setScreen(new MapCreationScreen(game, searchText, profiles));
        }
    };

    private String searchText = "";

    private void updateButtons() {
        List<Button> column = columns.get(0);
        column.clear();
        column.add(createButton);
        
        for (MapButton mapButton : maps) {
            if (mapButton.text.toLowerCase().contains(searchText.toLowerCase())) {
                column.add(mapButton);
            }
        }
    }

    public MapSelectionScreen(Main game, List<ControlsProfile> profiles) {
        super(game);
        this.profiles = profiles;
        
        FileHandle handle = Gdx.files.external(GameMap.MAPS_PATH);
        if (handle.exists() && handle.isDirectory()) {
            for (FileHandle file : handle.list()) {
                if (file.extension().equals("json")) {
                    String mapName = file.nameWithoutExtension();
                    maps.add(new MapButton(mapName));
                }
            }
        } else {
            Gdx.app.log("MapSelectionScreen", "No maps found in " + GameMap.MAPS_PATH);
        }

        this.columns.add(new ArrayList<>());
        updateButtons();

        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (character == '\n') {
                    return true;
                } else if (character == '\b') {
                    if (!searchText.isEmpty()) {
                        searchText = searchText.substring(0, searchText.length() - 1);
                    }
                } else {
                    searchText += character;
                }
                updateButtons();
                selectedButton = 0;
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

        if (searchText.isEmpty()) {
            font.draw(batch, "Выбор карты:", 1, 8f);
        } else {
            font.draw(batch, "Поиск: " + searchText + (Math.round(Util.time()) % 2 == 0 ? '_' : ' '), 1, 8f);
        }

        for (int i = columns.get(0).size() - 1; i >= 0; i--) {
            font.setColor(columns.get(0).get(i).getColor());
            String text = columns.get(0).get(i).text;
            if (selectedButton == i) {
                text = "-> " + text + " <-";
            }
            font.draw(batch, text, 1f, 6.5f - i * 0.8f);
        }
        batch.end();
    }

    private class MapButton extends Button {
        private final String mapName;
        private final static Color COLOR = new Color(0.7f, 0.8f, 0.9f, 1f);

        public MapButton(String mapName) {
            super(mapName, COLOR);
            this.mapName = mapName;
        }

        @Override
        public void onUse() {
            MenuScreen.clearControllers();

            GameWorld world = new ServerWorld(profiles, mapName);
            game.setScreen(new GameScreen(game, world));
        }
    }
}
