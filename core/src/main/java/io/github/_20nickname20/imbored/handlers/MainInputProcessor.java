package io.github._20nickname20.imbored.handlers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import io.github._20nickname20.imbored.Entity;
import io.github._20nickname20.imbored.util.FindBody;
import io.github._20nickname20.imbored.screens.GameScreen;
import io.github._20nickname20.imbored.screens.MainMenuScreen;

public class MainInputProcessor extends InputAdapter {
    GameScreen gameScreen;
    private MouseJoint mouseJoint = null;
    private Entity grabbed = null;

    public MainInputProcessor(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) return false;
        if (mouseJoint != null) return false;
        Vector2 point = gameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        Body body = FindBody.atPoint(gameScreen.world.world, point);
        if (body == null) return false;
        if (!(body.getUserData() instanceof Entity entity)) return false;
        grabbed = entity;
        MouseJointDef mouseJointDef = new MouseJointDef();
        mouseJointDef.target.set(point);
        mouseJointDef.bodyA = gameScreen.getGround();
        mouseJointDef.bodyB = body;
        mouseJointDef.maxForce = 15000;
        mouseJointDef.collideConnected = true;
        mouseJoint = (MouseJoint) gameScreen.world.world.createJoint(mouseJointDef);
        mouseJoint.setTarget(point);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) return false;
        if (mouseJoint == null) return false;
        if (!grabbed.isRemoved()) gameScreen.world.world.destroyJoint(mouseJoint);
        mouseJoint = null;
        grabbed = null;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (mouseJoint != null) {
            mouseJoint.setTarget(gameScreen.getViewport().unproject(new Vector2(screenX, screenY)));
            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        gameScreen.getCamera().position.add(amountX, -amountY, 0);
        gameScreen.getCamera().update();
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            gameScreen.game.setScreen(new MainMenuScreen(gameScreen.game));
        }
        return false;
    }
}
