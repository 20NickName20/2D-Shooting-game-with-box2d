package io.github._20nickname20.imbored;

import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public abstract class PlayerController {

    protected PlayerEntity player;
    public PlayerController() {

    }

    protected final void startUseMode() {
        if (player.getEquippedItem() == null) {
            if (!player.grab()) {
                player.equipSelectedItem();
            }
        } else {
            player.startUsingItem();
        }
    }

    protected final void stopUseMode() {
        if (player.getEquippedItem() == null) {
            player.put();
        } else {
            player.stopUsingItem();
        }
    }

    protected final void switchMode() {
        if (player.getEquippedItem() == player.getInventory().getSelectedItem()) {
            player.unequipItem();
        } else {
            player.equipSelectedItem();
        }
    }

    public void register(PlayerEntity player) {
        this.player = player;
    }

    public abstract void unregister();

    public abstract void update(float dt);
}
