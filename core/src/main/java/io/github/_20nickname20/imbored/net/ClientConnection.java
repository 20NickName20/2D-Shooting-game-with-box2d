package io.github._20nickname20.imbored.net;

import com.esotericsoftware.kryonet.Connection;
import io.github._20nickname20.imbored.controllers.NetworkPlayerController;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

import java.util.*;

public class ClientConnection extends Connection {
    public Map<UUID, NetworkPlayerController> players = new HashMap<>();
}
