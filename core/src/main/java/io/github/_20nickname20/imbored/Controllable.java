package io.github._20nickname20.imbored;

import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickname20.imbored.game_objects.Interact;

public interface Controllable {
    void move(float x);
    void jump();
    void setCursorAngle(float angle);

    void toggleItem();

    /**
     * starts applying selected item to the equipped item
     */
    void startApplying();
    /**
     * stops applying selected item to the equipped item
     */
    void stopApplying();

    void scrollInventory(int amount);

    void interact(Interact interact, boolean isPressed);
    void grabOrUse();
    void putOrStopUse();
    void dropOrThrow();

    Body getBody();
}
