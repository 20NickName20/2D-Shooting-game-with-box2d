package io.github._20nickname20.imbored.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.AdminTool;
import io.github._20nickname20.imbored.Controllable;
import io.github._20nickname20.imbored.PlayerController;
import io.github._20nickname20.imbored.game_objects.Interact;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.screens.GameScreen;
import io.github._20nickname20.imbored.util.Util;

import static io.github._20nickname20.imbored.Main.inputMultiplexer;

public class PlayerKeyboardAndMouseController extends PlayerController implements InputProcessor {

    @Override
    public void register(Controllable controllable) {
        super.register(controllable);
        inputMultiplexer.addProcessor(this);
    }

    @Override
    public void unregister() {
        inputMultiplexer.removeProcessor(this);
    }

    private final Vector2 cursorTarget = new Vector2(Vector2.Y);
    private float prevCursorAngle = 0;

    @Override
    public void update(float dt) {
        if (AdminTool.isEnabled) return;
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            cursorTarget.add(0, dt * 7.5f);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            cursorTarget.add(0, -dt * 7.5f);
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            cursorTarget.add(-dt * 7.5f, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            cursorTarget.add(dt * 7.5f, 0);
        }
        cursorTarget.nor();

        float cursorAngle = cursorTarget.angleRad();
        if (cursorAngle != prevCursorAngle) {
            controllable.setCursorAngle(cursorAngle);
            prevCursorAngle = cursorAngle;
        }
    }

    public void processInteract(int code, boolean isPressed) {
        switch (code) {
            case Keys.I -> controllable.interact(Interact.UP, isPressed);
            case Keys.K -> controllable.interact(Interact.DOWN, isPressed);
            case Keys.J -> controllable.interact(Interact.LEFT, isPressed);
            case Keys.L -> controllable.interact(Interact.RIGHT, isPressed);
            case Keys.O -> controllable.interact(Interact.USE, isPressed);
        }
    }

    @Override
    public boolean keyDown(int i) {
        if (AdminTool.isEnabled) return false;
        switch (i) {
            case Keys.SPACE -> controllable.jump();
            case Keys.A -> controllable.move(-1f);
            case Keys.D -> controllable.move(1f);
            case Keys.LEFT_BRACKET -> controllable.scrollInventory(-1);
            case Keys.RIGHT_BRACKET -> controllable.scrollInventory(1);
            case Keys.E -> controllable.toggleItem();
            case Keys.Z -> controllable.dropOrThrow();
            case Keys.Q -> controllable.grabOrUse();
            case Keys.R -> controllable.startApplying();
            default -> processInteract(i, true);
        }

        return false;
    }

    @Override
    public boolean keyUp(int i) {
        if (AdminTool.isEnabled) return false;
        switch (i) {
            case Keys.D -> {
                if (Gdx.input.isKeyPressed(Keys.A)) {
                    controllable.move(-1f);
                } else {
                    controllable.move(0f);
                }
                return false;
            }
            case Keys.A -> {
                if (Gdx.input.isKeyPressed(Keys.D)) {
                    controllable.move(1f);
                } else {
                    controllable.move(0f);
                }
            }
            case Keys.Q -> controllable.putOrStopUse();
            case Keys.R -> controllable.stopApplying();
            default -> processInteract(i, false);
        }

        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (AdminTool.isEnabled) return false;
        if (button == Input.Buttons.LEFT) {
            controllable.grabOrUse();
        }
        if (button == Input.Buttons.RIGHT) {
            controllable.toggleItem();
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (AdminTool.isEnabled) return false;
        if (button == Input.Buttons.LEFT) {
            controllable.putOrStopUse();
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int i2) {
        if (AdminTool.isEnabled) return false;
        Vector2 point = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        cursorTarget.set(point.sub(controllable.getBody().getPosition()));
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (AdminTool.isEnabled) return false;
        Vector2 point = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        cursorTarget.set(point.sub(controllable.getBody().getPosition()));
        return false;
    }

    @Override
    public boolean scrolled(float x, float y) {
        if (AdminTool.isEnabled) return false;
        controllable.scrollInventory((int) y);
        return false;
    }
}
