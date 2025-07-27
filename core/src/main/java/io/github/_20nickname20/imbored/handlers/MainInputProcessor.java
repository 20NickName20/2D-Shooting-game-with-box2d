package io.github._20nickname20.imbored.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

import io.github._20nickname20.imbored.AdminTool;
import io.github._20nickname20.imbored.screens.GameScreen;
import io.github._20nickname20.imbored.screens.MainMenuScreen;
import io.github._20nickname20.imbored.world.ServerWorld;

public class MainInputProcessor extends InputAdapter {
    GameScreen gameScreen;
    Camera camera;

    public MainInputProcessor(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.camera = GameScreen.getViewport().getCamera();

        AdminTool.world = gameScreen.world;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!AdminTool.isEnabled) return false;
        Vector2 worldPoint = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        Vector2 screenPoint = worldPoint.cpy().sub(camera.position.x, camera.position.y);
        AdminTool.onClickPressed(worldPoint, screenPoint, button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!AdminTool.isEnabled) return false;
        Vector2 worldPoint = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        Vector2 screenPoint = worldPoint.cpy().sub(camera.position.x, camera.position.y);
        AdminTool.onClickReleased(worldPoint, screenPoint, button);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!AdminTool.isEnabled) return false;
        Vector2 worldPoint = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        Vector2 screenPoint = worldPoint.cpy().sub(camera.position.x, camera.position.y);
        AdminTool.onMouseMove(worldPoint, screenPoint);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (!AdminTool.isEnabled) return false;
        Vector2 worldPoint = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        Vector2 screenPoint = worldPoint.cpy().sub(camera.position.x, camera.position.y);
        AdminTool.onMouseMove(worldPoint, screenPoint);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.TAB || (AdminTool.isEnabled && keycode == Input.Keys.ESCAPE)){
            AdminTool.isEnabled = !AdminTool.isEnabled;
            return false;
        }
        if (keycode == Input.Keys.ESCAPE) {
            gameScreen.game.setScreen(new MainMenuScreen(gameScreen.game));
            return false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && keycode == Input.Keys.S) {
            if (gameScreen.world instanceof ServerWorld serverWorld) {
                serverWorld.getMap().save();
                Gdx.app.log("Map Save", "Map \"" + serverWorld.getMap().name + "\" saved successfully.");
            } else {
                Gdx.app.error("Map Save", "Cannot save map, not in server world.");
            }
            return false;
        }
        if (!AdminTool.isEnabled) return false;
        AdminTool.keyPressed(keycode);
        return false;
    }
}
