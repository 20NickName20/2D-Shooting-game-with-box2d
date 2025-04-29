package io.github._20nickname20.imbored.items.usable.guns;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickname20.imbored.Entity;
import io.github._20nickname20.imbored.util.ClosestRaycast;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.entities.DamagableEntity;
import io.github._20nickname20.imbored.entities.InventoryHolder;
import io.github._20nickname20.imbored.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.items.usable.GunItem;

import java.util.ArrayList;
import java.util.List;

public abstract class RaycastGunItem extends GunItem {
    private float power;
    private float recoilScale;
    private float maxScatterAngle;

    private float rayDisplayTime;
    private List<Ray> rays = new ArrayList<>();

    private static class Ray {
        Ray(float fraction, float offsetAngle, float time) {
            this.fraction = fraction;
            this.offsetAngle = offsetAngle;
            this.time = time;
        }
        float fraction;
        float offsetAngle;
        float time;
    }
    // TODO: ADD LASER!!!! (shoots thru 5 entities)

    protected float range;
    public RaycastGunItem(Entity holder, float size, float cooldown, float damage, float power, float recoilScale, float range, float maxScatterAngle, float rayDisplayTime) {
        super(holder, size, cooldown, damage);
        this.power = power;
        this.recoilScale = recoilScale;
        this.range = range;
        this.maxScatterAngle = maxScatterAngle;
        this.rayDisplayTime = rayDisplayTime;
    }

    //TODO: make damaging ray as util
    public void shootRay(PlayerEntity player, float offsetAngle) {
        Body playerBody = player.b;
        offsetAngle += MathUtils.random(-maxScatterAngle, maxScatterAngle);
        Vector2 impulse = player.cursorDirection.cpy();
        Vector2 endPosition = playerBody.getPosition().cpy().add(impulse.rotateRad(offsetAngle).cpy().scl(range));
        ClosestRaycast.RaycastResult result = ClosestRaycast.cast(player.world, playerBody, playerBody.getPosition(), endPosition);
        if (result == null) {
            rays.add(new Ray(1, offsetAngle, Util.time()));
            return;
        }

        rays.add(new Ray(result.fraction, offsetAngle, Util.time()));
        impulse.scl(power);
        result.body.applyLinearImpulse(impulse.cpy().scl((float) (1f / Math.pow(result.body.getMass(), 0.9f))), result.point, true);
        impulse.scl(-recoilScale);
        playerBody.applyLinearImpulse(impulse, playerBody.getPosition(), true);
        if (result.body.getUserData() instanceof DamagableEntity entity) {
            entity.damage(damage);
        }
    }

    @Override
    protected void onShoot(PlayerEntity player) {
        shootRay(player, 0);
    }

    @Override
    public void onDeselect(InventoryHolder holder) {
        super.onDeselect(holder);
        rays.clear();
    }

    protected final void renderRays(ShapeRenderer renderer, float cursorDistance) {
        ArrayList<Ray> toRemove = new ArrayList<>();
        float time = Util.time();
        for (Ray ray : rays) {
            if (time - ray.time > rayDisplayTime) {
                toRemove.add(ray);
            }
            renderer.line(new Vector2(), new Vector2(range * ray.fraction - cursorDistance, 0).rotateRad(ray.offsetAngle));
        }
        for (Ray removed : toRemove) {
            rays.remove(removed);
        }
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        if (handHolder == null) return;
        renderRays(renderer, handHolder.getCursorDistance());
    }
}
