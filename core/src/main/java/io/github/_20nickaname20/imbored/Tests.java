package io.github._20nickaname20.imbored;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import io.github._20nickaname20.imbored.controllers.PlayerKeyboardController;
import io.github._20nickaname20.imbored.entities.BlockEntity;
import io.github._20nickaname20.imbored.entities.block.BoxEntity;
import io.github._20nickaname20.imbored.entities.damagable.living.human.cursor.PlayerEntity;

public class Tests {
    Main main;
    public Tests(Main main) {
        this.main = main;
    }

    void boxEnv() {
        main.ground = Util.createStaticBox(main.world, new Vector2(0, -55), 102, 2);
        Util.createStaticBox(main.world, new Vector2(-100, 0), 2, 57);
        Util.createStaticBox(main.world, new Vector2(100, 0), 2, 57);
    }

    void entityTest() {
        for (int i = 4; i > 0; i--) {
            new BlockEntity(main.world, -90, -20 - 5 * i, Util.boxShape(i, i), Material.WOOD, 150);
            new BlockEntity(main.world, -75, -20 - 5 * i, Util.boxShape(i, i), Material.ROCK, 500);
            new BlockEntity(main.world, -60, -20 - 5 * i, Util.boxShape(i, i), Material.METAL, 300);
        }
        new BlockEntity(main.world, 70, -40, Util.circleShape(1.5f), Material.ROCK, 100);
        new PlayerEntity(main.world, 0, -40,
            new PlayerKeyboardController(new PlayerKeyboardController.KeyboardMapping())
        );
        for (Controller controller : Controllers.getControllers()) {
            main.addXInputControllerPlayer(controller);
        }
        // new PlayerEntity(world, 1, 2, new PlayerController());
        // new PlayerEntity(world, 2, 2, new PlayerController());
    }

    void towersTest() {
        for (int x = 0; x < 6; x++) {
            int height = MathUtils.random(5, 15);
            for (int y = 0; y < height; y++) {
                boolean type = main.random.nextBoolean();
                new BoxEntity(main.world, -85 + x * 35 + main.random.nextFloat() / 4, -48 + y * 4, 2, 2, type ? Material.WOOD : Material.METAL, type ? 150 : 300);
            }
        }
        new PlayerEntity(main.world, 0, -40,
            new PlayerKeyboardController(new PlayerKeyboardController.KeyboardMapping())
        );
        for (Controller controller : Controllers.getControllers()) {
            main.addXInputControllerPlayer(controller);
        }
    }

    void distanceJointTest() {
        Entity box1 = new BlockEntity(main.world, -50, 0, Util.boxShape(4, 4), Material.WOOD, 150);
        Entity box2 = new BlockEntity(main.world, -30, 0, Util.circleShape(4), Material.WOOD, 150);
        DistanceJointDef defJoint = new DistanceJointDef ();
        //defJoint.length = 10;
        defJoint.frequencyHz = 5;
        defJoint.dampingRatio = 0.5f;
        defJoint.initialize(box1.b, box2.b, box1.b.getPosition(), box2.b.getPosition());
        DistanceJoint joint = (DistanceJoint) main.world.createJoint(defJoint);
        // TODO: make some joints hidden (make custom joint class)
        //  joint.setUserData();
    }
}
