package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.render.BarDisplay;

public abstract class TimedUsableItem extends UsableItem {
    private final float useTime;
    public TimedUsableItem(Entity holder, float size, float useTime) {
        super(holder, size);
        this.useTime = useTime;
    }

    private float usedTime = 0;
    private final BarDisplay progressBar = new BarDisplay(new Color(0.2f, 0, 0.7f, 1), new Color(0.9f, 0.6f, 0f, 1), 0, 0.3f);
    private boolean isUsing = false;

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        if (handHolder == null) return;

        if (progressBar.getDisplayValue() < 0.001f) return;
        this.renderBar(renderer, handHolder, 5, progressBar);
    }

    protected void onUseFinish(Entity holder) {
        this.remove();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (isUsing) {
            usedTime += dt;
            progressBar.setTargetValue(usedTime / useTime);
        }
        if (usedTime >= useTime) {
            onUseFinish(this.getHolder());
            isUsing = false;
        }
        progressBar.update(dt);
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        isUsing = true;
    }

    @Override
    public void onEndUse(PlayerEntity player) {
        isUsing = false;
        usedTime = 0;
        progressBar.setTargetValue(0);
    }
}
