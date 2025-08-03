package io.github._20nickname20.imbored.screens;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github._20nickname20.imbored.ControlsProfile;
import io.github._20nickname20.imbored.Main;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.world.ClientWorld;

public class MainMenuScreen extends MenuScreen {
    protected final Json json = new Json();

    List<ControlsProfile> profiles = new ArrayList<>();

    ClientWorld clientWorld = null;

    void refreshProfiles() {
        List<String> profileNames = new ArrayList<>();
        FileHandle profilesFile = Gdx.files.external(Main.SAVE_ROOT + "profiles.json");
        if (profilesFile.exists()) {
            String[] names = json.fromJson(String[].class, profilesFile.readString());
            profileNames.addAll(Arrays.asList(names));
        }

        profiles.add(new ControlsProfile.Keyboard(Util.listGetOrDefault(profileNames, 0, "Игрок0")));
        int i = 0;
        for (Controller controller : Controllers.getControllers()) {
            System.out.println(controller.getName() + " " + controller.getAxisCount());
            i++;
            profiles.add(new ControlsProfile.Gamepad(Util.listGetOrDefault(profileNames, i, "Игрок" + i), controller));
        }
    }

    public MainMenuScreen(Main game) {
        super(game);

        refreshProfiles();
        
        this.columns.add(List.of(
            new Button("Играть", new Color(0.4f, 1f, 0.3f, 1f)) {
                @Override
                public void onUse() {
                    MenuScreen.clearControllers();
                    game.setScreen(new MapSelectionScreen(game, profiles));
                }
            },
            new Button("meow", Color.LIGHT_GRAY) {
                @Override
                public void onUse() {
                    this.text += " :3";
                }
            }
        ));
    }

    @Override
    public void render(float dt) {
        super.render(dt);
        
        ScreenUtils.clear(new Color(0.02f, 0.05f, 0.1f, 1));

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.setColor(0.9f, 0.9f, 0.7f, 1f);
        font.draw(batch, "Добро пожаловать в SlaughterBox!", 1, 8f);
        font.setColor(0.7f, 0.65f, 0.4f, 1);
        font.draw(batch, "(Коробочки оффлайн)", 5, 7.6f);

        for (int i = columns.get(0).size() - 1; i >= 0; i--) {
            font.setColor(columns.get(0).get(i).getColor());
            String text = columns.get(0).get(i).text;
            if (selectedButton == i) {
                text = "-> " + text + " <-";
            }
            font.draw(batch, text, 0.5f, 5f - i * 0.8f);
        }

        for (int i = profiles.size() - 1; i >= 0; i--) {
            font.setColor(0.4f, 0.6f, 0.4f, 1f);
            font.draw(batch, profiles.get(i).getText(), 9.5f, 5f - i * 0.8f);
        }
        batch.end();
    }
}
