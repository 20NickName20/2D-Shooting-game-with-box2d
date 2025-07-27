package io.github._20nickname20.imbored.world;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.Gdx;

import io.github._20nickname20.imbored.ControlsProfile;
import io.github._20nickname20.imbored.GameMap;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.PlayerController;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.entities.statics.GroundEntity;
import io.github._20nickname20.imbored.util.Shapes;

public class ServerWorld extends GameWorld {
    private final HashMap<UUID, PlayerController> localControllersByUuid = new HashMap<>();

    private GameMap map;

    public GameMap getMap() {
        return map;
    }

    public ServerWorld(List<ControlsProfile> profiles, String mapName) {
        super();

        cameraFollowsPlayers = false;

        map = GameMap.loadMap(this, mapName);
        Gdx.app.log("ServerWorld", "Loading map \"" + mapName + "\"...");
        if (map == null) {
            map = new GameMap(this, mapName);
            Gdx.app.log("ServerWorld", "Map \"" + mapName + "\" not found, creating a new one.");

            new GroundEntity(this, 0, 0, Shapes.boxShape(100, 10)).spawn();
        } else {
            Gdx.app.log("ServerWorld", "Map \"" + mapName + "\" loaded successfully.");
        }

        for (ControlsProfile profile : profiles) {
            PlayerController controller = ControlsProfile.getPlayerController(profile);

            PlayerEntity player = new PlayerEntity(this, 0f, 50, controller, profile.username);
            player.spawn();

            localControllersByUuid.put(player.uuid, controller);

            cameraFollowsPlayers = true;
            this.players.put(player.uuid.toString(), player);
            System.out.println("Player " + player.username + " spawned with UUID: " + player.uuid);

            controller.register(player);
        }
    }
}
