package io.github._20nickname20.imbored.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import io.github._20nickname20.imbored.controllers.PlayerKeyboardController;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.LootGenerator;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.StaticEntity;
import io.github._20nickname20.imbored.game_objects.entities.container.CrateEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.loot.supply.GunSupplyLoot;
import io.github._20nickname20.imbored.game_objects.loot.supply.HealSupplyLoot;
import io.github._20nickname20.imbored.game_objects.loot.supply.StuffSupplyLoot;
import io.github._20nickname20.imbored.util.FindBody;
import io.github._20nickname20.imbored.screens.GameScreen;
import io.github._20nickname20.imbored.screens.MainMenuScreen;
import io.github._20nickname20.imbored.util.Shapes;

public class MainInputProcessor extends InputAdapter {
    GameScreen gameScreen;
    private MouseJoint mouseJoint = null;
    private Entity grabbed = null;

    public MainInputProcessor(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    float startX = 0, startY = 0;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 point = gameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        if (button == Input.Buttons.RIGHT) {
            startX = point.x;
            startY = point.y;
            return false;
        }
        if (button != Input.Buttons.LEFT) return false;
        if (mouseJoint != null) return false;
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
        Vector2 point = gameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        if (button == Input.Buttons.MIDDLE) {
            pressedBefore = false;
            return false;
        }
        if (button == Input.Buttons.RIGHT) {
            float endX = point.x;
            float endY = point.y;
            float centerX = (startX + endX) / 2;
            float centerY = (startY + endY) / 2;
            float sideX = Math.abs(centerX - endX);
            float sideY = Math.abs(centerY - endY);
            if (sideX < 1 || sideY < 1) return false;
            gameScreen.world.spawn(
                new StaticEntity(gameScreen.world, centerX, centerY, Shapes.boxShape(sideX, sideY), Material.GROUND)
            );
        }
        if (button != Input.Buttons.LEFT) return false;
        if (mouseJoint == null) return false;
        if (!grabbed.isRemoved()) gameScreen.world.world.destroyJoint(mouseJoint);
        mouseJoint = null;
        grabbed = null;
        return true;
    }

    Vector2 mousePos = new Vector2();
    boolean pressedBefore = false;

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector2 worldPos = gameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        Vector2 move = new Vector2(screenX, screenY).sub(mousePos);
        mousePos.set(screenX, screenY);

        if (mouseJoint != null) {
            mouseJoint.setTarget(worldPos);
            return true;
        }

        if (pressedBefore && Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            move.scl(1f / 20f);
            gameScreen.getCamera().position.add(-move.x, move.y, 0);
            gameScreen.getCamera().update();
        }
        pressedBefore = Gdx.input.isButtonPressed(Input.Buttons.MIDDLE);

        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        gameScreen.world.cameraOffset.add(amountX, -amountY);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            gameScreen.game.setScreen(new MainMenuScreen(gameScreen.game));
        }
        if (keycode == Input.Keys.ENTER) {
            gameScreen.world.spawn(
                new PlayerEntity(gameScreen.world, gameScreen.getCamera().position.x, -30, new PlayerKeyboardController(new PlayerKeyboardController.KeyboardMapping()))
            );
        }
        if (keycode == Input.Keys.B) {
            LootGenerator gunLoot = new GunSupplyLoot();
            LootGenerator healLoot = new HealSupplyLoot();
            LootGenerator stuffLoot = new StuffSupplyLoot();

            Vector2 worldPos = gameScreen.getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            CrateEntity entity = new CrateEntity(gameScreen.world, worldPos.x, worldPos.y, 3.5f, 3.5f, 200);
            LootGenerator lootGenerator = switch (MathUtils.random(2)) {
                case 0 -> gunLoot;
                case 1 -> healLoot;
                default -> stuffLoot;
            };
            entity.getInventory().addAll(lootGenerator.generate(1));
            gameScreen.world.spawn(entity);
        }
        return false;
    }
}
