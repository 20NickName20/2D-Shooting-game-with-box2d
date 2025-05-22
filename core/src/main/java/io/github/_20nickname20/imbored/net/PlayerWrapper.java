package io.github._20nickname20.imbored.net;

import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickname20.imbored.Controllable;
import io.github._20nickname20.imbored.game_objects.Interact;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public abstract class PlayerWrapper implements Controllable {
    private final PlayerEntity player;

    public PlayerWrapper(PlayerEntity player) {
        this.player = player;
    }

    public abstract void sendPacket(Network.ControlsPacket packet);

    @Override
    public void move(float x) {
        Network.Move packet = new Network.Move();
        packet.x = x;
        sendPacket(packet);
    }

    @Override
    public void jump() {
        sendPacket(new Network.Jump());
    }

    @Override
    public void setCursorAngle(float angle) {
        Network.SetCursorAngle packet = new Network.SetCursorAngle();
        packet.angleRad = angle;
        sendPacket(packet);
    }

    @Override
    public void toggleItem() {
        sendPacket(new Network.ToggleItem());
    }

    @Override
    public void startApplying() {
        sendPacket(new Network.StartApplying());
    }

    @Override
    public void stopApplying() {
        sendPacket(new Network.StopApplying());
    }

    @Override
    public void scrollInventory(int amount) {
        Network.ScrollInventory packet = new Network.ScrollInventory();
        packet.amount = amount;
        sendPacket(packet);
    }

    @Override
    public void interact(Interact interact, boolean isPressed) {
        Network.DoInteract packet = new Network.DoInteract();
        packet.action = interact;
        packet.state = isPressed;
        sendPacket(packet);
    }

    @Override
    public void grabOrUse() {
        sendPacket(new Network.GrabOrUse());
    }

    @Override
    public void putOrStopUse() {
        sendPacket(new Network.PutOrStopUse());
    }

    @Override
    public void dropOrThrow() {
        sendPacket(new Network.DropOrThrow());
    }

    @Override
    public Body getBody() {
        return player.getBody();
    }
}
