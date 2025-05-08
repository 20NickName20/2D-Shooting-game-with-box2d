package io.github._20nickname20.imbored.game_objects.entities.living.human;

import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.entities.living.HumanEntity;

public abstract class CursorEntity extends HumanEntity {

    protected float cursorDistance = 0;
    private final Vector2 cursorDirection = new Vector2(0, 1);

    public CursorEntity(GameWorld world, float x, float y) {
        super(world, x, y);
    }

    public CursorEntity(GameWorld world, EntityData data) {
        super(world, data);
    }

    public float getCursorDistance() {
        return cursorDistance;
    }

    public Vector2 getCursorDirection() {
        return cursorDirection.cpy();
    }

    public void setCursorDirection(Vector2 direction) {
        this.cursorDirection.set(direction);
        this.cursorDirection.nor();
    }

    public void addCursorDirection(Vector2 direction) {
        this.cursorDirection.add(direction);
        this.cursorDirection.nor();
    }

    public void addCursorDirection(float x, float y) {
        this.cursorDirection.add(x, y);
        this.cursorDirection.nor();
    }


    public void setCursorDistance(float distance) {
        this.cursorDistance = distance;
    }

    public Vector2 getCursorRelative() {
        return getCursorDirection().scl(getCursorDistance());
    }

    public Vector2 getCursorPosition() {
        return this.b.getPosition().cpy().add(getCursorRelative());
    }

    public float getHandCursorDistance() {
        return 8;
    }
}
