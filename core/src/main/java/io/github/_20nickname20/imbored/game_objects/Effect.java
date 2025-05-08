package io.github._20nickname20.imbored.game_objects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Effect implements Removable {
    protected float timeLeft;
    protected final float initTime;
    protected final float amplifier;
    protected Entity appliedEntity;
    //  add smth like attribute modifiers from MC

    public Effect(float time, float amplifier) {
        this.initTime = time;
        this.timeLeft = time;
        this.amplifier = amplifier;
    }

    public void onApply(Entity appliedEntity) {
        this.appliedEntity = appliedEntity;
    }

    public void render(ShapeRenderer renderer, Entity entity) {

    }

    @Override
    public void remove() {
        isRemoved = true;
    }

    public void update(float dt) {
        timeLeft -= dt;
        if (timeLeft <= 0) {
            this.remove();
        }
    }

    private boolean isRemoved = false;

    @Override
    public boolean isRemoved() {
        return isRemoved;
    }
}
