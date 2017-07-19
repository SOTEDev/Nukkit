package cn.nukkit.inventory;

public class FloatingInventory extends BaseInventory implements Cloneable {
    public FloatingInventory(InventoryHolder holder) {
        super(holder, InventoryType.PLAYER_FLOATING);
    }

    @Override
    public FloatingInventory clone() {
        try {
            FloatingInventory item = (FloatingInventory) super.clone();
            item.slots.putAll(this.slots);
            return item;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}