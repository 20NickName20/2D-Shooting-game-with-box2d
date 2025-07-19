package io.github._20nickname20.imbored.game_objects.entities.container.locked_crate.parachute;

import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.container.CrateEntity;
import io.github._20nickname20.imbored.game_objects.entities.container.locked_crate.LockedWoodenCrateEntity;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Util;

public class AirdropCrate extends LockedWoodenCrateEntity {
    private final Vector2 parachuteRelative;

    public AirdropCrate(GameWorld world, float x, float y, float sizeX, float sizeY, float parachuteRelativeX, float parachuteRelativeY) {
        super(world, x, y, sizeX, sizeY);
        parachuteRelative = new Vector2(
            parachuteRelativeX,
            parachuteRelativeY
        );
    }

    public AirdropCrate(GameWorld world, EntityData data) {
        super(world, data);
        if (data instanceof AirdropCrateData airdropCrateData) {
            parachuteRelative = new Vector2(
                airdropCrateData.parachuteRelativeX,
                airdropCrateData.parachuteRelativeY
            );
        } else {
            parachuteRelative = new Vector2();
        }
    }

    private final Vector2 parachuteDirection = Vector2.Y.cpy();

    private Vector2 getParachuteRelative() {
        return parachuteRelative.cpy().rotateRad(this.b.getAngle());
    }

    private Vector2 getParachuteAnchorVelocity() {
        return this.b.getLinearVelocityFromLocalPoint(parachuteRelative).cpy();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        parachuteDirection.add(getParachuteAnchorVelocity().scl(-dt * 2f));
        parachuteDirection.nor();

        this.b.applyLinearImpulse(parachuteDirection.cpy().scl(getParachuteAnchorVelocity().len() * 0.575f), getParachuteRelative().add(this.b.getPosition()), true);
    }

    @Override
    public boolean render(GameRenderer renderer) {
        super.render(renderer);
        Vector2 parachuteTranslation = getParachuteRelative();
        renderer.withTranslation(parachuteTranslation.x, parachuteTranslation.y, () -> {
            Vector2 prevPoint = null;
            float parachuteAngle = (float) (parachuteDirection.angleRad() - Math.PI / 2);
            float startAngle = (float) (Math.PI / 3.5f) + parachuteAngle;
            float endAngle = (float) (Math.PI - Math.PI / 3.5f) + parachuteAngle;
            float stepAngle = (float) (Math.PI * 6f / 7f / 12.001f);
            float length = Math.max(this.sizeX, this.sizeY) * 2.25f;
            for (float angle = startAngle; angle < endAngle; angle += stepAngle) {
                Vector2 point = Vector2.X.cpy().rotateRad(angle).scl(length);
                if (prevPoint == null) {
                    prevPoint = point;
                    renderer.setColor(Material.CLOTH.color);
                    renderer.line(prevPoint, Vector2.Zero);
                    continue;
                }

                renderer.setColor(Material.CLOTH.color);
                renderer.line(point, Vector2.Zero);
                renderer.line(point, prevPoint);
                prevPoint.set(point);
            }
        });
        return false;
    }

    @Override
    public EntityData createPersistentData() {
        AirdropCrateData airdropCrateData;
        if (this.persistentData == null) {
            airdropCrateData = new AirdropCrateData();
        } else {
            airdropCrateData = (AirdropCrateData) this.persistentData;
        }
        airdropCrateData.parachuteRelativeX = parachuteRelative.x;
        airdropCrateData.parachuteRelativeY = parachuteRelative.y;
        this.persistentData = airdropCrateData;
        return super.createPersistentData();
    }

    public static class AirdropCrateData extends CrateEntity.CrateEntityData {
        float parachuteRelativeX, parachuteRelativeY;
    }
}
