package io.github._20nickname20.imbored;

import com.badlogic.gdx.controllers.Controller;
import io.github._20nickname20.imbored.controllers.PlayerGamepadController;
import io.github._20nickname20.imbored.controllers.PlayerKeyboardAndMouseController;

public abstract class ControlsProfile {
    public String username;

    public ControlsProfile(String name) {
        username = name;
    }

    public abstract String getText();

    public static class Keyboard extends ControlsProfile {
        public Keyboard(String name) {
            super(name);
        }

        @Override
        public String getText() {
            return "[WASD] " + username;
        }
    }

    public static class Gamepad extends ControlsProfile {
        public Controller controller;

        public Gamepad(String name, Controller controller) {
            super(name);
            this.controller = controller;
        }

        @Override
        public String getText() {
            return "(ABXY) " + username;
        }
    }

    public static PlayerController getPlayerController(ControlsProfile profile) {
        PlayerController controller = null;

        if (profile instanceof ControlsProfile.Keyboard) {
            controller = new PlayerKeyboardAndMouseController();
        }
        if (profile instanceof ControlsProfile.Gamepad gamepadProfile) {
            controller = new PlayerGamepadController(gamepadProfile.controller);
        }
        if (controller == null) throw new RuntimeException("Unknown controls profile type!");
        return controller;
    }
}
