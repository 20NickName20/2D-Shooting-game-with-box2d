package io.github._20nickname20.imbored.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import io.github._20nickname20.imbored.ControlsProfile;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.PlayerController;
import io.github._20nickname20.imbored.controllers.NetworkPlayerController;
import io.github._20nickname20.imbored.game_objects.Chunk;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.JointEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.net.Network;
import io.github._20nickname20.imbored.net.wrappers.ClientPlayerWrapper;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class ClientWorld extends GameWorld {
    Client client;
    public boolean hostNotFound = false;

    Deque<Object> synchronizedReceive = new ArrayDeque<>();

    HashMap<UUID, PlayerController> localControllersByUuid = new HashMap<>();
    HashMap<UUID, NetworkPlayerController> networkControllers = new HashMap<>();
    HashMap<String, ControlsProfile> controlsProfiles = new HashMap<>();

    public ClientWorld(List<ControlsProfile> profiles) {
        try {
            client = new Client(8192, 8192 * 2);
            client.start();
            Network.register(client);
            InetAddress address = client.discoverHost(Network.udpPort, 5000);

            client.addListener(new Listener() {
                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof Network.LoadChunk loadChunk) {
                        synchronizedReceive.addLast(loadChunk);
                    }

                    if (object instanceof Network.LoginResponse loginResponse) {
                        synchronizedReceive.addLast(loginResponse);
                    }

                    if (object instanceof Network.ControlsPacket controlsPacket) {
                        UUID uuid = UUID.fromString(controlsPacket.uuid);
                        if (!networkControllers.containsKey(uuid)) {
                            if (!(Entity.getByUuid(uuid) instanceof PlayerEntity player)) {
                                throw new RuntimeException("control packet uuid is not player!");
                            }
                            NetworkPlayerController newController = new NetworkPlayerController();
                            newController.register(player);
                            networkControllers.put(player.uuid, newController);
                        }
                        NetworkPlayerController controller = networkControllers.get(uuid);
                        controller.receive(controlsPacket);
                    }
                }
            });

            if (address == null) {
                hostNotFound = true;
                return;
            }
            client.connect(5000, address, Network.tcpPort, Network.udpPort);

            for (ControlsProfile profile : profiles) {
                controlsProfiles.put(profile.username, profile);

                Network.Login login = new Network.Login();
                login.username = profile.username;

                client.sendTCP(login);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shootRay(Body ignored, Vector2 position, float angleRad, float range, float power, float damage, float rayLength, float raySpeed, Color rayColor, float penetrateAmount) {

    }

    @Override
    public Chunk loadChunk(int x) {
        return null;
    }

    void receiveChunk(int x, Chunk.ChunkData data) {
        if (isChunkLoaded(x)) return;
        Chunk chunk = new Chunk(this, x, data);
        loadedChunks.put(x, chunk);
        entitiesByChunk.put(x, new HashSet<>());
    }

    @Override
    public void update(float dt) {
        while (!synchronizedReceive.isEmpty()) {
            Object received = synchronizedReceive.removeFirst();

            if (received instanceof Network.LoadChunk loadChunk) {
                receiveChunk(loadChunk.position, loadChunk.chunkData);
            }
            if (received instanceof Network.LoginResponse loginResponse) {
                ControlsProfile profile = controlsProfiles.get(loginResponse.username);
                if (profile == null) throw new RuntimeException("Server sent the unknown username!");
                PlayerController controller = ControlsProfile.getPlayerController(profile);

                UUID uuid = UUID.fromString(loginResponse.uuid);
                localPlayers.add(uuid);
                if (!(Entity.getByUuid(uuid) instanceof PlayerEntity player)) {
                    throw new RuntimeException("login uuid is not player!");
                }
                ClientPlayerWrapper wrapper = new ClientPlayerWrapper(client, player, loginResponse.uuid);
                controller.register(wrapper);
                localControllersByUuid.put(uuid, controller);
            }
        }

        super.update(dt);
    }

    @Override
    public void unloadChunk(int x) {
        if (!isChunkLoaded(x)) return;

        for (JointEntity joint : getJointsInChunk(x)) {
            joint.remove();
        }

        for (Entity entity : getEntitiesInChunk(x)) {
            entity.remove();
        }

        entitiesByChunk.remove(x);
        loadedChunks.remove(x);
    }

    @Override
    public void dispose() {
        super.dispose();

        try {
            client.stop();
            client.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
