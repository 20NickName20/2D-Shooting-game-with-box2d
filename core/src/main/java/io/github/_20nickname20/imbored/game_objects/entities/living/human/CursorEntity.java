package io.github._20nickname20.imbored.game_objects.entities.living.human;

import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.entities.living.HumanEntity;

public abstract class CursorEntity extends HumanEntity {

    protected float cursorDistance = 0;
    public final Vector2 cursorDirection = new Vector2(0, 1);

    public CursorEntity(GameWorld world, float x, float y, float maxHealth, float defaultMaxWalkSpeed) {
        super(world, x, y, maxHealth, defaultMaxWalkSpeed);
    }

    public float getCursorDistance() {
        return cursorDistance;
    }

    public void setCursorDistance(float distance) {
        this.cursorDistance = distance;
    }

    public Vector2 getCursorRelative() {
        return cursorDirection.cpy().scl(getCursorDistance());
    }

    public Vector2 getCursorPosition() {
        return this.b.getPosition().cpy().add(getCursorRelative());
    }

    public float getDefaultCursorDistance() {
        return 8;
    }
}
