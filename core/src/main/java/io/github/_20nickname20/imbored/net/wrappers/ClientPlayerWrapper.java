package io.github._20nickname20.imbored.net.wrappers;

import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.kryonet.Client;
import io.github._20nickname20.imbored.Controllable;
import io.github._20nickname20.imbored.game_objects.Interact;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.net.Network;
import io.github._20nickname20.imbored.net.PlayerWrapper;

public class ClientPlayerWrapper extends PlayerWrapper {
    private final Client client;
    private final String uuid;

    public ClientPlayerWrapper(Client client, PlayerEntity player, String uuid) {
        super(player);
        this.client = client;
        this.uuid = uuid;
    }

    @Override
    public void sendPacket(Network.ControlsPacket packet) {
        packet.uuid = uuid;
        client.sendTCP(packet);
    }
}
