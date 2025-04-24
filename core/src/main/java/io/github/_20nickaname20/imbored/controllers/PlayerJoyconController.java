package io.github._20nickaname20.imbored.controllers;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerManager;
import com.badlogic.gdx.controllers.ControllerMapping;
import io.github._20nickaname20.imbored.PlayerController;

public class PlayerJoyconController extends PlayerController implements ControllerListener {
    private final Controller controller;
    private final ControllerMapping mapping;

    public PlayerJoyconController(Controller controller) {
        this.controller = controller;
        this.mapping = controller.getMapping();
    }

    @Override
    public void register() {
        controller.addListener(this);
    }

    @Override
    public void unregister() {
        controller.removeListener(this);
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (buttonCode == mapping.buttonB) {
            player.jump();
            return false;
        }
        if (buttonCode == mapping.buttonR1) {
            player.grab();
            return false;
        }
        if (buttonCode == mapping.buttonL1) {
            player.throwGrabbed();
            return false;
        }
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if (buttonCode == mapping.buttonR1) {
            player.put();
        }
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (axisCode == mapping.axisLeftX) {
            player.setXMovement(value * 2);
            return false;
        }
        if (axisCode == mapping.axisRightX) {
            player.cursorDirection.x = value;
            return false;
        }
        if (axisCode == mapping.axisRightY) {
            player.cursorDirection.y = -value;
            return false;
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
