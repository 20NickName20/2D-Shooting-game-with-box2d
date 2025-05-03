package io.github._20nickname20.imbored.game_objects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;

import java.util.*;

import static io.github._20nickname20.imbored.util.With.translation;

public class Inventory {
    private float sizeLimit;
    private float containedSize = 0;
    private final ArrayList<Item> items = new ArrayList<>();
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

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void setSizeLimit(float sizeLimit) {
        this.sizeLimit = sizeLimit;
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
        selectedSlot = scrolledSlot % count;
    }

    public boolean canFit(Item item) {
        return !(containedSize + item.getSize() > sizeLimit);
    }

    public boolean add(Item item) {
        if (item == null) return false;
        if (containedSize + item.getSize() > sizeLimit) return false;
        containedSize += item.getSize();
        item.setHolder(holder.getEntity());
        items.add(item);
        return true;
    }

    public List<Item> addAll(Collection<Item> collection) {
        List<Item> didNotFit = new ArrayList<>();
        for (Item item : collection) {
            if (!this.add(item)) {
                didNotFit.add(item);
            }
        }
        return didNotFit;
    }

    public Item get(int index) {
        if (index < 0 || index >= items.size()) return null;
        return items.get(index);
    }

    public int amount() {
        return items.size();
    }

    public List<Item> getItems() {
        return (List<Item>) items.clone();
    }

    public Item getSelectedItem() {
        int count = items.size();
        if (count == 0) return null;
        return items.get(selectedSlot);
    }

    public Item removeItem(int index) {
        Item removed = items.remove(index);
        containedSize -= removed.getSize();
        if (selectedSlot >= items.size() && selectedSlot != 0) {
            selectedSlot -= 1;
        }
        return removed;
    }

    public void removeItem(Item item) {
        int index = items.indexOf(item);
        if (index == -1) return;
        removeItem(index);
    }

    public Item removeSelectedItem() {
        return removeItem(selectedSlot);
    }

    private final List<Integer> itemsToRemove = new ArrayList<>();
    public void update(float dt) {
        int amount = this.amount();
        for (int i = 0; i < amount; i++) {
            Item item = this.get(i);
            item.update(dt);
            if (item.isRemoved()) {
                itemsToRemove.add(i);
            }
        }
        for (int i = itemsToRemove.size() - 1; i >= 0; i--) {
            this.removeItem(itemsToRemove.get(i));
        }
        itemsToRemove.clear();
    }

    void renderSlot(ShapeRenderer renderer, boolean active, float x, float y) {
        renderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        renderer.rect(x - 2, y - 2, 4, 4);
        if (active) {
            renderer.rect(x - 2.2f, y - 2.2f, 4.4f, 4.4f);
        }
    }

    public static final float slotSize = 4.6f;

    public void renderPart(ShapeRenderer renderer, int slotAmount) {
        int range = slotAmount / 2;
        for (int i = -range; i <= range; i++) {
            renderSlot(renderer, i == 0, i * slotSize, 0);
            Item item = this.get(i + this.getSelectedSlot());
            if (item != null) {
                translation(renderer, i * slotSize, 0, () -> {
                    item.render(renderer, null);
                });
            }
        }
    }
}
