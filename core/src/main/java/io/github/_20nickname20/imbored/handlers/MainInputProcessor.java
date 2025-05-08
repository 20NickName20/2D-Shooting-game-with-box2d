package io.github._20nickname20.imbored.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import io.github._20nickname20.imbored.AdminTool;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.controllers.PlayerKeyboardAndMouseController;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.LootGenerator;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.StaticEntity;
import io.github._20nickname20.imbored.game_objects.entities.container.CrateEntity;
import io.github._20nickname20.imbored.game_objects.entities.container.crate.WoodenCrateEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.entities.statics.GroundEntity;
import io.github._20nickname20.imbored.game_objects.loot.supply.GunSupplyLoot;
import io.github._20nickname20.imbored.game_objects.loot.supply.HealSupplyLoot;
import io.github._20nickname20.imbored.game_objects.loot.supply.StuffSupplyLoot;
import io.github._20nickname20.imbored.util.FindBody;
import io.github._20nickname20.imbored.screens.GameScreen;
import io.github._20nickname20.imbored.screens.MainMenuScreen;
import io.github._20nickname20.imbored.util.Shapes;

public class MainInputProcessor extends InputAdapter {
    GameScreen gameScreen;
    Camera camera;

    public MainInputProcessor(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.camera = GameScreen.getViewport().getCamera();

        if (Controllers.getControllers().isEmpty()) {
            keyboardPlayer = new PlayerEntity(gameScreen.world, gameScreen.getCamera().position.x, 120, new PlayerKeyboardAndMouseController());
            gameScreen.world.spawn(keyboardPlayer);
        }

        AdminTool.world = gameScreen.world;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!AdminTool.isEnabled) return false;
        Vector2 worldPoint = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        Vector2 screenPoint = worldPoint.cpy().sub(camera.position.x, camera.position.y);
        AdminTool.onClickPressed(worldPoint, screenPoint, button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!AdminTool.isEnabled) return false;
        Vector2 worldPoint = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        Vector2 screenPoint = worldPoint.cpy().sub(camera.position.x, camera.position.y);
        AdminTool.onClickReleased(worldPoint, screenPoint, button);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!AdminTool.isEnabled) return false;
        Vector2 worldPoint = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        Vector2 screenPoint = worldPoint.cpy().sub(camera.position.x, camera.position.y);
        AdminTool.onMouseMove(worldPoint, screenPoint);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (!AdminTool.isEnabled) return false;
        Vector2 worldPoint = GameScreen.getViewport().unproject(new Vector2(screenX, screenY));
        Vector2 screenPoint = worldPoint.cpy().sub(camera.position.x, camera.position.y);
        AdminTool.onMouseMove(worldPoint, screenPoint);
        return false;
    }

    PlayerEntity keyboardPlayer;

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.TAB){
            AdminTool.isEnabled = !AdminTool.isEnabled;  // Switch boolean for GameScreen to render
        }
        if (keycode == Input.Keys.ESCAPE) {
            gameScreen.game.setScreen(new MainMenuScreen(gameScreen.game));
        }
        if (!AdminTool.isEnabled) return false;
        AdminTool.keyPressed(keycode);
        return false;
    }
}
