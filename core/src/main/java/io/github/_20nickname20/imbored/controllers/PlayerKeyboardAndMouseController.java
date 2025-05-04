package io.github._20nickname20.imbored.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.PlayerController;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.screens.GameScreen;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

public class PlayerKeyboardAndMouseController extends PlayerController implements InputProcessor {

    @Override
    public void register(PlayerEntity player) {
        super.register(player);
        inputMultiplexer.addProcessor(this);
    }

    @Override
    public void unregister() {
        inputMultiplexer.removeProcessor(this);
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            player.addCursorDirection(0, 0.1f);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            player.addCursorDirection(0, -0.1f);
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            player.addCursorDirection(-0.1f, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            player.addCursorDirection(0.1f, 0);
        }

        if (Gdx.input.isKeyPressed(Keys.C)) {
            player.customColor += dt / 4;
        }
    }

    @Override
    public boolean keyDown(int i) {
        switch (i) {
            case Keys.SPACE -> player.jump();
            case Keys.A -> player.setXMovement(-1);
            case Keys.D -> player.setXMovement(1);
            case Keys.NUM_1 -> player.scrollItem(-1);
            case Keys.NUM_2 -> player.scrollItem(1);
            case Keys.E -> this.switchMode();
            case Keys.Z -> this.pushSmth();
            case Keys.LEFT_BRACKET -> player.scrollContainer(-1);
            case Keys.RIGHT_BRACKET -> player.scrollContainer(1);
            case Keys.X -> player.takeOutOfContainer();
            case Keys.Q -> this.startUseMode();
            case Keys.R -> player.startApplyingSelectedToEquipped();
        }

        return false;
    }

    @Override
    public boolean keyUp(int i) {
        switch (i) {
            case Keys.D -> {
                if (Gdx.input.isKeyPressed(Keys.A)) {
                    player.setXMovement(-1);
                } else {
                    player.clearXMovement();
                }
                return false;
            }
            case Keys.A -> {
                if (Gdx.input.isKeyPressed(Keys.D)) {
                    player.setXMovement(1);
                } else {
                    player.clearXMovement();
                }
            }
            case Keys.Q -> this.stopUseMode();
            case Keys.R -> player.stopApplyingSelectedToEquipped();
        }

        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            this.startUseMode();
        }
        if (button == Input.Buttons.RIGHT) {
            this.switchMode();
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            this.stopUseMode();
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int i2) {
        Vector2 point = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        player.setCursorDirection(point.sub(player.b.getPosition()));
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector2 point = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        player.setCursorDirection(point.sub(player.b.getPosition()));
        return false;
    }

    @Override
    public boolean scrolled(float x, float y) {
        if (!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
            player.scrollItem((int) y);
        } else {
            player.scrollContainer((int) y);
        }
        return false;
    }
}
