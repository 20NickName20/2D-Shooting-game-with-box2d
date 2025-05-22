package io.github._20nickname20.imbored.net.wrappers;

import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Server;
import io.github._20nickname20.imbored.Controllable;
import io.github._20nickname20.imbored.game_objects.Interact;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.net.Network;
import io.github._20nickname20.imbored.net.PlayerWrapper;

import static io.github._20nickname20.imbored.util.Util.printStackTrace;

public class ServerPlayerWrapper extends PlayerWrapper {
    private final Server server;
    private final PlayerEntity player;

    public ServerPlayerWrapper(Server server, PlayerEntity player) {
        super(player);
        this.server = server;
        this.player = player;
    }

    @Override
    public void sendPacket(Network.ControlsPacket packet) {
        packet.uuid = player.uuid.toString();
        server.sendToAllTCP(packet);
    }

    @Override
    public void move(float x) {
        super.move(x);
        player.move(x);
    }

    @Override
    public void jump() {
        super.jump();
        player.jump();
    }

    @Override
    public void setCursorAngle(float angle) {
        super.setCursorAngle(angle);
        player.setCursorAngle(angle);
    }

    @Override
    public void toggleItem() {
        super.toggleItem();
        player.toggleItem();
    }

    @Override
    public void startApplying() {
        super.startApplying();
        player.startApplying();
    }

    @Override
    public void stopApplying() {
        super.stopApplying();
        player.startApplying();
    }

    @Override
    public void scrollInventory(int amount) {
        super.scrollInventory(amount);
        player.scrollInventory(amount);
    }

    @Override
    public void interact(Interact interact, boolean isPressed) {
        super.interact(interact, isPressed);
        player.interact(interact, isPressed);
    }

    @Override
    public void grabOrUse() {
        super.grabOrUse();
        player.grabOrUse();
    }

    @Override
    public void putOrStopUse() {
        super.putOrStopUse();
        player.putOrStopUse();
    }

    @Override
    public void dropOrThrow() {
        super.dropOrThrow();
        player.dropOrThrow();
    }
}
