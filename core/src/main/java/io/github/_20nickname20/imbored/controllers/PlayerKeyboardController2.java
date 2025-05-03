package io.github._20nickname20.imbored.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.PlayerController;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.screens.GameScreen;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

public class PlayerKeyboardController2 extends PlayerController implements InputProcessor {
    PlayerKeyboardController.KeyboardMapping mapping;

    public PlayerKeyboardController2(PlayerKeyboardController.KeyboardMapping mapping) {
        this.mapping = mapping;
    }

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
            player.cursorDirection.add(0, 0.1f);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            player.cursorDirection.add(0, -0.1f);
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            player.cursorDirection.add(-0.1f, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            player.cursorDirection.add(0.1f, 0);
        }
        player.cursorDirection.nor();

        if (Gdx.input.isKeyPressed(Keys.C)) {
            player.customColor += dt / 4;
        }
    }

    @Override
    public boolean keyDown(int i) {
        if (i == mapping.jumpKey) {
            player.jump();
            return false;
        }
        if (i == mapping.leftKey) {
            player.setXMovement(-1);
            return false;
        }
        if (i == mapping.rightKey) {
            player.setXMovement(1);
            return false;
        }
        if (i == mapping.scrollItemLeftKey) {
            player.scrollItem(-1);
        }
        if (i == mapping.scrollItemRightKey) {
            player.scrollItem(1);
        }
        if (i == mapping.modeSwitchKey) {
            this.switchMode();
        }
        if (i == mapping.dropItemKey) {
            player.dropEquippedItem();
        }
        if (i == mapping.throwKey) {
            player.throwGrabbed();
        }
        if (i == mapping.containerScrollLeftKey) {
            player.scrollContainer(-1);
        }
        if (i == mapping.containerScrollRightKey) {
            player.scrollContainer(1);
        }
        if (i == mapping.containerTakeOutKey) {
            player.takeOutOfContainer();
        }
        if (i == mapping.containerPutKey) {
            player.putEquippedToContainer();
        }
        if (i == mapping.modeUseKey) {
            this.startUseMode();
        }

        return false;
    }

    @Override
    public boolean keyUp(int i) {
        if (i == mapping.rightKey) {
            if (Gdx.input.isKeyPressed(mapping.leftKey)) {
                player.setXMovement(-1);
            } else {
                player.clearXMovement();
            }
            return false;
        }
        if (i == mapping.leftKey) {
            if (Gdx.input.isKeyPressed(mapping.rightKey)) {
                player.setXMovement(1);
            } else {
                player.clearXMovement();
            }
            return false;
        }
        if (i == mapping.modeUseKey) {
            this.startUseMode();
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
        player.cursorDirection.set(point.sub(player.b.getPosition()));
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector2 point = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        player.cursorDirection.set(point.sub(player.b.getPosition()));
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        player.scrollItem((int) v1);

        return false;
    }
}
