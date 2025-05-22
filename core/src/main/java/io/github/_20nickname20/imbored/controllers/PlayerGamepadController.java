package io.github._20nickname20.imbored.controllers;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.Controllable;
import io.github._20nickname20.imbored.PlayerController;
import io.github._20nickname20.imbored.game_objects.Interact;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public class PlayerGamepadController extends PlayerController implements ControllerListener {
    private final Controller controller;
    private final ControllerMapping mapping;
    private final int ZL_AXIS = 4;
    private final int ZR_AXIS = 5;

    private int sticks = 2;

    public PlayerGamepadController(Controller controller) {
        this.controller = controller;
        this.mapping = controller.getMapping();
    }

    @Override
    public void register(Controllable controllable) {
        super.register(controllable);
        controller.addListener(this);
    }

    @Override
    public void unregister() {
        controller.removeListener(this);
    }

    Vector2 targetCursorDirection = new Vector2();

    @Override
    public void update(float dt) {

    }

    public void processInteract(int buttonCode, boolean isPressed) {
        if (buttonCode == mapping.buttonDpadLeft) {
            controllable.interact(Interact.LEFT, isPressed);
            return;
        }
        if (buttonCode == mapping.buttonDpadRight) {
            controllable.interact(Interact.RIGHT, isPressed);
            return;
        }
        if (buttonCode == mapping.buttonDpadUp) {
            controllable.interact(Interact.UP, isPressed);
            return;
        }
        if (buttonCode == mapping.buttonDpadDown) {
            controllable.interact(Interact.DOWN, isPressed);
            return;
        }
        if (buttonCode == mapping.buttonA) {
            controllable.interact(Interact.USE, isPressed);
        }
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (buttonCode == mapping.buttonLeftStick) {
            sticks = 1;
            return false;
        }
        if (buttonCode == mapping.buttonRightStick) {
            sticks = 2;
            return false;
        }

        if (buttonCode == mapping.buttonB) {
            controllable.jump();
            return false;
        }
        if (buttonCode == mapping.buttonL1) {
            controllable.scrollInventory(-1);
            return false;
        }
        if (buttonCode == mapping.buttonR1) {
            controllable.scrollInventory(1);
            return false;
        }
        if (buttonCode == mapping.buttonX) {
            controllable.dropOrThrow();
            return false;
        }
        if (buttonCode == mapping.buttonY) {
            controllable.startApplying();
            return false;
        }

        processInteract(buttonCode, true);
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if (buttonCode == mapping.buttonY) {
            controllable.stopApplying();
            return false;
        }

        processInteract(buttonCode, false);
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (axisCode == mapping.axisLeftX) {
            controllable.move(value);
        }
        Vector2 newDirection = targetCursorDirection.cpy();
        boolean moveX = axisCode == mapping.axisRightX || (sticks == 1 && axisCode == mapping.axisLeftX);
        if (moveX) {
            newDirection.x = value;
        }
        boolean moveY = axisCode == mapping.axisRightY || (sticks == 1 && axisCode == mapping.axisLeftY);
        if (moveY) {
            newDirection.y = -value;
        }
        if (newDirection.len() > 0.4) {
            if (moveX || moveY) {
                targetCursorDirection.set(newDirection);
                targetCursorDirection.nor();
                controllable.setCursorAngle(targetCursorDirection.angleRad());
            }
        }
        if (axisCode == ZL_AXIS) {
            if (value == 1) {
                controllable.grabOrUse();
            } else {
                controllable.putOrStopUse();
            }
            return false;
        }
        if (axisCode == ZR_AXIS) {
            if (value == 1) {
                controllable.toggleItem();
            }
        }
        return false;
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }
}
