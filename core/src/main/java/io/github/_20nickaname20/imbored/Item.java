package io.github._20nickaname20.imbored;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Item {
    private final float size;
    private boolean isUpdating = false;

    public Item(float size) {
        this.size = size;
    }

    public abstract void render(ShapeRenderer renderer, boolean inHand);

    public final boolean isUpdating() {
        return this.isUpdating;
    }

    public final void setUpdating(boolean isUpdating) {
        this.isUpdating = isUpdating;
    }

    public void update(Entity holder, float dt) {

    }

    public final float getSize() {
        return size;
    }
}
