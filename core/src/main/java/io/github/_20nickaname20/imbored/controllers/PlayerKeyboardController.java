package io.github._20nickaname20.imbored.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import io.github._20nickaname20.imbored.PlayerController;

import static io.github._20nickaname20.imbored.Main.inputMultiplexer;

public class PlayerKeyboardController extends PlayerController implements InputProcessor {
    final int leftKey, rightKey, jumpKey, grabKey, rotateLeftKey, rotateRightKey, sprintKey;

    public PlayerKeyboardController(int leftKey, int rightKey, int jumpKey, int grabKey, int rotateLeftKey, int rotateRightKey, int sprintKey) {
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.jumpKey = jumpKey;
        this.grabKey = grabKey;
        this.rotateLeftKey = rotateLeftKey;
        this.rotateRightKey = rotateRightKey;
        this.sprintKey = sprintKey;
    }

    @Override
    public void register() {
        inputMultiplexer.addProcessor(this);
    }

    @Override
    public void unregister() {
        inputMultiplexer.removeProcessor(this);
    }

    @Override
    public boolean keyDown(int i) {
        if (i == jumpKey) {
            player.jump();
            return false;
        }
        if (i == leftKey) {
            player.setXMovement(-1);
            return false;
        }
        if (i == rightKey) {
            player.setXMovement(1);
            return false;
        }
        if (i == grabKey) {
            player.grab();
            return false;
        }
        if (i == rotateLeftKey) {
            player.cursorRotationVel = 120f;
            return false;
        }
        if (i == rotateRightKey) {
            player.cursorRotationVel = -120f;
            return false;
        }
        if (i == Input.Keys.F) {
            player.throwGrabbed();
            return false;
        }
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        if (i == leftKey || i == rightKey) {
            player.clearXMovement();
            return false;
        }
        if (i == grabKey) {
            player.put();
            return false;
        }
        if (i == rotateLeftKey || i == rotateRightKey) {
            player.cursorRotationVel = 0;
            return false;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
