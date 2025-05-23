package io.github._20nickname20.imbored.game_objects;

import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;
import io.github._20nickname20.imbored.render.GameRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Inventory {
    private float sizeLimit;
    private float containedSize = 0;
    private final ArrayList<Item> items = new ArrayList<>();
    private int selectedSlot = 0;
    private InventoryHolder holder;

    public Inventory(float sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public Inventory(InventoryData data) {
        this.sizeLimit = data.sizeLimit;
        for (Item.ItemData itemData : data.items) {
            this.add(Item.createFromData(itemData, null));
        }
    }

    public void setHolder(InventoryHolder holder) {
        this.holder = holder;
        for (Item item : items) {
            item.setHolder(holder.getEntity());
        }
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

    public List<Item> setSizeLimit(float sizeLimit) {
        List<Item> didNotFit = new ArrayList<>();
        while (containedSize > sizeLimit) {
            this.removeItem(0);
        }
        this.sizeLimit = sizeLimit;
        return didNotFit;
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
        if (holder != null) item.setHolder(holder.getEntity());
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
        return Collections.unmodifiableList(items);
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

    void renderSlot(GameRenderer renderer, boolean active, float x, float y) {
        renderer.rect(x - 2, y - 2, 4, 4);
        if (active) {
            renderer.rect(x - 2.2f, y - 2.2f, 4.4f, 4.4f);
        }
    }

    public static final float slotSize = 4.6f;

    public void renderPart(GameRenderer renderer, int slotAmount, Item special) {
        int range = slotAmount / 2;
        for (int i = -range; i <= range; i++) {
            Item item = this.get(i + this.getSelectedSlot());
            if (special != null && item == special) {
                renderer.setColor(0.9f, 0.4f, 0.4f, 1f);
            } else {
                renderer.setColor(0.5f, 0.5f, 0.5f, 1f);
            }
            renderSlot(renderer, i == 0, i * slotSize, 0);
            if (item != null) {
                renderer.withTranslation(i * slotSize, 0, () -> {
                    item.render(renderer, null);
                });
            }
        }
    }

    public void renderPart(GameRenderer renderer, int slotAmount) {
        renderPart(renderer, slotAmount, null);
    }

    public InventoryData createPersistentData() {
        InventoryData data = new InventoryData();
        Item.ItemData[] itemsData = new Item.ItemData[items.size()];
        int i = 0;
        for (Item item : items) {
            itemsData[i] = item.createPersistentData();
            i++;
        }
        data.items = itemsData;
        data.sizeLimit = sizeLimit;
        return data;
    }

    public static class InventoryData {
        public float sizeLimit;
        public Item.ItemData[] items;
    }
}
