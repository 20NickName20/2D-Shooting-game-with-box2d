package io.github._20nickaname20.imbored;

import io.github._20nickaname20.imbored.entities.damagable.living.human.PlayerEntity;

public abstract class PlayerController {

    protected PlayerEntity player;
    public PlayerController() {

    }

    public void register(PlayerEntity player) {
        this.player = player;
    }

    public abstract void unregister();

    public abstract void update(float dt);
}
