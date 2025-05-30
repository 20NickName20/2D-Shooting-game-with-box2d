package io.github._20nickname20.imbored.game_objects.items.usable.joint;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.JointItem;
import io.github._20nickname20.imbored.game_objects.JointEntity;
import io.github._20nickname20.imbored.render.GameRenderer;

public abstract class DistanceJointItem extends JointItem {
    public DistanceJointItem() {
        super();
    }

    public DistanceJointItem(ItemData data) {
        super(data);
    }

    public abstract float getFrequency();
    public abstract float getDamping();

    @Override
    public void createJoint(PlayerEntity player, Body bodyA, Body bodyB, Vector2 posA, Vector2 posB) {
        player.gameWorld.spawn(new JointEntity(player.gameWorld, getColor(), posA, posB, getFrequency(), getDamping()));
    }

    @Override
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(getColor());
        if (handHolder != null && this.isATargeted()) {
            renderer.circle(0, 0, 1.75f, 6);

            renderer.saveTransform();
            renderer.resetTransform();

            Vector2 posB = handHolder.getCursorPosition();
            if (this.posA.dst(posB) > this.getMaxDistance() || getClosestBodyForConnecting(handHolder.world, handHolder.getCursorPosition()) == null) {
                renderer.setColor(0.8f, 0.3f, 0.3f, 1);
            }

            renderer.line(this.posA, posB);

            renderer.loadTransform();
            return;
        }
        if (handHolder != null && getClosestBodyForConnecting(handHolder.world, handHolder.getCursorPosition()) != null) {
            renderer.circle(0, 0, 1.75f, 6);
            return;
        }
        renderer.line(-1, -1, 1, 1);
    }
}
