package io.github._20nickname20.imbored;

import io.github._20nickname20.imbored.entities.living.human.cursor.PlayerEntity;

public abstract class PlayerController {

    protected PlayerEntity player;
    public PlayerController() {

    }

    protected final void startUseMode() {
        if (player.getMode() == PlayerEntity.Mode.GRAB) {
            player.grab();
        }
        if (player.getMode() == PlayerEntity.Mode.INV) {
            player.startUsingItem();
        }
    }

    protected final void stopUseMode() {
        if (player.getMode() == PlayerEntity.Mode.GRAB) {
            player.put();
        }
        if (player.getMode() == PlayerEntity.Mode.INV) {
            player.stopUsingItem();
        }
    }

    public void register(PlayerEntity player) {
        this.player = player;
    }

    public abstract void unregister();

    public abstract void update(float dt);
}
