package io.github._20nickname20.imbored.util;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.LootGenerator;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.controllers.PlayerKeyboardController;
import io.github._20nickname20.imbored.game_objects.entities.BlockEntity;
import io.github._20nickname20.imbored.game_objects.entities.StaticEntity;
import io.github._20nickname20.imbored.game_objects.entities.block.BoxEntity;
import io.github._20nickname20.imbored.game_objects.entities.container.CrateEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.loot.TestRandomLoot;
import io.github._20nickname20.imbored.game_objects.loot.supply.GunSupplyLoot;
import io.github._20nickname20.imbored.game_objects.loot.supply.HealSupplyLoot;
import io.github._20nickname20.imbored.game_objects.loot.supply.StuffSupplyLoot;
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
            gameScreen.addControllerPlayer(controller, 0);
        }
    }

    public void crates() {
        LootGenerator gunLoot = new GunSupplyLoot();
        LootGenerator healLoot = new HealSupplyLoot();
        LootGenerator stuffLoot = new StuffSupplyLoot();

        for (float x = -15; x < 16; x++) {
            for (int i = -2; i < 3; i++) {
                if (MathUtils.random(2) == 0) continue;
                int height = MathUtils.random(1, (int) (10f - (float) Math.sin(x / 10) * 7f));
                for (int y = 0; y < height; y++) {
                    boolean type = MathUtils.randomBoolean();
                    world.spawn(new BoxEntity(world, x * 60 + MathUtils.random(0, 0.25f) + i * 6f, -48 + y * 4, 2, 2, type ? Material.WOOD : Material.METAL, type ? 150 : 300));
                }
            }

            CrateEntity entity = new CrateEntity(world, x * 60 + 30, -40, 3.5f, 3.5f, 200);
            LootGenerator lootGenerator = switch (MathUtils.random(2)) {
                case 0 -> gunLoot;
                case 1 -> healLoot;
                default -> stuffLoot;
            };
            entity.getInventory().addAll(lootGenerator.generate(1));
            world.spawn(entity);
        }

        for (Controller controller : Controllers.getControllers()) {
            gameScreen.addControllerPlayer(controller, 15);
        }
    }
}
