package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.util.Ray;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseGrenadeItem extends UsableItem {
    protected boolean isPinOut = false;
    protected float timeLeft = getInitTime();
    private boolean isExploded = false;

    public BaseGrenadeItem() {
        super();
    }

    public BaseGrenadeItem(ItemData data) {
        super(data);
    }

    @Override
    public float getSize() {
        return 0;
    }

    public abstract float getPower();

    public abstract float getDamage();

    public abstract float getRange();

    public abstract float getInitTime();

    @Override
    public void update(float dt) {
        super.update(dt);
        if (isPinOut) {
            timeLeft -= dt;
        }
        if (isExploded) return;
        if (timeLeft <= 0) {
            isExploded = true;
            explode();
        }
    }

    private void explode() {
        Entity holder = this.getHolder();
        holder.gameWorld.explode(holder.b, holder.b.getPosition(), (float) (Math.PI / 25f), getRange(), getPower(), getDamage(), 20 * MathUtils.degRad, 50, 40, Color.ORANGE, 0.5f);

        this.remove();
        if (this.getHolder() instanceof DamagableEntity damagable) {
            damagable.kill();
        }
    }

    @Override
    public void onStartUse(PlayerEntity player) {

    }

    @Override
    public void onEndUse(PlayerEntity player) {
        isPinOut = true;
    }

    @Override
    public void onUnequip(PlayerEntity holder) {
        this.isEquipped = false;
    }

    @Override
    public boolean canPickup() {
        return !this.isPinOut;
    }
}
