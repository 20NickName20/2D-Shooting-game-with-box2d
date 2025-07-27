package io.github._20nickname20.imbored.screens;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github._20nickname20.imbored.Main;
import io.github._20nickname20.imbored.util.Util;

public abstract class MenuScreen extends ScreenAdapter {
    protected final SpriteBatch batch;
    protected final BitmapFont font;
    protected final FitViewport viewport;
    OrthographicCamera camera;

    protected final Main game;

    protected int selectedColumn = 0;
    protected int selectedButton = 0;

    List<List<Button>> columns = new ArrayList<>();

    protected void resetButtons() {
        for (int i = 0; i < columns.size(); i++) {
            for (Button button : columns.get(i)) {
                button.reset();
            }
        }
    }

    protected List<Button> getSelectedColumn() {
        if (selectedColumn < 0 || selectedColumn >= columns.size()) return Collections.emptyList();
        return columns.get(selectedColumn);
    }

    protected void useSelectedButton() {
        if (selectedColumn < 0 || selectedColumn >= columns.size()) return;
        List<Button> column = this.columns.get(selectedColumn);
        column.get(selectedButton).onUse();
    }

    protected void scrollButton(int delta) {
        selectedButton += delta;
        if (selectedButton < 0) {
            selectedButton = getSelectedColumn().size() - 1;
        }
        if (selectedButton >= getSelectedColumn().size()) {
            selectedButton = 0;
        }
    }

    protected void scrollColumn(int delta) {
        selectedColumn += delta;
        if (selectedColumn < 0) {
            selectedColumn = columns.size() - 1;
        }
        if (selectedColumn >= columns.size()) {
            selectedColumn = 0;
        }
        selectedButton = 0;
    }

    protected boolean isFrozen = false;

    private InputProcessor createMenuInputProcessor() {
        return new InputAdapter() {
            private static final Set<Integer> continueKeys = Set.of(Input.Keys.SPACE, Input.Keys.ENTER);

            @Override
            public boolean keyDown(int keycode) {
                if (isFrozen) return false;

                resetButtons();

                if (continueKeys.contains(keycode)) {
                    useSelectedButton();
                    return false;
                }                

                switch (keycode) {
                    case Input.Keys.DOWN:
                        scrollButton(1);
                        break;
                
                    case Input.Keys.UP:
                        scrollButton(-1);
                        break;

                    case Input.Keys.LEFT:
                        scrollColumn(-1);
                        break;

                    case Input.Keys.RIGHT:
                        scrollColumn(1);
                        break;

                    default:
                        break;
                }
                return false;
            }
        };
    }

    public MenuScreen(Main game) {
        this.game = game;
        inputMultiplexer.clear();
        inputMultiplexer.addProcessor(createMenuInputProcessor());

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

        Controllers.addListener(new ControllerAdapter() {
            @Override
            public boolean buttonDown(Controller controller, int buttonIndex) {
                if (buttonIndex == controller.getMapping().buttonDpadDown) {
                    scrollButton(1);
                } else if (buttonIndex == controller.getMapping().buttonDpadUp) {
                    scrollButton(-1);
                } else if (buttonIndex == controller.getMapping().buttonDpadLeft) {
                    scrollColumn(-1);
                } else if (buttonIndex == controller.getMapping().buttonDpadRight) {
                    scrollColumn(1);
                } else if (buttonIndex == controller.getMapping().buttonStart || buttonIndex == controller.getMapping().buttonA || buttonIndex == controller.getMapping().buttonB) {
                    useSelectedButton();
                }
                return false;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());
    }

    float lastAxisScrollTime = 0f;

    @Override
    public void render(float delta) {
        float time = Util.time();
        if (time - lastAxisScrollTime < 0.3f) return;

        Controller current = Controllers.getCurrent();
        if (current != null) {
            if (current.getAxis(current.getMapping().axisLeftY) < -0.5f) {
                scrollButton(1);
                lastAxisScrollTime = time;
            } else if (current.getAxis(current.getMapping().axisLeftY) > 0.5f) {
                scrollButton(-1);
                lastAxisScrollTime = time;
            }
            
            if (current.getAxis(current.getMapping().axisLeftX) < -0.5f) {
                scrollColumn(-1);
                lastAxisScrollTime = time;
            } else if (current.getAxis(current.getMapping().axisLeftX) > 0.5f) {
                scrollColumn(1);
                lastAxisScrollTime = time;
            }
        }
    }

    protected static void clearControllers() {
        inputMultiplexer.clear();
        Controllers.clearListeners();
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
