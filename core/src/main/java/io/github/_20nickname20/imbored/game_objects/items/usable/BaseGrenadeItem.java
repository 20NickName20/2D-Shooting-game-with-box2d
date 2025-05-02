package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;
import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;
import io.github._20nickname20.imbored.game_objects.entities.ItemEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.util.ClosestRaycast;
import io.github._20nickname20.imbored.util.Ray;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseGrenadeItem extends UsableItem {
    private final float power, damage, range;
    protected boolean isPinOut = false;
    protected float timeLeft;
    private boolean isExploded = false;
    private final float rayDisplayTime = 1.5f;

    public BaseGrenadeItem(Entity holder, float size, float timeLeft, float power, float damage, float range) {
        super(holder, size);
        this.timeLeft = timeLeft;
        this.power = power;
        this.damage = damage;
        this.range = range;
    }
    private final List<Ray> rays = new ArrayList<>();

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

    protected final void renderRays(ShapeRenderer renderer) {
        ArrayList<Ray> toRemove = new ArrayList<>();
        float time = Util.time();
        for (Ray ray : rays) {
            float rayTime = time - ray.time;
            if (rayTime > rayDisplayTime) {
                toRemove.add(ray);
            }
            Vector2 start = new Vector2();
            //Vector2 start = new Vector2(range * ray.fraction * Math.min(1, rayTime / rayDisplayTime), 0).rotateRad(ray.offsetAngle);
            Vector2 end = new Vector2(range * ray.fraction, 0).rotateAroundRad(new Vector2(), ray.offsetAngle);
            renderer.line(start, end);
        }
        for (Ray removed : toRemove) {
            rays.remove(removed);
        }
    }

    private void explode() {
        Entity holder = this.getHolder();
        Util.explode(holder.gameWorld, holder.b, rays, holder.b.getPosition(), (float) (Math.PI / 24f), range, power, damage);

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
    public void onDeselect(InventoryHolder holder) {

    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(1f, 0.6f, 0.1f, 1);
        With.rotation(renderer, -handHolder.b.getAngle() * MathUtils.radiansToDegrees, () -> {
            renderRays(renderer);
        });
    }

    @Override
    public boolean canPickup() {
        return !this.isPinOut;
    }
}
