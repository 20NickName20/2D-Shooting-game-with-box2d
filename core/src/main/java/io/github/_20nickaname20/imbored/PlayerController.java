package io.github._20nickaname20.imbored;

import io.github._20nickaname20.imbored.entities.living.human.PlayerEntity;

public abstract class PlayerController {

    protected PlayerEntity player;
    public PlayerController() {
        // TODO: make an abstract class for controller (for different entities and control types (and keybinds))
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

    public void register() {

    }

    public void unregister() {

    }
}
