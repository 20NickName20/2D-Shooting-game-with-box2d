package io.github._20nickname20.imbored.game_objects.items.usable.guns;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickname20.imbored.util.ClosestRaycast;
import io.github._20nickname20.imbored.util.Ray;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.BaseGunItem;

import java.util.ArrayList;
import java.util.List;

public abstract class RaycastGunItem extends BaseGunItem {
    // TODO: ADD LASER!!!! (shoots thru 5 entities)
    public RaycastGunItem(int ammo) {
        super(ammo);
    }

    public RaycastGunItem(ItemData data) {
        super(data);
    }

    public abstract float getPower();
    public abstract float getRecoil();
    public abstract float getMaxScatterAngle();
    public abstract float getRange();
    public abstract float getRayLength();
    public abstract float getRaySpeed();
    public abstract Color getRayColor();

    public void shootRay(PlayerEntity player, float offsetAngle) {
        Body playerBody = player.b;
        offsetAngle += MathUtils.random(-getMaxScatterAngle(), getMaxScatterAngle());
        Vector2 impulse = player.getCursorDirection();
        Vector2 endPosition = playerBody.getPosition().cpy().add(impulse.rotateRad(offsetAngle).cpy().scl(getRange()));
        ClosestRaycast.RaycastResult result = ClosestRaycast.cast(player.world, playerBody, playerBody.getPosition(), endPosition);
        if (result == null) {
            player.gameWorld.addRay(new Ray(player.getCursorPosition(), endPosition, Util.time(), getRayLength(), getRaySpeed(), getRayColor()));
            return;
        }

        player.gameWorld.addRay(new Ray(player.getCursorPosition(), result.point, Util.time(), getRayLength(), getRaySpeed(), getRayColor()));
        impulse.scl(getPower());
        result.body.applyLinearImpulse(impulse.cpy().scl((float) (1f / Math.pow(result.body.getMass(), 0.9f))), result.point, true);
        impulse.setLength(getRecoil());
        impulse.scl(-1);
        playerBody.applyLinearImpulse(impulse, playerBody.getPosition(), true);
        if (result.body.getUserData() instanceof DamagableEntity entity) {
            entity.damage(getDamage());
        }
    }

    @Override
    protected void onShoot(PlayerEntity player) {
        super.onShoot(player);
        shootRay(player, 0);
    }

    @Override
    public void onUnequip(PlayerEntity holder) {
        super.onUnequip(holder);
    }
}
