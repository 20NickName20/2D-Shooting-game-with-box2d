package io.github._20nickaname20.imbored.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickaname20.imbored.PlayerController;
import io.github._20nickaname20.imbored.entities.damagable.living.human.PlayerEntity;

public class PlayerGamepadController extends PlayerController implements ControllerListener {
    private final Controller controller;
    private final ControllerMapping mapping;
    private final int ZL_AXIS = 4;
    private final int ZR_AXIS = 5;

    public PlayerGamepadController(Controller controller) {
        this.controller = controller;
        this.mapping = controller.getMapping();
    }

    @Override
    public void register(PlayerEntity player) {
        super.register(player);
        controller.addListener(this);
    }

    @Override
    public void unregister() {
        controller.removeListener(this);
    }

    Vector2 targetCursorDirection = new Vector2();

    @Override
    public void update(float dt) {
        player.cursorDirection.add(targetCursorDirection.cpy().scl(dt * 70));
        player.cursorDirection.nor();
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (buttonCode == mapping.buttonB) {
            player.jump();
            return false;
        }
        if (buttonCode == mapping.buttonL1) {
            if (player.getMode() == PlayerEntity.Mode.INV) {
                player.scrollItem(-1);
                return false;
            }
        }
        if (buttonCode == mapping.buttonR1) {
            if (player.getMode() == PlayerEntity.Mode.GRAB) {
                player.throwGrabbed();
                return false;
            }
            if (player.getMode() == PlayerEntity.Mode.INV) {
                player.scrollItem(1);
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (axisCode == mapping.axisLeftX) {
            player.setXMovement(value);
            return false;
        }
        Vector2 newDirection = targetCursorDirection.cpy();
        if (axisCode == mapping.axisRightX) {
            newDirection.x = value;
        }
        if (axisCode == mapping.axisRightY) {
            newDirection.y = -value;
        }
        if (newDirection.len() > 0.4) {
            if (axisCode == mapping.axisRightX) {
                targetCursorDirection.x = value;
                targetCursorDirection.nor();
                return false;
            }
            if (axisCode == mapping.axisRightY) {
                targetCursorDirection.y = -value;
                targetCursorDirection.nor();
                return false;
            }
        }
        if (axisCode == ZL_AXIS) {
            if (value == 1) {
                if (player.getMode() == PlayerEntity.Mode.GRAB) {
                    player.grab();
                }
                if (player.getMode() == PlayerEntity.Mode.INV) {
                    player.startUsingItem();
                }
            } else {
                if (player.getMode() == PlayerEntity.Mode.GRAB) {
                    player.put();
                }
                if (player.getMode() == PlayerEntity.Mode.INV) {
                    player.stopUsingItem();
                }
            }
            return false;
        }
        if (axisCode == ZR_AXIS) {
            if (value == 1) {
                player.nextMode();
            }
        }
        return false;
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {
        player.remove();
    }
}
