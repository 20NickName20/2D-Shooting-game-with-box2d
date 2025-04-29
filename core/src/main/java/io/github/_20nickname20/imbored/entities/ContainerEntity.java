package io.github._20nickname20.imbored.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.Inventory;
import io.github._20nickname20.imbored.Item;
import io.github._20nickname20.imbored.Material;
import io.github._20nickname20.imbored.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.util.FindBody;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

public abstract class ContainerEntity extends BlockEntity implements InventoryHolder {
    Inventory inventory;

    public ContainerEntity(GameWorld world, float x, float y, Shape shape, Material material, float maxHealth, float inventorySize) {
        super(world, x, y, shape, material, maxHealth);
        this.inventory = new Inventory(this, inventorySize);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void scrollItem(int amount) {
        inventory.scroll(amount);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Vector2 position = this.b.getPosition();
        for (int i = inventory.amount() - 1; i >= 0; i--) {
            gameWorld.dropItem(position, new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).scl(20), inventory.get(i));
        }
    }
}
