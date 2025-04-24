package io.github._20nickaname20.imbored;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;

public class CursorInputProcessor extends InputAdapter {
    Main main;

    public CursorInputProcessor(Main main) {
        this.main = main;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) return false;
        if (main.mouseJoint != null) return false;
        Vector2 point = Main.viewport.unproject(new Vector2(screenX, screenY));
        Array<Body> bodies = new Array<>();
        main.world.getBodies(bodies);
        for (Body body : bodies) {
            if (body.getType() == BodyDef.BodyType.StaticBody) continue;
            for (Fixture fixture : body.getFixtureList()) {
                if (fixture.testPoint(point)) {
                    MouseJointDef mouseJointDef = new MouseJointDef();
                    mouseJointDef.target.set(point);
                    mouseJointDef.bodyA = main.ground;
                    mouseJointDef.bodyB = body;
                    mouseJointDef.maxForce = 1500;
                    mouseJointDef.collideConnected = true;
                    main.mouseJoint = (MouseJoint) main.world.createJoint(mouseJointDef);
                    main.mouseJoint.setTarget(point);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) return false;
        if (main.mouseJoint == null) return false;
        main.world.destroyJoint(main.mouseJoint);
        main.mouseJoint = null;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (main.mouseJoint != null) {
            main.mouseJoint.setTarget(Main.viewport.unproject(new Vector2(screenX, screenY)));
            return true;
        }
        return false;
    }
}
