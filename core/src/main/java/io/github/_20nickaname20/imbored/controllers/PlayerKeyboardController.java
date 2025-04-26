package io.github._20nickaname20.imbored.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import io.github._20nickaname20.imbored.PlayerController;

import static io.github._20nickaname20.imbored.Main.inputMultiplexer;

import com.badlogic.gdx.Input.Keys;
import io.github._20nickaname20.imbored.entities.damagable.living.human.PlayerEntity;

public class PlayerKeyboardController extends PlayerController implements InputProcessor {
    public static class KeyboardMapping {
        public int leftKey = Keys.A;
        public int rightKey = Keys.D;
        public int jumpKey = Keys.SPACE;
        public int grabKey = Keys.E;
        public int itemKey = Keys.Q;
        public int rotateLeftKey = Keys.NUM_1;
        public int rotateRightKey = Keys.NUM_2;
        public int scrollItemLeftKey = Keys.TAB;
        public int scrollItemRightKey = Keys.R;

        public KeyboardMapping setLeftKey(int leftKey) {
            this.leftKey = leftKey;
            return this;
        }

        public KeyboardMapping setRightKey(int rightKey) {
            this.rightKey = rightKey;
            return this;
        }

        public KeyboardMapping setJumpKey(int jumpKey) {
            this.jumpKey = jumpKey;
            return this;
        }

        public KeyboardMapping setGrabKey(int grabKey) {
            this.grabKey = grabKey;
            return this;
        }

        public KeyboardMapping setRotateLeftKey(int rotateLeftKey) {
            this.rotateLeftKey = rotateLeftKey;
            return this;
        }

        public KeyboardMapping setRotateRightKey(int rotateRightKey) {
            this.rotateRightKey = rotateRightKey;
            return this;
        }

        public KeyboardMapping setItemKey(int itemKey) {
            this.itemKey = itemKey;
            return this;
        }

        public KeyboardMapping setScrollItemLeftKey(int scrollItemLeftKey) {
            this.scrollItemLeftKey = scrollItemLeftKey;
            return this;
        }

        public KeyboardMapping setScrollItemRightKey(int scrollItemRightKey) {
            this.scrollItemRightKey = scrollItemRightKey;
            return this;
        }
    }

    KeyboardMapping mapping;

    public PlayerKeyboardController(KeyboardMapping mapping) {
        this.mapping = mapping;
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
        if (i == mapping.rotateLeftKey) {
            player.cursorRotationVel = 120f;
            return false;
        }
        if (i == mapping.rotateRightKey) {
            player.cursorRotationVel = -120f;
            return false;
        }
        if (i == mapping.scrollItemLeftKey) {
            if (player.getMode() == PlayerEntity.Mode.INV) {
                player.scrollItem(-1);
                return false;
            }
        }
        if (i == mapping.scrollItemRightKey) {
            if (player.getMode() == PlayerEntity.Mode.INV) {
                player.scrollItem(1);
                return false;
            }
        }
        // if (i == Keys.F) {
        //     player.throwGrabbed();
        //     return false;
        // }
        if (i == mapping.grabKey) {
            if (player.getMode() == PlayerEntity.Mode.GRAB) {
                player.grab();
            } else {
                player.setMode(PlayerEntity.Mode.GRAB);
            }
        }
        if (i == mapping.itemKey) {
            if (player.getMode() == PlayerEntity.Mode.INV) {
                player.startUsingItem();
            } else {
                player.setMode(PlayerEntity.Mode.INV);
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        if (i == mapping.leftKey || i == mapping.rightKey) {
            player.clearXMovement();
            return false;
        }
        if (i == mapping.rotateLeftKey || i == mapping.rotateRightKey) {
            player.cursorRotationVel = 0;
            return false;
        }

        if (i == mapping.grabKey) {
            if (player.getMode() == PlayerEntity.Mode.GRAB) {
                player.put();
            }
        }
        if (i == mapping.itemKey) {
            if (player.getMode() == PlayerEntity.Mode.INV) {
                player.stopUsingItem();
            }
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
