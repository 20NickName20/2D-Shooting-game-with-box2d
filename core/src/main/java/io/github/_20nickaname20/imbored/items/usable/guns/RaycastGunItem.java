package io.github._20nickaname20.imbored.items.usable.guns;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import io.github._20nickaname20.imbored.Util;
import io.github._20nickaname20.imbored.entities.DamagableEntity;
import io.github._20nickaname20.imbored.entities.damagable.living.human.PlayerEntity;
import io.github._20nickaname20.imbored.items.usable.GunItem;

public abstract class RaycastGunItem extends GunItem {
    private float power;
    private float recoilScale;
    private float shootRayDisplayTime;
    protected float range;
    public RaycastGunItem(float size, float cooldown, float damage, float power, float recoilScale, float range, float shootRayDisplayTime) {
        super(size, cooldown, damage);
        this.power = power;
        this.recoilScale = recoilScale;
        this.range = range;
        this.shootRayDisplayTime = shootRayDisplayTime;
    }

    // TODO: добавить разбос

    @Override
    public boolean shootFrom(PlayerEntity player) {
        if (!super.shootFrom(player)) return false;
        Body playerBody = player.b;
        playerBody.getWorld().rayCast(Util.Raycast.calculateCollisionPoint(playerBody), playerBody.getPosition(), playerBody.getPosition().cpy().add(player.cursorDirection.cpy().scl(range)));
        // TODO: Add range property
        if (!Util.Raycast.hadCollision) return true;
        Util.Raycast.collisionBody.applyLinearImpulse(player.cursorDirection.cpy().scl(power), Util.Raycast.collisionPoint, true);
        playerBody.applyLinearImpulse(player.cursorDirection.cpy().scl(-power * recoilScale), playerBody.getPosition(), true);
        if (Util.Raycast.collisionBody.getUserData() instanceof DamagableEntity entity) {
            entity.damage(damage);
        }
        return true;
    }

    protected void renderRay(ShapeRenderer renderer) {
        renderer.line(0, 0, range, 0);
    }

    @Override
    public void render(ShapeRenderer renderer, boolean inHand) {
        if (!inHand) return;
        if (Util.time() - lastShootTime > shootRayDisplayTime) return;
        renderRay(renderer);
    }
}
