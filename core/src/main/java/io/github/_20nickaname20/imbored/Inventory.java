package io.github._20nickaname20.imbored;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickaname20.imbored.entities.InventoryHolder;

import java.util.ArrayList;

public class Inventory {
    private float sizeLimit;
    private float containedSize = 0;
    private ArrayList<Item> items = new ArrayList<>();
    private int selectedSlot = 0;
    private final InventoryHolder holder;

    public Inventory(InventoryHolder holder, float sizeLimit) {
        this.sizeLimit = sizeLimit;
        this.holder = holder;
    }

    public InventoryHolder getHolder() {
        return holder;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public float getSizeLimit() {
        return sizeLimit;
    }

    public float getContainedSize() {
        return containedSize;
    }

    public float getFreeSpace() {
        return sizeLimit - containedSize;
    }

    public void scroll(int amount) {
        int count = items.size();
        if (count == 0) {
            selectedSlot = 0;
            return;
        }
        int scrolledSlot = selectedSlot + amount;
        if (scrolledSlot < 0) {
            scrolledSlot += count;
        }
        getSelectedItem().onDeselect(holder);
        selectedSlot = scrolledSlot % count;
        getSelectedItem().onSelect(holder);
    }

    public boolean add(Item item) {
        if (containedSize + item.getSize() > sizeLimit) return false;
        containedSize += item.getSize();
        items.add(item);
        return true;
    }

    public Item get(int index) {
        if (index < 0 || index >= items.size()) return null;
        return items.get(index);
    }

    public int amount() {
        return items.size();
    }

    public Item getSelectedItem() {
        int count = items.size();
        if (count == 0) return null;
        return items.get(selectedSlot);
    }

    public void removeItem(int index) {
        Item removed = items.remove(index);
        removed.onRemove(holder);
        containedSize -= removed.getSize();
        if (selectedSlot >= items.size() && selectedSlot != 0) {
            selectedSlot -= 1;
        }
    }

    public void removeSelectedItem() {
        removeItem(selectedSlot);
    }

    public void update(float dt) {
        for (Item item : items) {
            if (!item.isUpdating()) continue;
            item.update(holder, dt);
        }
    }

    void renderSlot(ShapeRenderer renderer, boolean active, float x, float y) {
        renderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        renderer.rect(x - 2, y - 2, 4, 4);
        if (active) {
            renderer.rect(x - 2.2f, y - 2.2f, 4.4f, 4.4f);
        }
    }

    public static final float slotSize = 4.6f;

    //inventory.renderFull(renderer, 3);
    public void renderFull(ShapeRenderer renderer, int lineSize) {
        int itemCount = items.size();
        int slotCount = (itemCount / lineSize + 1) * lineSize;
        renderer.translate(-(lineSize - 1) * slotSize / 2, 0, 0);
        for (int i = 0; i < slotCount; i++) {
            renderSlot(renderer, getSelectedSlot() == i, (i % lineSize) * slotSize, -i / lineSize * slotSize);
            if (i >= itemCount) continue;
            Item item = items.get(i);
            renderer.translate((i % lineSize) * slotSize, -i / lineSize * slotSize, 0);

            item.render(renderer, false);

            renderer.translate(-(i % lineSize) * slotSize, i / lineSize * slotSize, 0);

        }
        renderer.translate((lineSize - 1) * slotSize / 2, 0, 0);
    }

    public void renderPart(ShapeRenderer renderer, int slotAmount) {
        int range = slotAmount / 2;
        for (int i = -range; i <= range; i++) {
            renderSlot(renderer, i == 0, i * slotSize, 0);
            Item item = this.get(i + this.getSelectedSlot());
            if (item != null) {
                renderer.translate(i * slotSize, 0, 0);

                item.render(renderer, false);

                renderer.translate(-i * slotSize, 0, 0);
            }
        }
    }
}
