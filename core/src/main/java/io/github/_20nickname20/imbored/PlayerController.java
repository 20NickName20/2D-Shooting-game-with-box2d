package io.github._20nickname20.imbored;

import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public abstract class PlayerController {

    protected Controllable controllable;
    public PlayerController() {

    }

    public void register(Controllable controllable) {
        this.controllable = controllable;
    }

    public void unregister() {

    }

    public abstract void update(float dt);
}
