package io.github._20nickname20.imbored.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import io.github._20nickname20.imbored.ControlsProfile;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.Main;
import io.github._20nickname20.imbored.PlayerController;
import io.github._20nickname20.imbored.controllers.NetworkPlayerController;
import io.github._20nickname20.imbored.controllers.PlayerGamepadController;
import io.github._20nickname20.imbored.controllers.PlayerKeyboardAndMouseController;
import io.github._20nickname20.imbored.game_objects.Chunk;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.JointEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.net.ClientConnection;
import io.github._20nickname20.imbored.net.Network;
import io.github._20nickname20.imbored.net.wrappers.ServerPlayerWrapper;

import java.io.Console;
import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerWorld extends GameWorld {
    Server server;

    protected final Json json = new Json();

    private final int saveId = 0;
    private final String savePath = Main.SAVE_ROOT + "saves/save" + saveId + "/";

    private final HashMap<UUID, PlayerController> localControllersByUuid = new HashMap<>();

    public ServerWorld(List<ControlsProfile> profiles) {
        cameraFollowsPlayers = false;

        FileHandle serverFileHandle = Gdx.files.external(savePath + "data.json");
        if (serverFileHandle.exists()) {
            ServerData serverData = json.fromJson(ServerData.class, serverFileHandle.readString());
            for (PlayerData playerData : serverData.playerData) {
                playersByUsername.put(playerData.username, playerData);
            }
        }

        try {
            server = new Server() {
                @Override
                protected Connection newConnection() {
                    return new ClientConnection();
                }
            };
            server.start();
            Network.register(server);

            server.addListener(new Listener() {
                @Override
                public void received(Connection connection, Object object) {
                    ClientConnection clientConnection = (ClientConnection) connection;

                    if (object instanceof Network.RequestChunk requestChunk) {
                        Network.LoadChunk loadChunk = new Network.LoadChunk();
                        loadChunk.position = requestChunk.position;
                        loadChunk.chunkData = loadChunk(requestChunk.position).createPersistentData();
                        connection.sendTCP(loadChunk);
                    }

                    if (object instanceof Network.Login login) {
                        NetworkPlayerController controller = new NetworkPlayerController();
                        PlayerData playerData = loadPlayerData(login.username, controller);

                        clientConnection.players.put(UUID.fromString(playerData.uuid), controller);

                        Network.LoadChunk loadChunk = new Network.LoadChunk();
                        loadChunk.position = playerData.chunk;
                        loadChunk.chunkData = loadChunk(playerData.chunk).createPersistentData();
                        connection.sendTCP(loadChunk);

                        Network.LoginResponse loginResponse = new Network.LoginResponse();
                        loginResponse.uuid = playerData.uuid;
                        loginResponse.username = login.username;
                        clientConnection.sendTCP(loginResponse);
                    }

                    if (object instanceof Network.ControlsPacket controlsPacket) {
                        UUID uuid = UUID.fromString(controlsPacket.uuid);
                        NetworkPlayerController controller = clientConnection.players.get(uuid);
                        if (controller == null) return;
                        controller.receive(controlsPacket);
                    }
                }
            });

            server.bind(Network.tcpPort, Network.udpPort);
            server.getUpdateThread().setUncaughtExceptionHandler((thread, e) -> {
                if (e instanceof ClosedSelectorException) {
                    System.out.println("server thread closed");
                } else {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ControlsProfile profile : profiles) {
            PlayerController controller = ControlsProfile.getPlayerController(profile);

            loadPlayerData(profile.username, controller);
        }
    }

    @Override
    public void spawn(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            if (localControllersByUuid.containsKey(player.uuid)) {
                player.controller = localControllersByUuid.get(player.uuid);
            }
        }
        super.spawn(entity);
        if (entity instanceof PlayerEntity player) {
            if (playersByUsername.containsKey(player.username)) return;
            PlayerData playerData = new PlayerData();
            playerData.username = player.username;
            playerData.uuid = player.uuid.toString();
            playerData.chunk = player.chunkPos;
            playersByUsername.put(player.username, playerData);
        }
    }

    protected PlayerData loadPlayerData(String username, PlayerController controller) {
        if (!playersByUsername.containsKey(username)) {
            PlayerEntity player = new PlayerEntity(this, 0f, 120f, controller, username);
            player.spawn();
            PlayerData playerData = new PlayerData();
            playerData.chunk = 0;
            playerData.uuid = player.uuid.toString();
            playerData.username = username;
            playersByUsername.put(username, playerData);
        }

        PlayerData playerData = playersByUsername.get(username);
        if (!(controller instanceof NetworkPlayerController)) {
            UUID uuid = UUID.fromString(playerData.uuid);
            localControllersByUuid.put(uuid, controller);
            PlayerEntity player = PlayerEntity.getByUsername(username);
            if (player != null) {
                player.controller = controller;
            }
            localPlayers.add(uuid);
            cameraFollowsPlayers = true;
        }
        loadChunk(playerData.chunk);

        PlayerEntity player = PlayerEntity.getByUsername(username);
        ServerPlayerWrapper wrapper = new ServerPlayerWrapper(server, player);
        controller.register(wrapper);

        return playerData;
    }

    @Override
    public Chunk loadChunk(int x) {
        if (isChunkLoaded(x)) return getLoadedChunk(x);

        loadedChunks.put(x, null);

        FileHandle handle = Gdx.files.external(savePath + "chunks/chunk" + x + ".json");
        if (handle.exists()) {
            Chunk.ChunkData data = json.fromJson(Chunk.ChunkData.class, handle.readString());
            Chunk chunk = new Chunk(this, x, data);
            Chunk left = getLoadedChunk(x - 1);
            if (left != null) {
                left.right = chunk;
                chunk.left = left;
            }
            Chunk right = getLoadedChunk(x + 1);
            if (right != null) {
                right.left = chunk;
                chunk.right = right;
            }
            loadedChunks.put(x, chunk);
            return chunk;
        }

        Chunk chunk = new Chunk(this, x);
        Chunk left = getLoadedChunk(x - 1);
        if (left != null) {
            left.right = chunk;
            chunk.left = left;
        }
        Chunk right = getLoadedChunk(x + 1);
        if (right != null) {
            right.left = chunk;
            chunk.right = right;
        }
        chunk.generate(left, right);
        loadedChunks.put(x, chunk);
        return chunk;
    }

    @Override
    public void unloadChunk(int x) {
        if (!isChunkLoaded(x)) return;

        if (getEntitiesInChunk(x) == null) return;

        Chunk chunk = getLoadedChunk(x);
        chunk.onUnload();
        Chunk.ChunkData data = chunk.createPersistentData();

        for (JointEntity joint : getJointsInChunk(x)) {
            joint.remove();
        }

        for (Entity entity : getEntitiesInChunk(x)) {
            entity.remove();
        }

        if (data.entityData.length != 0) {
            json.toJson(data, Gdx.files.external(savePath + "chunks/chunk" + x + ".json"));
        }

        entitiesByChunk.remove(x);
        loadedChunks.remove(x);
    }

    @Override
    public void dispose() {
        try {
            server.stop();
            server.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerData serverData = new ServerData();
        serverData.playerData = new PlayerData[playersByUsername.size()];
        int i = 0;
        for (PlayerData playerData : playersByUsername.values()) {
            serverData.playerData[i] = playerData;
            i++;
        }
        json.toJson(serverData, Gdx.files.external(savePath + "data.json"));
        super.dispose();
    }

    public static class ServerData {
        PlayerData[] playerData;
    }

    public static class PlayerData {
        public int chunk;
        public String uuid;
        public String username;

        @Override
        public String toString() {
            return "PlayerData{" +
                "chunk=" + chunk +
                ", uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                '}';
        }
    }
}
