package io.github._20nickaname20.imbored.entities.damagable.living.human;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickaname20.imbored.entities.damagable.living.HumanEntity;

public class CursorEntity extends HumanEntity {

    protected float cursorDistance = 0;
    public final Vector2 cursorDirection = new Vector2(0, 1);

    public CursorEntity(World world, float x, float y, float maxHealth) {
        super(world, x, y, maxHealth);
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
