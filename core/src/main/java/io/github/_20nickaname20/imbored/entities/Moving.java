package io.github._20nickaname20.imbored.entities;

import com.badlogic.gdx.math.Vector2;

public interface Moving {
    default Vector2 getMovement() {
        return new Vector2();
    }

    boolean isMoving();

    void setXMovement(float x);
    void setYMovement(float y);

    void clearXMovement();
    void clearYMovement();

    float getLastXMovement();
    float getLastYMovement();
}
