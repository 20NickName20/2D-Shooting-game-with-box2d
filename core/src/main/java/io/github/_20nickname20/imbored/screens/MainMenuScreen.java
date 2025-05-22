package io.github._20nickname20.imbored.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github._20nickname20.imbored.ControlsProfile;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.Main;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.world.ClientWorld;
import io.github._20nickname20.imbored.world.ServerWorld;

import java.util.*;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

public class MainMenuScreen extends ScreenAdapter {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final FitViewport viewport;
    OrthographicCamera camera;

    private final Main game;

    protected final Json json = new Json();

    private int selectedButton = 0;

    private boolean isConnecting;

    List<ControlsProfile> profiles = new ArrayList<>();
    private int selectedProfile = 0;

    ClientWorld clientWorld = null;

    List<Button> buttons = List.of(
        new Button("Присоедениться", Color.GREEN) {
            @Override
            public void onUse() {
                if (profiles.isEmpty()) {
                    this.text = "Нет игроков! Нажмите DELETE";
                    this.setColor(Color.RED);
                    return;
                }
                for (ControlsProfile profile : profiles) {
                    if (profile.username.length() < 4) {
                        this.text = "Слишком короткий ник!";
                        this.setColor(Color.RED);
                        return;
                    }
                }
                if (isConnecting) {
                    return;
                }
                this.text = "Подключение";
                this.setColor(Color.BLUE);

                new Thread(() -> {
                    isConnecting = true;
                    ClientWorld attemptClientWorld = new ClientWorld(profiles);

                    if (!attemptClientWorld.hostNotFound) {
                        clientWorld = attemptClientWorld;
                    } else {
                        this.text = "Не удалось подключиться";
                        this.setColor(Color.RED);
                        isConnecting = false;
                    }
                }).start();

                String originalText = this.text;
                new Thread(() -> {
                    int i = 0;
                    while (isConnecting) {
                        i++;
                        this.text = originalText + ".".repeat(i % 4);
                        this.addColor(0, 0, -0.06f);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignored) {

                        }
                    }
                }).start();
            }
        },
        new Button("Создать", Color.FOREST) {
            @Override
            public void onUse() {
                for (ControlsProfile profile : profiles) {
                    if (profile.username.length() < 4) {
                        this.text = "Слишком короткий ник!";
                        this.setColor(Color.RED);
                        return;
                    }
                }

                inputMultiplexer.clear();
                Controllers.clearListeners();
                setScreenGame(new ServerWorld(profiles));
            }
        },
        new Button("meow", Color.BLACK) {
            @Override
            public void onUse() {
                this.text += " :3";
            }
        }
    );

    void resetButtons() {
        for (Button button : buttons) {
            button.reset();
        }
    }

    <T> T listGetOrDefault(List<? extends T> list, int index, T defaultValue) {
        try {
            return list.get(index);
        } catch (IndexOutOfBoundsException e) {
            return defaultValue;
        }
    }

    void refreshProfiles() {
        selectedProfile = 0;

        List<String> profileNames = new ArrayList<>();
        FileHandle profilesFile = Gdx.files.external(Main.SAVE_ROOT + "profiles.json");
        if (profilesFile.exists()) {
            String[] names = json.fromJson(String[].class, profilesFile.readString());
            profileNames.addAll(Arrays.asList(names));
        }

        profiles.add(new ControlsProfile.Keyboard(listGetOrDefault(profileNames, 0, "Игрок0")));
        int i = 0;
        for (Controller controller : Controllers.getControllers()) {
            if (controller.getName().contains("Joy-Con")) continue;
            i++;
            profiles.add(new ControlsProfile.Gamepad(listGetOrDefault(profileNames, i, "Игрок" + i), controller));
        }
    }

    InputProcessor menuInputProcessor() {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                buttons.get(selectedButton).onUse();
                return false;
            }

            private final Set<Integer> continueKeys = Set.of(Input.Keys.SPACE, Input.Keys.ENTER);

            @Override
            public boolean keyDown(int keycode) {
                if (isConnecting) return false;

                resetButtons();

                if (continueKeys.contains(keycode)) {
                    buttons.get(selectedButton).onUse();
                    return false;
                }

                if (keycode == Input.Keys.BACKSPACE) {
                    if (profiles.get(selectedProfile).username.isEmpty()) return false;
                    profiles.get(selectedProfile).username = profiles.get(selectedProfile).username.substring(0, profiles.get(selectedProfile).username.length() - 1);
                }

                if (keycode == Input.Keys.FORWARD_DEL) {
                    if (profiles.isEmpty()) {
                        refreshProfiles();
                        return false;
                    }

                    profiles.remove(selectedProfile);
                    if (selectedProfile == profiles.size()) {
                        selectedProfile -= 1;
                    }
                }

                if (keycode == Input.Keys.DOWN) {
                    selectedButton += 1;
                    selectedProfile += 1;
                }
                if (keycode == Input.Keys.UP) {
                    selectedButton -= 1;
                    selectedProfile -= 1;
                }

                if (selectedButton < 0) {
                    selectedButton = buttons.size() - 1;
                }
                if (selectedButton >= buttons.size()) {
                    selectedButton = 0;
                }

                if (selectedProfile < 0) {
                    selectedProfile = profiles.size() - 1;
                }
                if (selectedProfile >= profiles.size()) {
                    selectedProfile = 0;
                }
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                if (isConnecting) return false;
                if (profiles.isEmpty()) return false;
                if (profiles.get(selectedProfile).username.length() >= 16) return false;
                if (Character.isLetterOrDigit(character) || character == '_') {
                    profiles.get(selectedProfile).username += character;
                }
                return false;
            }
        };
    }

    public MainMenuScreen(Main game) {
        this.game = game;
        inputMultiplexer.clear();
        inputMultiplexer.addProcessor(menuInputProcessor());

        batch = new SpriteBatch();

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
                buttons.get(selectedButton).onUse();
                return false;
            }
        };
        Controllers.addListener(listener);

        refreshProfiles();
    }

    private float holdTime = 0f;

    @Override
    public void render(float dt) {
        ScreenUtils.clear(new Color(0.99f, 0.99f, 1f, 1));

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.setColor(Color.BLACK);
        font.draw(batch, "Добро пожаловать в SlaughterBox!", 1, 8f);
        font.setColor(0.7f, 0.65f, 0.8f, 1);
        font.draw(batch, "(Коробочки онлайн)", 5, 7.6f);

        for (int i = buttons.size() - 1; i >= 0; i--) {
            font.setColor(buttons.get(i).getColor());
            String text = buttons.get(i).text;
            if (selectedButton == i) {
                text = "-> " + text + " <-";
            }
            font.draw(batch, text, 0.5f, 5f - i * 0.8f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            holdTime += 10 * dt;
        } else {
            holdTime -= dt * 7.5f;
        }
        holdTime = MathUtils.clamp(holdTime, 0f, 1f);

        for (int i = profiles.size() - 1; i >= 0; i--) {
            if (i == selectedProfile) {
                if (holdTime > 0.1f && profiles.get(i).username.length() < 4) {
                    font.setColor(Color.RED);
                } else {
                    font.setColor(Color.BLACK);
                }
            } else {
                font.setColor(0.4f, 0.4f, 0.4f, 1f);

                if (profiles.get(i).username.length() < 4) {
                    font.setColor(0.8f, 0.3f, 0.3f, 1f);
                }
            }
            font.draw(batch, profiles.get(i).getText(), 9.5f, 5f - i * 0.8f);
        }
        font.setColor(Color.BLACK);
        if (selectedProfile > -1) {
            font.draw(batch, "Ник:", 9.2f, 5.32f - selectedProfile * 0.8f);
        } else {
            font.setColor(0.3f, 0.05f, 0.3f, 1f);
            font.draw(batch, "Нет игроков", 9.5f, 5f);
        }
        batch.end();

        if (clientWorld != null) {
            setScreenGame(clientWorld);
        }
    }

    public void setScreenGame(GameWorld world) {
        game.setScreen(new GameScreen(game, world));
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

        String[] names = new String[profiles.size()];
        int i = 0;
        for (ControlsProfile profile : profiles) {
            names[i] = profile.username;
            i++;
        }
        json.toJson(names, Gdx.files.external(Main.SAVE_ROOT + "profiles.json"));
    }
}
