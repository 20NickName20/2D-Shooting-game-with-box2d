package io.github._20nickname20.imbored.util;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.controllers.PlayerKeyboardController;
import io.github._20nickname20.imbored.game_objects.entities.BlockEntity;
import io.github._20nickname20.imbored.game_objects.entities.StaticEntity;
import io.github._20nickname20.imbored.game_objects.entities.block.BoxEntity;
import io.github._20nickname20.imbored.game_objects.entities.container.CrateEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.ShotgunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.TestGunItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic.AutomaticRifleItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.distance.HardDistanceJointItem;
import io.github._20nickname20.imbored.screens.GameScreen;

public class Tests {
    GameScreen gameScreen;
    GameWorld world;
    public Tests(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.world = gameScreen.world;
    }

    public void boxEnv() {

    }

    public void plane() {
        Entity ground = new StaticEntity(world, 0, -55,  Shapes.boxShape(10_000f, 2), Material.GROUND);
        world.spawn(ground);
        ground.onSpawnAction(() -> {
            gameScreen.setGround(ground.b);
        });
    }

    public void towersTest() {
        for (int x = 0; x < 6; x++) {
            int height = MathUtils.random(5, 15);
            for (int y = 0; y < height; y++) {
                boolean type = MathUtils.randomBoolean();
                world.spawn(new BoxEntity(world, -85 + x * 35 + MathUtils.random(0, 0.25f), -48 + y * 4, 2, 2, type ? Material.WOOD : Material.METAL, type ? 150 : 300));
            }
        }
        world.spawn(new PlayerEntity(world, 0, -40, new PlayerKeyboardController(new PlayerKeyboardController.KeyboardMapping())));
        for (Controller controller : Controllers.getControllers()) {
            gameScreen.addXInputControllerPlayer(controller, 0);
        }
    }

    public void crates() {
        for (int x = -3; x < 4; x++) {
            int height = MathUtils.random(1, 6);
            for (int y = 0; y < height; y++) {
                boolean type = MathUtils.randomBoolean();
                world.spawn(new BoxEntity(world, x * 60 + MathUtils.random(0, 0.25f), -48 + y * 4, 2, 2, type ? Material.WOOD : Material.METAL, type ? 150 : 300));
            }

            CrateEntity entity = new CrateEntity(world, x * 60 + 30, -40, 3.5f, 3.5f, 200);
            Class<? extends Item> type = switch (MathUtils.random(3)) {
                case 0 -> TestGunItem.class;
                case 1 -> AutomaticRifleItem.class;
                case 2 -> ShotgunItem.class;
                default -> HardDistanceJointItem.class;
            };
            for (int i = 0; i < 5; i++) {
                entity.getInventory().add(Item.createFromType(type, entity));
            }
            world.spawn(entity);
        }

        world.spawn(new PlayerEntity(world, 15, -40, new PlayerKeyboardController(new PlayerKeyboardController.KeyboardMapping())));
        for (Controller controller : Controllers.getControllers()) {
            gameScreen.addXInputControllerPlayer(controller, 15);
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
        DistanceJoint joint = (DistanceJoint) world.world.createJoint(defJoint);
    }
}
