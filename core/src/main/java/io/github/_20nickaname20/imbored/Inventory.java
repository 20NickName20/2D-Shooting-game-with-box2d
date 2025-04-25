package io.github._20nickaname20.imbored;

import java.util.ArrayList;

public class Inventory {
    public float sizeLimit;
    private float containedSize = 0;
    private ArrayList<Item> items = new ArrayList<>();
    private int selectedSlot = 0;
    private final Entity holder;

    public Inventory(Entity holder, float sizeLimit) {
        this.sizeLimit = sizeLimit;
        this.holder = holder;
    }

    public Entity getHolder() {
        return holder;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void scroll(int amount) {
        int count = items.size();
        if (count == 0) {
            selectedSlot = 0;
            return;
        }
        int scrolledSlot = selectedSlot + amount;
        if (scrolledSlot < 0) {
            scrolledSlot = count - scrolledSlot;
        }
        selectedSlot = scrolledSlot % count;
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

    public float getContainedSize() {
        return containedSize;
    }

    public void remove(int index) {
        Item removed = items.remove(index);
        containedSize -= removed.getSize();
        if (selectedSlot >= items.size() && selectedSlot != 0) {
            selectedSlot -= 1;
        }
    }

    public void update(float dt) {
        for (Item item : items) {
            if (!item.isUpdating()) continue;
            item.update(holder, dt);
        }
    }
}
