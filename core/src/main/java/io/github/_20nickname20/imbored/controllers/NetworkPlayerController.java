package io.github._20nickname20.imbored.controllers;

import io.github._20nickname20.imbored.PlayerController;
import io.github._20nickname20.imbored.net.Network;

public class NetworkPlayerController extends PlayerController {
    @Override
    public void update(float dt) {

    }

    public void receive(Network.ControlsPacket packet) {
        if (packet instanceof Network.Move move) {
            controllable.move(move.x);
        }
        if (packet instanceof Network.Jump) {
            controllable.jump();
        }
        if (packet instanceof Network.SetCursorAngle setCursorAngle) {
            controllable.setCursorAngle(setCursorAngle.angleRad);
        }
        if (packet instanceof Network.ToggleItem) {
            controllable.toggleItem();
        }
        if (packet instanceof Network.StartApplying) {
            controllable.startApplying();
        }
        if (packet instanceof Network.StopApplying) {
            controllable.stopApplying();
        }
        if (packet instanceof Network.ScrollInventory scrollInventory) {
            controllable.scrollInventory(scrollInventory.amount);
        }
        if (packet instanceof Network.DoInteract interact) {
            controllable.interact(interact.action, interact.state);
        }
        if (packet instanceof Network.GrabOrUse) {
            controllable.grabOrUse();
        }
        if (packet instanceof Network.PutOrStopUse) {
            controllable.putOrStopUse();
        }
        if (packet instanceof Network.DropOrThrow) {
            controllable.dropOrThrow();
        }
    }
}
