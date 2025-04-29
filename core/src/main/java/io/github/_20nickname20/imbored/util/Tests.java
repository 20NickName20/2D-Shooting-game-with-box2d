package io.github._20nickname20.imbored.util;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import io.github._20nickname20.imbored.Entity;
import io.github._20nickname20.imbored.Material;
import io.github._20nickname20.imbored.controllers.PlayerKeyboardController;
import io.github._20nickname20.imbored.entities.BlockEntity;
import io.github._20nickname20.imbored.entities.block.BoxEntity;
import io.github._20nickname20.imbored.entities.damagable.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.screens.GameScreen;

public class Tests {
    GameScreen gameScreen;
    World world;
    public Tests(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.world = gameScreen.getWorld();
    }

    public void boxEnv() {
        gameScreen.setGround(
            Util.createStaticBox(world, new Vector2(0, -55), 102, 2)
        );
        Util.createStaticBox(world, new Vector2(-100, 0), 2, 57);
        Util.createStaticBox(world, new Vector2(100, 0), 2, 57);
    }

    public void plane() {
        gameScreen.setGround(
            Util.createStaticBox(world, new Vector2(0, -55), 10_000f, 1)
        );
    }

    public void towersTest() {
        for (int x = 0; x < 6; x++) {
            int height = MathUtils.random(5, 15);
            for (int y = 0; y < height; y++) {
                boolean type = MathUtils.randomBoolean();
                GameScreen.spawnEntity(new BoxEntity(world, -85 + x * 35 + MathUtils.random(0, 0.25f), -48 + y * 4, 2, 2, type ? Material.WOOD : Material.METAL, type ? 150 : 300));
            }
        }
        GameScreen.spawnEntity(new PlayerEntity(world, 0, -40, new PlayerKeyboardController(new PlayerKeyboardController.KeyboardMapping())));
        for (Controller controller : Controllers.getControllers()) {
            gameScreen.addXInputControllerPlayer(controller);
        }
    }

    void distanceJointTest() {
        Entity box1 = new BlockEntity(world, -50, 0, Shapes.boxShape(4, 4), Material.WOOD, 150);
        Entity box2 = new BlockEntity(world, -30, 0, Shapes.circleShape(4), Material.WOOD, 150);
        DistanceJointDef defJoint = new DistanceJointDef ();
        //defJoint.length = 10;
        defJoint.frequencyHz = 5;
        defJoint.dampingRatio = 0.5f;
        defJoint.initialize(box1.b, box2.b, box1.b.getPosition(), box2.b.getPosition());
        DistanceJoint joint = (DistanceJoint) world.createJoint(defJoint);
        // TODO: make some joints hidden (make custom joint class)
        //  joint.setUserData();
    }
}
