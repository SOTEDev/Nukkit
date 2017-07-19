package cn.nukkit.inventory;

import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

abstract class TemporaryInventory extends ContainerInventory {

    public TemporaryInventory(InventoryHolder holder, InventoryType type) {
        super(holder, type);
    }

    abstract public int getResultSlotIndex();

    @Override
    public void onClose(Player who){
        for(Map.Entry<Integer, Item> e : this.getContents().entrySet()){
            if(e.getKey() == this.getResultSlotIndex()){
                continue;
            }
            who.dropItem(e.getValue());
        }
        this.clearAll();
    }
}